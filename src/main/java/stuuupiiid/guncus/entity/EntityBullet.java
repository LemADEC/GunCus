package stuuupiiid.guncus.entity;

import cpw.mods.fml.common.FMLCommonHandler;

import java.util.List;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.item.ItemBullet;
import stuuupiiid.guncus.network.PacketHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.world.BlockEvent;

public class EntityBullet extends EntityArrow implements IProjectile {
	private static float fDegToRadFactor = ((float)Math.PI) / 180.0F;
	private static double dRadToDegFactor = 180.0D / Math.PI;
	
	private final static float slowMotionFactor = 1F;
	private final static int MAX_FLIGHT_DURATION_TICKS = Math.round(60 * slowMotionFactor);	// 3 s to reach a target
	private final static int MAX_BOUNCING_DURATION_TICKS = Math.round(600 * slowMotionFactor);	// 30 s bouncing around
	private final static int MAX_ENTITYHIT_DURATION_TICKS = Math.round(100 * slowMotionFactor);	// 5 s on an entity
	private final static int MAX_BLOCKHIT_DURATION_TICKS = Math.round(400 * slowMotionFactor);	// 20 s on the ground
	private final static int MAX_LIFE_DURATION_TICKS = Math.round(6000 * slowMotionFactor);	// 5 mn max total time
	
	public final static int STATE_FLYING = 0;
	public final static int STATE_BOUNCING = 1;
	public final static int STATE_ENTITYHIT = 2;
	public final static int STATE_BLOCKHIT = 3;
	public int state = STATE_FLYING;
	private int stateTicks = 0;
	private boolean isFirstHit = true;
	private int blockX = -1;	// field_145791_d
	private int blockY = -1;	// field_145792_e
	private int blockZ = -1;	// field_145789_f
	private Block blockCollided = null;	// field field_145790_g
	private int blockCollidedMetadata = -1;	// field inData
	
	private float damage = 0.0F;
	private boolean lowerGravity = false;
	public ItemBullet bullet = null;
	
	public EntityBullet(World world) {
		super(world);
		setSize(0.1F, 0.1F);
		canBePickedUp = 0;
	}
	
	public EntityBullet(World parWorld, EntityPlayer parEntityPlayer, float speed, float parDamage, int accuracy) {
		super(parWorld);
		setSize(0.2F, 0.2F);
		canBePickedUp = 0;
		renderDistanceWeight = 10.0D;
		shootingEntity = parEntityPlayer;
		setLocationAndAngles(parEntityPlayer.posX, parEntityPlayer.posY + parEntityPlayer.getEyeHeight(), parEntityPlayer.posZ, parEntityPlayer.rotationYaw, parEntityPlayer.rotationPitch);
		posX -= MathHelper.cos(rotationYaw * fDegToRadFactor) * 0.16F;
		posY -= 0.1000000014901161D;
		posZ -= MathHelper.sin(rotationYaw * fDegToRadFactor) * 0.16F;
		setPosition(parEntityPlayer.posX, parEntityPlayer.posY + parEntityPlayer.getEyeHeight(), parEntityPlayer.posZ);
		yOffset = 0.0F;
		motionX = - MathHelper.sin(rotationYaw   * fDegToRadFactor) * MathHelper.cos(rotationPitch * fDegToRadFactor);
		motionZ =   MathHelper.cos(rotationYaw   * fDegToRadFactor) * MathHelper.cos(rotationPitch * fDegToRadFactor);
		motionY = - MathHelper.sin(rotationPitch * fDegToRadFactor);
		if (accuracy < 100) {
			int accX1 = rand.nextInt(101 - accuracy);
			int accY1 = rand.nextInt(101 - accuracy);
			int accZ1 = rand.nextInt(101 - accuracy);
			double accX2 = accX1 - rand.nextInt(accX1 + 1) * 2;
			double accY2 = accY1 - rand.nextInt(accY1 + 1) * 2;
			double accZ2 = accZ1 - rand.nextInt(accZ1 + 1) * 2;
			GunCus.logger.info("initial motion " + motionX + " " + motionY + " " + motionZ);
			motionX += accX2 / 370.0D;
			motionY += accY2 / 370.0D;
			motionZ += accZ2 / 370.0D;
			GunCus.logger.info("adjusted motion " + motionX + " " + motionY + " " + motionZ);
		}
		damage = parDamage;
		setThrowableHeading(motionX, motionY, motionZ, speed / slowMotionFactor, 1.0F);
	}
	
	public EntityBullet setLowerGravity(boolean flag) {
		lowerGravity = flag;
		return this;
	}
	
	public EntityBullet setBullet(ItemBullet parBullet) {
		bullet = parBullet;
		return this;
	}
	
	@Override
	public void onUpdate() {
		// skip the arrow computation
		super.onEntityUpdate();
		
		if (ticksExisted >= MAX_LIFE_DURATION_TICKS) {
			setDead();
		}
		
		// (EntityArrow) Re-orient bullet depending on motion vector - only used after recovering?)
		if ((prevRotationPitch == 0.0F) && (prevRotationYaw == 0.0F)) {
			float f = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
			prevRotationYaw = (rotationYaw = (float) (Math.atan2(motionX, motionZ) * dRadToDegFactor));
			prevRotationPitch = (rotationPitch = (float) (Math.atan2(motionY, f) * dRadToDegFactor));
		}
		
		// Do collision resolution on server side only
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			return;
		}
		
		if (state == STATE_BLOCKHIT) {
			// get collided block
			Block block = worldObj.getBlock(blockX, blockY, blockZ);
			int blockMetadata = worldObj.getBlockMetadata(blockX, blockY, blockZ);
			
			if (block == blockCollided && blockMetadata == blockCollidedMetadata) {
				stateTicks++;
				
				if (stateTicks >= MAX_BLOCKHIT_DURATION_TICKS) {
					GunCus.logger.info("BlockHit elapsed");
					setDead();
				}
			} else {
				motionX *= rand.nextFloat() * 0.2F;
				motionY *= rand.nextFloat() * 0.2F;
				motionZ *= rand.nextFloat() * 0.2F;
				state = STATE_BOUNCING;
				stateTicks = 0;
			}
			
		} else if (state == STATE_ENTITYHIT) {
			stateTicks++;
			
			if (stateTicks >= MAX_ENTITYHIT_DURATION_TICKS) {
				GunCus.logger.info("EntityHit elapsed");
				setDead();
			}
			
		} else {
			stateTicks++;
			if (state == STATE_FLYING) {
				if (stateTicks >= MAX_FLIGHT_DURATION_TICKS) {
					GunCus.logger.info("Flight elapsed");
					setDead();
				}
			} else if (state == STATE_BOUNCING) {// BOUNCING or default
				if (stateTicks >= MAX_BOUNCING_DURATION_TICKS) {
					GunCus.logger.info("Bouncing elapsed");
					setDead();
				}
			} else {
				GunCus.logger.error("Killing bullet with invalid state " + state + " " + this);
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
				if ((mopCollision.entityHit != null) && (ticksExisted >= 1)) {
					// (no critical hits)
					
					DamageSource damagesource = null;
					if (shootingEntity instanceof EntityPlayer) {
						damagesource = DamageSource.causePlayerDamage((EntityPlayer)shootingEntity);
					} else if (shootingEntity != null) {
						damagesource = DamageSource.causeArrowDamage(this, shootingEntity);
					} else {
						damagesource = DamageSource.causeArrowDamage(this, this);
					}
					
					if ((damage > 0.0F) && (mopCollision.entityHit.attackEntityFrom(damagesource, damage))) {
						
						onEntityHit(mopCollision.entityHit);
						
						if (mopCollision.entityHit instanceof EntityLivingBase) {
							if (!mopCollision.entityHit.isDead) {
								EntityLivingBase entityLivingBase = (EntityLivingBase) mopCollision.entityHit;
								entityLivingBase.hurtResistantTime = 0;
							}
							
							// (Vanilla arrow) Play random.successful_hit when player hits another player
							if ( mopCollision.entityHit != shootingEntity
							  && mopCollision.entityHit instanceof EntityPlayer
							  && shootingEntity instanceof EntityPlayerMP) {
								((EntityPlayerMP) this.shootingEntity).playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(6, 0.0F));
							}
							
							if ( mopCollision.entityHit != shootingEntity
							  && !mopCollision.entityHit.isDead
							  && shootingEntity instanceof EntityPlayer) {
								PacketHandler.sendToClient_showHitMarker(worldObj, mopCollision.hitVec, (EntityPlayer)shootingEntity);
							}
						}
						
						playSound("guncus:inground", 1.0F, 1.2F / (rand.nextFloat() * 0.2F + 0.9F));
						
						// no marks on Mob
						if (!(mopCollision.entityHit instanceof EntityPlayer)) {
							// setDead();
						}
						
						// Fix the bullet 5% in the entity
						motionX = ((float) (mopCollision.hitVec.xCoord - posX));
						motionY = ((float) (mopCollision.hitVec.yCoord - posY));
						motionZ = ((float) (mopCollision.hitVec.zCoord - posZ));
						float speed = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
						posX -= motionX / speed * 0.05D;
						posY -= motionY / speed * 0.05D;
						posZ -= motionZ / speed * 0.05D;
						state = STATE_ENTITYHIT;
						stateTicks = 0;
					} else {
						// (not a valid target) Bouncing
						motionX *= -0.1D;
						motionY *= -0.1D;
						motionZ *= -0.1D;
						rotationYaw += 180.0F;
						prevRotationYaw += 180.0F;
						state = STATE_BOUNCING;
						stateTicks = 0;
					}
					
				} else if (mopCollision.entityHit == null) {
					blockX = mopCollision.blockX;
					blockY = mopCollision.blockY;
					blockZ = mopCollision.blockZ;
					blockCollided = worldObj.getBlock(blockX, blockY, blockZ);
					blockCollidedMetadata = worldObj.getBlockMetadata(blockX, blockY, blockZ);
					// 20 Glass, 30 Cobweb, 89 Glowstone, 102 Glass pane 
					if ( GunCus.enableBlockDamage && (
					     (blockCollided == Blocks.glass) || (blockCollided == Blocks.stained_glass)
					  || (blockCollided == Blocks.glass_pane) || (blockCollided == Blocks.stained_glass_pane) 
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
							} else {
								worldObj.setBlockToAir(blockX, blockY, blockZ);
								onBlockHit(mopCollision.hitVec);
							}
						}
					} else if (!blockCollided.isAir(worldObj, blockX, blockY, blockZ)) {
						// (not in air) Fix the bullet 5% in the block
						motionX = ((float) (mopCollision.hitVec.xCoord - posX));
						motionY = ((float) (mopCollision.hitVec.yCoord - posY));
						motionZ = ((float) (mopCollision.hitVec.zCoord - posZ));
						float speed = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
						posX -= motionX / speed * 0.05D;
						posY -= motionY / speed * 0.05D;
						posZ -= motionZ / speed * 0.05D;
						state = STATE_BLOCKHIT;
						stateTicks = 0;
						blockCollided.onEntityCollidedWithBlock(worldObj, blockX, blockY, blockZ, this);
						onBlockHit(mopCollision.hitVec);
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
				motionY -= (lowerGravity ? 0.014D : 0.02D) * (bullet == null ? 1.0D : bullet.gravity) / slowMotionFactor;
			}
			setPosition(posX, posY, posZ);
			func_145775_I();	// doBlockCollisions();
		}
	}
	
	@Override
	protected void func_145775_I() {	// doBlockCollisions();
		int i = MathHelper.floor_double(boundingBox.minX + 0.001D);
		int j = MathHelper.floor_double(boundingBox.minY + 0.001D);
		int k = MathHelper.floor_double(boundingBox.minZ + 0.001D);
		int l = MathHelper.floor_double(boundingBox.maxX - 0.001D);
		int i1 = MathHelper.floor_double(boundingBox.maxY - 0.001D);
		int j1 = MathHelper.floor_double(boundingBox.maxZ - 0.001D);
		
		if (this.worldObj.checkChunksExist(i, j, k, l, i1, j1)) {
			for (int k1 = i; k1 <= l; k1++) {
				for (int l1 = j; l1 <= i1; l1++) {
					for (int i2 = k; i2 <= j1; i2++) {
						Block block = this.worldObj.getBlock(k1, l1, i2);
						
						// 20 Glass, 30 Cobweb, 89 Glowstone, 102 Glass pane 
						if ((block != Blocks.air) && (block != Blocks.glass) && (block != Blocks.web) && (block != Blocks.glowstone) && (block != Blocks.glass_pane)) {
							block.onEntityCollidedWithBlock(worldObj, k1, l1, i2, this);
						}
					}
				}
			}
		}
	}
	
	public void onEntityHit(Entity entity) {
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			return;
		}
		if (!isFirstHit) {
			return;
		}
		isFirstHit = false;
		if (!(entity instanceof EntityLivingBase)) {
			return;
		}
		
		EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
		
		if (bullet.effectModifiers.containsKey(1)) {
			entityLivingBase.addPotionEffect(new PotionEffect(Potion.poison.id, (int)Math.floor(bullet.effectModifiers.get(1) * 20F), 0));
		}
		
		if (bullet.effectModifiers.containsKey(2)) {
			entityLivingBase.addPotionEffect(new PotionEffect(Potion.confusion.id, (int)Math.floor(bullet.effectModifiers.get(2) * 20F), 0));
		}
		
		if (bullet.effectModifiers.containsKey(3)) {
			entityLivingBase.setFire((int)Math.floor(bullet.effectModifiers.get(3)));
		}
		
		if (bullet.effectModifiers.containsKey(7)) {
			entityLivingBase.addPotionEffect(new PotionEffect(Potion.blindness.id, (int)Math.floor(bullet.effectModifiers.get(7) * 20F), 0));
		}
		
		if (bullet.effectModifiers.containsKey(6)) {
			entityLivingBase.heal(bullet.effectModifiers.get(6).floatValue());
		}
		
		if (bullet.effectModifiers.containsKey(8)) {// instant damage / harm
			entityLivingBase.addPotionEffect(new PotionEffect(Potion.harm.id, 1, (int)Math.floor(bullet.effectModifiers.get(8))));
		}
		
		if (bullet.effectModifiers.containsKey(9)) {// weaken / negative resistance (20% damage increased per level)
			entityLivingBase.addPotionEffect(new PotionEffect(Potion.resistance.id, (int)Math.floor(bullet.effectModifiers.get(9) * 20F), - bullet.effectAmplifiers.get(9)));
		}
		
		if (bullet.effectModifiers.containsKey(10)) {// knockback
			double speed = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
			if (speed > 0.0F) {
				entityLivingBase.addVelocity(
						motionX * bullet.effectModifiers.get(9) / speed,
						Math.max(0.1D, motionY * bullet.effectAmplifiers.get(9) / speed),
						motionZ * bullet.effectModifiers.get(9) / speed);
			}
		}
	}
	
	public void onBlockHit(Vec3 vecHit) {
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			return;
		}
		if (!isFirstHit) {
			return;
		}
		isFirstHit = false;
		
		if (bullet.effectModifiers.containsKey(4)) {
			worldObj.createExplosion(shootingEntity, vecHit.xCoord, vecHit.yCoord, vecHit.zCoord, bullet.effectModifiers.get(4), GunCus.enableBlockDamage);
		}
		
		if (bullet.effectModifiers.containsKey(5)) {
			worldObj.createExplosion(shootingEntity, vecHit.xCoord, vecHit.yCoord, vecHit.zCoord, bullet.effectModifiers.get(5), false);
		}
		
		if ((bullet.effectModifiers.containsKey(3)) && (blockY > 0)) {
			if (worldObj.isAirBlock(blockX, blockY + 1, blockZ) && !worldObj.isAirBlock(blockX, blockY, blockZ)) {
				worldObj.setBlock(blockX, blockY + 1, blockZ, Blocks.fire);
			} else if (worldObj.isAirBlock(blockX, blockY, blockZ) && !worldObj.isAirBlock(blockX, blockY - 1, blockZ)) {
				worldObj.setBlock(blockX, blockY, blockZ, Blocks.fire);
			}
		}
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
		// FIXME: not implemented
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
		// FIXME: not implemented
		setDead();
	}
}
