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


public class MessageWeaponBoxSelection implements IMessage, IMessageHandler<MessageWeaponBoxSelection, IMessage> {
	int actualIndex = 0;
	
	public MessageWeaponBoxSelection() {
		// required on receiving side
	}
	
	public MessageWeaponBoxSelection(final int actualIndex) {
		this.actualIndex = actualIndex;
	}
	
	@Override
	public void fromBytes(ByteBuf buffer) {
		actualIndex = buffer.readInt();
	}
	
	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(actualIndex);
	}
	
	@SideOnly(Side.CLIENT)
	private void handle(EntityClientPlayerMP player) {
		GunCus.actualIndex = actualIndex;
		GunCus.actualItem = GunCus.instance.guns.get(actualIndex);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageWeaponBoxSelection bulletImpactMessage, MessageContext context) {
		// skip in case player just logged in
		if (Minecraft.getMinecraft().theWorld == null) {
			GunCus.logger.error("WorldObj is null, ignoring cloak packet");
			return null;
		}
		
		if (GunCus.logging_enableNetwork) {
			GunCus.logger.info("Received bulletImpact packet");
		}
		
		EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
		bulletImpactMessage.handle(player);
		
		return null;	// no response
	}
}
