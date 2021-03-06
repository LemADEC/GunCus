package stuuupiiid.guncus.item;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemMag extends GunCusItem {
	public int bulletId;
	public String gunName;
	public String pack;
	
	public ItemMag(String pack, String gunName, String gunIcon, int magSize, int bulletId) {
		super(gunIcon + "magazine", pack + "." + gunName + ".magazine");
		setMaxDamage(magSize);
		setMaxStackSize(1);
		this.bulletId = bulletId;
		this.gunName = gunName;
		this.pack = pack;
	}
	
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
		ItemStack itemStackBullet = new ItemStack(ItemBullet.bullets.get(pack).get(bulletId), 0);
		int bulletCount = getMaxDamage() - itemStack.getItemDamage();
		if (bulletCount == getMaxDamage()) {
			list.add("Full magazine of " + itemStackBullet.getDisplayName());
			list.add(bulletCount + " / " + getMaxDamage() + " bullets");
		} else if (bulletCount == 0) {
			list.add("Empty magazine of " + itemStackBullet.getDisplayName());
			list.add("Use an Ammo box to fill it up.");
		} else {
			list.add("Magazine of " + itemStackBullet.getDisplayName());
			list.add(bulletCount + " / " + getMaxDamage() + " bullets");
		}
		list.add("");
		list.add(pack + " pack");
	}
}
