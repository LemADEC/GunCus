package stuuupiiid.guncus.network;

import java.nio.charset.Charset;

import stuuupiiid.guncus.GunCus;
import net.minecraft.entity.player.EntityPlayerMP;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;


public class MessageKickPlayer implements IMessage, IMessageHandler<MessageKickPlayer, IMessage> {
	private String gunName = "";
	private int code = -1;
	
	public MessageKickPlayer() {
		// required on receiving side
	}
	
	public MessageKickPlayer(final String gunName, final int code) {
		this.gunName = gunName;
		this.code = code;
	}
	
	@Override
	public void fromBytes(ByteBuf buffer) {
		int nameLength = buffer.readByte();
		gunName = buffer.toString(buffer.readerIndex(), nameLength, Charset.forName("UTF8"));
		buffer.readerIndex(buffer.readerIndex() + nameLength);
		
		code = buffer.readInt();
	}
	
	@Override
	public void toBytes(ByteBuf buffer) {
		byte[] bytesString = gunName.getBytes(Charset.forName("UTF8"));
		buffer.writeByte(bytesString.length);
		buffer.writeBytes(bytesString);
		
		buffer.writeInt(code);
	}
	
	private void handle(EntityPlayerMP entityPlayerMp) {
		if (entityPlayerMp != null) {
			entityPlayerMp.playerNetServerHandler.kickPlayerFromServer("Checksum failed, you might need to reinstall...");
			GunCus.logger.info("Kicked player " + entityPlayerMp.getDisplayName() + " because he was using modified weapons! (gunName " + gunName + ", code " + code + ")");
		}
	}
	
	@Override
	public IMessage onMessage(MessageKickPlayer kickPlayerMessage, MessageContext context) {
		if (GunCus.logging_enableNetwork) {
			GunCus.logger.info("Received kickPlayer packet (gunName " + kickPlayerMessage.gunName + " code " + kickPlayerMessage.code);
		}
		
		EntityPlayerMP entityPlayerMp = context.getServerHandler().playerEntity;
		kickPlayerMessage.handle(entityPlayerMp);
		
		return null;	// no response
	}
}
