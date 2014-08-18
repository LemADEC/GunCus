package stuuupiiid.guncus;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.creativetab.CreativeTabs;

public class GunCusCreativeTab extends CreativeTabs {
	private int icon = 0;

	public GunCusCreativeTab(String label, int icon) {
		super(label);
		this.icon = icon;
		LanguageRegistry.instance().addStringLocalization("itemGroup." + label, label);
	}

	@Override
	public int getTabIconItemIndex() {
		return this.icon;
	}
}
