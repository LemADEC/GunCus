package stuuupiiid.guncus.network;

import java.nio.charset.Charset;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.block.ContainerAmmoBox;
import stuuupiiid.guncus.block.ContainerMagazineFiller;
import stuuupiiid.guncus.block.ContainerBulletBox;
import stuuupiiid.guncus.block.ContainerGunBox;
import stuuupiiid.guncus.block.ContainerMagazineBox;
import stuuupiiid.guncus.block.ContainerWeaponBox;
import stuuupiiid.guncus.gui.GuiHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

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
		if (guiId == GuiHandler.gunBox) {// 3 GuiGun (client -> server)
			ContainerGunBox container = (ContainerGunBox) entityPlayer.openContainer;
			
			if (buttonId == 0) {
				container.split();
			} else if (buttonId == 1) {
				container.build(entityPlayer);
			}
		} else if (guiId == GuiHandler.ammoBox) {// 4 GuiAmmo (client -> server)
			ContainerAmmoBox container = (ContainerAmmoBox) entityPlayer.openContainer;
			
			if (buttonId == 0) {
				container.fill();
			} else if (buttonId == 1) {
				container.empty();
			}
		} else if (guiId == GuiHandler.magazineFillerItem) {// 5 GuiAmmoMan (client -> server)
			ContainerMagazineFiller container = (ContainerMagazineFiller) entityPlayer.openContainer;
			
			if (buttonId == 0) {
				container.fill();
			} else if (buttonId == 1) {
				container.empty();
			}
		} else if (guiId == GuiHandler.magazineBox) {// 6 GuiMag (client -> server)
			ContainerMagazineBox container = (ContainerMagazineBox) entityPlayer.openContainer;
			
			if (buttonId == 0) {
				container.create();
			} else if (buttonId == 1) {
				GunCus.addChatMessage(entityPlayer, container.info());
			}
		} else if (guiId == GuiHandler.bulletBox) {// 7 GuiBullet (client -> server)
			ContainerBulletBox container = (ContainerBulletBox) entityPlayer.openContainer;
			
			if (buttonId == 0) {
				container.create();
			} else if (buttonId == 1) {
				GunCus.addChatMessage(entityPlayer, container.info());
			}
		} else if (guiId == GuiHandler.weaponBox) {// 9 GuiWeapon
			ContainerWeaponBox container = (ContainerWeaponBox) entityPlayer.openContainer;
			
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
