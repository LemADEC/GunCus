package stuuupiiid.guncus.gui;

import stuuupiiid.guncus.block.ContainerAmmoBox;
import stuuupiiid.guncus.block.ContainerMagazineFiller;
import stuuupiiid.guncus.block.ContainerBulletBox;
import stuuupiiid.guncus.block.ContainerGunBox;
import stuuupiiid.guncus.block.ContainerMagazineBox;
import stuuupiiid.guncus.block.ContainerWeaponBox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	static public final int gunBox = 0;
	static public final int ammoBox = 1;
	static public final int magazineFillerItem = 2;
	static public final int magazineBox = 3;
	static public final int bulletBox = 4;
	static public final int weaponBox = 5;
	
	@Override
	public Object getClientGuiElement(int guiId, EntityPlayer player, World world, int x, int y, int z) {
		switch (guiId) {
		case gunBox:
			return new GuiGunBox(player.inventory, world, x, y, z);
		case ammoBox:
			return new GuiAmmoBox(player.inventory, world, x, y, z);
		case magazineFillerItem:
			return new GuiMagazineFillerItem(player.inventory);
		case magazineBox:
			return new GuiMagazineBox(player.inventory, world, x, y, z);
		case bulletBox:
			return new GuiBulletBox(player.inventory, world, x, y, z);
		case weaponBox:
			return new GuiWeaponBox(player.inventory, world, x, y, z);
		default:
			return null;
		}
	}
	
	@Override
	public Object getServerGuiElement(int guiId, EntityPlayer player, World world, int x, int y, int z) {
		switch (guiId) {
		case gunBox:
			return new ContainerGunBox(player.inventory, world, x, y, z);
		case ammoBox:
			return new ContainerAmmoBox(player.inventory, world, x, y, z);
		case magazineFillerItem:
			return new ContainerMagazineFiller(player.inventory);
		case magazineBox:
			return new ContainerMagazineBox(player.inventory, world, x, y, z);
		case bulletBox:
			return new ContainerBulletBox(player.inventory, world, x, y, z);
		case weaponBox:
			return new ContainerWeaponBox(player.inventory, world, x, y, z);
		default:
			return null;
		}
	}
}
