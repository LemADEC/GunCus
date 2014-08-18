package stuuupiiid.guncus;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class GunCusItemKnife extends GunCusItem {
	public GunCusItemKnife(int par1) {
		super(par1, "guncus:knife", "Knife", "gcKnife");
	}

	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int par1, boolean flag) {
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			GunCus.doKnife();
		}
	}
}
