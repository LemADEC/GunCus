package stuuupiiid.guncus.item;

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
	public CustomizationPart[] customizationParts;
	public int maxId;
	public IIcon[] icons;
	
	public ItemMetadata(String unlocalizedName, String iconName, CustomizationPart[] customizationParts) {
		super(iconName, unlocalizedName);
		
		this.customizationParts = customizationParts;
		maxId = 0;
		for (CustomizationPart customizationPart : customizationParts) {
			if (maxId < customizationPart.id) {
				maxId = customizationPart.id;
			}
		}
		setHasSubtypes(true);
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs creativeTab, List list) {
		for (int metadata = 0; metadata < customizationParts.length; metadata++) {
			ItemStack itemStack = new ItemStack(item, 1, customizationParts[metadata].id);
			list.add(itemStack);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		icons = new IIcon[maxId + 1];
		
		for (int indexPart = 0; indexPart < customizationParts.length; indexPart++) {
			icons[customizationParts[indexPart].id] = iconRegister.registerIcon("guncus:" + iconString + customizationParts[indexPart].id);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int damage) {
		if (damage >= 0 && damage <= maxId) {
			return icons[damage];
		} else {
			return null;
		}
	}
	
	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		return super.getUnlocalizedName() + itemStack.getItemDamage();
	}
}
