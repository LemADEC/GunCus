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
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
		ItemStack itemStackBullet = new ItemStack(ItemBullet.bulletsList.get(pack).get(bulletId), 0);
		int bulletCount = getMaxDamage() - itemStack.getItemDamage();
		if (bulletCount == getMaxDamage()) {
			list.add("Full magazine of " + itemStackBullet.getDisplayName());
			list.add("Bullet count: " + bulletCount + " / " + getMaxDamage());
		} else if (bulletCount == 0) {
			list.add("Empty magazine of " + itemStackBullet.getDisplayName());
			list.add("Use an Ammo box to fill it up.");
		} else {
			list.add("Magazine of " + itemStackBullet.getDisplayName());
			list.add("Bullet count: " + bulletCount + " / " + getMaxDamage());
		}
		list.add("Pack: " + pack);
	}
}
