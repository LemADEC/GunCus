package stuuupiiid.guncus.gui;

import stuuupiiid.guncus.block.ContainerAmmo;
import stuuupiiid.guncus.block.ContainerAmmoMan;
import stuuupiiid.guncus.block.ContainerBullet;
import stuuupiiid.guncus.block.ContainerGun;
import stuuupiiid.guncus.block.ContainerMag;
import stuuupiiid.guncus.block.ContainerWeapon;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {
	static public final int gunBlock = 0;
	static public final int ammoBlock = 1;
	static public final int magItem = 2;
	static public final int magBlock = 3;
	static public final int bulletBlock = 4;
	static public final int weaponBlock = 5;
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID) {
		case gunBlock:
			return new GuiGunBlock(player.inventory, world, x, y, z);
		case ammoBlock:
			return new GuiAmmoBlock(player.inventory, world, x, y, z);
		case magItem:
			return new GuiMagItem(player.inventory, world, x, y, z);
		case magBlock:
			return new GuiMagBlock(player.inventory, world, x, y, z);
		case bulletBlock:
			return new GuiBulletBlock(player.inventory, world, x, y, z);
		case weaponBlock:
			return new GuiWeaponBlock(player.inventory, world, x, y, z);
		}

		return null;
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID) {
		case 0:
			return new ContainerGun(player.inventory, world, x, y, z);
		case 1:
			return new ContainerAmmo(player.inventory, world, x, y, z);
		case 2:
			return new ContainerAmmoMan(player.inventory, world, x, y, z);
		case 3:
			return new ContainerMag(player.inventory, world, x, y, z);
		case 4:
			return new ContainerBullet(player.inventory, world, x, y, z);
		case 5:
			return new ContainerWeapon(player.inventory, world, x, y, z);
		}

		return null;
	}
}
