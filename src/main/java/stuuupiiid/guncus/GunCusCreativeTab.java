package stuuupiiid.guncus;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class GunCusCreativeTab extends CreativeTabs {
	Item item = null;
	
	public GunCusCreativeTab(String label, Item item) {
		super(label);
		LanguageRegistry.instance().addStringLocalization("itemGroup." + label, label);
	}
	
	@Override
	public Item getTabIconItem() {
		if (item == null) {
			return GunCus.quickKnife;
		} else {
			return item;
		}
	}
}
