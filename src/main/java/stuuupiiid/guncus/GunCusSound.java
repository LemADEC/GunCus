package stuuupiiid.guncus;

import java.util.ArrayList;
import net.minecraft.client.audio.SoundManager;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;

public class GunCusSound {
	public static ArrayList<String> soundFiles = new ArrayList();

	@ForgeSubscribe
	public void forgeSubscribe(SoundLoadEvent event) {
		String[] sounds = { "shoot_normal.ogg", "inground.ogg", "knife.ogg", "shoot_silenced.ogg", "shoot_sniper.ogg",
				"reload.ogg", "click.ogg", "reload_tube.ogg", "reload_rpg.ogg" };
		for (String s : soundFiles) {
			GunCus.log("Found custom sound file: \"" + s + "\"");
			event.manager.addSound("minecraft:" + s);
		}
		for (String s : sounds) {
			event.manager.addSound("guncus:" + s);
		}
	}

	public static void addSound(String s) {
		soundFiles.add(s);
	}
}
