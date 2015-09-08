package stuuupiiid.guncus.network;

import stuuupiiid.guncus.GunCus;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageKnife implements IMessage, IMessageHandler<MessageKnife, IMessage> {
	
	public MessageKnife() {
		// required on receiving side
	}
	
	@Override
	public void fromBytes(ByteBuf buffer) {
	}
	
	@Override
	public void toBytes(ByteBuf buffer) {
	}
	
	private void handle(EntityPlayerMP entityPlayer) {
		entityPlayer.worldObj.playSoundAtEntity(entityPlayer, "guncus:knife", 1.0F, 1.0F);
		
		double x = entityPlayer.posX;
		double y = entityPlayer.posY;
		double z = entityPlayer.posZ;
		
		EntityLiving targetEntity = null;
		double targetDistance = 300.0D;
		for (Object object : entityPlayer.worldObj.loadedEntityList) {
			if (object instanceof EntityLivingBase) {
				EntityLivingBase entity = (EntityLivingBase) object;
				double dx = ((EntityLiving) entity).posX - x;
				double dy = ((EntityLiving) entity).posY - y;
				double dz = ((EntityLiving) entity).posZ - z;
				
				double distance = MathHelper.sqrt_double(dx * dx + dy * dy + dz * dz);
				
				if ((distance <= 2.0001D) && (distance < targetDistance) && ((!(entity instanceof EntityPlayer)) || ((EntityPlayer) entity != entityPlayer))) {
					targetEntity = (EntityLiving) entity;
					targetDistance = distance;
				}
			}
		}
		
		if ((targetEntity != null) && (targetDistance <= 2.0001D)) {
			targetEntity.attackEntityFrom(DamageSource.causePlayerDamage(entityPlayer), 20.0F);
		}
	}
	
	@Override
	public IMessage onMessage(MessageKnife knifeMessage, MessageContext context) {
		if (GunCus.logging_enableNetwork) {
			GunCus.logger.info("Received knife packet");
		}
		
		knifeMessage.handle(context.getServerHandler().playerEntity);
		
		return null;	// no response
	}
}