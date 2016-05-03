package stuuupiiid.guncus.network;

import stuuupiiid.guncus.GunCus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;


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
	private void handle() {
		GunCus.reloading = true;
		GunCus.shootTime += 90;
		Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("guncus:reload")));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageReloading reloadingMessage, MessageContext context) {
		// skip in case player just logged in
		if (Minecraft.getMinecraft().theWorld == null) {
			GunCus.logger.error("WorldObj is null, ignoring reloading packet");
			return null;
		}
		
		if (GunCus.logging_enableNetwork) {
			GunCus.logger.info("Received reloading packet");
		}
		
		reloadingMessage.handle();
		
		return null;	// no response
	}
}
