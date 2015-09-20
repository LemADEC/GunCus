package stuuupiiid.guncus.network;

import stuuupiiid.guncus.GunCus;
import net.minecraft.entity.player.EntityPlayerMP;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;


public class MessageKickPlayer implements IMessage, IMessageHandler<MessageKickPlayer, IMessage> {
	private int index = -1;
	private int code = -1;
	
	public MessageKickPlayer() {
		// required on receiving side
	}
	
	public MessageKickPlayer(final int index, final int code) {
		this.index = index;
		this.code = code;
	}
	
	@Override
	public void fromBytes(ByteBuf buffer) {
		index = buffer.readInt();
		code = buffer.readInt();
	}
	
	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(index);
		buffer.writeInt(code);
	}
	
	private void handle(EntityPlayerMP entityPlayerMp) {
		if (entityPlayerMp != null) {
			entityPlayerMp.playerNetServerHandler.kickPlayerFromServer("Checksum failed, you might need to reinstall...");
			GunCus.logger.info("Kicked player " + entityPlayerMp.getDisplayName() + " because he was using modified weapons! (index " + index + ", code " + code + ")");
		}
	}
	
	@Override
	public IMessage onMessage(MessageKickPlayer kickPlayerMessage, MessageContext context) {
		if (GunCus.logging_enableNetwork) {
			GunCus.logger.info("Received kickPlayer packet (index " + kickPlayerMessage.index + " code " + kickPlayerMessage.code);
		}
		
		EntityPlayerMP entityPlayerMp = context.getServerHandler().playerEntity;
		kickPlayerMessage.handle(entityPlayerMp);
		
		return null;	// no response
	}
}
