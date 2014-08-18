package stuuupiiid.guncus;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GunCusGuiHandler implements IGuiHandler {
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID) {
		case 0:
			return new GunCusGuiGun(player.inventory, world, x, y, z);
		case 1:
			return new GunCusGuiAmmo(player.inventory, world, x, y, z);
		case 2:
			return new GunCusGuiAmmoMan(player.inventory, world, x, y, z);
		case 3:
			return new GunCusGuiMag(player.inventory, world, x, y, z);
		case 4:
			return new GunCusGuiBullet(player.inventory, world, x, y, z);
		case 5:
			return new GunCusGuiWeapon(player.inventory, world, x, y, z);
		}

		return null;
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID) {
		case 0:
			return new GunCusContainerGun(player.inventory, world, x, y, z);
		case 1:
			return new GunCusContainerAmmo(player.inventory, world, x, y, z);
		case 2:
			return new GunCusContainerAmmoMan(player.inventory, world, x, y, z);
		case 3:
			return new GunCusContainerMag(player.inventory, world, x, y, z);
		case 4:
			return new GunCusContainerBullet(player.inventory, world, x, y, z);
		case 5:
			return new GunCusContainerWeapon(player.inventory, world, x, y, z);
		}

		return null;
	}
}
