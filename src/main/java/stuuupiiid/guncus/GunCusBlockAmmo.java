package stuuupiiid.guncus;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class GunCusBlockAmmo extends GunCusBlock {

	@SideOnly(Side.CLIENT)
	private IIcon field_94385_a;

	@SideOnly(Side.CLIENT)
	private IIcon field_94384_b;

	protected GunCusBlockAmmo() {
		super(Material.iron, "blockAmmo", "Ammo Box");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int par1, int par2) {
		return par1 == 0 ? this.field_94384_b : par1 == 1 ? this.field_94385_a : this.blockIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		this.blockIcon = par1IconRegister.registerIcon("guncus:side");
		this.field_94385_a = par1IconRegister.registerIcon("guncus:ammo");
		this.field_94384_b = par1IconRegister.registerIcon("guncus:bot");
	}

	@Override
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer,
			int par6, float par7, float par8, float par9) {
		par5EntityPlayer.openGui(GunCus.instance, 1, par1World, par2, par3, par4);
		return true;
	}
}
