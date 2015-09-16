package stuuupiiid.guncus.network;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.block.ContainerAmmo;
import stuuupiiid.guncus.block.ContainerAmmoMan;
import stuuupiiid.guncus.block.ContainerBullet;
import stuuupiiid.guncus.block.ContainerGun;
import stuuupiiid.guncus.block.ContainerMag;
import stuuupiiid.guncus.block.ContainerWeapon;
import stuuupiiid.guncus.gui.GuiHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageGUIaction implements IMessage, IMessageHandler<MessageGUIaction, IMessage> {
	private int guiId;
	private int buttonId;
	private int currentGun;
	
	public MessageGUIaction() {
		// required on receiving side
	}
	
	public MessageGUIaction(final int guiId, final int buttonId, final int currentGun) {
		this.guiId = guiId;
		this.buttonId = buttonId;
		this.currentGun = currentGun;
	}
	
	@Override
	public void fromBytes(ByteBuf buffer) {
		guiId = buffer.readInt();
		buttonId = buffer.readInt();
		currentGun = buffer.readInt();
	}
	
	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(guiId);
		buffer.writeInt(buttonId);
		buffer.writeInt(currentGun);
	}
	
	private void handle(EntityPlayerMP entityPlayer) {
		if (guiId == GuiHandler.gunBlock) {// 3 GuiGun (client -> server)
			ContainerGun container = (ContainerGun) entityPlayer.openContainer;
			
			if (buttonId == 0) {
				container.split();
			} else if (buttonId == 1) {
				container.build();
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
				entityPlayer.addChatComponentMessage(new ChatComponentText(container.info()[0]));
				if (container.info()[1] != null) {
					entityPlayer.addChatComponentMessage(new ChatComponentText(container.info()[1]));
				}
			}
		} else if (guiId == GuiHandler.bulletBlock) {// 7 GuiBullet (client -> server)
			ContainerBullet container = (ContainerBullet) entityPlayer.openContainer;
			
			if (buttonId == 0) {
				container.create();
			} else if (buttonId == 1) {
				entityPlayer.addChatComponentMessage(new ChatComponentText(container.info()[0]));
				if (container.info()[1] != null) {
					entityPlayer.addChatComponentMessage(new ChatComponentText(container.info()[1]));
				}
			}
		} else if (guiId == GuiHandler.weaponBlock) {// 9 GuiWeapon
			ContainerWeapon container = (ContainerWeapon) entityPlayer.openContainer;
			
			if (buttonId == 1) {
				int actual = currentGun;
				actual++;
				if (actual >= GunCus.instance.guns.size()) {
					actual = 0;
				}
				
				container.actualGunIndex = actual;
				container.actualGunItem = GunCus.instance.guns.get(actual);
				
				MessageWeaponBoxSelection weaponBoxSelectionMessage = new MessageWeaponBoxSelection(actual);
				PacketHandler.simpleNetworkManager.sendTo(weaponBoxSelectionMessage, entityPlayer);
			} else if (buttonId == 0) {
				entityPlayer.addChatComponentMessage(new ChatComponentText(container.info()[0]));
				if (container.info()[1] != null) {
					entityPlayer.addChatComponentMessage(new ChatComponentText(container.info()[1]));
				}
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
			GunCus.logger.info("Received guiAction packet: (GUIid " + guiActionMessage.guiId + " buttonId " + guiActionMessage.buttonId + " currentGun " + guiActionMessage.currentGun + ")");
		}
		
		guiActionMessage.handle(context.getServerHandler().playerEntity);
		
		return null;	// no response
	}
}
