package stuuupiiid.guncus;

import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class GunCusItemMetadata extends GunCusItem {
	public GunCusCustomizationPart[] metadatas;
	public IIcon[] icons;
	public String unlocalized;

	public GunCusItemMetadata(String unlocalized, String iconName, GunCusCustomizationPart[] metadatas) {
		super();

		this.unlocalized = unlocalized;
		this.iconName = iconName;
		this.metadatas = metadatas;
		setHasSubtypes(true);

		for (int v1 = 0; v1 < this.metadatas.length; v1++) {
			LanguageRegistry.addName(new ItemStack(this, 1, v1), this.metadatas[v1].localized);
		}
	}

	@Override
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
		for (int j = 0; j < this.metadatas.length; j++) {
			ItemStack itemStack = new ItemStack(par1, 1, j);
			par3List.add(itemStack);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		this.icons = new IIcon[this.metadatas.length];

		for (int i = 0; i < this.metadatas.length; i++) {
			this.icons[i] = par1IconRegister.registerIcon("guncus:" + this.iconName + i);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int par1) {
		return this.icons[par1];
	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		return this.unlocalized + par1ItemStack.getItemDamage();
	}
}
