package stuuupiiid.guncus.item;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.gui.GuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemMagazineFiller extends ItemBase {
	public ItemMagazineFiller(String unlocalizedName) {
		super(unlocalizedName);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
		par3EntityPlayer.openGui(GunCus.instance, GuiHandler.magazineFillerItem,
				par2World,
				MathHelper.floor_double(par3EntityPlayer.posX),
				MathHelper.floor_double(par3EntityPlayer.posY),
				MathHelper.floor_double(par3EntityPlayer.posZ));
		return par1ItemStack;
	}
}
