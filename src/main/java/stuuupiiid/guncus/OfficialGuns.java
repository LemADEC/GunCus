package stuuupiiid.guncus;

import stuuupiiid.guncus.item.ItemBullet;
import stuuupiiid.guncus.item.ItemGun;
import net.minecraft.item.Item;

public class OfficialGuns {
	public static Item acp;
	public static Item nato;
	public static Item wp;
	public static Item parabellum;
	public static Item natoHeavy;
	public static Item wpHeavy;
	public static Item rHeavy;
	public static Item m16a3;
	public static Item aek971;
	public static Item l96;
	public static Item sv98;
	public static Item g17;
	public static Item g18;
	public static String pack = "official";

	public static void load() {
		acp = new ItemBullet("GC .45 ACP", 0, 0, 1, 1, 4, pack, "guncusofficial:bullets/bullet_0", 5.0F);
		nato = new ItemBullet("GC 5.56x45mm NATO", 1, 0, 1, 1, 4, pack, "guncusofficial:bullets/bullet_1", 5.0F);
		wp = new ItemBullet("GC 5.45x39mm WP", 2, 0, 1, 1, 4, pack, "guncusofficial:bullets/bullet_2", 5.0F);
		parabellum = new ItemBullet("GC 9x19mm Parabellum", 3, 0, 1, 1, 4, pack, "guncusofficial:bullets/bullet_3", 4.0F);
		natoHeavy = new ItemBullet("GC 7.62x51mm NATO", 4, 0, 2, 3, 4, pack, "guncusofficial:bullets/bullet_4", 17.0F);
		wpHeavy = new ItemBullet("GC 7.62x39mm WP", 5, 0, 2, 3, 4, pack, "guncusofficial:bullets/bullet_5", 17.0F);
		rHeavy = new ItemBullet("GC 7.62x54mm R", 6, 0, 2, 3, 4, pack, "guncusofficial:bullets/bullet_6", 17.0F);
		
		int[] scopes = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13 };
		
		m16a3 = new ItemGun(5, 2, 3, "m16a3", "guncusofficial:gun_m16a3/", 31, 1, 3, 5, 3, pack, true, new int[] { 3, 4 }, new int[] { 1, 2, 3 }, scopes, false, new int[0]);
		aek971 = new ItemGun(5, 2, 3, "aek971", "guncusofficial:gun_aek-971/", 31, 2, 3, 5, 3, pack, true, new int[] { 3, 4 }, new int[] { 1, 2, 3 }, scopes, false, new int[0]);
		l96 = new ItemGun(17, 0, 25, "l96", "guncusofficial:gun_l96/", 11, 4, 3, 5, 3, pack, true, new int[] { 1, 2 }, new int[] { 1, 2, 3 }, scopes, false, new int[0]);
		sv98 = new ItemGun(17, 0, 25, "sv98", "guncusofficial:gun_sv98/", 11, 6, 3, 5, 3, pack, true, new int[] { 1, 2 }, new int[] { 1, 2, 3 }, scopes, false, new int[0]);
		g17 = new ItemGun(4, 0, 2, "g17", "guncusofficial:gun_g17/", 18, 3, 2, 4, 3, pack, true, new int[] { 5, 6 }, new int[] { 1, 2, 3 }, scopes, false, new int[0]);
		g18 = new ItemGun(4, 2, 2, "g18", "guncusofficial:gun_g18/", 18, 3, 2, 4, 3, pack, true, new int[] { 5, 6 }, new int[] { 1, 2, 3 }, scopes, false, new int[0]);
	}
}
