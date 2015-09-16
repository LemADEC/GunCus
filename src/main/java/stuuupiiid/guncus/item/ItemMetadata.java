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
	private CustomizationPart[] customizationParts;
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
		for (CustomizationPart customizationPart : customizationParts) {
			ItemStack itemStack = new ItemStack(item, 1, customizationPart.id);
			list.add(itemStack);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		icons = new IIcon[maxId + 1];
		
		for (CustomizationPart customizationPart : customizationParts) {
			icons[customizationPart.id] = iconRegister.registerIcon("guncus:" + iconString + customizationPart.id);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int damage) {
		if (damage > 0 && damage <= maxId) {
			return icons[damage];
		} else {
			return null;
		}
	}
	
	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		return super.getUnlocalizedName() + itemStack.getItemDamage();
	}
	
	public CustomizationPart getCustomizationPart(final int partId) {
		for (CustomizationPart customizationPart : customizationParts) {
			if (customizationPart.id == partId) {
				return customizationPart;
			}
		}
		
		return null;
	}
}
