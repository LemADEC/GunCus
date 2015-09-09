package stuuupiiid.guncus.entity;

import cpw.mods.fml.common.FMLCommonHandler;

import java.util.List;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.item.ItemBullet;
import stuuupiiid.guncus.network.PacketHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityBullet extends EntityArrow implements IProjectile {
	private int blockX = -1;
	private int blockY = -1;
	private int blockZ = -1;
	private boolean blockCollision = false;
	private EntityPlayer entityPlayer;
	private int ticksCount = 0;
	private float damage;
	private int liveTime = 300;
	public int ticks = 0;
	private boolean hasBarrel = false;
	private ItemBullet bullet = null;

	public EntityBullet(World world) {
		super(world);
		setSize(0.5F, 0.5F);
	}

	public EntityBullet(World parWorld, EntityPlayer parEntityPlayer, float speed, float parDamage, int accuracy) {
		super(parWorld);
		renderDistanceWeight = 10.0D;
		entityPlayer = parEntityPlayer;
		setSize(0.5F, 0.5F);
		setLocationAndAngles(parEntityPlayer.posX, parEntityPlayer.posY + parEntityPlayer.getEyeHeight(), parEntityPlayer.posZ, parEntityPlayer.rotationYaw, parEntityPlayer.rotationPitch);
		posX -= MathHelper.cos(rotationYaw / 180.0F * 3.141593F) * 0.16F;
		posY -= 0.1000000014901161D;
		posZ -= MathHelper.sin(rotationYaw / 180.0F * 3.141593F) * 0.16F;
		setPosition(parEntityPlayer.posX, parEntityPlayer.posY + parEntityPlayer.getEyeHeight(), parEntityPlayer.posZ);
		yOffset = 0.0F;
		motionX = (-MathHelper.sin(rotationYaw   / 180.0F * 3.141593F) * MathHelper.cos(rotationPitch / 180.0F * 3.141593F));
		motionZ = ( MathHelper.cos(rotationYaw   / 180.0F * 3.141593F) * MathHelper.cos(rotationPitch / 180.0F * 3.141593F));
		motionY = (-MathHelper.sin(rotationPitch / 180.0F * 3.141593F));
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
		damage = parDamage;
		setThrowableHeading(motionX, motionY, motionZ, speed, 1.0F);
	}
	
	public EntityBullet setLowerGravity(boolean flag) {
		hasBarrel = flag;
		return this;
	}
	
	public EntityBullet setBullet(ItemBullet parBullet) {
		bullet = parBullet;
		return this;
	}
	
	@Override
	public void onUpdate() {
		super.onEntityUpdate();

		ticks += 1;

		liveTime -= 1;

		if (liveTime <= 0) {
			setDead();
		}

		if ((prevRotationPitch == 0.0F) && (prevRotationYaw == 0.0F)) {
			float f = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
			prevRotationYaw = (rotationYaw = (float) (Math.atan2(motionX, motionZ) * 180.0D / Math.PI));
			prevRotationPitch = (rotationPitch = (float) (Math.atan2(motionY, f) * 180.0D / Math.PI));
		}

		Block block = worldObj.getBlock(blockX, blockY, blockZ);

		// 20 Glass, 30 Cobweb, 89 Glowstone, 102 Glass pane 
		if ((block != Blocks.air) && (block != Blocks.glass) && (block != Blocks.web) && (block != Blocks.glowstone) && (block != Blocks.glass_pane)) {
			block.setBlockBoundsBasedOnState(worldObj, blockX, blockY, blockZ);
			AxisAlignedBB axisalignedbb = block.getCollisionBoundingBoxFromPool(worldObj, blockX, blockY, blockZ);

			if ((axisalignedbb != null) && (axisalignedbb.isVecInside(Vec3.createVectorHelper(posX, posY, posZ)))) {
				this.blockCollision = true;
			}
		}

		if (this.blockCollision) {
			onInGround();
		} else {
			ticksCount += 1;
			Vec3 vec3 = Vec3.createVectorHelper(posX, posY, posZ);
			Vec3 vec31 = Vec3.createVectorHelper(posX + motionX, posY + motionY, posZ + motionZ);
			MovingObjectPosition movingobjectposition = worldObj.func_147447_a(vec3, vec31, false, true, false); // rayTraceBlocks_do_do
			vec3 = Vec3.createVectorHelper(posX, posY, posZ);
			vec31 = Vec3.createVectorHelper(posX + motionX, posY + motionY, posZ + motionZ);

			if (movingobjectposition != null) {
				vec31 = Vec3.createVectorHelper(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
			}

			Entity entity = null;
			List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.addCoord(motionX, motionY, motionZ).expand(1.0D, 1.0D, 1.0D));
			double d0 = 0.0D;

			for (int l = 0; l < list.size(); l++) {
				Entity entity1 = (Entity) list.get(l);

				if ((entity1.canBeCollidedWith()) && ((entity1 != entityPlayer) || (ticksCount >= 5))) {
					float f1 = 0.3F;
					AxisAlignedBB axisalignedbb1 = entity1.boundingBox.expand(f1, f1, f1);
					MovingObjectPosition movingobjectposition1 = axisalignedbb1.calculateIntercept(vec3, vec31);

					if (movingobjectposition1 != null) {
						double d1 = vec3.distanceTo(movingobjectposition1.hitVec);

						if ((d1 < d0) || (d0 == 0.0D)) {
							entity = entity1;
							d0 = d1;
						}
					}
				}
			}

			if (entity != null) {
				movingobjectposition = new MovingObjectPosition(entity);
			}

			if ((movingobjectposition != null) && (movingobjectposition.entityHit != null) && ((movingobjectposition.entityHit instanceof EntityPlayer))) {
				EntityPlayer entityplayer = (EntityPlayer) movingobjectposition.entityHit;

				if ((entityplayer.capabilities.disableDamage) || ((entityPlayer != null) && (!entityPlayer.canAttackPlayer(entityplayer)))) {
					movingobjectposition = null;
				}
			}

			if (movingobjectposition != null) {
				if ((movingobjectposition.entityHit != null) && (ticks >= 1)) {
					blockCollision = true;
					DamageSource damagesource = null;

					if (entityPlayer != null) {
						damagesource = DamageSource.causePlayerDamage(entityPlayer);
					} else {
						damagesource = DamageSource.causeArrowDamage(this, this);
					}

					if ((damage > 0.0F) && (movingobjectposition.entityHit.attackEntityFrom(damagesource, damage))) {
						if (((movingobjectposition.entityHit instanceof EntityLiving)) && (!movingobjectposition.entityHit.isDead) && (damage > 0.0F)) {
							EntityLiving entityliving = (EntityLiving) movingobjectposition.entityHit;
							entityliving.hurtResistantTime = 0;
						}
					}
					if (!movingobjectposition.entityHit.isDead) {
						PacketHandler.sendToClient_bulletImpact(entityPlayer);
					}

					hitEntity(movingobjectposition.entityHit);
				} else {
					blockX = movingobjectposition.blockX;
					blockY = movingobjectposition.blockY;
					blockZ = movingobjectposition.blockZ;
					block = worldObj.getBlock(blockX, blockY, blockZ);
					// 20 Glass, 30 Cobweb, 89 Glowstone, 102 Glass pane 
					if ((block != Blocks.glass) && (block != Blocks.web) && (block != Blocks.glowstone) && (block != Blocks.glass_pane)) {
						GunCus.removeBlockServer(entityPlayer, MathHelper.floor_double(blockX), MathHelper.floor_double(blockY), MathHelper.floor_double(blockZ));
					} else if (block != Blocks.air) {
						// int blockMetadata =
						// this.worldObj.getBlockMetadata(blockX, blockY,
						// blockZ);
						motionX = ((float) (movingobjectposition.hitVec.xCoord - posX));
						motionY = ((float) (movingobjectposition.hitVec.yCoord - posY));
						motionZ = ((float) (movingobjectposition.hitVec.zCoord - posZ));
						float f2 = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
						posX -= motionX / f2 * 0.05D;
						posY -= motionY / f2 * 0.05D;
						posZ -= motionZ / f2 * 0.05D;
						setIsCritical(false);
						blockCollision = true;
						block.onEntityCollidedWithBlock(worldObj, blockX, blockY, blockZ, this);
					}
				}
			}

			posX += motionX;
			posY += motionY;
			posZ += motionZ;
			float f2 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
			rotationYaw = ((float) (Math.atan2(motionX, motionZ) * 180.0D / Math.PI));

			for (rotationPitch = ((float) (Math.atan2(motionY, f2) * 180.0D / Math.PI)); rotationPitch - prevRotationPitch < -180.0F; prevRotationPitch -= 360.0F)
				;
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
			float f4 = 0.99F;
			// float f1 = 0.05F;

			if (isInWater()) {
				for (int j1 = 0; j1 < 4; j1++) {
					float f3 = 0.25F;
					worldObj.spawnParticle("bubble", posX - motionX * f3, posY - motionY * f3, posZ - motionZ * f3, motionX, motionY, motionZ);
				}

				f4 = 0.8F;
			}

			motionX *= f4;
			motionY *= f4;
			motionZ *= f4;
			motionY -= (hasBarrel ? 0.014D : 0.02D) * (bullet == null ? 1.0D : bullet.gravity);
			setPosition(posX, posY, posZ);
			func_145775_I();	// doBlockCollisions();

			if (blockCollision) {
				onInGround();
			}
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

	public void hitEntity(Entity parEntity) {
		if (FMLCommonHandler.instance().getEffectiveSide().isServer() && (parEntity instanceof EntityLivingBase)) {
			EntityLivingBase entity = (EntityLivingBase) parEntity;

			if (bullet.effectModifiers.containsKey(1)) {
				entity.addPotionEffect(new PotionEffect(Potion.poison.id, (int)Math.floor(bullet.effectModifiers.get(1) * 20F), 0));
			}

			if (bullet.effectModifiers.containsKey(2)) {
				entity.addPotionEffect(new PotionEffect(Potion.confusion.id, (int)Math.floor(bullet.effectModifiers.get(2) * 20F), 0));
			}

			if (bullet.effectModifiers.containsKey(3)) {
				entity.setFire((int)Math.floor(bullet.effectModifiers.get(3)));
			}

			if (bullet.effectModifiers.containsKey(7)) {
				entity.addPotionEffect(new PotionEffect(Potion.blindness.id, (int)Math.floor(bullet.effectModifiers.get(7) * 20F), 0));
			}

			if (bullet.effectModifiers.containsKey(6)) {
				entity.heal(bullet.effectModifiers.get(6).floatValue());
			}

			if (bullet.effectModifiers.containsKey(8)) {// instant damage / harm
				entity.addPotionEffect(new PotionEffect(Potion.harm.id, 1, (int)Math.floor(bullet.effectModifiers.get(8))));
			}

			if (bullet.effectModifiers.containsKey(9)) {// weaken / negative resistance (20% damage increased per level)
				entity.addPotionEffect(new PotionEffect(Potion.resistance.id, (int)Math.floor(bullet.effectModifiers.get(9) * 20F), - bullet.effectAmplifiers.get(9)));
			}
		}
	}

	public void onInGround() {
		if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			if (bullet.effectModifiers.containsKey(4)) {
				worldObj.createExplosion(entityPlayer, posX, posY, posZ, bullet.effectModifiers.get(4), GunCus.enableBlockDamage);
			}
			
			if (bullet.effectModifiers.containsKey(5)) {
				worldObj.createExplosion(entityPlayer, posX, posY, posZ, bullet.effectModifiers.get(5), false);
			}
			
			if ((bullet.effectModifiers.containsKey(3)) && (blockY > 0)) {
				if (worldObj.isAirBlock(blockX, blockY + 1, blockZ) && !worldObj.isAirBlock(blockX, blockY, blockZ)) {
					worldObj.setBlock(blockX, blockY + 1, blockZ, Blocks.fire);
				} else if (worldObj.isAirBlock(blockX, blockY, blockZ) && !worldObj.isAirBlock(blockX, blockY - 1, blockZ)) {
					worldObj.setBlock(blockX, blockY, blockZ, Blocks.fire);
				}
			}
			
			setDead();
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
		setDead();
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
		setDead();
	}
}
