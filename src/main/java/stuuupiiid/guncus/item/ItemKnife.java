package stuuupiiid.guncus.item;

import org.lwjgl.input.Keyboard;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.network.PacketHandler;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemKnife extends GunCusItem {
	public ItemKnife() {
		super("guncus:knife", "Knife", "quickKnife");
		setFull3D();
	}

	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int par1, boolean flag) {
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			doKnife();
		}
	}

	@SideOnly(Side.CLIENT)
	public static void doKnife() {
		if ((GunCus.knifeTime <= 0) && (FMLClientHandler.instance().getClient().thePlayer != null)
				&& (FMLClientHandler.instance().getClient().theWorld != null)) {
			if (((Keyboard.isKeyDown(29)) || (Keyboard.isKeyDown(157))) && (Keyboard.isKeyDown(33))
					&& (FMLClientHandler.instance().getClient().currentScreen == null)) {
				GunCus.knifeTime += 25;
				GunCus.shootTime += 24;
				PacketHandler.sendToServer_playerAction_knife();
			}
		}
	}
}
