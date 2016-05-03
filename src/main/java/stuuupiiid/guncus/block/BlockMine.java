package stuuupiiid.guncus.block;

import stuuupiiid.guncus.GunCus;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMine extends BlockBase {
	
	public BlockMine() {
		super(Material.iron, "mine");
		setHardness(1.0F);
		setResistance(1.0F);
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
		setTickRandomly(false);
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public void harvestBlock(World world, EntityPlayer entityPlayer, BlockPos blockPos, IBlockState state, TileEntity tileEntity) {
		super.harvestBlock(world, entityPlayer, blockPos, state, tileEntity);
		world.setBlockToAir(blockPos);
	}
	
	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, BlockPos blockPos, EnumFacing side) {
		return side == EnumFacing.UP ? true : super.shouldSideBeRendered(world, blockPos, side);
	}
	
	@Override
	public boolean canPlaceBlockAt(World world, BlockPos blockPos) {
		return world.getBlockState(blockPos.add(0, -1, 0)).getBlock().isOpaqueCube();
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos blockPos, Entity entity) {
		if (entity instanceof EntityLivingBase) {
			explode(world, blockPos);
		}
	}
	
	@Override
	public boolean canDropFromExplosion(Explosion explosion) {
		return false;
	}
	
	public void explode(World world, BlockPos blockPos) {
		if (!world.isRemote) {
			world.setBlockToAir(blockPos);
			world.createExplosion(null, blockPos.getX() + 0.5D, blockPos.getY() + 1, blockPos.getZ() + 0.5D, 1.3F, GunCus.enableBlockDamage);
			world.createExplosion(null, blockPos.getX() + 0.5D, blockPos.getY() + 1, blockPos.getZ() + 0.5D, 3.0F, false);
		}
	}
	
	@Override
	public void onBlockDestroyedByExplosion(World world, BlockPos blockPos, Explosion explosion) {
		explode(world, blockPos);
	}
}
