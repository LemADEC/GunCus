package stuuupiiid.guncus;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class GunCusItemMag extends GunCusItem {
	public int bulletType;
	public String gunName;
	public String pack;

	public GunCusItemMag(String weaponName, String unlocalized, int magSize, String weaponIcon,
			int bulletType, String pack) {
		super(weaponIcon + "magazine", weaponName + " Magazine", unlocalized);
		setMaxDamage(magSize);
		setMaxStackSize(1);
		this.bulletType = bulletType;
		this.gunName = weaponName;
		this.pack = pack;
	}

	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par2List, boolean par4) {
		par2List.add(this.pack);
	}
}
