package stuuupiiid.guncusexplosives;

import stuuupiiid.guncus.GunCusItem;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class GunCusExplosivesMineItem extends GunCusItem {
	public GunCusExplosivesMineItem(int par1) {
		super(par1, "guncusexplosives:mine", "Mine", "gcmineitem");
	}

	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4,
			int par5, int par6, int par7, float par8, float par9, float par10) {
		if (par7 != 1) {
			return false;
		}

		par5++;
		Block block = GunCusExplosives.mineBlock;

		if ((par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack))
				&& (par2EntityPlayer.canPlayerEdit(par4, par5 + 1, par6, par7, par1ItemStack))) {
			if (!block.canPlaceBlockAt(par3World, par4, par5, par6)) {
				return false;
			}

			par3World.setBlock(par4, par5, par6, GunCusExplosives.mineBlock.blockID);
			par1ItemStack.stackSize -= 1;
			return true;
		}

		return false;
	}
}
