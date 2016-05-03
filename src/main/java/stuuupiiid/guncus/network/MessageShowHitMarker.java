package stuuupiiid.guncus.network;

import stuuupiiid.guncus.GunCus;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;


public class MessageShowHitMarker implements IMessage, IMessageHandler<MessageShowHitMarker, IMessage> {
	
	public MessageShowHitMarker() {
		// required on receiving side
	}
	
	@Override
	public void fromBytes(ByteBuf buffer) {
	}

	@Override
	public void toBytes(ByteBuf buffer) {
	}
	
	@SideOnly(Side.CLIENT)
	private void handle() {
		GunCus.hitmarker = 5;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageShowHitMarker showHitMarkerMessage, MessageContext context) {
		// skip in case player just logged in
		if (Minecraft.getMinecraft().theWorld == null) {
			GunCus.logger.error("WorldObj is null, ignoring showHitMarker packet");
			return null;
		}
		
		if (GunCus.logging_enableNetwork) {
			GunCus.logger.info("Received showHitMarker packet");
		}
		
		showHitMarkerMessage.handle();
		
		return null;	// no response
	}
}
