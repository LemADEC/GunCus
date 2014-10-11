package stuuupiiid.guncus;

import cpw.mods.fml.common.registry.LanguageRegistry;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

public class GunCusBlock extends Block {
	public GunCusBlock(Material par2Material, String unlocalized, String name) {
		super(par2Material);
		this.setBlockName(unlocalized);
		LanguageRegistry.addName(this, name);
		setCreativeTab(GunCus.gcTab);
		setHardness(2.0F);
		setResistance(5.0F);
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
		return Item.getItemFromBlock(this);
	}
}
