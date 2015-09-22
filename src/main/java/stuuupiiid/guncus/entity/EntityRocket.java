package stuuupiiid.guncus.entity;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.network.ISynchronisingEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityRocket extends EntityGrenade implements IProjectile, IEntityAdditionalSpawnData, ISynchronisingEntity {
	
	{
		SAFETY_FUSE_TICKS = 3;	// 150 ms flight
	}
	
	public EntityRocket(World world) {
		super(world);
		setSize(0.5F, 0.5F);
		canBePickedUp = 0;
	}
	
	public EntityRocket(World parWorld, EntityPlayer entityPlayer, float speed, int accuracy) {
		super(parWorld, entityPlayer, speed, accuracy);
		
		setSize(0.5F, 0.5F);
		canBePickedUp = 0;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onClientUpdate() {
		super.onClientUpdate();
		
		// (always draw rocket fume trails)
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
						-0.5 * motionX, -0.5 * motionY + 0.1, -0.5 * motionZ,
						1.5F);
				float color = 0.7F + rand.nextFloat() * 0.15F; 
				effect.setRBGColorF(color, color + 0.05F, color + 0.05F);
				mc.effectRenderer.addEffect(effect);
			}
		}
	}
	
	@Override
	protected double getGravity() {
		if (state == STATE_FLYING || state == STATE_BOUNCING) { 
			return 0.0122D;
		} else if (state == STATE_BLOCKHIT) {
			return 0.007D;
		}
		return 0.0D;
	}
	
	@Override
	public void explode() {
		if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			worldObj.createExplosion(shootingEntity, posX, posY, posZ, 7.0F, GunCus.enableBlockDamage);
		}
	}
}
