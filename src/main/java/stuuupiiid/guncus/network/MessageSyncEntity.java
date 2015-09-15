package stuuupiiid.guncus.network;

import stuuupiiid.guncus.GunCus;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MessageSyncEntity implements IMessage, IMessageHandler<MessageSyncEntity, IMessage> {
	private int entityId;
	private NBTTagCompound entitySyncDataCompound;
	
	public MessageSyncEntity() {
		// required on receiving side
	}
	
	public MessageSyncEntity(final ISynchronisingEntity entity) {
		entityId = ((Entity)entity).getEntityId();
		entitySyncDataCompound = entity.writeSyncDataCompound();
	}
	
	@Override
	public void fromBytes(ByteBuf buffer) {
		entityId = buffer.readInt();
		entitySyncDataCompound = ByteBufUtils.readTag(buffer);
	}
	
	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(entityId);
		ByteBufUtils.writeTag(buffer, entitySyncDataCompound);
	}
	
	@SideOnly(Side.CLIENT)
	private void handle(EntityPlayer player) {
		ISynchronisingEntity entity = (ISynchronisingEntity)player.worldObj.getEntityByID(entityId);
		if (entity != null) {
			entity.readSyncDataCompound(entitySyncDataCompound);
		} else {
			GunCus.logger.warn("Skipping update for missing entity with id " + entityId); // FIXME: defer update?
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageSyncEntity syncEntityMessage, MessageContext context) {
		// skip in case player just logged in
		if (Minecraft.getMinecraft().theWorld == null) {
			GunCus.logger.error("WorldObj is null, ignoring syncEntity packet");
			return null;
		}
		
		if (GunCus.logging_enableNetwork) {
			GunCus.logger.info("Received syncEntity packet for entityId " + entityId);
		}
		
		EntityPlayer player = (context.side.isClient() ? Minecraft.getMinecraft().thePlayer : context.getServerHandler().playerEntity);
		syncEntityMessage.handle(player);
		
		return null;	// no response
	}
}
