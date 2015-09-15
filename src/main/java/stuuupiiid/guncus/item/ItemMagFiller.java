package stuuupiiid.guncus.item;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.gui.GuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemMagFiller extends GunCusItem {
	public ItemMagFiller() {
		super("guncus:magFiller", "magFiller");
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
		par3EntityPlayer.openGui(GunCus.instance, GuiHandler.magItem,
				par2World,
				MathHelper.floor_double(par3EntityPlayer.posX),
				MathHelper.floor_double(par3EntityPlayer.posY),
				MathHelper.floor_double(par3EntityPlayer.posZ));
		return par1ItemStack;
	}
}
