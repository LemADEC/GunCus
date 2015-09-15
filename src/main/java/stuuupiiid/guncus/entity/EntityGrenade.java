package stuuupiiid.guncus.entity;

import io.netty.buffer.ByteBuf;

import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.network.ISynchronisingEntity;
import stuuupiiid.guncus.network.PacketHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.world.BlockEvent;

public class EntityGrenade extends EntityArrow implements IProjectile, IEntityAdditionalSpawnData, ISynchronisingEntity {
	private static float fDegToRadFactor = ((float)Math.PI) / 180.0F;
	private static double dRadToDegFactor = 180.0D / Math.PI;
	
	private final static float slowMotionFactor = 1F;
	private final static int MAX_FLIGHT_DURATION_TICKS = Math.round(60 * slowMotionFactor);	// 3 s to reach a target
	private final static int MAX_BOUNCING_DURATION_TICKS = Math.round(600 * slowMotionFactor);	// 30 s bouncing around
	private final static int MAX_ENTITYHIT_DURATION_TICKS = Math.round(100 * slowMotionFactor);	// 5 s on an entity
	private final static int MAX_BLOCKHIT_DURATION_TICKS = Math.round(100 * slowMotionFactor);	// 5 s on the ground
	private final static int MAX_LIFE_DURATION_TICKS = Math.round(6000 * slowMotionFactor);	// 5 mn max total time
	
	public final static int STATE_FLYING = 0;
	public final static int STATE_BOUNCING = 1;
	public final static int STATE_ENTITYHIT = 2;
	public final static int STATE_BLOCKHIT = 3;
	public int state = STATE_FLYING;
	private int stateTicks = 0;
	private int blockX = -1;	// field_145791_d
	private int blockY = -1;	// field_145792_e
	private int blockZ = -1;	// field_145789_f
	private Block blockCollided = Blocks.air;
	private int blockCollidedMetadata = -1;	// field inData
	
	private boolean isRocket = false;
	
	public EntityGrenade(World world) {
		super(world);
		setSize(0.5F, 0.5F);
		// canBePickedUp = 0;
	}
	
	public EntityGrenade(World par1World, EntityPlayer parEntityPlayer, int accuracy, boolean isRocket) {
		super(par1World);
		setSize(0.5F, 0.5F);
		renderDistanceWeight = 10.0D;
		shootingEntity = parEntityPlayer;
		setLocationAndAngles(parEntityPlayer.posX, parEntityPlayer.posY + parEntityPlayer.getEyeHeight(), parEntityPlayer.posZ, parEntityPlayer.rotationYaw, parEntityPlayer.rotationPitch);
		posX -= MathHelper.cos(rotationYaw * fDegToRadFactor) * 0.16F;
		posY -= 0.1000000014901161D;
		posZ -= MathHelper.sin(rotationYaw * fDegToRadFactor) * 0.16F;
		setPosition(parEntityPlayer.posX, parEntityPlayer.posY + parEntityPlayer.getEyeHeight(), parEntityPlayer.posZ);
		this.yOffset = 0.0F;
		this.motionX = -MathHelper.sin(rotationYaw   * fDegToRadFactor) * MathHelper.cos(rotationPitch * fDegToRadFactor);
		this.motionZ =  MathHelper.cos(rotationYaw   * fDegToRadFactor) * MathHelper.cos(rotationPitch * fDegToRadFactor);
		this.motionY = -MathHelper.sin(rotationPitch * fDegToRadFactor);
		if (accuracy < 100) {
			int accX1 = rand.nextInt(101 - accuracy);
			int accY1 = rand.nextInt(101 - accuracy);
			int accZ1 = rand.nextInt(101 - accuracy);
			double accX2 = accX1 - rand.nextInt(accX1 + 1) * 2;
			double accY2 = accY1 - rand.nextInt(accY1 + 1) * 2;
			double accZ2 = accZ1 - rand.nextInt(accZ1 + 1) * 2;
			motionX += accX2 / 370.0D;
			motionY += accY2 / 370.0D;
			motionZ += accZ2 / 370.0D;
		}
		this.isRocket = isRocket;
		setThrowableHeading(motionX, motionY, motionZ, (isRocket ? 3.0F : 4.2F) / slowMotionFactor, 1.0F);
	}
	
	@SideOnly(Side.CLIENT)
	public void onUpdate_tailParticles() {
		Minecraft mc = Minecraft.getMinecraft();
		double dX = mc.renderViewEntity.posX - posX;
		double dY = mc.renderViewEntity.posY - posY;
		double dZ = mc.renderViewEntity.posZ - posZ;
		double range = 96 / (1 + 2 * mc.gameSettings.particleSetting);
		if (dX * dX + dY * dY + dZ * dZ < range * range) {
			double tailX = posX - 1.75 * width * MathHelper.sin(rotationYaw   * fDegToRadFactor) * MathHelper.cos(rotationPitch * fDegToRadFactor);
			double tailZ = posZ - 1.75 * width * MathHelper.cos(rotationYaw   * fDegToRadFactor) * MathHelper.cos(rotationPitch * fDegToRadFactor);
			double tailY = posY - 1.75 * width * MathHelper.sin(rotationPitch * fDegToRadFactor);
			
			for (int smokeIndex = 0; smokeIndex < (4 - mc.gameSettings.particleSetting); smokeIndex++) {
				double factor = 0.20 * smokeIndex;
				// Directly spawn largesmoke as per RenderGlobal.doSpawnParticle
				mc.effectRenderer.addEffect(new EntitySmokeFX(
						worldObj,
						tailX - motionX * factor,
						tailY - motionY * factor,
						tailZ - motionZ * factor,
						motionX, motionY + 0.3, motionZ,
						1.5F));
			}
		}
	}
	
	@Override
	public void onUpdate() {
		// skip the arrow computation
		super.onEntityUpdate();
		
		if ((posY > 300.0D) || (ticksExisted >= MAX_LIFE_DURATION_TICKS)) {
			setDead();
		}
		
		// (EntityArrow) Re-orient bullet depending on motion vector - only used after recovering?)
		if ((prevRotationPitch == 0.0F) && (prevRotationYaw == 0.0F)) {
			float f = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
			prevRotationYaw = (rotationYaw = (float) (Math.atan2(motionX, motionZ) * dRadToDegFactor));
			prevRotationPitch = (rotationPitch = (float) (Math.atan2(motionY, f) * dRadToDegFactor));
		}
		
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			onUpdate_tailParticles();
			
			// Do collision resolution on server side only
			return;
		}
		
		if (state == STATE_BLOCKHIT) {
			// get collided block
			Block block = worldObj.getBlock(blockX, blockY, blockZ);
			int blockMetadata = worldObj.getBlockMetadata(blockX, blockY, blockZ);
			
			if (block == blockCollided && blockMetadata == blockCollidedMetadata) {
				stateTicks++;
				
				if (stateTicks >= MAX_BLOCKHIT_DURATION_TICKS) {
					explode();
					setDead();
				}
			} else {
				motionX *= rand.nextFloat() * 0.2F;
				motionY *= rand.nextFloat() * 0.2F;
				motionZ *= rand.nextFloat() * 0.2F;
				state = STATE_BOUNCING;
				stateTicks = 0;
				PacketHandler.sendToClient_syncEntity(this);
			}
			
		} else if (state == STATE_ENTITYHIT) {
			stateTicks++;
			
			if (stateTicks >= MAX_ENTITYHIT_DURATION_TICKS) {
				explode();
				setDead();
			}
			
		} else {
			stateTicks++;
			if (state == STATE_FLYING) {
				if (stateTicks >= MAX_FLIGHT_DURATION_TICKS) {
					explode();
					setDead();
				}
			} else if (state == STATE_BOUNCING) {
				if (stateTicks >= MAX_BOUNCING_DURATION_TICKS) {
					explode();
					setDead();
				}
			} else {
				GunCus.logger.error("Killing grenade with invalid state " + state + " " + this);
				setDead();
			}
			
			// Check for block collision
			Vec3 vecCurrent = Vec3.createVectorHelper(posX, posY, posZ);
			Vec3 vecNextTick = Vec3.createVectorHelper(posX + motionX, posY + motionY, posZ + motionZ);
			MovingObjectPosition mopCollision = worldObj.func_147447_a(vecCurrent, vecNextTick, false, true, false); // rayTraceBlocks_do_do
			vecCurrent = Vec3.createVectorHelper(posX, posY, posZ);
			vecNextTick = Vec3.createVectorHelper(posX + motionX, posY + motionY, posZ + motionZ);
			
			if (mopCollision != null) {
				vecNextTick = Vec3.createVectorHelper(mopCollision.hitVec.xCoord, mopCollision.hitVec.yCoord, mopCollision.hitVec.zCoord);
			}
			
			List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
			double distance2Closest = 0.0D;
			
			for (Entity entityInRange : list) {
				// Check for no-PvP attributes
				if (entityInRange instanceof EntityPlayer) {
					EntityPlayer entityPlayerHit = (EntityPlayer) entityInRange;
					
					if (entityPlayerHit.capabilities.disableDamage || ((shootingEntity instanceof EntityPlayer) && (!((EntityPlayer)shootingEntity).canAttackPlayer(entityPlayerHit)))) {
						continue;
					}
				}
				
				// Prevent immediate self-shooting
				if (!entityInRange.canBeCollidedWith() || ((entityInRange == shootingEntity) && (stateTicks < 5))) {
					continue;
				}
				
				float tolerance = 0.1F;
				AxisAlignedBB axisalignedbb1 = entityInRange.boundingBox.expand(tolerance, tolerance, tolerance);
				MovingObjectPosition mopEntityInRange = axisalignedbb1.calculateIntercept(vecCurrent, vecNextTick);
				
				if (mopEntityInRange != null) {
					double distance2InRange = vecCurrent.squareDistanceTo(mopEntityInRange.hitVec);
					
					if ((distance2InRange < distance2Closest) || (distance2Closest == 0.0D)) {
						mopCollision = mopEntityInRange;
						mopCollision.entityHit = entityInRange;
						distance2Closest = distance2InRange;
					}
				}
			}
			
			// Resolve collision
			if (mopCollision != null) {
				if (ticksExisted < 5) {
					// Fix the grenade on collision point
					setPosition(mopCollision.hitVec.xCoord, mopCollision.hitVec.yCoord, mopCollision.hitVec.zCoord);
					state = STATE_ENTITYHIT;
					stateTicks = 0;
					PacketHandler.sendToClient_syncEntity(this);
				} else if (mopCollision.entityHit != null) {
					// (entity is too close) Bouncing
					motionX *= -0.1D;
					motionY *= -0.1D;
					motionZ *= -0.1D;
					rotationYaw += 180.0F;
					prevRotationYaw += 180.0F;
					state = STATE_BOUNCING;
					stateTicks = 0;
					PacketHandler.sendToClient_syncEntity(this);
				} else {
					blockX = mopCollision.blockX;
					blockY = mopCollision.blockY;
					blockZ = mopCollision.blockZ;
					blockCollided = worldObj.getBlock(blockX, blockY, blockZ);
					blockCollidedMetadata = worldObj.getBlockMetadata(blockX, blockY, blockZ);
					// break weak blocks and pass through them 
					if ( GunCus.enableBlockDamage && (
					     (blockCollided == Blocks.glass_pane) || (blockCollided == Blocks.stained_glass_pane) 
					  || (blockCollided == Blocks.web) || (blockCollided == Blocks.glowstone)) ) {
						if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
							boolean isCanceled = true;
							if (shootingEntity instanceof EntityPlayerMP) {
								BlockEvent.BreakEvent event = ForgeHooks.onBlockBreakEvent(worldObj, GameType.SURVIVAL, (EntityPlayerMP)shootingEntity, blockX, blockY, blockZ);
								isCanceled = event.isCanceled();
							}
							if (isCanceled) {
								// (protected block) Bouncing
								motionX *= -0.1D;
								motionY *= -0.1D;
								motionZ *= -0.1D;
								rotationYaw += 180.0F;
								prevRotationYaw += 180.0F;
								state = STATE_BOUNCING;
								stateTicks = 0;
								PacketHandler.sendToClient_syncEntity(this);
							} else {
								worldObj.setBlockToAir(blockX, blockY, blockZ);
								// onBlockHit(mopCollision.hitVec);
							}
						}
					} else if (!blockCollided.isAir(worldObj, blockX, blockY, blockZ)) {
						// (not in air) Fix the grenade 5% in the block
						motionX = ((float) (mopCollision.hitVec.xCoord - posX));
						motionY = ((float) (mopCollision.hitVec.yCoord - posY));
						motionZ = ((float) (mopCollision.hitVec.zCoord - posZ));
						float speed = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
						posX -= motionX / speed * 0.05D;
						posY -= motionY / speed * 0.05D;
						posZ -= motionZ / speed * 0.05D;
						state = STATE_BLOCKHIT;
						stateTicks = 0;
						PacketHandler.sendToClient_syncEntity(this);
						blockCollided.onEntityCollidedWithBlock(worldObj, blockX, blockY, blockZ, this);
						// onBlockHit(mopCollision.hitVec);
					}
				}
			}
			
			// apply movement
			posX += motionX;
			posY += motionY;
			posZ += motionZ;
			
			// clamp angulations
			float f2 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
			rotationYaw = ((float) (Math.atan2(motionX, motionZ) * dRadToDegFactor));
			
			for (rotationPitch = ((float) (Math.atan2(motionY, f2) * dRadToDegFactor)); rotationPitch - prevRotationPitch < -180.0F; prevRotationPitch -= 360.0F) {
				;
			}
			while (rotationPitch - prevRotationPitch >= 180.0F) {
				prevRotationPitch += 360.0F;
			}
			
			while (rotationYaw - prevRotationYaw < -180.0F) {
				prevRotationYaw -= 360.0F;
			}
			
			while (rotationYaw - prevRotationYaw >= 180.0F) {
				prevRotationYaw += 360.0F;
			}
			
			rotationPitch = (prevRotationPitch + (rotationPitch - prevRotationPitch) * 0.2F);
			rotationYaw = (prevRotationYaw + (rotationYaw - prevRotationYaw) * 0.2F);
			double friction = 0.01;
			
			if (isInWater()) {
				for (int bubleIndex = 0; bubleIndex < 4; bubleIndex++) {
					double factor = 0.20 * bubleIndex;
					worldObj.spawnParticle("bubble",
							posX - motionX * factor,
							posY - motionY * factor,
							posZ - motionZ * factor,
							motionX, motionY, motionZ);
				}
				
				friction = 0.2;
			}
			
			double motionFactor = 1.0D - friction / slowMotionFactor;
			motionX *= motionFactor;
			motionY *= motionFactor;
			motionZ *= motionFactor;
			// apply gravity when applicable
			if (state == STATE_FLYING || state == STATE_BOUNCING) { 
				motionY -= (isRocket ? 0.0122D : 0.15D) / slowMotionFactor;
			} else if (state == STATE_BLOCKHIT) {
				motionY -= (isRocket ? 0.007D : 0.15D) / slowMotionFactor;
			}
			setPosition(posX, posY, posZ);
			func_145775_I();	// doBlockCollisions();
		}
	}
	
	public void explode() {
		if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			worldObj.createExplosion(shootingEntity, posX, posY, posZ, isRocket ? 7.0F : 3.5F, GunCus.enableBlockDamage);
		}
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		// (ancestor fully replaced, not calling it)
		nbttagcompound.setByte("state", (byte)state);
		nbttagcompound.setInteger("stateTicks", stateTicks);
		nbttagcompound.setInteger("blockX", blockX);
		nbttagcompound.setInteger("blockY", blockY);
		nbttagcompound.setInteger("blockZ", blockZ);
		nbttagcompound.setInteger("blockCollided", Block.getIdFromBlock(blockCollided));
		nbttagcompound.setByte("blockCollidedMetadata", (byte)blockCollidedMetadata);
		nbttagcompound.setBoolean("isRocket", isRocket);
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		state = nbttagcompound.getByte("state");
		stateTicks = nbttagcompound.getInteger("stateTicks");
		blockX = nbttagcompound.getInteger("blockX");
		blockY = nbttagcompound.getInteger("blockY");
		blockZ = nbttagcompound.getInteger("blockZ");
		blockCollided = Block.getBlockById(nbttagcompound.getInteger("blockCollided"));
		blockCollidedMetadata = nbttagcompound.getByte("blockCollidedMetadata");
		isRocket = nbttagcompound.getBoolean("isRocket");
	}
	
	@Override
	public NBTTagCompound writeSyncDataCompound() {
		NBTTagCompound syncDataCompound = new NBTTagCompound();
		syncDataCompound.setByte("state", (byte)state);
		syncDataCompound.setInteger("stateTicks", stateTicks);
		return syncDataCompound;
	}
	
	@Override
	public void readSyncDataCompound(NBTTagCompound syncDataCompound) {
		state = syncDataCompound.getByte("state");
		stateTicks = syncDataCompound.getInteger("stateTicks");
	}
	
	@Override
	public void writeSpawnData(ByteBuf buffer) {
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		writeEntityToNBT(nbttagcompound);
		ByteBufUtils.writeTag(buffer, nbttagcompound);
	}
	
	@Override
	public void readSpawnData(ByteBuf buffer) {
		readEntityFromNBT(ByteBufUtils.readTag(buffer));
	}
}
