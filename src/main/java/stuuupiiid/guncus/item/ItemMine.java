package stuuupiiid.guncus.item;

import stuuupiiid.guncus.GunCus;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemMine extends GunCusItem {
	public ItemMine() {
		super("guncus:mine", "mine");
	}
	
	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int par7, float par8, float par9, float par10) {
		if (par7 != 1) {
			return false;
		}
		
		y++;
		Block block = GunCus.mineBlock;
		
		if (entityPlayer.canPlayerEdit(x, y, z, par7, itemStack) && entityPlayer.canPlayerEdit(x, y + 1, z, par7, itemStack)) {
			if (!block.canPlaceBlockAt(world, x, y, z)) {
				return false;
			}
			
			world.setBlock(x, y, z, GunCus.mineBlock);
			itemStack.stackSize -= 1;
			return true;
		}
		
		return false;
	}
}
