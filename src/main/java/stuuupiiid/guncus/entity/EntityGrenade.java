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
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityGrenade extends EntityProjectile implements IProjectile, IEntityAdditionalSpawnData, ISynchronisingEntity {
	private static final Set<Blocks> weakBlocks = new HashSet(Arrays.asList(
			// Blocks.glass, Blocks.stained_glass,
			Blocks.glass_pane, Blocks.stained_glass_pane,
			Blocks.web, Blocks.glowstone));
	
	{
		// slowMotionFactor = 1.0D;
		MAX_FLIGHT_DURATION_TICKS    = (int) Math.round(  60 * slowMotionFactor);	// 3 s to reach a target
		MAX_BOUNCING_DURATION_TICKS  = (int) Math.round( 600 * slowMotionFactor);	// 30 s bouncing around
		MAX_ENTITYHIT_DURATION_TICKS = (int) Math.round(  20 * slowMotionFactor);	// 1 s on an entity
		MAX_BLOCKHIT_DURATION_TICKS  = (int) Math.round( 100 * slowMotionFactor);	// 5 s on the ground
		MAX_LIFE_DURATION_TICKS      = (int) Math.round(6000 * slowMotionFactor);	// 5 mn max total time
		SAFETY_FUSE_TICKS = 4;	// 200 ms flight
		
		WEAK_BLOCKS = weakBlocks;
	}
	
	public EntityGrenade(World world) {
		super(world);
		setSize(0.5F, 0.5F);
		canBePickedUp = 0;
	}
	
	public EntityGrenade(World parWorld, EntityPlayer entityPlayer, double speed, float accuracy) {
		super(parWorld, entityPlayer, speed, accuracy);
		
		setSize(0.5F, 0.5F);
		canBePickedUp = 0;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onClientUpdate() {
		super.onClientUpdate();
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
	protected double getFriction() {
		if (isInWater()) {
			return 0.5D;
		} else {
			return 0.01D;
		}
	}
	
	@Override
	protected double getGravity() {
		if (state == STATE_FLYING || state == STATE_BOUNCING) { 
			return 0.15D;
		} else if (state == STATE_BLOCKHIT) {
			return 0.15D;
		}
		return 0.0D;
	}
	
	public void explode() {
		if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			worldObj.createExplosion(shootingEntity, posX, posY, posZ, 3.5F, GunCus.enableBlockDamage);
		}
	}
}
