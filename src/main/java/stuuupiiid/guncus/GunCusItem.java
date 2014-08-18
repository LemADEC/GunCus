package stuuupiiid.guncus;

import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class GunCusItem extends Item {
	String iconName;

	public GunCusItem(int par1, String iconName, String name, String unlocalized) {
		super(par1);
		setCreativeTab(GunCus.gcTab);
		this.iconName = iconName;
		setUnlocalizedName(unlocalized);
		LanguageRegistry.addName(this, name);
	}

	public GunCusItem(int par1) {
		super(par1);
		setCreativeTab(GunCus.gcTab);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		this.itemIcon = iconRegister.registerIcon(this.iconName);
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
			par2List.add(infoLine(info));
		}
	}

	private String infoLine(String s) {
		return s;
	}
}
