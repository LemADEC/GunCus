package stuuupiiid.guncus.block;

import stuuupiiid.guncus.GunCus;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMine extends GunCusBlock {
	public BlockMine() {
		super(Material.iron, "mine");
		setHardness(1.0F);
		setResistance(1.0F);
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
		setTickRandomly(false);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int metadata) {
		return blockIcon;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		blockIcon = par1IconRegister.registerIcon("guncus:mine");
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	@Override
	public void harvestBlock(World world, EntityPlayer entityPlayer, int x, int y, int z, int metadata) {
		super.harvestBlock(world, entityPlayer, x, y, z, metadata);
		world.setBlockToAir(x, y, z);
	}
	
	@Override
	public Item getItemDropped(int par1, Random par2Random, int par3) {
		return GunCus.mineItem;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
		return par5 == 1 ? true : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
	}
	
	@Override
	public boolean canPlaceBlockAt(World par1World, int x, int y, int z) {
		return par1World.getBlock(x, y - 1, z).isOpaqueCube();
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		if (entity instanceof EntityLivingBase) {
			explode(world, x, y, z);
		}
	}
	
	@Override
	public boolean canDropFromExplosion(Explosion p_149659_1_) {
		return false;
	}
	
	public void explode(World world, int x, int y, int z) {
		if (!world.isRemote) {
			world.setBlockToAir(x, y, z);
			world.createExplosion(null, x + 0.5D, y + 1, z + 0.5D, 1.3F, GunCus.enableBlockDamage);
			world.createExplosion(null, x + 0.5D, y + 1, z + 0.5D, 3.0F, false);
		}
	}
	
	@Override
	public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion) {
		explode(world, x, y, z);
	}
}
