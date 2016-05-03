package stuuupiiid.guncus.block;

import java.util.Random;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.item.ItemBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBase extends Block {
	public BlockBase(Material material, String unlocalizedName) {
		super(material);
		setUnlocalizedName(unlocalizedName);
		setRegistryName(unlocalizedName);
		setCreativeTab(GunCus.creativeTabModifications);
		setHardness(2.0F);
		setResistance(5.0F);
		GameRegistry.registerBlock(this);
	}
	
	@SideOnly(Side.CLIENT)
	public void onForgePreinit() {
		Item item = Item.getItemFromBlock(this);
		ItemBase.onForgePreinit(item);
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(this);
	}
}
