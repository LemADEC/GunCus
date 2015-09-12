package stuuupiiid.guncus.network;

import stuuupiiid.guncus.GunCus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


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
	private void handle(EntityClientPlayerMP player) {
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
		
		EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
		showHitMarkerMessage.handle(player);
		
		return null;	// no response
	}
}
