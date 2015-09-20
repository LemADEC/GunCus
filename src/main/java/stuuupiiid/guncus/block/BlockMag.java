package stuuupiiid.guncus.block;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.gui.GuiHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockMag extends GunCusBlock {
	
	public BlockMag() {
		super(Material.iron, "blockMag");
	}
	
	@SideOnly(Side.CLIENT)
	private IIcon iconTop;
	
	@SideOnly(Side.CLIENT)
	private IIcon iconBottom;
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int par1, int par2) {
		return par1 == 0 ? iconBottom : par1 == 1 ? iconTop : blockIcon;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		blockIcon = par1IconRegister.registerIcon("guncus:side");
		iconTop = par1IconRegister.registerIcon("guncus:mag");
		iconBottom = par1IconRegister.registerIcon("guncus:bot");
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer,
			int side, float xOffset, float yOffset, float zOffset) {
		if (world.isRemote) {
			return true;
		}
		entityPlayer.openGui(GunCus.instance, GuiHandler.magBlock, world, x, y, z);
		return true;
	}
}
