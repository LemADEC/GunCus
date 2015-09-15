package stuuupiiid.guncus.block;

import stuuupiiid.guncus.GunCus;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMine extends GunCusBlock {
	public BlockMine() {
		super(Material.iron, "mine");
		setHardness(1.0F);
		setResistance(1.0F);
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
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int x, int y, int z) {
		int l = par1World.getBlockMetadata(x, y, z) & 0x7;
		float f = 0.125F;
		return AxisAlignedBB.getBoundingBox(
				x + minX, y + minY , z + minZ,
				x + maxX, y + l * f, z + maxZ);
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
	public void setBlockBoundsForItemRender() {
		setBlockBoundsForSnowDepth(0);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
		setBlockBoundsForSnowDepth(par1IBlockAccess.getBlockMetadata(par2, par3, par4));
	}

	protected void setBlockBoundsForSnowDepth(int par1) {
		int j = par1 & 0x7;
		float f = 2 * (1 + j) / 16.0F;
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);
	}

	@Override
	public void harvestBlock(World par1World, EntityPlayer par2EntityPlayer, int par3, int par4, int par5, int par6) {
		super.harvestBlock(par1World, par2EntityPlayer, par3, par4, par5, par6);
		par1World.setBlockToAir(par3, par4, par5);
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
	public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity) {
		if ((par5Entity instanceof EntityLivingBase)) {
			explode(par2, par3, par4, par1World);
		}
	}

	public void explode(int x, int y, int z, World world) {
		if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			world.createExplosion(null, x + 0.5D, y + 1, z + 0.5D, 1.3F, GunCus.enableBlockDamage);
			world.createExplosion(null, x + 0.5D, y + 1, z + 0.5D, 3.0F, false);
			world.setBlockToAir(x, y, z);
		}
	}

	@Override
	public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion) {
		explode(x, y, z, world);
	}
}
