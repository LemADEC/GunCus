package stuuupiiid.guncus.network;

import java.nio.charset.Charset;

import stuuupiiid.guncus.GunCus;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;


public class MessageWeaponBoxSelection implements IMessage, IMessageHandler<MessageWeaponBoxSelection, IMessage> {
	String gunName = null;
	
	public MessageWeaponBoxSelection() {
		// required on receiving side
	}
	
	public MessageWeaponBoxSelection(final String gunName) {
		this.gunName = gunName;
	}
	
	@Override
	public void fromBytes(ByteBuf buffer) {
		int nameLength = buffer.readByte();
		gunName = buffer.toString(buffer.readerIndex(), nameLength, Charset.forName("UTF8"));
		buffer.readerIndex(buffer.readerIndex() + nameLength);
	}
	
	@Override
	public void toBytes(ByteBuf buffer) {
		byte[] bytesString = gunName.getBytes(Charset.forName("UTF8"));
		buffer.writeByte(bytesString.length);
		buffer.writeBytes(bytesString);
	}
	
	@SideOnly(Side.CLIENT)
	private void handle() {
		GunCus.clientGUI_actualGunName = gunName;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageWeaponBoxSelection bulletImpactMessage, MessageContext context) {
		// skip in case player just logged in
		if (Minecraft.getMinecraft().theWorld == null) {
			GunCus.logger.error("WorldObj is null, ignoring WeaponBoxSelection packet");
			return null;
		}
		
		if (GunCus.logging_enableNetwork) {
			GunCus.logger.info("Received bulletImpact packet");
		}
		
		bulletImpactMessage.handle();
		
		return null;	// no response
	}
}
