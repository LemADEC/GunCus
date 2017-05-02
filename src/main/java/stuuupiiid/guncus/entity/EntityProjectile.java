package stuuupiiid.guncus.entity;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

import java.util.List;
import java.util.Set;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.network.ISynchronisingEntity;
import stuuupiiid.guncus.network.PacketHandler;
import mcheli.aircraft.MCH_EntityAircraft;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityBubbleFX;
import net.minecraft.client.particle.EntityDiggingFX;
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

public abstract class EntityProjectile extends EntityArrow implements IProjectile, IEntityAdditionalSpawnData, ISynchronisingEntity {
	protected static float fDegToRadFactor = ((float)Math.PI) / 180.0F;
	protected static float fRadToDegFactor = 180.0F / ((float)Math.PI);
	
	// Bullet time effect: 1 is default, higher is slower
	protected double slowMotionFactor = 1.0D;
	// Maximum duration before hitting a target block or entity
	protected int MAX_FLIGHT_DURATION_TICKS;
	// Maximum duration while bouncing between targets
	protected int MAX_BOUNCING_DURATION_TICKS;
	// Maximum duration after hitting an entity (and remaining stick to it)
	protected int MAX_ENTITYHIT_DURATION_TICKS;
	// Maximum duration after hitting a block (and remaining stick to it)
	protected int MAX_BLOCKHIT_DURATION_TICKS;
	// Maximum total duration
	protected int MAX_LIFE_DURATION_TICKS;
	// Time while the ammunition isn't armed
	protected int SAFETY_FUSE_TICKS;
	// Blocks that get broken when hit
	protected Set<Blocks> WEAK_BLOCKS = null;	// shall be set by descendant
	// Since projectile starts inside shooting player, we're protecting him at initial tick
	protected int SHOOTER_SAFETY_TICKS = 2;
	
	public final static int STATE_FLYING = 0;
	public final static int STATE_BOUNCING = 1;
	public final static int STATE_ENTITYHIT = 2;
	public final static int STATE_BLOCKHIT = 3;
	public int state = STATE_FLYING;
	protected int stateTicks = 0;
	protected boolean isFirstHit = true;
	protected int blockX = -1;	// field_145791_d
	protected int blockY = -1;	// field_145792_e
	protected int blockZ = -1;	// field_145789_f
	protected Block blockCollided = null;	// field field_145790_g
	protected int blockCollidedMetadata = -1;	// field inData
	protected int brokenCount = 0;
	protected int entityHitID = -1;
	
	protected double previousX = Double.NaN;
	protected double previousY = Double.NaN;
	protected double previousZ = Double.NaN;
	
	protected Object shooterRide = null;
	
	public EntityProjectile(World world) {
		super(world);
	}
	
	public EntityProjectile(World parWorld, EntityPlayer entityPlayer, double speed, float accuracy) {
		super(parWorld);
		isImmuneToFire = true;
		renderDistanceWeight = 10.0D;
		shootingEntity = entityPlayer;
		rotationYaw = entityPlayer.rotationYaw;
		rotationPitch = entityPlayer.rotationPitch;
		motionX = - MathHelper.sin(rotationYaw   * fDegToRadFactor) * MathHelper.cos(rotationPitch * fDegToRadFactor);
		motionZ =   MathHelper.cos(rotationYaw   * fDegToRadFactor) * MathHelper.cos(rotationPitch * fDegToRadFactor);
		motionY = - MathHelper.sin(rotationPitch * fDegToRadFactor);
		posX = entityPlayer.posX - motionX * 0.16F;
		posY = entityPlayer.posY + entityPlayer.getEyeHeight() - 0.0D;
		posZ = entityPlayer.posZ - motionZ * 0.16F;
		setLocationAndAngles(posX, posY, posZ, rotationYaw, rotationPitch);
		
		yOffset = 0.0F;
		if (accuracy < 100) {
			int accuracyInt = Math.round(accuracy);
			int accX1 = rand.nextInt(101 - accuracyInt);
			int accY1 = rand.nextInt(101 - accuracyInt);
			int accZ1 = rand.nextInt(101 - accuracyInt);
			double accX2 = accX1 - rand.nextInt(accX1 + 1) * 2;
			double accY2 = accY1 - rand.nextInt(accY1 + 1) * 2;
			double accZ2 = accZ1 - rand.nextInt(accZ1 + 1) * 2;
			motionX += accX2 / 370.0D;
			motionY += accY2 / 370.0D;
			motionZ += accZ2 / 370.0D;
		}
		
		setThrowableHeading(motionX, motionY, motionZ, (float) (speed * (1.0D + 0.1D * rand.nextGaussian()) / slowMotionFactor), 1.0F);
		previousX = posX;
		previousY = posY;
		previousZ = posZ;
	}
	
	@SideOnly(Side.CLIENT)
	public void onClientUpdate() {
		double deltaX = posX - previousX;
		double deltaY = posY - previousY;
		double deltaZ = posZ - previousZ;
		// draw tail if we're fast enough
		if (previousX != Double.NaN && Math.abs(deltaX) + Math.abs(deltaY) + Math.abs(deltaZ) > 1.0D) {
			
			// Check render distance
			Minecraft mc = Minecraft.getMinecraft();
			double dX = mc.renderViewEntity.posX - posX;
			double dY = mc.renderViewEntity.posY - posY;
			double dZ = mc.renderViewEntity.posZ - posZ;
			double range = 96 / (1 + 2 * mc.gameSettings.particleSetting);
			if (dX * dX + dY * dY + dZ * dZ < range * range) {
				// build orientation vector
				double tailX = posX - 1.75 * width * MathHelper.sin(rotationYaw   * fDegToRadFactor) * MathHelper.cos(rotationPitch * fDegToRadFactor);
				double tailZ = posZ - 1.75 * width * MathHelper.cos(rotationYaw   * fDegToRadFactor) * MathHelper.cos(rotationPitch * fDegToRadFactor);
				double tailY = posY - 1.75 * width * MathHelper.sin(rotationPitch * fDegToRadFactor);
				
				// play splash sound
				float speedFactor = Math.min(1.0F, MathHelper.sqrt_double(motionX * motionX * 0.2D + motionY * motionY + motionZ * motionZ * 0.2D) * 0.2F);
				playSound(getSplashSound(), speedFactor, 1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.4F);
				
				int count = 3 * (4 - mc.gameSettings.particleSetting);
				double step = 1.0D / count;
				for (int index = 0; index < count; index++) {
					double factor = step * index;
					double x = tailX - deltaX * factor;
					double y = tailY - deltaY * factor;
					double z = tailZ - deltaZ * factor;
					
					// Directly spawn bubbles as per RenderGlobal.doSpawnParticle
					if (worldObj.getBlock(
							(int)Math.floor(x),
							(int)Math.floor(y),
							(int)Math.floor(z)) instanceof BlockLiquid) {
						mc.effectRenderer.addEffect(new EntityBubbleFX(
								worldObj, x, y, z,
								0.2D * deltaX + (rand.nextDouble() * 2.0D - 1.0D) * width,
								0.2D * deltaY - rand.nextDouble() * 0.2D,
								0.2D * deltaZ + (rand.nextDouble() * 2.0D - 1.0D) * width));
					}
				}
			}
		}
		
		// save position
		previousX = posX;
		previousY = posY;
		previousZ = posZ;
	}
	
	@SideOnly(Side.CLIENT)
	public void onClientBlockHit(boolean isBroken) {
		double hitX = posX + motionX;
		double hitY = posY + motionY;
		double hitZ = posZ + motionZ;
		
		// Check render distance
		Minecraft mc = Minecraft.getMinecraft();
		double dX = mc.renderViewEntity.posX - posX;
		double dY = mc.renderViewEntity.posY - posY;
		double dZ = mc.renderViewEntity.posZ - posZ;
		double range = 96 / (1 + 2 * mc.gameSettings.particleSetting);
		if (dX * dX + dY * dY + dZ * dZ < range * range) {
			
			// Directly spawn crack particles as per RenderGlobal.doSpawnParticle
			double particleMotionX = -0.1 * MathHelper.sin(rotationYaw   * fDegToRadFactor) * MathHelper.cos(rotationPitch * fDegToRadFactor);
			double particleMotionZ = -0.1 * MathHelper.sin(rotationPitch * fDegToRadFactor);
			double particleMotionY = -0.1 * MathHelper.cos(rotationYaw   * fDegToRadFactor) * MathHelper.cos(rotationPitch * fDegToRadFactor);
			
			float particleSpeed = 0.0F;
			int particleQuantity = isBroken ? 20 : 3;
			for (int index = 0; index < particleQuantity; index++) {
				mc.effectRenderer.addEffect(new EntityDiggingFX(
						worldObj,
						hitX + rand.nextGaussian() * particleMotionX,
						hitY + rand.nextGaussian() * particleMotionY,
						hitZ + rand.nextGaussian() * particleMotionZ,
						rand.nextGaussian() * particleSpeed,
						rand.nextGaussian() * particleSpeed,
						rand.nextGaussian() * particleSpeed,
						blockCollided, blockCollidedMetadata).applyRenderColor(blockCollidedMetadata));
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void onClientEntityHit() {
	}
	
	protected boolean isSafetyOn() {
		return ticksExisted < SAFETY_FUSE_TICKS;
	}
	
	abstract protected void onBlockCollidedIsNowAir();
	
	abstract protected void onSubTickElapsed();
	
	abstract protected void onTotalTickElapsed();
	
	abstract protected void onServerEntityCollision(Entity entityHit, Vec3 hitVec);
	
	abstract protected void onServerBlockCollision(final boolean isWeakBlock, Vec3 hitVec);
	
	abstract protected double getFriction();
	
	abstract protected double getGravity();
	
	@Override
	public void onUpdate() {
		// skip the arrow computation
		super.onEntityUpdate();
		
		if ((posY < -60.0D) || (posY > 300.0D) || (ticksExisted >= MAX_LIFE_DURATION_TICKS)) {
			onTotalTickElapsed();
			setDead();
		}
		
		// (EntityArrow) Re-orient bullet depending on motion vector - only used after recovering?)
		if ((prevRotationPitch == 0.0F) && (prevRotationYaw == 0.0F)) {
			float f = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
			prevRotationYaw = (rotationYaw = (float) (Math.atan2(motionX, motionZ)) * fRadToDegFactor);
			prevRotationPitch = (rotationPitch = (float) (Math.atan2(motionY, f)) * fRadToDegFactor);
		}
		
		if (worldObj.isRemote) {
			onClientUpdate();
			
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
					onSubTickElapsed();
					setDead();
				}
			} else {
				onBlockCollidedIsNowAir();
			}
			
		} else if (state == STATE_ENTITYHIT) {
			stateTicks++;
			
			if (stateTicks >= MAX_ENTITYHIT_DURATION_TICKS) {
				onSubTickElapsed();
				setDead();
			}
			
		} else {
			stateTicks++;
			if (state == STATE_FLYING) {
				if (stateTicks >= MAX_FLIGHT_DURATION_TICKS) {
					onSubTickElapsed();
					setDead();
				}
			} else if (state == STATE_BOUNCING) {// BOUNCING
				if (stateTicks >= MAX_BOUNCING_DURATION_TICKS) {
					onSubTickElapsed();
					setDead();
				}
			} else {
				GunCus.logger.error("Killing projectile with invalid state " + state + " " + this);
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
			
			// Check for entity collision
			List<Entity> list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.addCoord(motionX, motionY, motionZ).expand(1.0D, 1.0D, 1.0D));
			double distance2Closest = 0.0D;
			for (Entity entityInRange : list) {
				// Check for no-PvP attributes
				if (entityInRange instanceof EntityPlayer) {
					EntityPlayer entityPlayerHit = (EntityPlayer) entityInRange;
					
					if (entityPlayerHit.capabilities.disableDamage || ((shootingEntity instanceof EntityPlayer) && (!((EntityPlayer)shootingEntity).canAttackPlayer(entityPlayerHit)))) {
						continue;
					}
				}
				
				// Prevent immediate self-shooting or related rides or co-riders
				if (!entityInRange.canBeCollidedWith()) {
					continue;
				}
				if (stateTicks < SHOOTER_SAFETY_TICKS) {
					if (entityInRange == shootingEntity) {
						continue;
					}
					if (entityInRange.riddenByEntity != null) {
						if (entityInRange.riddenByEntity == shootingEntity) {
							continue;
						}
						if (Loader.isModLoaded("mcheli")) {
							if (shooterRide == null) {
								shooterRide = MCHeli_getRootEntity(shootingEntity);
							}
							if (shooterRide != null && MCHeli_getRootEntity(entityInRange) == shooterRide) {
								continue;
							}
						}
					}
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
				if (mopCollision.entityHit != null) {
					entityHitID = mopCollision.entityHit.getEntityId();
					onServerEntityCollision(mopCollision.entityHit, mopCollision.hitVec);
				} else if (mopCollision.entityHit == null) {
					// (block collision) Resolve block breaking first
					blockX = mopCollision.blockX;
					blockY = mopCollision.blockY;
					blockZ = mopCollision.blockZ;
					blockCollided = worldObj.getBlock(blockX, blockY, blockZ);
					blockCollidedMetadata = worldObj.getBlockMetadata(blockX, blockY, blockZ);
					// break weak blocks and pass through them 
					if (GunCus.enableBlockDamage && WEAK_BLOCKS.contains(blockCollided)) {
						if (!worldObj.isRemote) {
							boolean isCanceled = true;
							if (shootingEntity instanceof EntityPlayerMP) {
								BlockEvent.BreakEvent event = ForgeHooks.onBlockBreakEvent(worldObj, GameType.SURVIVAL, (EntityPlayerMP)shootingEntity, blockX, blockY, blockZ);
								isCanceled = event.isCanceled();
							}
							if (isCanceled) {
								// (protected block) Bouncing
								setPosition(mopCollision.hitVec.xCoord, mopCollision.hitVec.yCoord, mopCollision.hitVec.zCoord);
								motionX *= -0.1D;
								motionY *= -0.1D;
								motionZ *= -0.1D;
								rotationYaw += 180.0F;
								prevRotationYaw += 180.0F;
								state = STATE_BOUNCING;
								stateTicks = 0;
								PacketHandler.sendToClient_syncEntity(this);
							} else {
								brokenCount++;
								posX = mopCollision.hitVec.xCoord - motionX;
								posY = mopCollision.hitVec.yCoord - motionY;
								posZ = mopCollision.hitVec.zCoord - motionZ;
								worldObj.setBlockToAir(blockX, blockY, blockZ);
								onServerBlockCollision(true, mopCollision.hitVec);
								PacketHandler.sendToClient_syncEntity(this);
							}
						}
					} else if (!blockCollided.isAir(worldObj, blockX, blockY, blockZ)) {
						onServerBlockCollision(false, mopCollision.hitVec);
					}
				}
			}
			
			// apply movement
			posX += motionX;
			posY += motionY;
			posZ += motionZ;
			
			// update rotation as per current motion
			if (motionX != 0.0D || motionY != 0.0D || motionZ != 0.0D) {
				float f2 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
				rotationYaw = atan2_approximation3((float)motionX, (float)motionZ) * fRadToDegFactor;
				rotationPitch = atan2_approximation3((float)motionY, f2) * fRadToDegFactor;
			}
			
			while (rotationPitch - prevRotationPitch < -180.0F) {
				prevRotationPitch -= 360.0F;
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
			
			// apply friction
			double motionFactor = 1.0D - getFriction() / slowMotionFactor;
			motionX *= motionFactor;
			motionY *= motionFactor;
			motionZ *= motionFactor;
			
			// apply gravity when applicable
			if (state == STATE_FLYING || state == STATE_BOUNCING) {
				motionY -= getGravity() / slowMotionFactor;
			}
			
			setPosition(posX, posY, posZ);
			func_145775_I();	// doBlockCollisions();
		}
	}
	
	@Optional.Method(modid = "mcheli")
	protected static Object MCHeli_getRootEntity(Entity entity) {
		return MCH_EntityAircraft.getAircraft_RiddenOrControl(entity);		// as of 0.10.7
	}
	
	@Override
	protected void func_145775_I() {	// doBlockCollisions() but ignore the weak blocks
		int i = MathHelper.floor_double(boundingBox.minX + 0.001D);
		int j = MathHelper.floor_double(boundingBox.minY + 0.001D);
		int k = MathHelper.floor_double(boundingBox.minZ + 0.001D);
		int l = MathHelper.floor_double(boundingBox.maxX - 0.001D);
		int i1 = MathHelper.floor_double(boundingBox.maxY - 0.001D);
		int j1 = MathHelper.floor_double(boundingBox.maxZ - 0.001D);
		
		if (worldObj.checkChunksExist(i, j, k, l, i1, j1)) {
			for (int k1 = i; k1 <= l; k1++) {
				for (int l1 = j; l1 <= i1; l1++) {
					for (int i2 = k; i2 <= j1; i2++) {
						Block block = worldObj.getBlock(k1, l1, i2);
						if (!WEAK_BLOCKS.contains(block)) {
							block.onEntityCollidedWithBlock(worldObj, k1, l1, i2, this);
						}
					}
				}
			}
		}
	}
	
	@Override
	public boolean isSprinting() {// minor speed optimization
		return false;
	}
	
	@Override
	public boolean isBurning() {
		return false;
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		// (ancestor fully replaced, not calling it)
		nbttagcompound.setByte("state", (byte)state);
		nbttagcompound.setInteger("stateTicks", stateTicks);
		nbttagcompound.setBoolean("isFirstHit", isFirstHit);
		nbttagcompound.setInteger("blockX", blockX);
		nbttagcompound.setInteger("blockY", blockY);
		nbttagcompound.setInteger("blockZ", blockZ);
		nbttagcompound.setInteger("blockCollided", Block.getIdFromBlock(blockCollided));
		nbttagcompound.setByte("blockCollidedMetadata", (byte)blockCollidedMetadata);
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		state = nbttagcompound.getByte("state");
		stateTicks = nbttagcompound.getInteger("stateTicks");
		isFirstHit = nbttagcompound.getBoolean("isFirstHit");
		blockX = nbttagcompound.getInteger("blockX");
		blockY = nbttagcompound.getInteger("blockY");
		blockZ = nbttagcompound.getInteger("blockZ");
		blockCollided = Block.getBlockById(nbttagcompound.getInteger("blockCollided"));
		blockCollidedMetadata = nbttagcompound.getByte("blockCollidedMetadata");
	}
	
	// Keep memory allocated
	protected NBTTagCompound syncDataCompound_cachedWrite = new NBTTagCompound();
	
	@Override
	public NBTTagCompound writeSyncDataCompound() {
		syncDataCompound_cachedWrite.setByte("state", (byte)state);
		syncDataCompound_cachedWrite.setDouble("posX", posX);
		syncDataCompound_cachedWrite.setDouble("posY", posY);
		syncDataCompound_cachedWrite.setDouble("posZ", posZ);
		syncDataCompound_cachedWrite.setDouble("motionX", motionX);
		syncDataCompound_cachedWrite.setDouble("motionY", motionY);
		syncDataCompound_cachedWrite.setDouble("motionZ", motionZ);
		if (state == STATE_BLOCKHIT || brokenCount > 0) {
			syncDataCompound_cachedWrite.setInteger("brokenCount", brokenCount);
			syncDataCompound_cachedWrite.setInteger("blockX", blockX);
			syncDataCompound_cachedWrite.setInteger("blockY", blockY);
			syncDataCompound_cachedWrite.setInteger("blockZ", blockZ);
			syncDataCompound_cachedWrite.setInteger("blockCollided", Block.getIdFromBlock(blockCollided));
			syncDataCompound_cachedWrite.setByte("blockCollidedMetadata", (byte)blockCollidedMetadata);
		}
		if (state == STATE_ENTITYHIT) {
			syncDataCompound_cachedWrite.setInteger("entityHitID", entityHitID);
		}
		return syncDataCompound_cachedWrite;
	}
	
	@Override
	public void readSyncDataCompound(NBTTagCompound syncDataCompound) {
		int previousState = state;
		state = syncDataCompound.getByte("state");
		posX = syncDataCompound.getDouble("posX");
		posY = syncDataCompound.getDouble("posY");
		posZ = syncDataCompound.getDouble("posZ");
		motionX = syncDataCompound.getDouble("motionX");
		motionY = syncDataCompound.getDouble("motionY");
		motionZ = syncDataCompound.getDouble("motionZ");
		
		if (syncDataCompound.hasKey("brokenCount")) {
			int previousBrokenCount = brokenCount;
			brokenCount = syncDataCompound.getInteger("brokenCount");
			blockX = syncDataCompound.getInteger("blockX");
			blockY = syncDataCompound.getInteger("blockY");
			blockZ = syncDataCompound.getInteger("blockZ");
			blockCollided = Block.getBlockById(syncDataCompound.getInteger("blockCollided"));
			blockCollidedMetadata = syncDataCompound.getByte("blockCollidedMetadata");
			if (previousState != state || previousBrokenCount != brokenCount) {
				onClientBlockHit(previousBrokenCount != brokenCount);
			}
		}
		
		if (previousState != state && state == STATE_ENTITYHIT) {
			if (syncDataCompound.hasKey("entityHitID")) {
				entityHitID = syncDataCompound.getInteger("entityHitID");
			}
			onClientEntityHit();
		}
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
	
	private static final float PI = (float) Math.PI;
	private static final float PI_1_2 = (float) (Math.PI / 2.0D);
	
	// |error| < 0.000204 (0.012 deg)
	public float atan2_approximation3(float y, float x) {
		if (x == 0.0f) {
			if (y > 0.0F) {
				return PI_1_2;
			}
			if (y == 0.0F) {
				return 0.0F;
			}
			return -PI_1_2;
		}
		
		// From njuffa at http://math.stackexchange.com/questions/1098487/atan2-faster-approximation
		// a := min (|x|, |y|) / max (|x|, |y|)
		// s := a * a
		// r := ((-0.0464964749 * s + 0.15931422) * s - 0.327622764) * s * a + a
		// if |y| > |x| then r := 1.57079637 - r
		// if x < 0 then r := 3.14159274 - r
		// if y < 0 then r := -r
		float ax = Math.abs(x);
		float ay = Math.abs(y);
		float a = (ay > ax) ? ax / ay : ay / ax;
		float s = a * a;
		float r = ((-0.0464964749F * s + 0.15931422F) * s - 0.327622764F) * s * a + a;
		if (ay > ax) {
			r = PI_1_2 - r;
		}
		if (x < 0) {
			r = PI - r;
		}
		if (y < 0) {
			r = -r;
		}
		return r;
	}
}
