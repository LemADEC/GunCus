package stuuupiiid.guncus.entity;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.item.ItemBullet;
import stuuupiiid.guncus.network.ISynchronisingEntity;
import stuuupiiid.guncus.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityBullet extends EntityProjectile implements IProjectile, IEntityAdditionalSpawnData, ISynchronisingEntity {
	private static final Set<Blocks> weakBlocks = new HashSet(Arrays.asList(
			Blocks.glass, Blocks.stained_glass,
			Blocks.glass_pane, Blocks.stained_glass_pane,
			Blocks.web, Blocks.glowstone));
	
	private float damage = 0.0F;
	private boolean lowerGravity = false;
	private String pack = null;
	private int bulletId = -1;
	private boolean isBurning = false;
	
	{
		// slowMotionFactor = 1.0F;
		MAX_FLIGHT_DURATION_TICKS    = (int) Math.round(60 * slowMotionFactor);	// 3 s to reach a target
		MAX_BOUNCING_DURATION_TICKS  = (int) Math.round(600 * slowMotionFactor);	// 30 s bouncing around
		MAX_ENTITYHIT_DURATION_TICKS = (int) Math.round(100 * slowMotionFactor);	// 5 s on an entity
		MAX_BLOCKHIT_DURATION_TICKS  = (int) Math.round(400 * slowMotionFactor);	// 20 s on the ground
		MAX_LIFE_DURATION_TICKS      = (int) Math.round(6000 * slowMotionFactor);	// 5 mn max total time
		SAFETY_FUSE_TICKS = 2;	// 2 ticks since bullet starts inside shooting player...
		
		WEAK_BLOCKS = weakBlocks;
	}
	
	public EntityBullet(World world) {
		super(world);
		setSize(0.2F, 0.2F);
		canBePickedUp = 0;
	}
	
	public EntityBullet(World parWorld, EntityPlayer entityPlayer, double speed, float parDamage, int accuracy, boolean parLowerGravity, ItemBullet bullet) {
		super(parWorld, entityPlayer, speed, accuracy);
		setSize(0.2F, 0.2F);
		canBePickedUp = 0;
		
		damage = parDamage;
		lowerGravity = parLowerGravity;
		pack = bullet.pack;
		bulletId = bullet.bulletId;
		isBurning = getBullet().effectModifiers.containsKey(3);
	}
	
	public ItemBullet getBullet() {
		if (pack != null) {
			return ItemBullet.bullets.get(pack).get(bulletId);
		} else {
			return null;
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onClientUpdate() {
		super.onClientUpdate();
		
		// (always draw flaming bullet fumes)
		if (isBurning) {
			if (isInWater() || state == STATE_BLOCKHIT) {
				setDead();
			}
			
			if (ticksExisted > 3) {
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
					
					int count = 2 * (4 - mc.gameSettings.particleSetting);
					for (int smokeIndex = 0; smokeIndex < count; smokeIndex++) {
						double factor = 0.20 * smokeIndex;
						// Directly spawn largesmoke as per RenderGlobal.doSpawnParticle
						// adjust color to be more rocket style (white/yellowish)
						EntitySmokeFX effect = new EntitySmokeFX(
								worldObj,
								tailX - motionX * factor,
								tailY - motionY * factor,
								tailZ - motionZ * factor,
								0.15 * motionX + rand.nextFloat() * 0.2F - 0.1F,
								0.15 * motionY + rand.nextFloat() * 0.1F,
								0.15 * motionZ + rand.nextFloat() * 0.2F - 0.1F,
								Math.min(Math.max(1.0F, 0.05F * ticksExisted), 4.0F));
						effect.setRBGColorF(
								0.95F + rand.nextFloat() * 0.10F,
								0.65F + rand.nextFloat() * 0.35F,
								0.05F + rand.nextFloat() * 0.15F);
						mc.effectRenderer.addEffect(effect);
					}
				}
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onClientBlockHit(boolean isBroken) {
		super.onClientBlockHit(isBroken);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onClientEntityHit() {
		super.onClientEntityHit();
		
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
			double particleMotionX = -0.05 * MathHelper.sin(rotationYaw   * fDegToRadFactor) * MathHelper.cos(rotationPitch * fDegToRadFactor);
			double particleMotionZ = -0.05 * MathHelper.sin(rotationPitch * fDegToRadFactor);
			double particleMotionY = -0.05 * MathHelper.cos(rotationYaw   * fDegToRadFactor) * MathHelper.cos(rotationPitch * fDegToRadFactor);
			
			// particle effect at collision point
			float particleSpeed = 0.0F;
			int particleQuantity = 5;
			for (int index = 0; index < particleQuantity; index++) {
				mc.effectRenderer.addEffect(new EntityDiggingFX(
						worldObj,
						hitX + rand.nextGaussian() * particleMotionX,
						hitY + rand.nextGaussian() * particleMotionY,
						hitZ + rand.nextGaussian() * particleMotionZ,
						rand.nextGaussian() * particleSpeed,
						rand.nextGaussian() * particleSpeed,
						rand.nextGaussian() * particleSpeed,
						Blocks.redstone_block, 0).applyRenderColor(blockCollidedMetadata));
			}
		}
	}
	
	@Override
	protected void onBlockCollidedIsNowAir() {
		motionX = 0.1F - rand.nextFloat() * 0.2F;
		motionY = 0.8F - rand.nextFloat() * 1.2F;
		motionZ = 0.1F - rand.nextFloat() * 0.2F;
		state = STATE_BOUNCING;
		stateTicks = 0;
		PacketHandler.sendToClient_syncEntity(this);
	}
	
	@Override
	protected void onSubTickElapsed() {
		// nothing
	}
	
	@Override
	protected void onTotalTickElapsed() {
		// nothing
	}
	
	@Override
	protected void onServerEntityCollision(Entity entityHit, Vec3 hitVec) {
		if (isSafetyOn()) {
			return;
		}
		
		// (no critical hits)
		
		boolean isAttaching = false;
		if (entityHit instanceof EntityLivingBase) {
			DamageSource damagesource = null;
			if (shootingEntity instanceof EntityPlayer) {
				damagesource = DamageSource.causePlayerDamage((EntityPlayer)shootingEntity);
			} else if (shootingEntity != null) {
				damagesource = DamageSource.causeArrowDamage(this, shootingEntity);
			} else {
				damagesource = DamageSource.causeArrowDamage(this, this);
			}
			
			if ((damage > 0.0F) && entityHit.attackEntityFrom(damagesource, damage)) {
				
				applyEffectOnEntityCollision(entityHit, hitVec);
				
				if (entityHit instanceof EntityLivingBase) {
					if (!entityHit.isDead) {
						EntityLivingBase entityLivingBase = (EntityLivingBase) entityHit;
						entityLivingBase.hurtResistantTime = 0;
					}
					
					// (Vanilla arrow) Play random.successful_hit when player hits another player
					if ( entityHit != shootingEntity
					  && entityHit instanceof EntityPlayer
					  && shootingEntity instanceof EntityPlayerMP) {
						((EntityPlayerMP) shootingEntity).playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(6, 0.0F));
					}
					
					if ( entityHit != shootingEntity
					  && !entityHit.isDead
					  && shootingEntity instanceof EntityPlayer) {
						PacketHandler.sendToClient_showHitMarker(worldObj, hitVec, (EntityPlayer)shootingEntity);
					}
				}
				
				isAttaching = true;
				
			} else if (((EntityLivingBase) entityHit).getHealth() <= 0.0F) {
				isAttaching = true;
			}
		}
		
		if (isAttaching) {
			// Fix the bullet 25% in the entity
			motionX = ((float) (hitVec.xCoord - posX));
			motionY = ((float) (hitVec.yCoord - posY));
			motionZ = ((float) (hitVec.zCoord - posZ));
			float speed = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
			posX -= motionX / speed * 0.25D;
			posY -= motionY / speed * 0.25D;
			posZ -= motionZ / speed * 0.25D;
			state = STATE_ENTITYHIT;
			stateTicks = 0;
			PacketHandler.sendToClient_syncEntity(this);
			
		} else {
			// (not a valid entity target) Bouncing
			motionX *= -0.1D;
			motionY *= -0.1D;
			motionZ *= -0.1D;
			rotationYaw += 180.0F;
			prevRotationYaw += 180.0F;
			posX = hitVec.xCoord - motionX;
			posY = hitVec.yCoord - motionY;
			posZ = hitVec.zCoord - motionZ;
			state = STATE_BOUNCING;
			stateTicks = 0;
			PacketHandler.sendToClient_syncEntity(this);
		}
	}
	
	@Override
	protected void onServerBlockCollision(final boolean isWeakBlock, Vec3 hitVec) {
		if (isWeakBlock) {
			
		} else {
			// (hard block)
			// Fix the bullet 5% in the block if flying, right on edge if bouncing
			double depth = 0.25D * width;
			if (state == STATE_BOUNCING) {
				depth = -0.70D * width;
				prevRotationPitch = 0.0F;
			}
			motionX = 0.0F;
			motionY = 0.0F;
			motionZ = 0.0F;
			posX = hitVec.xCoord + depth * MathHelper.sin(rotationYaw   * fDegToRadFactor) * MathHelper.cos(rotationPitch * fDegToRadFactor);
			posZ = hitVec.zCoord + depth * MathHelper.cos(rotationYaw   * fDegToRadFactor) * MathHelper.cos(rotationPitch * fDegToRadFactor);
			posY = hitVec.yCoord + depth * MathHelper.sin(rotationPitch * fDegToRadFactor);
			state = STATE_BLOCKHIT;
			stateTicks = 0;
			PacketHandler.sendToClient_syncEntity(this);
			blockCollided.onEntityCollidedWithBlock(worldObj, blockX, blockY, blockZ, this);
		}
		
		applyEffectOnBlockCollision(isWeakBlock, hitVec);
	}
	
	@Override
	protected double getFriction() {
		if (isInWater()) {
			return getBullet().frictionInLiquid;
		} else {
			return getBullet().frictionInAir;
		}
	}
	
	@Override
	protected double getGravity() {
		if (state == STATE_FLYING || state == STATE_BOUNCING) { 
			ItemBullet itemBullet = getBullet();
			return (lowerGravity ? 0.014D : 0.02D) * (itemBullet == null ? 1.0D : itemBullet.gravityModifier);
		}
		return 0.0D;
	}
	
	@Override
	public boolean isBurning() {
		return isBurning;
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		
		nbttagcompound.setFloat("damage", damage);
		nbttagcompound.setBoolean("lowerGravity", lowerGravity);
		nbttagcompound.setString("pack", pack);
		nbttagcompound.setInteger("bulletId", bulletId);
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		
		damage = nbttagcompound.getFloat("damage");
		lowerGravity = nbttagcompound.getBoolean("lowerGravity");
		pack = nbttagcompound.getString("pack");
		bulletId = nbttagcompound.getInteger("bulletId");
		isBurning = getBullet().effectModifiers.containsKey(3);
	}
	
	public void applyEffectOnEntityCollision(Entity entity, Vec3 vecHit) {
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			return;
		}
		
		// play hit sound
		playSound("guncus:inground", 1.0F, 1.5F / (rand.nextFloat() * 0.4F + 0.8F));
		
		// bullet effect upon first collision
		if (!isFirstHit) {
			return;
		}
		isFirstHit = false;
		if (!(entity instanceof EntityLivingBase)) {
			return;
		}
		
		EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
		
		ItemBullet itemBullet = getBullet();
		if (itemBullet.effectModifiers.containsKey(1)) {
			entityLivingBase.addPotionEffect(new PotionEffect(Potion.poison.id, (int)Math.floor(itemBullet.effectModifiers.get(1) * 20F), 0));
		}
		
		if (itemBullet.effectModifiers.containsKey(2)) {
			entityLivingBase.addPotionEffect(new PotionEffect(Potion.confusion.id, (int)Math.floor(itemBullet.effectModifiers.get(2) * 20F), 0));
		}
		
		if (itemBullet.effectModifiers.containsKey(3)) {
			entityLivingBase.setFire((int)Math.floor(itemBullet.effectModifiers.get(3)));
		}
		
		if (itemBullet.effectModifiers.containsKey(7)) {
			entityLivingBase.addPotionEffect(new PotionEffect(Potion.blindness.id, (int)Math.floor(itemBullet.effectModifiers.get(7) * 20F), 0));
		}
		
		if (itemBullet.effectModifiers.containsKey(6)) {
			entityLivingBase.heal(itemBullet.effectModifiers.get(6).floatValue());
		}
		
		if (itemBullet.effectModifiers.containsKey(8)) {// instant damage / harm
			entityLivingBase.addPotionEffect(new PotionEffect(Potion.harm.id, 1, (int)Math.floor(itemBullet.effectModifiers.get(8))));
		}
		
		if (itemBullet.effectModifiers.containsKey(9)) {// weaken / negative resistance (20% damage increased per level)
			entityLivingBase.addPotionEffect(new PotionEffect(Potion.resistance.id, (int)Math.floor(itemBullet.effectModifiers.get(9) * 20F), - itemBullet.effectAmplifiers.get(9)));
		}
		
		if (itemBullet.effectModifiers.containsKey(10)) {// knockback
			double speed = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
			if (speed > 0.0F) {
				entityLivingBase.addVelocity(
						motionX * itemBullet.effectModifiers.get(9) / speed,
						Math.max(0.1D, motionY * itemBullet.effectAmplifiers.get(9) / speed),
						motionZ * itemBullet.effectModifiers.get(9) / speed);
			}
		}
	}
	
	public void applyEffectOnBlockCollision(final boolean isWeakBlock, Vec3 vecHit) {
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			return;
		}
		
		// play hit sound
		playSound("guncus:inground", 1.0F, 1.5F / (rand.nextFloat() * 0.4F + 0.8F));
		
		// bullet effect upon first collision
		if (!isFirstHit) {
			return;
		}
		isFirstHit = false;
		
		ItemBullet itemBullet = getBullet();
		if (itemBullet.effectModifiers.containsKey(4)) {
			worldObj.createExplosion(shootingEntity, vecHit.xCoord, vecHit.yCoord, vecHit.zCoord, itemBullet.effectModifiers.get(4), GunCus.enableBlockDamage);
		}
		
		if (itemBullet.effectModifiers.containsKey(5)) {
			worldObj.createExplosion(shootingEntity, vecHit.xCoord, vecHit.yCoord, vecHit.zCoord, itemBullet.effectModifiers.get(5), false);
		}
		
		if (itemBullet.effectModifiers.containsKey(3) && (blockY > 0)) {
			if (worldObj.isAirBlock(blockX, blockY + 1, blockZ) && !worldObj.isAirBlock(blockX, blockY, blockZ)) {
				worldObj.setBlock(blockX, blockY + 1, blockZ, Blocks.fire);
			} else if (worldObj.isAirBlock(blockX, blockY, blockZ) && !worldObj.isAirBlock(blockX, blockY - 1, blockZ)) {
				worldObj.setBlock(blockX, blockY, blockZ, Blocks.fire);
			}
		}
	}
}
