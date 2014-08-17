package assets.guncusofficial;

import assets.guncus.GunCus;
import assets.guncus.GunCusItemBullet;
import assets.guncus.GunCusItemGun;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import net.minecraft.item.Item;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

@Mod(modid="GunCusOfficialGuns", name="Gun Customization Official Guns", version="1.7.10 BETA v4")
@NetworkMod(clientSideRequired=true, serverSideRequired=false)
public class GunCusOfficial
{
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

  @Mod.EventHandler
  public void preInit(FMLPreInitializationEvent preEvent)
  {
preEvent.getModMetadata().parent = "GunCus";

Configuration config = GunCus.config;

config.load();
boolean enable = config.get("Gun Customization", "Enable Official Guns", true).getBoolean(true);
config.save();

if (!enable)
    {
return;
    }

config.load();
int acp = config.get("Official Guns Bullet IDs", 
"GC .45 ACP", 30000).getInt(30000);
int nato = config.get("Official Guns Bullet IDs", 
"GC 5.56x45mm NATO", 30001).getInt(30001);
int wp = config.get("Official Guns Bullet IDs", "GC 
5.45x39mm WP", 30002).getInt(30002);
int parabellum = config.get("Official Guns Bullet 
IDs", "GC 9x19mm Parabellum", 30003).getInt(30003);
int natoHeavy = config.get("Official Guns Bullet 
IDs", "GC 7.62x51mm NATO", 30004).getInt(30004);
int wpHeavy = config.get("Official Guns Bullet 
IDs", "GC 7.62x39mm WP", 30005).getInt(30005);
int rHeavy = config.get("Official Guns Bullet IDs", 
"GC 7.62x54mm R", 30006).getInt(30006);

int m16a3m = config.get("Official Guns Mag IDs", 
"GC M16A3 Magazine", 30007).getInt(30007);
int m16a3 = config.get("Official Guns Gun IDs", "GC 
M16A3", 30008).getInt(30008);

int aek971m = config.get("Official Guns Mag IDs", 
"GC AEK-971 Magazine", 30009).getInt(30009);
int aek971 = config.get("Official Guns Gun IDs", 
"GC AEK-971", 30010).getInt(30010);

int l96m = config.get("Official Guns Mag IDs", "GC 
L96 Magazine", 30011).getInt(30011);
int l96 = config.get("Official Guns Gun IDs", "GC 
L96", 30012).getInt(30012);

int sv98m = config.get("Official Guns Mag IDs", "GC 
SV98 Magazine", 30013).getInt(30013);
int sv98 = config.get("Official Guns Gun IDs", "GC 
SV98", 30014).getInt(30014);

int g17m = config.get("Official Guns Mag IDs", "GC 
G17 Magazine", 30015).getInt(30015);
int g17 = config.get("Official Guns Gun IDs", "GC 
G17", 30016).getInt(30016);

int g18m = config.get("Official Guns Mag IDs", "GC 
G18 Magazine", 30017).getInt(30017);
int g18 = config.get("Official Guns Gun IDs", "GC 
G18", 30018).getInt(30018);
config.save();

acp = new GunCusItemBullet(acp, "GC .45 ACP", 0, 1, 
1, 4, pack, "guncusofficial:bullets/bullet_0", 5.0F);
nato = new GunCusItemBullet(nato, "GC 5.56x45mm 
NATO", 1, 1, 1, 4, pack, "guncusofficial:bullets/bullet_1", 5.0F);
wp = new GunCusItemBullet(wp, "GC 5.45x39mm WP", 2, 
1, 1, 4, pack, "guncusofficial:bullets/bullet_2", 5.0F);
parabellum = new GunCusItemBullet(parabellum, "GC 
9x19mm Parabellum", 3, 1, 1, 4, pack, "guncusofficial:bullets/bullet_3", 4.0F);
natoHeavy = new GunCusItemBullet(natoHeavy, "GC 
7.62x51mm NATO", 4, 2, 3, 4, pack, "guncusofficial:bullets/bullet_4", 17.0F);
wpHeavy = new GunCusItemBullet(wpHeavy, "GC 
7.62x39mm WP", 5, 2, 3, 4, pack, "guncusofficial:bullets/bullet_5", 17.0F);
rHeavy = new GunCusItemBullet(rHeavy, "GC 7.62x54mm 
R", 6, 2, 3, 4, pack, "guncusofficial:bullets/bullet_6", 17.0F);

int[] scopes = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 
12, 13 };

m16a3 = new GunCusItemGun(m16a3, 5, 2, 3, "GC 
M16A3", buildIcon("m16a3"), 31, m16a3m, 1, 3, 5, 3, pack, true, new int[] { 3, 4 }, new int[] { 1, 2, 3 }, scopes, false, new int[0]);
aek971 = new GunCusItemGun(aek971, 5, 2, 3, "GC 
AEK-971", buildIcon("aek-971"), 31, aek971m, 2, 3, 5, 3, pack, true, new int[] { 3, 4 }, new int[] { 1, 2, 3 }, scopes, false, new int[0]);
l96 = new GunCusItemGun(l96, 17, 0, 25, "GC L96", buildIcon("l96"), 11, l96m, 4, 3, 5, 3, pack, true, new int[] { 1, 2 }, new int[] { 1, 2, 3 }, scopes, false, new int[0]);
sv98 = new GunCusItemGun(sv98, 17, 0, 25, "GC 
SV98", buildIcon("sv98"), 11, sv98m, 6, 3, 5, 3, pack, true, new int[] { 1, 2 }, new int[] { 1, 2, 3 }, scopes, false, new int[0]);
g17 = new GunCusItemGun(g17, 4, 0, 2, "GC G17", 
buildIcon("g17"), 18, g17m, 3, 2, 4, 3, pack, true, new int[] { 5, 6 }, new int[] { 1, 2, 3 }, scopes, false, new int[0]);
g18 = new GunCusItemGun(g18, 4, 2, 2, "GC G18", 
buildIcon("g18"), 18, g18m, 3, 2, 4, 3, pack, true, new int[] { 5, 6 }, new int[] { 1, 2, 3 }, scopes, false, new int[0]);
  }

  public static String buildIcon(String s)
  {
return "guncusofficial:gun_" + s + "/";
  }
}

