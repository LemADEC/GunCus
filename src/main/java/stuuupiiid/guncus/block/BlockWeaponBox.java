package stuuupiiid.guncus.block;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.gui.GuiHandler;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockWeaponBox extends BlockBase {
	
	public BlockWeaponBox() {
		super(Material.iron, "weaponbox");
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos blockPos, IBlockState blockState_unused, EntityPlayer entityPlayer,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return true;
		}
		entityPlayer.openGui(GunCus.instance, GuiHandler.weaponBox, world, blockPos.getX(), blockPos.getY(), blockPos.getZ());
		return true;
	}
}