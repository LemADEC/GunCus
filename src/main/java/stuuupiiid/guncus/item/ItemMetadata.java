package stuuupiiid.guncus.item;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import stuuupiiid.guncus.data.CustomizationPart;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemMetadata extends GunCusItem {
	public CustomizationPart[] metadatas;
	public IIcon[] icons;
	public String unlocalized;
	
	public ItemMetadata(String unlocalized, String iconName, CustomizationPart[] metadatas) {
		super();
		
		this.unlocalized = unlocalized;
		this.iconString = iconName;
		this.metadatas = metadatas;
		setHasSubtypes(true);
		
		GameRegistry.registerItem(this, unlocalized);
		
		for (int v1 = 0; v1 < metadatas.length; v1++) {
			LanguageRegistry.addName(new ItemStack(this, 1, v1), this.metadatas[v1].localized);
		}
	}
	
	@Override
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
		for (int j = 0; j < metadatas.length; j++) {
			ItemStack itemStack = new ItemStack(par1, 1, j);
			par3List.add(itemStack);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		icons = new IIcon[metadatas.length];
		
		for (int i = 0; i < metadatas.length; i++) {
			icons[i] = par1IconRegister.registerIcon("guncus:" + iconString + i);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int damage) {
		return icons[damage];
	}
	
	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		return unlocalized + par1ItemStack.getItemDamage();
	}
}
