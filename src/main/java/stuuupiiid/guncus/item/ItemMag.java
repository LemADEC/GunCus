package stuuupiiid.guncus.item;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemMag extends GunCusItem {
	public int bulletId;
	public String gunName;
	public String pack;
	
	public ItemMag(String gunName, String unlocalized, int magSize, String weaponIcon, int bulletId, String pack) {
		super(weaponIcon + "magazine", gunName + " Magazine", unlocalized + ".magazine");
		setMaxDamage(magSize);
		setMaxStackSize(1);
		this.bulletId = bulletId;
		this.gunName = gunName;
		this.pack = pack;
	}
	
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par2List, boolean par4) {
		par2List.add("Pack: " + pack);
	}
}
