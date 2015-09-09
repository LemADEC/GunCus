package stuuupiiid.guncus.network;

import java.nio.charset.Charset;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.item.ItemGun;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class MessageClientValidation implements IMessage, IMessageHandler<MessageClientValidation, IMessage> {
	private int gunIndex = 0;
	private String gunName = "";
	private int shootType = 0;
	private int delay = 0;
	private String magName = "";
	private int bullets = 0;
	private double recoilModifier = 0;
	
	public MessageClientValidation() {
		// required on receiving side
	}
	
	public MessageClientValidation(final int gunIndex) {
		if (gunIndex >= 0 && gunIndex < GunCus.instance.guns.size()) {
			ItemGun gun = GunCus.instance.guns.get(gunIndex);
			this.gunIndex = gunIndex;
			gunName = gun.getUnlocalizedName();
			shootType = gun.shootType;
			delay = gun.delay;
			magName = gun.mag.getUnlocalizedName();
			bullets = gun.mag.bulletType;
			recoilModifier = gun.recoilModifier;
		}
	}
	
	@Override
	public void fromBytes(ByteBuf buffer) {
		gunIndex = buffer.readShort();
		int nameLength = buffer.readByte();
		gunName = buffer.toString(buffer.readerIndex(), nameLength, Charset.forName("UTF8"));
		shootType = buffer.readShort();
		delay = buffer.readShort();
		nameLength = buffer.readByte();
		magName = buffer.toString(buffer.readerIndex(), nameLength, Charset.forName("UTF8"));
		bullets = buffer.readShort();
		recoilModifier = buffer.readDouble();
	}
	
	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeShort(gunIndex);
		
		byte[] bytesString = gunName.getBytes(Charset.forName("UTF8"));
		buffer.writeByte(bytesString.length);
		buffer.writeBytes(bytesString);
		
		buffer.writeShort(shootType);
		buffer.writeShort(delay);
		
		bytesString = magName.getBytes(Charset.forName("UTF8"));
		buffer.writeByte(bytesString.length);
		buffer.writeBytes(bytesString);
		
		buffer.writeShort(bullets);
		buffer.writeDouble(recoilModifier);
	}
	
	@SideOnly(Side.CLIENT)
	private void handle(EntityClientPlayerMP player) {
		if (gunIndex >= 0 && gunIndex < GunCus.instance.guns.size()) {
			ItemGun gun = GunCus.instance.guns.get(gunIndex);
			
			byte[] bytesString = gun.getUnlocalizedName().getBytes(Charset.forName("UTF8"));
			String encoded_gunName = bytesString.toString();
			bytesString = gun.mag.getUnlocalizedName().getBytes(Charset.forName("UTF8"));
			String encoded_magName = bytesString.toString();
			if ( (!encoded_gunName.equals(gunName))
			  || (shootType != gun.shootType)
			  || (delay != gun.delay)
			  || (!encoded_magName.equals(magName))
			  || (bullets != gun.mag.bulletType)
			  || (recoilModifier != gun.recoilModifier)) {
				MessageKickPlayer kickPlayerMessage = new MessageKickPlayer(gunIndex, 1);
				PacketHandler.simpleNetworkManager.sendToServer(kickPlayerMessage);
			}
		} else {
			MessageKickPlayer kickPlayerMessage = new MessageKickPlayer(gunIndex, 0);
			PacketHandler.simpleNetworkManager.sendToServer(kickPlayerMessage);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageClientValidation bulletImpactMessage, MessageContext context) {
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
