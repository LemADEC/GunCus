package stuuupiiid.guncus;

import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;

public class GunCusKeyBindings {
	
	
	
	public static boolean m320Switch = false;
	public boolean selectFire = false;
	public boolean specialAmmo = false;
	public boolean quickKnife = false;
	
	public static KeyBinding M320Switch = new KeyBinding("key.M320Switch", Keyboard.KEY_C, "key.GunCus.catagory");
	public static KeyBinding SelectFire = new KeyBinding("key.SelectFire", Keyboard.KEY_V, "key.GunCus.catagory");
	public static KeyBinding SpecialAmmo = new KeyBinding("key.SpecialAmmo", Keyboard.KEY_G, "key.GunCus.catagory");
	public static KeyBinding QuickKnife = new KeyBinding("key.QuickKnife", Keyboard.KEY_F, "key.GunCus.catagory");
	
	public GunCusKeyBindings() {
		ClientRegistry.registerKeyBinding(M320Switch);
		ClientRegistry.registerKeyBinding(SelectFire);
		ClientRegistry.registerKeyBinding(SpecialAmmo);
		ClientRegistry.registerKeyBinding(QuickKnife);
		FMLCommonHandler.instance().bus().register(this);
	}
}