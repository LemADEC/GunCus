package stuuupiiid.guncus.item;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemMagazine extends ItemBase {
	public int[] bulletIds;
	public String gunName;
	public String packName;
	
	public ItemMagazine(String packName, String gunName, int magSize, int[] bulletIds) {
		super(packName + "." + gunName + ".magazine");
		setMaxDamage(magSize);
		setMaxStackSize(1);
		this.bulletIds = bulletIds;
		this.gunName = gunName;
		this.packName = packName;
	}
	
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
		ItemStack itemStackBullet = new ItemStack(ItemBullet.bullets.get(packName).get(bulletIds[0]), 0);	// FIXME: show all bullet types
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
		list.add(packName + " pack");
	}
}
