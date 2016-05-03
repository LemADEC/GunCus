package stuuupiiid.guncus.item;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.GunCusKeyBindings;
import stuuupiiid.guncus.network.PacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemQuickKnife extends ItemBase {
	public ItemQuickKnife(String unlocalizedName) {
		super(unlocalizedName);
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
