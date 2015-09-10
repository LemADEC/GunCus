package stuuupiiid.guncus.block;

import java.util.Random;

import stuuupiiid.guncus.GunCus;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

public class GunCusBlock extends Block {
	public GunCusBlock(Material par2Material, String unlocalized) {
		super(par2Material);
		setBlockName(unlocalized);
		setCreativeTab(GunCus.creativeTabGunCus);
		setHardness(2.0F);
		setResistance(5.0F);
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
		return Item.getItemFromBlock(this);
	}
}
