package stuuupiiid.guncus.item;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import stuuupiiid.guncus.GunCus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class GunCusItem extends Item {
	
	public GunCusItem(String iconName, String unlocalizedName) {
		super();
		setMaxStackSize(1);
		setCreativeTab(GunCus.creativeTabModifications);
		iconString = iconName;
		setUnlocalizedName(unlocalizedName.replace(" ", "_"));
		GameRegistry.registerItem(this, getUnlocalizedName());
	}
	
	public GunCusItem() {
		super();
		setCreativeTab(GunCus.creativeTabModifications);
	}
	
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par2List, boolean par4) {
		String info = "GunCusDebugInfo";
		
		if (getUnlocalizedName(par1ItemStack).contains("scope")) {
			info = "Scope (Top)";
		} else if (getUnlocalizedName(par1ItemStack).contains("attachment")) {
			info = "Attachment (Bottom)";
		} else if (getUnlocalizedName(par1ItemStack).contains("barrel")) {
			info = "Barrel (Left)";
		}
		
		if (!info.equals("GunCusDebugInfo")) {
			par2List.add(info);
		}
	}
}
