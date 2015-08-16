package stuuupiiid.guncus;

import stuuupiiid.guncus.item.ItemBullet;
import stuuupiiid.guncus.item.ItemGun;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;

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
	public static String pack = "GC Official Guns";

	public static void load() {
		Configuration config = GunCus.config;

		config.load();

		int m16a3mID = config.get("Official Guns Mag IDs", "GC M16A3 Magazine", 30007).getInt(30007);

		int aek971mID = config.get("Official Guns Mag IDs", "GC AEK-971 Magazine", 30009).getInt(30009);

		int l96mID = config.get("Official Guns Mag IDs", "GC L96 Magazine", 30011).getInt(30011);

		int sv98mID = config.get("Official Guns Mag IDs", "GC SV98 Magazine", 30013).getInt(30013);

		int g17mID = config.get("Official Guns Mag IDs", "GC G17 Magazine", 30015).getInt(30015);

		int g18mID = config.get("Official Guns Mag IDs", "GC G18 Magazine", 30017).getInt(30017);
		config.save();

		acp = new ItemBullet("GC .45 ACP", 0, 1, 1, 4, pack, "guncusofficial:bullets/bullet_0", 5.0F);
		nato = new ItemBullet("GC 5.56x45mm NATO", 1, 1, 1, 4, pack, "guncusofficial:bullets/bullet_1", 5.0F);
		wp = new ItemBullet("GC 5.45x39mm WP", 2, 1, 1, 4, pack, "guncusofficial:bullets/bullet_2", 5.0F);
		parabellum = new ItemBullet("GC 9x19mm Parabellum", 3, 1, 1, 4, pack, "guncusofficial:bullets/bullet_3", 4.0F);
		natoHeavy = new ItemBullet("GC 7.62x51mm NATO", 4, 2, 3, 4, pack, "guncusofficial:bullets/bullet_4", 17.0F);
		wpHeavy = new ItemBullet("GC 7.62x39mm WP", 5, 2, 3, 4, pack, "guncusofficial:bullets/bullet_5", 17.0F);
		rHeavy = new ItemBullet("GC 7.62x54mm R", 6, 2, 3, 4, pack, "guncusofficial:bullets/bullet_6", 17.0F);

		int[] scopes = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13 };

		m16a3 = new ItemGun(5, 2, 3, "GC M16A3", "guncusofficial:gun_m16a3/", 31, m16a3mID, 1, 3, 5, 3, pack, true, new int[] { 3, 4 }, new int[] { 1, 2, 3 }, scopes, false, new int[0]);
		aek971 = new ItemGun(5, 2, 3, "GC AEK-971", "guncusofficial:gun_aek-971/", 31, aek971mID, 2, 3, 5, 3, pack, true, new int[] { 3, 4 }, new int[] { 1, 2, 3 }, scopes, false, new int[0]);
		l96 = new ItemGun(17, 0, 25, "GC L96", "guncusofficial:gun_l96/", 11, l96mID, 4, 3, 5, 3, pack, true, new int[] { 1, 2 }, new int[] { 1, 2, 3 }, scopes, false, new int[0]);
		sv98 = new ItemGun(17, 0, 25, "GC SV98", "guncusofficial:gun_sv98/", 11, sv98mID, 6, 3, 5, 3, pack, true, new int[] { 1, 2 }, new int[] { 1, 2, 3 }, scopes, false, new int[0]);
		g17 = new ItemGun(4, 0, 2, "GC G17", "guncusofficial:gun_g17/", 18,	g17mID, 3, 2, 4, 3, pack, true, new int[] { 5, 6 }, new int[] {	1, 2, 3 }, scopes, false, new int[0]);
		g18 = new ItemGun(4, 2, 2, "GC G18", "guncusofficial:gun_g18/", 18,	g18mID, 3, 2, 4, 3, pack, true, new int[] { 5, 6 }, new int[] {	1, 2, 3 }, scopes, false, new int[0]);
	}
}
