package stuuupiiid.guncus;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class GunCusCreativeTab extends CreativeTabs {
	Item item = null;
	
	public GunCusCreativeTab(String label, Item item) {
		super(label);
		this.item = item;
	}
	
	@Override
	public Item getTabIconItem() {
		if (item == null) {
			return GunCus.itemQuickKnife;
		} else {
			return item;
		}
	}
}
