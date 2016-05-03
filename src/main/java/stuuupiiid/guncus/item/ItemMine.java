package stuuupiiid.guncus.item;

import stuuupiiid.guncus.GunCus;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemMine extends ItemBase {
	public ItemMine(String unlocalizedName) {
		super(unlocalizedName);
	}
	
	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, BlockPos blockPos, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (side != EnumFacing.UP) {
			return false;
		}
		
		BlockPos posMine = blockPos.add(0, 1, 0);
		Block block = GunCus.blockMine;
		
		if (entityPlayer.canPlayerEdit(posMine, side, itemStack) && entityPlayer.canPlayerEdit(posMine.add(0, 1, 0), side, itemStack)) {
			if (!block.canPlaceBlockAt(world, posMine)) {
				return false;
			}
			
			world.setBlockState(posMine, GunCus.blockMine.getDefaultState());
			itemStack.stackSize -= 1;
			return true;
		}
		
		return false;
	}
}
