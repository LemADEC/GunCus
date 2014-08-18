package stuuupiiid.guncus;

import cpw.mods.fml.common.registry.LanguageRegistry;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class GunCusBlock extends Block {
	public GunCusBlock(int par1, Material par2Material, String unlocalized, String name) {
		super(par1, par2Material);
		setUnlocalizedName(unlocalized);
		LanguageRegistry.addName(this, name);
		setCreativeTab(GunCus.gcTab);
		setHardness(2.0F);
		setResistance(5.0F);
	}

	@Override
	public int idDropped(int par1, Random par2Random, int par3) {
		return this.blockID;
	}
}
