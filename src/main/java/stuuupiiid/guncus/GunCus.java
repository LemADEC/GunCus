package stuuupiiid.guncus;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import stuuupiiid.guncus.block.BlockAmmo;
import stuuupiiid.guncus.block.BlockBullet;
import stuuupiiid.guncus.block.BlockGun;
import stuuupiiid.guncus.block.BlockMag;
import stuuupiiid.guncus.block.BlockMine;
import stuuupiiid.guncus.block.BlockWeapon;
import stuuupiiid.guncus.data.CustomizationPart;
import stuuupiiid.guncus.data.ScopePart;
import stuuupiiid.guncus.entity.EntityGrenade;
import stuuupiiid.guncus.entity.EntityBullet;
import stuuupiiid.guncus.gui.GuiHandler;
import stuuupiiid.guncus.item.GunCusItem;
import stuuupiiid.guncus.item.ItemAttachment;
import stuuupiiid.guncus.item.ItemBullet;
import stuuupiiid.guncus.item.ItemGun;
import stuuupiiid.guncus.item.ItemKnife;
import stuuupiiid.guncus.item.ItemMagFill;
import stuuupiiid.guncus.item.ItemMetadata;
import stuuupiiid.guncus.item.ItemMine;
import stuuupiiid.guncus.item.ItemRPG;
import stuuupiiid.guncus.item.ItemScope;

/**
 * @author LemADEC
 */
@Mod(modid = GunCus.MODID, name = "Gun Customization", version = GunCus.VERSION, dependencies = "")
public class GunCus {
	public static final String MODID = "GunCus";
	public static final String VERSION = "@version@";
	
	@SidedProxy(clientSide = "stuuupiiid.guncus.ClientProxy", serverSide = "stuuupiiid.guncus.CommonProxy")
	public static CommonProxy commonProxy = new CommonProxy();
	public static Configuration config;
	public static boolean enableBlockDamage;
	public static int shootTime = 0;
	public static int switchTime = 0;
	public static double accuracy = 100.0D;
	public static int accuracyReset = 5;
	public static float zoomLevel = 1.0F;
	public static float maxX;
	public static float maxY;
	public static boolean scopingX;
	public static boolean scopingY;
	public static int counter = 0;
	public static boolean startedBreathing;
	public static boolean breathing = false;
	public static int breathCounter = 0;
	public static boolean reloading = false;
	public static int hitmarker = 0;
	public static String cameraZoom = "Y";
	public static Item actualItem = null;
	public static int actualIndex = 0;

	public LinkedList<ItemGun> guns = new LinkedList<ItemGun>();

	public static int check = 300;

	private List<String> loadedGuns = new ArrayList();
	private List<String> loadedBullets = new ArrayList();
	public static File path;

	@Mod.Instance("GunCus")
	public static GunCus instance;
	public GuiHandler guiHandler = new GuiHandler();
	private boolean enableExplosives;
	private boolean enableOfficialGuns;
	public static Item quickKnife;
	public static CreativeTabs gcTab;
	public static Block blockWeapon;
	public static Block blockMag;
	public static Block blockBullet;
	public static Block blockAmmo;
	public static Block blockGun;
	public static Item magFill;
	public static Item part;
	public static ItemScope scope;
	public static ItemMetadata barrel;
	public static ItemMetadata attachment;
	public static Item ammoM320;
	public static int knifeTime = 0;
	
	// explosives extension
	public static Item rpgm;
	public static Item rpg;
	public static Item smawm;
	public static Item smaw;
	public static Block mineBlock;
	public static Item mineItem;
	
	// logging options
	public static boolean logging_enableNetwork = true;
	
	public static Logger logger;
	
	@Mod.EventHandler
	public void onFMLPreInitialization(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		quickKnife = new ItemKnife();
		gcTab = new GunCusCreativeTab("Gun Customization Modification", quickKnife);
		quickKnife.setCreativeTab(gcTab);
		
		enableBlockDamage = config.get("Gun Customization", "enableBlockDamage", true).getBoolean(true);
		enableExplosives = config.get("Gun Customization", "enableExplosives", true).getBoolean(true);
		enableOfficialGuns = config.get("Gun Customization", "enableOfficialGuns", true).getBoolean(true);
		
		config.save();
		
		blockWeapon = new BlockWeapon();
		blockMag = new BlockMag();
		blockBullet = new BlockBullet();
		blockAmmo = new BlockAmmo();
		blockGun = new BlockGun();
		
		magFill = new ItemMagFill();
		part = new GunCusItem("guncus:boxpart", "Box Part", "boxpart");
		
		scope = new ItemScope("scope", "scope",
				new ScopePart[] {
					new ScopePart("Reflex (RDS) Scope", "reflex", 1.0F, 1),
					new ScopePart("Kobra (RDS) Scope", "kobra", 1.0F, 2),
					new ScopePart("Holographic (Holo) Scope", "holographic", 1.0F, 3),
					new ScopePart("PKA-S (Holo) Scope", "pka-s", 1.0F, 4),
					new ScopePart("M145 (3.4x) Scope", "m145", 3.4F, 5),
					new ScopePart("PK-A (3.4x) Scope", "pk-a", 3.4F, 6),
					new ScopePart("ACOG (4x) Scope", "acog", 4.0F, 7),
					new ScopePart("PSO-1 (4x) Scope", "pso-1", 4.0F, 8),
					new ScopePart("Rifle (6x) Scope", "rifle", 6.0F, 9),
					new ScopePart("PKS-07 (7x) Scope", "pks-07", 7.0F, 10),
					new ScopePart("Rifle (8x) Scope", "rifle", 8.0F, 11),
					new ScopePart("Ballistic (12x) Scope", "ballistic", 12.0F, 12),
					new ScopePart("Ballistic (20x) Scope", "ballistic", 20.0F, 13) });
		barrel = new ItemMetadata("barrel", "barrel",
				new CustomizationPart[] {
					new CustomizationPart("Silencer", "-sln", 1),
					new CustomizationPart("Heavy Barrel", "-hbl", 2),
					new CustomizationPart("Rifled Barrel", "-rbl", 3),
					new CustomizationPart("Polygonal Barrel", "-pbl", 4) });
		attachment = new ItemAttachment("attachment", "attachment",
				new CustomizationPart[] {
					new CustomizationPart("Straight Pull Bolt", "-spb", 1),
					new CustomizationPart("Bipod", "-bpd", 2),
					new CustomizationPart("Foregrip", "-grp", 3),
					new CustomizationPart("M320", "-320", 4),
					new CustomizationPart("Strong Spiral Spring", "-sss", 5),
					new CustomizationPart("Improved Grip", "-img", 6),
					new CustomizationPart("Laser Pointer", "-ptr", 7) });
		
		ammoM320 = new GunCusItem("guncus:ammoM320", "GC 40x46mm SR Frag", "ammoM320").setMaxStackSize(8);
		
		if (enableExplosives) {
			mineBlock = new BlockMine();
			mineItem = new ItemMine();
			
			rpgm = new GunCusItem("guncus:rpgm", "GC PG-7VL Rocket", "gcrpgm");
			smawm = new GunCusItem("guncus:smawm", "GC HEDP Rocket", "gcsmawm");
			
			rpg = new ItemRPG("guncus:rpg", "GC RPG-7V2", "gcrpg", rpgm);
			smaw = new ItemRPG("guncus:smaw", "GC SMAW", "gcsmaw", smawm);
			
			GameRegistry.registerBlock(mineBlock, mineBlock.getUnlocalizedName());
		}
		
		if (enableOfficialGuns) {
			OfficialGuns.load();
		}
		
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			FMLCommonHandler.instance().bus().register(new TickHandler());
		}
		
		path = new File(event.getModConfigurationDirectory().getParentFile().getAbsolutePath() + "/GunCus");
		
		if (!path.exists()) {
		path.mkdirs();
			logger.info("Created the GunCus directory!");
			logger.info("You should install some gun packs now!");
		}
		
		loadGunPacks(path);
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		commonProxy.render();
		instance = this;
		LanguageRegistry.addName(quickKnife, "Quick Knife");
		EntityRegistry.registerModEntity(EntityBullet.class, "guncusbullet", 200, this, 500, 1, true);
		
		EntityRegistry.registerModEntity(EntityGrenade.class, "guncusat", 201, this, 500, 1, true);
		NetworkRegistry.INSTANCE.registerGuiHandler(this, this.guiHandler);
		
		GameRegistry.registerBlock(blockGun, blockGun.getUnlocalizedName());
		GameRegistry.registerBlock(blockAmmo, blockAmmo.getUnlocalizedName());
		GameRegistry.registerBlock(blockMag, blockMag.getUnlocalizedName());
		GameRegistry.registerBlock(blockBullet, blockBullet.getUnlocalizedName());
		GameRegistry.registerBlock(blockWeapon, blockWeapon.getUnlocalizedName());

		GameRegistry.addShapedRecipe(new ItemStack(part),
				new Object[] { "ABA", "BCB", "ABA",
					'A', new ItemStack(Items.iron_ingot),
					'B', new ItemStack(Items.redstone),
					'C', new ItemStack(Items.gold_ingot) });
		GameRegistry.addShapedRecipe(new ItemStack(magFill),
				new Object[] { "ABA", "BAB", "ABA",
					'B', new ItemStack(Items.iron_ingot),
					'A', new ItemStack(Items.redstone) });
		GameRegistry.addShapedRecipe(new ItemStack(blockAmmo),
				new Object[] { "BBB", "ABA", "BCB",
					'A', new ItemStack(part),
					'B', new ItemStack(Items.iron_ingot),
					'C', new ItemStack(Blocks.iron_block) });
		GameRegistry.addShapedRecipe(new ItemStack(blockBullet),
				new Object[] { "BAB", "AAA", "BCB",
					'A', new ItemStack(part),
					'B', new ItemStack(Items.iron_ingot),
					'C', new ItemStack(Blocks.iron_block) });
		GameRegistry.addShapedRecipe(new ItemStack(blockMag),
				new Object[] { "BAB", "ABA", "BCB",
					'A', new ItemStack(part),
					'B', new ItemStack(Items.iron_ingot),
					'C', new ItemStack(Blocks.iron_block) });
		GameRegistry.addShapedRecipe(new ItemStack(blockGun),
				new Object[] { "BAB", "AAA", "BAB",
					'A', new ItemStack(part),
					'B', new ItemStack(Items.iron_ingot) });
		GameRegistry.addShapedRecipe(new ItemStack(blockWeapon),
				new Object[] { "ABA", "ABA", "BCB",
					'A', new ItemStack(part),
					'B', new ItemStack(Items.iron_ingot),
					'C', new ItemStack(Blocks.iron_block) });
		GameRegistry.addShapedRecipe(
				new ItemStack(scope, 1, 0),
				new Object[] { " IG", "IRI",
					'G', new ItemStack(Blocks.glass_pane, 1),
					'I', new ItemStack(Items.iron_ingot, 1),
					'R', new ItemStack(Items.redstone, 1) });
		GameRegistry.addShapedRecipe(
				new ItemStack(scope, 1, 1),
				new Object[] { "IG ", "IRI",
					'G', new ItemStack(Blocks.glass_pane, 1),
					'I', new ItemStack(Items.iron_ingot, 1),
					'R', new ItemStack(Items.redstone, 1) });
		GameRegistry.addShapedRecipe(new ItemStack(scope, 1, 2),
				new Object[] { " I ", "GRG", "I I",
					'G', new ItemStack(Blocks.glass_pane, 1),
					'I', new ItemStack(Items.iron_ingot, 1),
					'R', new ItemStack(Items.redstone, 1) });
		GameRegistry.addShapedRecipe(new ItemStack(scope, 1, 3),
				new Object[] { "I I", "GRG", " I ",
					'G', new ItemStack(Blocks.glass_pane, 1),
					'I', new ItemStack(Items.iron_ingot, 1),
					'R', new ItemStack(Items.redstone, 1) });
		GameRegistry.addShapedRecipe(new ItemStack(scope, 1, 4),
				new Object[] { " I ", "GDG", "I I",
					'G', new ItemStack(Blocks.glass_pane, 1),
					'I', new ItemStack(Items.iron_ingot, 1),
					'D', new ItemStack(Items.diamond, 1) });
		GameRegistry.addShapedRecipe(new ItemStack(scope, 1, 5),
				new Object[] { "I I", "GDG", " I ",
					'G', new ItemStack(Blocks.glass_pane, 1),
					'I', new ItemStack(Items.iron_ingot, 1),
					'D', new ItemStack(Items.diamond, 1) });
		GameRegistry.addShapedRecipe(new ItemStack(scope, 1, 6),
				new Object[] { "I I", "GDG", "I I",
					'G', new ItemStack(Blocks.glass_pane, 1),
					'I', new ItemStack(Items.iron_ingot, 1),
					'D', new ItemStack(Items.diamond, 1) });
		GameRegistry.addShapedRecipe(new ItemStack(scope, 1, 7),
				new Object[] { "I I", "GDG", " II",
					'G', new ItemStack(Blocks.glass_pane, 1),
					'I', new ItemStack(Items.iron_ingot, 1),
					'D', new ItemStack(Items.diamond, 1) });
		GameRegistry.addShapedRecipe(new ItemStack(scope, 1, 8),
				new Object[] { "III", "GDG", "I I",
					'G', new ItemStack(Blocks.glass_pane, 1),
					'I', new ItemStack(Items.iron_ingot, 1),
					'D', new ItemStack(Items.diamond, 1) });
		GameRegistry.addShapedRecipe(new ItemStack(scope, 1, 9),
				new Object[] { "I I", "D8G", "I I",
					'G', new ItemStack(Blocks.glass_pane, 1),
					'I', new ItemStack(Items.iron_ingot, 1),
					'D', new ItemStack(Items.diamond, 1),
					'8', new ItemStack(scope, 1, 7) });
		GameRegistry.addShapedRecipe(
				new ItemStack(scope, 1, 10),
				new Object[] { "D9G", " I ",
					'G', new ItemStack(Blocks.glass_pane, 1),
					'I', new ItemStack(Items.iron_ingot, 1),
					'D', new ItemStack(Items.diamond, 1), Character.valueOf('9'), new ItemStack(scope, 1, 8) });
		GameRegistry.addShapedRecipe(new ItemStack(scope, 1, 11),
				new Object[] { "GIG", "DDD", "III",
					'G', new ItemStack(Blocks.glass_pane, 1),
					'I', new ItemStack(Items.iron_ingot, 1),
					'D', new ItemStack(Items.diamond, 1) });
		GameRegistry.addShapedRecipe(new ItemStack(scope, 1, 12),
				new Object[] { " I ", "DBG", "I I",
					'G', new ItemStack(Blocks.glass_pane, 1),
					'I', new ItemStack(Items.iron_ingot, 1),
					'D', new ItemStack(Items.diamond, 1), Character.valueOf('B'), new ItemStack(scope, 1, 11) });

		GameRegistry.addShapedRecipe(
				new ItemStack(ammoM320),
				new Object[] { "GI ", "IGI", " IG",
					'I', new ItemStack(Items.iron_ingot, 1),
					'G', new ItemStack(Items.gunpowder, 1) });
		GameRegistry.addShapedRecipe(new ItemStack(attachment, 1, 0),
				new Object[] { "I  ", " I ", "I I",
					'I', new ItemStack(Items.iron_ingot, 1) });
		GameRegistry.addShapedRecipe(new ItemStack(attachment, 1, 1),
				new Object[] { " I ", "I I", "I I",
					'I', new ItemStack(Items.iron_ingot, 1) });
		GameRegistry.addShapedRecipe(new ItemStack(attachment, 1, 2),
				new Object[] { "II ", " I ", " II",
					'I', new ItemStack(Items.iron_ingot, 1) });
		GameRegistry.addShapedRecipe(
				new ItemStack(attachment, 1, 3),
				new Object[] { " II", "IRR", "I I",
					'I', new ItemStack(Items.iron_ingot, 1),
					'R', new ItemStack(Items.redstone, 1) });
		GameRegistry.addShapedRecipe(
				new ItemStack(attachment, 1, 4),
				new Object[] { "I  ", "IGI", "  I",
					'I', new ItemStack(Items.iron_ingot, 1),
					'G', new ItemStack(Items.gold_ingot, 1) });
		GameRegistry.addShapedRecipe(
				new ItemStack(attachment, 1, 5),
				new Object[] { " L ", "LGL", " L ",
					'L', new ItemStack(Items.leather, 1),
					'G', new ItemStack(attachment, 1, 2) });
		GameRegistry.addShapedRecipe(
				new ItemStack(attachment, 1, 6),
				new Object[] { "II ", "RRI", "II ",
					'I', new ItemStack(Items.iron_ingot, 1),
					'R', new ItemStack(Items.redstone, 1) });

		GameRegistry.addShapedRecipe(
				new ItemStack(barrel, 1, 0),
				new Object[] { "SI ", "ISI", " IS",
					'I', new ItemStack(Items.iron_ingot, 1),
					'S', new ItemStack(Items.slime_ball, 1) });
		GameRegistry.addShapedRecipe(new ItemStack(barrel, 1, 1),
				new Object[] { "II ", "II ", "  I",
					'I', new ItemStack(Items.iron_ingot, 1) });
		GameRegistry.addShapedRecipe(
				new ItemStack(barrel, 1, 2),
				new Object[] { "GI ", "IGI", " IG",
					'I', new ItemStack(Items.iron_ingot, 1),
					'G', new ItemStack(Items.gold_ingot, 1) });
		GameRegistry.addShapedRecipe(
				new ItemStack(barrel, 1, 3),
				new Object[] { "II ", "IDI", " II",
					'I', new ItemStack(Items.iron_ingot, 1),
					'D', new ItemStack(Items.diamond, 1) });
		
		if (enableExplosives) {
			GameRegistry.addRecipe(
					new ItemStack(rpg, 1),
					new Object[] { "IIR", "IWR", "RRW",
						Character.valueOf('I'), new ItemStack(Items.iron_ingot),
						Character.valueOf('W'), new ItemStack(Blocks.planks),
						Character.valueOf('R'), new ItemStack(Items.redstone) });
			GameRegistry.addRecipe(
					new ItemStack(smaw, 1),
					new Object[] { "RI ", "IRI", " IR",
						Character.valueOf('I'), new ItemStack(Items.iron_ingot),
						Character.valueOf('R'), new ItemStack(Items.redstone) });
			
			GameRegistry.addRecipe(
					new ItemStack(rpgm, 2),
					new Object[] { "II ", "IG ", "  G",
						Character.valueOf('I'), new ItemStack(Items.iron_ingot),
						Character.valueOf('G'), new ItemStack(Items.gunpowder) });
			GameRegistry.addRecipe(
					new ItemStack(smawm, 2),
					new Object[] { "G  ", " GI", " II",
						Character.valueOf('I'), new ItemStack(Items.iron_ingot),
						Character.valueOf('G'), new ItemStack(Items.gunpowder) });
		}
	}
	
	public static void createExplosionServer(Entity entity, double x, double y, double z, float str) {
		if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			World world = entity.worldObj;
			world.createExplosion(entity, x, y, z, str, enableBlockDamage);
		}
	}
	
	public static void removeBlockServer(Entity entity, int x, int y, int z) {
		if ((FMLCommonHandler.instance().getEffectiveSide().isServer()) && (enableBlockDamage)) {
			World world = entity.worldObj;
			world.setBlockToAir(x, y, z);
		}
	}
	
	private void loadGunPacks(File path1) {
		ClassLoader classloader = MinecraftServer.class.getClassLoader();
		Method method = null;
		try {
			method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
			method.setAccessible(true);
		} catch (Exception exception) {
			logger.info("Failed to get the classloader; the textures wont work!");
			exception.printStackTrace();
		}
		
		defaultPack(path1.getAbsolutePath());
		
		for (File pack : path1.listFiles()) {
			if ((pack.isDirectory()) && (!pack.getName().equals("default"))) {
				bullets(pack.getAbsolutePath(), pack.getName());
				guns(pack.getAbsolutePath(), pack.getName());
				sounds(pack.getAbsolutePath());
				
				if (method != null) {
					try {
						method.invoke(classloader, new Object[] { pack.toURI().toURL() });
					} catch (Exception exception) {
						logger.info("Failed to add some textures to class path");
						exception.printStackTrace();
					}
				}
			}
		}
		
		if (this.loadedBullets.size() > 0) {
			System.out.println("");
			logger.info("The GunCus addon found the following bullet files:");
			
			for (int v1 = 0; v1 < loadedBullets.size(); v1++) {
				logger.info(loadedBullets.get(v1));
			}
		}
		
		if (this.loadedGuns.size() > 0) {
			System.out.println("");
			logger.info("The GunCus addon found the following gun files:");
			
			for (int v1 = 0; v1 < loadedGuns.size(); v1++) {
				logger.info(loadedGuns.get(v1));
			}
		}
		
		if ((loadedBullets.size() > 0) || (loadedGuns.size() > 0)) {
			logger.info("");
		}
	}
	
	private void defaultPack(String pathS) {
	File path1 = new File(pathS + "/default");
		if (!path1.exists()) {
			path1.mkdirs();
		}
		
		// default bullet configuration
		File bulletConfigFile = new File(path1.getAbsolutePath() + "/bullets/default.cfg");
		Configuration bulletConfig = new Configuration(bulletConfigFile);
		Property idProp = bulletConfig.get("general", "ID", 1010);
		idProp.comment = "Item ID of the bullet";
		
		Property bulProp = bulletConfig.get("general", "BulletID", 1);
		bulProp.comment = "Bullet ID of the bullet";
		
		Property ironProp = bulletConfig.get("general", "Iron", 1);
		ironProp.comment = "How much iron you need to craft this bullet type";
		
		Property sulProp = bulletConfig.get("general", "Gunpowder", 3);
		sulProp.comment = "How much gunpowder you need to craft this bullet type";
		
		Property stackProp = bulletConfig.get("general", "stackSize", 4);
		stackProp.comment = "How much bullets you get at a time by crafting this bullet type";
		
		Property nameProp = bulletConfig.get("general", "Name", "default");
		nameProp.comment = "Name of the bullet";
		
		Property iconProp = bulletConfig.get("general", "Icon", "");
		iconProp.comment = "Texture of this bullet. Leave blank for default";
		
		Property splitProp = bulletConfig.get("general", "Split", 1);
		splitProp.comment = "How much bullets being shot at a time.";
		
		Property sprayProp = bulletConfig.get("general", "Spray", 100);
		sprayProp.comment = "Maximum accuracy when using this bullet in percent. 100 = perfect accuracy. 30 = shotgun spray.";
		
		Property onImpactProp = bulletConfig.get("general", "Impact", "");
		onImpactProp.comment = "Semicolon separated list of effects on impact. Example: \"1:3;2:3;4:1.0;5:3.5;7:10\""
				+ "\n'X' and 'Y' are modifiers."
				+ "\n1:X = Poison for X seconds"
				+ "\n2:X = Nausea for X seconds"
				+ "\n3:X = Fire for X seconds"
				+ "\n4:X = Explosion of X Strength (3 is TnT, 7 is RPG, 4.5 is M320)"
				+ "\n5:X = Explosion without Block Damage of X Strength"
				+ "\n6:X = Heal of X points"
				+ "\n7:X = Blindness for X seconds"
				+ "\n8:X = Instant damage (harm) of X damages"
				+ "\n9:X:Y = weaken +Y * 20% damage increase for X seconds.";
		
		Property gravityProp = bulletConfig.get("general", "GravityModifier", 1.0D);
		gravityProp.comment = "Modifies the applied gravity of a bullet.\nApplied Gravity is Gravity x GravityModifier";
		
		Property damageProp = bulletConfig.get("general", "Damage Modifier", 1.0D);
		damageProp.comment = "Applied damage is Gun Damage * Damage Modifier";
		bulletConfig.save();
		
		// default gun configuration
		File gunConfigFile = new File(path1 + "/guns/default.cfg");
		Configuration gunConfig = new Configuration(gunConfigFile);
		
		Property idMagProp = gunConfig.get("general", "Mag ID", 1000);
		idMagProp.comment = "ID of the magazines. Should be 1 lower than the gun's ID";
		
		Property idProp2 = gunConfig.get("general", "ID", 1001);
		idProp2.comment = "ID of the gun";
		
		Property shootTypeProp = gunConfig.get("general", "Shoot", 2);
		shootTypeProp.comment = "0 = Single Shooting | 1 = Burst Shooting | 2 = Auto Shooting";
		
		Property delayProp = gunConfig.get("general", "Delay", 3);
		delayProp.comment = "Delay between shots of the gun (in ticks)";
		
		Property magProp = gunConfig.get("general", "Magsize", 1);
		magProp.comment = "Size of the magazines";
		
		Property magIngotProp = gunConfig.get("general", "Mag Ingots", 1);
		magIngotProp.comment = "Number of iron ingots a mag needs to be crafted";
		
		Property ingotProp = gunConfig.get("general", "Iron Ingots", 1);
		ingotProp.comment = "Number of iron ingots this gun needs to be crafted";
		
		Property redProp = gunConfig.get("general", "Redstone", 1);
		redProp.comment = "Number of redstone this gun needs to be crafted";
		
		Property nameProp2 = gunConfig.get("general", "Name", "default");
		nameProp2.comment = "Name of the gun";
		
		Property bulletProp = gunConfig.get("general", "Bullets", "1");
		bulletProp.comment = "Semicolon separated list of bullet IDs for this gun."
				+ "\nYou may type more than 1 bullet ID if this gun doesn't use magazines!";
		
		Property usingMagProp = gunConfig.get("general", "UsingMags", true);
		usingMagProp.comment = "Does this gun use magazines? False, if the gun is for example a shotgun.";
		
		Property iconProp2 = gunConfig.get("general", "Texture", "");
		iconProp2.comment = "The texture of the gun. Leave blanc for default";
		
		Property recProp = gunConfig.get("general", "RecoilModifier", 1.0D);
		recProp.comment = "This modifies the recoil. | Recoil x RecoilModifier = Applied Recoil";
		
		Property sound_normalP = gunConfig.get("general", "NormalSound", "Sound_DERP2");
		sound_normalP.comment = "The sound being used when shooting the gun. Only .ogg or .wav!!! Leave blanc for default";
		
		Property sound_silencedP = gunConfig.get("general", "SilencedSound", "");
		sound_silencedP.comment = "The sound being used when shooting the gun that has a silencer. Only .ogg or .wav!!! Leave blanc for default";
		
		Property sndProp = gunConfig.get("general", "SoundModifier", 1.0D);
		sndProp.comment = "Modifies the sound volume (does not affect the volume of silenced shots).\nDefault Sound Volume x SoundModifier = Used Sound Volume";
		
		Property extra1Prop = gunConfig.get("general", "Attachments", "1;3;2;6");
		extra1Prop.comment = "Semicolon separated list of attachments valid on this gun."
				+ "\n1 = Straight Pull Bolt | 2 = Bipod | 3 = Foregrip | 4 = M320 | 5 = Strong Spiral Spring"
				+ "\n6 = Improved Grip | 7 = Laser Pointer.";
		
		Property bar1Prop = gunConfig.get("general", "Barrels", "1;2;3");
		bar1Prop.comment = "Semicolon separated list of barrels valid on this gun."
				+ "\n1 = Silencer | 2 = Heavy Barrel | 3 = Rifled Barrel | 4 = Polygonal Barrel.";
		
		Property scopesProp = gunConfig.get("general", "Scopes", "1;2;3;4;5;6;7;8;9;10;11;12;13");
		scopesProp.comment = "Semicolon separated list of scopes valid on this gun."
				+ "\n1 = Reflex | 2 = Kobra | 3 = Holographic | 4 = PKA-S | 5 = M145"
				+ "\n6 = PK-A | 7 = ACOG | 8 = PSO-1 | 9 = Rifle 6x | 10 = PKS-07"
				+ "\n11 = Rifle 8x | 12 = Ballistic 12x | 13 = Ballistic 20x.";
		
		Property defaultZoomProp = gunConfig.get("general", "Zoom", 1.0D);
		defaultZoomProp.comment = "Zoom factor without any scope. Default 1.0";
		
		Property damageProp2 = gunConfig.get("general", "Damage", 6);
		damageProp2.comment = "Damage dealt (1 is half a heart)";
		gunConfig.save();
		
		File textures = new File(path1.getAbsolutePath() + "/assets/minecraft/textures");
		if (!textures.exists()) {
			textures.mkdirs();
		}
		File items = new File(textures.getAbsolutePath() + "/items");
		if (!items.exists()) {
			items.mkdirs();
		}
		File blocks = new File(textures.getAbsolutePath() + "/blocks");
		if (!blocks.exists()) {
			blocks.mkdirs();
		}
		File sounds = new File(path1.getAbsolutePath() + "/assets/minecraft/sound");
		if (!sounds.exists()) {
			sounds.mkdirs();
		}
	}
	
	private void bullets(String packPath, String pack) {
		File file = new File(packPath + "/bullets");
		file.mkdirs();
		File[] filesFound = file.listFiles();
		ArrayList files = new ArrayList();
		
		for (int v1 = 0; v1 < filesFound.length; v1++) {
			if (filesFound[v1].getAbsolutePath().endsWith(".cfg")) {
				files.add(filesFound[v1]);
			}
		}
		
		for (int v1 = 0; v1 < files.size(); v1++) {
	Configuration config1 = new Configuration((File) files.get(v1));
			config1.load();
			
			Property idProp = config1.get("general", "ID", 1010);
			idProp.comment = "Item ID of the bullet";
			
			Property bulProp = config1.get("general", "Bullet ID", 1);
			bulProp.comment = "Bullet ID of the bullet";
			
			Property ironProp = config1.get("general", "Iron", 1);
			ironProp.comment = "How much iron you need to craft this bullet type";
			
			Property sulProp = config1.get("general", "Gunpowder", 3);
			sulProp.comment = "How much gunpowder you need to craft this bullet type";
			
			Property stackProp = config1.get("general", "stackSize", 4);
			stackProp.comment = "How much bullets you get at a time by crafting this bullet type";
			
			Property nameProp = config1.get("general", "Name", "default");
			nameProp.comment = "Name of the bullet";
			
			Property iconProp = config1.get("general", "Icon", "");
			iconProp.comment = "Texture of this bullet. Leave blanc for default";
			
			Property splitProp = config1.get("general", "Split", 1);
			splitProp.comment = "How much bullets being shot at a time.";
			
			Property sprayProp = config1.get("general", "Spray", 100);
			sprayProp.comment = "Maximum accuracy by using this bullet. 100 = 100% accuracy. 30 = shotgun spray.";

	Property onImpactProp = config1.get("general", "Impact", "");
			onImpactProp.comment = "Semicolon separated list of effects on impact. Example: \"1:3;2:3;4:1.0;5:3.5;7:10\""
					+ "\n'X' and 'Y' are modifiers."
					+ "\n1:X = Poison for X seconds"
					+ "\n2:X = Nausea for X seconds"
					+ "\n3:X = Fire for X seconds"
					+ "\n4:X = Explosion of X Strength (3 is TnT, 7 is RPG, 4.5 is M320)"
					+ "\n5:X = Explosion without Block Damage of X Strength"
					+ "\n6:X = Heal of X points"
					+ "\n7:X = Blindness for X seconds"
					+ "\n8:X = Instant damage (harm) of X damages"
					+ "\n9:X:Y = weaken +Y * 20% damage increase for X seconds.";
			
			Property gravityProp = config1.get("general", "GravityModifier", 1.0D);
			gravityProp.comment = "Modifies the applied gravity of a bullet.\nApplied Gravity is Gravity x GravityModifier";
			
			Property damageProp = config1.get("general", "Damage Modifier", 1.0D);
			damageProp.comment = "Applied damage is Gun Damage * Damage Modifier";
			
			float damage = (float) damageProp.getDouble(1.0D);
			int bul = bulProp.getInt(1);
			int iron = ironProp.getInt(1);
			int sul = sulProp.getInt(3);
			int stack = stackProp.getInt(4);
			String name = nameProp.getString();
			String icon = iconProp.getString();
			String[] effects = onImpactProp.getString().split(";");
			int split = splitProp.getInt(1);
			int spray = sprayProp.getInt(100);
			double gravity = gravityProp.getDouble(1.0D);
			
			if (!ItemBullet.bulletsList.containsKey(pack)) {
				ItemBullet.bulletsList.put(pack, new ArrayList());
			}
			
			if ( (name != null)
			  && (bul > 0)
			  && (iron >= 0)
			  && (sul >= 0)
			  && ((iron > 0) || (sul > 0))
			  && (stack > 0)
			  && ( ( (ItemBullet.bulletsList.get(pack).size() > bul) && (ItemBullet.bulletsList.get(pack).get(bul) == null) )
				|| (ItemBullet.bulletsList.get(pack).size() <= bul) ) ) {
				if (icon.equals("") || icon.equals(" ")) {
					icon = "guncus:bullet";
				} else {
					icon = "minecraft:bullets/" + icon;
				}
				
				ItemBullet bullet = new ItemBullet(name, bul, sul, iron, stack, pack, icon, damage).setSplit(split).setGravityModifier(gravity).setSpray(spray);
				
				for (String effect : effects) {
					try {
						if (effect.contains(":")) {
							String[] effectParameters = effect.split(":");
							
							if (effectParameters.length == 2) {
								bullet.addEffect(Integer.parseInt(effectParameters[0]), Float.parseFloat(effectParameters[1]), 0);
							} else if (effectParameters.length == 3) {
								bullet.addEffect(Integer.parseInt(effectParameters[0]), Float.parseFloat(effectParameters[1]), Integer.parseInt(effectParameters[2]));
							}
						}
					} catch (Exception exception) {
						logger.info("[" + pack + "] [" + name + "] Exception while trying to add effect \"" + effect + "\" to bullet");
						exception.printStackTrace();
					}
				}
				
				loadedBullets.add(" - " + name + " (Bullet ID:" + bul + ", Pack:" + pack + ")");
			} else {
				logger.info("[" + pack + "] Something went wrong while initializing the bullet \"" + name + "\"! Ignoring this bullet!");
			}
			
			config1.save();
		}
	}
	
	private void guns(String packPath, String path1) {
		File file = new File(packPath + "/guns");
		file.mkdirs();
		File[] filesFound = file.listFiles();
		ArrayList files = new ArrayList();
		
		for (int v1 = 0; v1 < filesFound.length; v1++) {
			if (filesFound[v1].getAbsolutePath().endsWith(".cfg")) {
				files.add(filesFound[v1]);
			}
		}

		for (int v1 = 0; v1 < files.size(); v1++) {
			Configuration gunConfig = new Configuration((File) files.get(v1));
			gunConfig.load();
			
			int bullets = -1;
			
			Property idMagProp = gunConfig.get("general", "Mag ID", 1000);
			idMagProp.comment = "ID of the magazines. Should be 1 lower than the gun's ID";
			
			Property idProp = gunConfig.get("general", "ID", 1001);
			idProp.comment = "ID of the gun";
			
			Property shootTypeProp = gunConfig.get("general", "Shoot", 2);
			shootTypeProp.comment = "0 = Single Shooting | 1 = Burst Shooting | 2 = Auto Shooting";
			
			Property delayProp = gunConfig.get("general", "Delay", 3);
			delayProp.comment = "Delay between shots of the gun (in ticks)";
			
			Property magProp = gunConfig.get("general", "Magsize", 1);
			magProp.comment = "Size of the magazines";
			
			Property magIngotProp = gunConfig.get("general", "Mag Ingots", 1);
			magIngotProp.comment = "Number of iron ingots a mag needs to be crafted";
			
			Property ingotProp = gunConfig.get("general", "Iron Ingots", 1);
			ingotProp.comment = "Number of iron ingots this gun needs to be crafted";
			
			Property redProp = gunConfig.get("general", "Redstone", 1);
			redProp.comment = "Number of redstone this gun needs to be crafted";
			
			Property nameProp = gunConfig.get("general", "Name", "default");
			nameProp.comment = "Name of the gun";
			
			Property bulletProp = gunConfig.get("general", "Bullets", "1");
			bulletProp.comment = "Semicolon separated list of bullet IDs for this gun."
					+ "\nYou may type more than 1 bullet ID if this gun doesn't use magazines!";
			
			Property usingMagProp = gunConfig.get("general", "UsingMags", true);
			usingMagProp.comment = "Does this gun use magazines? False, if the gun is for example a shotgun.";
			
			Property iconProp = gunConfig.get("general", "Texture", "");
			iconProp.comment = "Texture of the gun. Leave blanc for default";
			
			Property recProp = gunConfig.get("general", "RecoilModifier", 1.0D);
			recProp.comment = "This modifies the recoil. | Recoil x RecoilModifier = Applied Recoil";
			
			Property sound_normalP = gunConfig.get("general", "NormalSound", "Sound_DERP2");
			sound_normalP.comment = "Sound played when shooting. Only .ogg or .wav!!! Leave blanc for default";
			
			Property sound_silencedP = gunConfig.get("general", "SilencedSound", "");
			sound_silencedP.comment = "Sound played when shooting while gun has a silencer. Only .ogg or .wav!!! Leave blanc for default";
			
			Property sndProp = gunConfig.get("general", "SoundModifier", 1.0D);
			sndProp.comment = "Modifies the sound volume (does not affect the volume of silenced shots). | Default Sound Volume x SoundModifier = Used Sound Volume";
			
			Property extra1Prop = gunConfig.get("general", "Attachments", "1;3;2;6");
			extra1Prop.comment = "Semicolon separated list of attachments valid on this gun."
					+ "\n1 = Straight Pull Bolt | 2 = Bipod | 3 = Foregrip | 4 = M320 | 5 = Strong Spiral Spring"
					+ "\n6 = Improved Grip | 7 = Laser Pointer.";
			
			Property bar1Prop = gunConfig.get("general", "Barrels", "1;2;3");
			bar1Prop.comment = "Semicolon separated list of barrels valid on this gun."
					+ "\n1 = Silencer | 2 = Heavy Barrel | 3 = Rifled Barrel | 4 = Polygonal Barrel.";
			
			Property scopesProp = gunConfig.get("general", "Scopes", "1;2;3;4;5;6;7;8;9;10;11;12;13");
			scopesProp.comment = "Semicolon separated list of scopes valid on this gun."
					+ "\n1 = Reflex | 2 = Kobra | 3 = Holographic | 4 = PKA-S | 5 = M145"
					+ "\n6 = PK-A | 7 = ACOG | 8 = PSO-1 | 9 = Rifle 6x | 10 = PKS-07"
					+ "\n11 = Rifle 8x | 12 = Ballistic 12x | 13 = Ballistic 20x.";
			
			Property defaultZoomProp = gunConfig.get("general", "Zoom", 1.0D);
			defaultZoomProp.comment = "Zoom factor without any scope. Default 1.0";
			
			Property damageProp = gunConfig.get("general", "Damage", 6);
			damageProp.comment = "Damage dealth (1 is half a heart)";
			
			int id = idProp.getInt(-1);
			int shootType = shootTypeProp.getInt(2);
			int delay = delayProp.getInt(3);
			int magSize = magProp.getInt(1);
			int magId = idMagProp.getInt(1000);
			String[] bullets2 = bulletProp.getString().split(";");
			int ingotsMag = magIngotProp.getInt(1);
			int ingots = ingotProp.getInt(1);
			int red = redProp.getInt(1);
			String name = nameProp.getString();
			String icon2 = iconProp.getString();
			double recModify = recProp.getDouble(1.0D);
			double sndModify = sndProp.getDouble(1.0D);
			String snormal = sound_normalP.getString();
			String ssln = sound_silencedP.getString();
			String[] attach1 = extra1Prop.getString().split(";");
			String[] bar1 = bar1Prop.getString().split(";");
			String[] scopes1 = scopesProp.getString().split(";");
			
			float zoom = (float) defaultZoomProp.getDouble(1.1D);
			boolean usingMag = usingMagProp.getBoolean(true);
			
			int damage = damageProp.getInt(6);
			
			if (sndModify < 1.E-005D) {
				sndModify = 1.E-005D;
			}
			
			if (sndModify > 20.0D) {
				sndModify = 20.0D;
			}
			
			boolean errored = false;
			String pack = new File(packPath).getName();
			int[] bulletsArray;
			if (!usingMag) {
				bullets = -1;
				bulletsArray = new int[bullets2.length];
				magId = -1;
				for (int v2 = 0; v2 < bullets2.length; v2++) {
					try {
						bulletsArray[v2] = Integer.parseInt(bullets2[v2]);
					} catch (Exception e) {
						logger.info("[" + pack + "] Something went wrong while initializing bullets of the gun \"" + name
								+ "\"! Caused by: \"" + bullets2[v2] + "\"!");
						errored = true;
					}
				}
			} else {
				bulletsArray = new int[0];
				try {
					bullets = Integer.parseInt(bullets2[0]);
				} catch (Exception e) {
					logger.info("[" + pack + "] Something went wrong while initializing bullets of the gun \"" + name
							+ "\"! Caused by: \"" + bullets2[0] + "\"!");
					errored = true;
				}
			}
			
			if ((!errored)
					&& (name != null)
					&& (icon2 != null)
					&& (shootType >= 0)
					&& (shootType < 3)
					&& (delay >= 0)
					&& (magSize >= 1)
					&& (magId != id)
					&& (ingots > 0)
					&& (ingotsMag >= 0)
					&& (red >= 0)
					&& ((ingots > 0) || (red > 0))
					&& (usingMag
							|| ((!usingMag) && (bulletsArray.length >= 1)
									&& (ItemBullet.bulletsList.get(pack) != null)
									&& (ItemBullet.bulletsList.get(pack).size() > bullets)
									&& (ItemBullet.bulletsList.get(pack).get(bullets) != null) ) ) ) {
				boolean def = false;
				String icon;
				if ((icon2.equals("")) || (icon2.equals(" "))) {
					logger.info("[" + pack + "] The texture of the gun \"" + name + "\" is missing!");
					icon = "guncus:gun_default/";
					def = true;
				} else {
					icon = "minecraft:gun_" + icon2 + "/";
				}
				try {
					int[] attach;
					if ((attach1.length > 0) && (!attach1[0].replace(" ", "").equals(""))) {
						attach = new int[attach1.length];
						for (int v2 = 0; v2 < attach1.length; v2++) {
							attach[v2] = Integer.parseInt(attach1[v2]);
						}
					} else {
						attach = new int[0];
					}
					int[] bar;
					if ((bar1.length > 0) && (!bar1[0].replace(" ", "").equals(""))) {
						bar = new int[bar1.length];
						for (int v2 = 0; v2 < bar1.length; v2++) {
							bar[v2] = Integer.parseInt(bar1[v2]);
						}
					} else {
						bar = new int[0];
					}
					int[] scopes;
					if ((scopes1.length > 0) && (!scopes1[0].replace(" ", "").equals(""))) {
						scopes = new int[scopes1.length];
						for (int v2 = 0; v2 < scopes1.length; v2++) {
							scopes[v2] = Integer.parseInt(scopes1[v2]);
						}
					} else {
						scopes = new int[0];
					}
					
					ItemGun gun = new ItemGun(damage, shootType, delay, name, icon, magSize, magId,
							bullets, ingotsMag, ingots, red, pack, false, attach, bar, scopes, !usingMag, bulletsArray)
							.setRecoilModifier(recModify).setSoundModifier(sndModify).defaultTexture(def).setZoom(zoom);
					
					if (!snormal.trim().isEmpty()) {
						gun.setNormalSound("minecraft:" + snormal);
					}
					
					if (!ssln.trim().isEmpty()) {
						gun.setSilencedSound("minecraft:" + ssln);
					}
					guns.add(gun);
				} catch (Exception exception) {
					logger.info("[" + pack + "] Error while trying to add the gun \"" + name + "\": ! Pls check the attachments, barrels and scopes of it!");
					exception.printStackTrace();
				}
				
				loadedGuns.add(" - " + name + " (ID:" + id + ", Pack:" + pack + ")");
			} else if ( (!ItemBullet.bulletsList.containsKey(pack))
					|| (ItemBullet.bulletsList.get(pack).size() <= bullets)
					|| (ItemBullet.bulletsList.get(pack).get(bullets) == null) ) {
				logger.info("[" + pack + "] The bullets of the gun \"" + name + "\" do not exist (Bullet ID:" + bullets + ")! Ignoring this gun!");
			} else {
				logger.info("[" + pack + "] Something went wrong while initializing the gun \"" + name + "\"! Ignoring this gun!");
			}
			gunConfig.save();
		}
	}
	
	private void sounds(String path1) {
		File file = new File(path1 + "/assets/minecraft/sound");
		
		if (file.exists()) {
			for (File f : file.listFiles()) {
				if ((f.getName().endsWith(".ogg")) || ((f.getName().endsWith(".wav")) && (!f.getName().contains(" ")))) {
					// FIXME: GunCusSound.addSound(f.getName());
				}
			}
		} else {
			logger.info("Could not load sounds!");
		}
	}
}
