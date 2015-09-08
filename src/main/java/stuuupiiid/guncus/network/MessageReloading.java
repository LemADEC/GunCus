package stuuupiiid.guncus.network;

import stuuupiiid.guncus.GunCus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.util.ResourceLocation;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class MessageReloading implements IMessage, IMessageHandler<MessageReloading, IMessage> {
	
	public MessageReloading() {
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
		GunCus.reloading = true;
		GunCus.shootTime += 90;
		Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147673_a(new ResourceLocation("guncus:reload")));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageReloading reloadingMessage, MessageContext context) {
		// skip in case player just logged in
		if (Minecraft.getMinecraft().theWorld == null) {
			GunCus.logger.error("WorldObj is null, ignoring cloak packet");
			return null;
		}
		
		if (GunCus.logging_enableNetwork) {
			GunCus.logger.info("Received reloading packet");
		}
		
		EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
		reloadingMessage.handle(player);
		
		return null;	// no response
	}
}
