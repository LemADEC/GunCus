package stuuupiiid.guncus.network;

import java.nio.charset.Charset;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.block.ContainerAmmo;
import stuuupiiid.guncus.block.ContainerAmmoMan;
import stuuupiiid.guncus.block.ContainerBullet;
import stuuupiiid.guncus.block.ContainerGun;
import stuuupiiid.guncus.block.ContainerMag;
import stuuupiiid.guncus.block.ContainerWeapon;
import stuuupiiid.guncus.gui.GuiHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageGUIaction implements IMessage, IMessageHandler<MessageGUIaction, IMessage> {
	private int guiId;
	private int buttonId;
	private String gunName;
	
	public MessageGUIaction() {
		// required on receiving side
	}
	
	public MessageGUIaction(final int guiId, final int buttonId, final String gunName) {
		this.guiId = guiId;
		this.buttonId = buttonId;
		this.gunName = gunName;
	}
	
	@Override
	public void fromBytes(ByteBuf buffer) {
		guiId = buffer.readInt();
		buttonId = buffer.readInt();
		
		int nameLength = buffer.readByte();
		gunName = buffer.toString(buffer.readerIndex(), nameLength, Charset.forName("UTF8"));
		buffer.readerIndex(buffer.readerIndex() + nameLength);
	}
	
	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(guiId);
		buffer.writeInt(buttonId);
		
		byte[] bytesString = gunName.getBytes(Charset.forName("UTF8"));
		buffer.writeByte(bytesString.length);
		buffer.writeBytes(bytesString);
	}
	
	private void handle(EntityPlayerMP entityPlayer) {
		if (guiId == GuiHandler.gunBlock) {// 3 GuiGun (client -> server)
			ContainerGun container = (ContainerGun) entityPlayer.openContainer;
			
			if (buttonId == 0) {
				container.split();
			} else if (buttonId == 1) {
				container.build(entityPlayer);
			}
		} else if (guiId == GuiHandler.ammoBlock) {// 4 GuiAmmo (client -> server)
			ContainerAmmo container = (ContainerAmmo) entityPlayer.openContainer;
			
			if (buttonId == 0) {
				container.fill();
			} else if (buttonId == 1) {
				container.empty();
			}
		} else if (guiId == GuiHandler.magItem) {// 5 GuiAmmoMan (client -> server)
			ContainerAmmoMan container = (ContainerAmmoMan) entityPlayer.openContainer;
			
			if (buttonId == 0) {
				container.fill();
			} else if (buttonId == 1) {
				container.empty();
			}
		} else if (guiId == GuiHandler.magBlock) {// 6 GuiMag (client -> server)
			ContainerMag container = (ContainerMag) entityPlayer.openContainer;
			
			if (buttonId == 0) {
				container.create();
			} else if (buttonId == 1) {
				GunCus.addChatMessage(entityPlayer, container.info());
			}
		} else if (guiId == GuiHandler.bulletBlock) {// 7 GuiBullet (client -> server)
			ContainerBullet container = (ContainerBullet) entityPlayer.openContainer;
			
			if (buttonId == 0) {
				container.create();
			} else if (buttonId == 1) {
				GunCus.addChatMessage(entityPlayer, container.info());
			}
		} else if (guiId == GuiHandler.weaponBlock) {// 9 GuiWeapon
			ContainerWeapon container = (ContainerWeapon) entityPlayer.openContainer;
			
			if (buttonId == 0) {// previous
				int index = 0;
				String[] names = GunCus.gunNames.toArray(new String[0]);
				while (index < names.length && !names[index].equals(gunName)) {
					index++;
				}
				index = (index - 1 + names.length) % names.length;
				
				container.actualGunName = names[index];
				
				MessageWeaponBoxSelection weaponBoxSelectionMessage = new MessageWeaponBoxSelection(container.actualGunName);
				PacketHandler.simpleNetworkManager.sendTo(weaponBoxSelectionMessage, entityPlayer);
				
			} else if (buttonId == 1) {// next
				int index = 0;
				String[] names = GunCus.gunNames.toArray(new String[0]);
				while (index < names.length && !names[index].equals(gunName)) {
					index++;
				}
				index = (index + 1) % names.length;
				
				container.actualGunName = names[index];
				
				MessageWeaponBoxSelection weaponBoxSelectionMessage = new MessageWeaponBoxSelection(container.actualGunName);
				PacketHandler.simpleNetworkManager.sendTo(weaponBoxSelectionMessage, entityPlayer);
				
			} else if (buttonId == 2) {
				container.create();
			}
		} else {
			GunCus.logger.error(this + " Invalid guiId " + guiId);
		}
	}
	
	@Override
	public IMessage onMessage(MessageGUIaction guiActionMessage, MessageContext context) {
		if (GunCus.logging_enableNetwork) {
			GunCus.logger.info("Received guiAction packet: (GUIid " + guiActionMessage.guiId + " buttonId " + guiActionMessage.buttonId + " gunName " + guiActionMessage.gunName + ")");
		}
		
		guiActionMessage.handle(context.getServerHandler().playerEntity);
		
		return null;	// no response
	}
}
