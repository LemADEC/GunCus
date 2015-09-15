package stuuupiiid.guncus;

import stuuupiiid.guncus.item.ItemBullet;
import stuuupiiid.guncus.item.ItemGun;

public class OfficialGuns {
	public static ItemBullet acp;
	public static ItemBullet nato;
	public static ItemBullet wp;
	public static ItemBullet parabellum;
	public static ItemBullet natoHeavy;
	public static ItemBullet wpHeavy;
	public static ItemBullet rHeavy;
	public static ItemGun m16a3;
	public static ItemGun aek971;
	public static ItemGun l96;
	public static ItemGun sv98;
	public static ItemGun g17;
	public static ItemGun g18;
	public static String pack = "Official";
	
	public static void load() {
		acp        = new ItemBullet(pack, "acp"       , 0, "guncusofficial:bullets/bullet", 0, 1, 1, 4,  5.0F);
		nato       = new ItemBullet(pack, "nato"      , 1, "guncusofficial:bullets/bullet", 0, 1, 1, 4,  5.0F);
		wp         = new ItemBullet(pack, "wp"        , 2, "guncusofficial:bullets/bullet", 0, 1, 1, 4,  5.0F);
		parabellum = new ItemBullet(pack, "parabellum", 3, "guncusofficial:bullets/bullet", 0, 1, 1, 4,  4.0F);
		natoHeavy  = new ItemBullet(pack, "natoHeavy" , 4, "guncusofficial:bullets/bullet", 0, 2, 3, 4, 17.0F);
		wpHeavy    = new ItemBullet(pack, "wpHeavy"   , 5, "guncusofficial:bullets/bullet", 0, 2, 3, 4, 17.0F);
		rHeavy     = new ItemBullet(pack, "rHeavy"    , 6, "guncusofficial:bullets/bullet", 0, 2, 3, 4, 17.0F);
		
		int[] scopes = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13 };
		
		m16a3  = new ItemGun(pack, true, "m16a3" , "guncusofficial:gun_m16a3/"  ,  5, 2,  3, 31, 1, 3, 5, 3, new int[] { 3, 4 }, new int[] { 1, 2, 3 }, scopes, false, new int[0]);
		aek971 = new ItemGun(pack, true, "aek971", "guncusofficial:gun_aek-971/",  5, 2,  3, 31, 2, 3, 5, 3, new int[] { 3, 4 }, new int[] { 1, 2, 3 }, scopes, false, new int[0]);
		l96    = new ItemGun(pack, true, "l96"   , "guncusofficial:gun_l96/"    , 17, 0, 25, 11, 4, 3, 5, 3, new int[] { 1, 2 }, new int[] { 1, 2, 3 }, scopes, false, new int[0]);
		sv98   = new ItemGun(pack, true, "sv98"  , "guncusofficial:gun_sv98/"   , 17, 0, 25, 11, 6, 3, 5, 3, new int[] { 1, 2 }, new int[] { 1, 2, 3 }, scopes, false, new int[0]);
		g17    = new ItemGun(pack, true, "g17"   , "guncusofficial:gun_g17/"    ,  4, 0,  2, 18, 3, 2, 4, 3, new int[] { 5, 6 }, new int[] { 1, 2, 3 }, scopes, false, new int[0]);
		g18    = new ItemGun(pack, true, "g18"   , "guncusofficial:gun_g18/"    ,  4, 2,  2, 18, 3, 2, 4, 3, new int[] { 5, 6 }, new int[] { 1, 2, 3 }, scopes, false, new int[0]);
		
		GunCusCreativeTab tab;
		tab = new GunCusCreativeTab(pack + ".m16a3", m16a3);
		m16a3.setCreativeTab(tab);
		m16a3.mag.setCreativeTab(tab);
		tab = new GunCusCreativeTab(pack + ".aek971", aek971);
		aek971.setCreativeTab(tab);
		aek971.mag.setCreativeTab(tab);
		tab = new GunCusCreativeTab(pack + ".l96", l96);
		l96.setCreativeTab(tab);
		l96.mag.setCreativeTab(tab);
		tab = new GunCusCreativeTab(pack + ".sv98", sv98);
		sv98.setCreativeTab(tab);
		sv98.mag.setCreativeTab(tab);
		tab = new GunCusCreativeTab(pack + ".g17", g17);
		g17.setCreativeTab(tab);
		g17.mag.setCreativeTab(tab);
		tab = new GunCusCreativeTab(pack + ".g18", g18);
		g18.setCreativeTab(tab);
		g18.mag.setCreativeTab(tab);
	}
}
