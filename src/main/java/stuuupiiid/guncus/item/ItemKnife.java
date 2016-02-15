package stuuupiiid.guncus.item;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.GunCusKeyBindings;
import stuuupiiid.guncus.network.PacketHandler;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemKnife extends GunCusItem {
	public ItemKnife() {
		super("guncus:quickKnife", "quickKnife");
		setFull3D();
	}
	
	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int par1, boolean flag) {
		if (world.isRemote) {
			doKnife();
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void doKnife() {
		if ( (GunCusKeyBindings.QuickKnife.isPressed())
		  && (GunCus.knifeTime <= 0)
		  && (FMLClientHandler.instance().getClient().thePlayer != null)
		  && (FMLClientHandler.instance().getClient().theWorld != null)
		  && (FMLClientHandler.instance().getClient().currentScreen == null)) {
				GunCus.knifeTime += GunCus.knifeCooldown;
				GunCus.shootTime += 24;
				PacketHandler.sendToServer_playerAction_knife();
		}
	}
}
