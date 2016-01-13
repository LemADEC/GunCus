package stuuupiiid.guncus.network;

import stuuupiiid.guncus.GunCus;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
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
		
		double knifeRangeSquared = GunCus.knifeRange * GunCus.knifeRange;
		
		double x = entityPlayer.posX;
		double y = entityPlayer.posY;
		double z = entityPlayer.posZ;
		
		EntityLivingBase targetEntity = null;
		double closestEntity = GunCus.knifeRange * GunCus.knifeRange + 0.001;
		for (Object object : entityPlayer.worldObj.loadedEntityList) {
			if (object instanceof EntityLivingBase) {
				EntityLivingBase entity = (EntityLivingBase) object;
				double dx = entity.posX - x;
				double dy = entity.posY - y;
				double dz = entity.posZ - z;
				
				double distanceSquared = (dx * dx + dy * dy + dz * dz);
								
				if ((distanceSquared <= knifeRangeSquared) && (distanceSquared < closestEntity) && (entity != entityPlayer)) {
					targetEntity = entity;
					closestEntity = distanceSquared;
				}
			}
		}
		
		if (targetEntity != null) {
			targetEntity.attackEntityFrom(DamageSource.causePlayerDamage(entityPlayer), GunCus.knifeDamage);
			if (GunCus.logging_enableDamageData) {
				GunCus.logger.info(GunCus.knifeDamage + " damage done to " + targetEntity);
				GunCus.logger.info(targetEntity + " is at a range of " + Math.sqrt(closestEntity));
			}
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