package stuuupiiid.guncus;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class GunCusCreativeTab extends CreativeTabs {
	private Item item = null;

	public GunCusCreativeTab(String label, Item parItem) {
		super(label);
		item = parItem;
		LanguageRegistry.instance().addStringLocalization("itemGroup." + label, label);
	}

	@Override
	public Item getTabIconItem() {
		return item;
	}
}
