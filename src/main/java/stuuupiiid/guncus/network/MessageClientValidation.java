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
	private String gunName = "";
	private int shootType = 0;
	private int delay = 0;
	private String bullets = "";
	private double recoilModifier = 0;
	
	public MessageClientValidation() {
		// required on receiving side
	}
	
	public MessageClientValidation(final ItemGun itemGun) {
		this.gunName = itemGun.getUnlocalizedName();
		shootType = itemGun.shootType;
		delay = itemGun.delay;
		if (itemGun.mag != null) {
			bullets = "" + itemGun.mag.bulletId;
		} else {
			bullets = "" + itemGun.bullets;
		}
		recoilModifier = itemGun.recoilModifier;
	}
	
	@Override
	public void fromBytes(ByteBuf buffer) {
		int nameLength = buffer.readByte();
		gunName = buffer.toString(buffer.readerIndex(), nameLength, Charset.forName("UTF8"));
		buffer.readerIndex(buffer.readerIndex() + nameLength);
		
		shootType = buffer.readShort();
		delay = buffer.readShort();
		
		int bulletsLength = buffer.readByte();
		bullets = buffer.toString(buffer.readerIndex(), bulletsLength, Charset.forName("UTF8"));
		buffer.readerIndex(buffer.readerIndex() + bulletsLength);
		
		recoilModifier = buffer.readDouble();
	}
	
	@Override
	public void toBytes(ByteBuf buffer) {
		byte[] bytesString = gunName.getBytes(Charset.forName("UTF8"));
		buffer.writeByte(bytesString.length);
		buffer.writeBytes(bytesString);
		
		buffer.writeShort(shootType);
		buffer.writeShort(delay);
		
		bytesString = bullets .getBytes(Charset.forName("UTF8"));
		buffer.writeByte(bytesString.length);
		buffer.writeBytes(bytesString);
		
		buffer.writeDouble(recoilModifier);
		if (GunCus.logging_enableNetwork) {
			GunCus.logger.info("Sending clientValidation packet"
					+ " '" + gunName + "' "
					+ shootType + " " + delay + " "
					+ "'" + bullets  + "' " + recoilModifier);
		}
	}
	
	@SideOnly(Side.CLIENT)
	private void handle(EntityClientPlayerMP player) {
		ItemGun gun = GunCus.instance.guns.get(gunName);
		
		if (gun != null) {
			String encoded_bullets;
			if (gun.mag != null) {
				encoded_bullets = "" + gun.mag.bulletId;
			} else {
				encoded_bullets = "" + gun.bullets;
			}
			
			if ( (!gunName.equals(gun.getUnlocalizedName()))
			  || (shootType != gun.shootType)
			  || (delay != gun.delay)
			  || (!bullets.equals(encoded_bullets))
			  || (recoilModifier != gun.recoilModifier)) {
				GunCus.logger.info("Validation failed for '" + gun.getUnlocalizedName() + "'!");
				MessageKickPlayer kickPlayerMessage = new MessageKickPlayer(gunName, 1);
				PacketHandler.simpleNetworkManager.sendToServer(kickPlayerMessage);
			}
		} else {
			MessageKickPlayer kickPlayerMessage = new MessageKickPlayer(gunName, 0);
			PacketHandler.simpleNetworkManager.sendToServer(kickPlayerMessage);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageClientValidation clientValidationMessage, MessageContext context) {
		// skip in case player just logged in
		if (Minecraft.getMinecraft().theWorld == null) {
			GunCus.logger.error("WorldObj is null, ignoring clientValidation packet");
			return null;
		}
		
		if (GunCus.logging_enableNetwork && false) {
			GunCus.logger.info("Received clientValidation packet"
					+ " '" + clientValidationMessage.gunName + "' "
					+ clientValidationMessage.shootType + " " + clientValidationMessage.delay + " "
					+ "'" + clientValidationMessage.bullets + "' " + clientValidationMessage.recoilModifier);
		}
		
		EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
		clientValidationMessage.handle(player);
		
		return null;	// no response
	}
}
