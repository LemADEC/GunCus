package stuuupiiid.guncus.entity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.network.ISynchronisingEntity;
import stuuupiiid.guncus.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityGrenade extends EntityProjectile implements IProjectile, IEntityAdditionalSpawnData, ISynchronisingEntity {
	private static final Set<Blocks> weakBlocks = new HashSet(Arrays.asList(
			// Blocks.glass, Blocks.stained_glass,
			Blocks.glass_pane, Blocks.stained_glass_pane,
			Blocks.web, Blocks.glowstone));
	
	private boolean isRocket = false;
	
	{
		// slowMotionFactor = 1.0F;
		MAX_FLIGHT_DURATION_TICKS = Math.round(60 * slowMotionFactor);	// 3 s to reach a target
		MAX_BOUNCING_DURATION_TICKS = Math.round(600 * slowMotionFactor);	// 30 s bouncing around
		MAX_ENTITYHIT_DURATION_TICKS = Math.round(100 * slowMotionFactor);	// 5 s on an entity
		MAX_BLOCKHIT_DURATION_TICKS = Math.round(100 * slowMotionFactor);	// 5 s on the ground
		MAX_LIFE_DURATION_TICKS = Math.round(6000 * slowMotionFactor);	// 5 mn max total time
		SAFETY_FUSE_TICKS = 5;	// 250 ms flight
		
		WEAK_BLOCKS = weakBlocks;
	}
	
	public EntityGrenade(World world) {
		super(world);
		setSize(0.5F, 0.5F);
		canBePickedUp = 0;
	}
	
	public EntityGrenade(World parWorld, EntityPlayer entityPlayer, int accuracy, boolean isRocket) {
		super(parWorld, entityPlayer, (isRocket ? 3.0F : 4.2F), accuracy);
		setSize(0.5F, 0.5F);
		canBePickedUp = 0;
		
		this.isRocket = isRocket;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onClientUpdate() {
		super.onClientUpdate();
		
		// (always draw rocket fume trails)
		if (isRocket) {
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
		explode();
	}
	
	@Override
	protected void onTotalTickElapsed() {
		
	}
	
	@Override
	protected void onServerEntityCollision(Entity entityHit, Vec3 hitVec) {
		if (isSafetyOn()) {
			// (entity is too close) Bouncing
			motionX = motionX * -0.001D + 0.05F - rand.nextFloat() * 0.1F;;
			motionY = motionY * -0.001D + 0.50F - rand.nextFloat() * 0.8F;
			motionZ = motionZ * -0.001D + 0.05F - rand.nextFloat() * 0.1F;
			
			rotationYaw += 180.0F;
			prevRotationYaw += 180.0F;
			state = STATE_BOUNCING;
			stateTicks = 0;
			PacketHandler.sendToClient_syncEntity(this);
			
		} else {
			// (entity is far enough) Fix the grenade on collision point and blow it up
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
			state = STATE_BOUNCING;
			stateTicks = 0;
			PacketHandler.sendToClient_syncEntity(this);
			setPosition(posX, posY, posZ);
			explode();
			setDead();
		}
	}
	
	@Override
	protected void onServerBlockCollision(final boolean isWeakBlock, Vec3 hitVec) {
		if (isWeakBlock) {
			// just pass through
			return;
		}
		
		// (hard block)
		// Fix the grenade 5% in the block if flying, right on edge if bouncing
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
		
		// explode instantly if it's passed fuse duration
		if (!isSafetyOn()) {
			explode();
			setDead();
		}
	}
	
	@Override
	protected float getFriction() {
		if (isInWater()) {
			return 0.5F;
		} else {
			return 0.01F;
		}
	}
	
	@Override
	protected double getGravity() {
		if (state == STATE_FLYING || state == STATE_BOUNCING) { 
			return (isRocket ? 0.0122D : 0.15D);
		} else if (state == STATE_BLOCKHIT) {
			return (isRocket ? 0.007D : 0.15D);
		}
		return 0.0D;
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		
		nbttagcompound.setBoolean("isRocket", isRocket);
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		
		isRocket = nbttagcompound.getBoolean("isRocket");
	}
	
	public void explode() {
		if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			worldObj.createExplosion(shootingEntity, posX, posY, posZ, isRocket ? 7.0F : 3.5F, GunCus.enableBlockDamage);
		}
	}
}
