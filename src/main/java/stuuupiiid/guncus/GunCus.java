package stuuupiiid.guncus;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.command.ICommandSender;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
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
import stuuupiiid.guncus.entity.EntityRocket;
import stuuupiiid.guncus.event.TickHandler;
import stuuupiiid.guncus.gui.GuiHandler;
import stuuupiiid.guncus.item.GunCusItem;
import stuuupiiid.guncus.item.ItemAttachment;
import stuuupiiid.guncus.item.ItemBullet;
import stuuupiiid.guncus.item.ItemGun;
import stuuupiiid.guncus.item.ItemKnife;
import stuuupiiid.guncus.item.ItemMagFiller;
import stuuupiiid.guncus.item.ItemMetadata;
import stuuupiiid.guncus.item.ItemMine;
import stuuupiiid.guncus.item.ItemRPG;
import stuuupiiid.guncus.item.ItemScope;
import stuuupiiid.guncus.network.PacketHandler;

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
	
	public static String clientGUI_actualGunName = null;
	
	public static Field cameraZoom = null;
	
	public static HashMap<String, ItemGun> guns = new HashMap<String, ItemGun>();
	public static Set<String> gunNames = null; 
	
	@Mod.Instance(GunCus.MODID)
	public static GunCus instance;
	
	public GuiHandler guiHandler = null;
	private boolean enableExplosives;
	private boolean enableOfficialGuns;
	public static Item quickKnife;
	public static CreativeTabs creativeTabModifications;
	public static CreativeTabs creativeTabBullets;
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
	public static boolean logging_enableNetwork = false;
	
	public static Logger logger;
	
	@Mod.EventHandler
	public void onFMLPreInitialization(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		instance = this;
		
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			GunCusResourceLoader myResourceLoader = new GunCusResourceLoader();
			Field field = null;
			try {
				try {
					field = Minecraft.class.getDeclaredField("defaultResourcePacks");
				} catch (Exception exception) {
					field = Minecraft.class.getDeclaredField("field_110449_ao");
				}
				field.setAccessible(true);
				((List) field.get(Minecraft.getMinecraft())).add(myResourceLoader);
			} catch (Exception exception) {
				logger.info("Failed to get the classloader; the textures wont work!");
				exception.printStackTrace();
			}
			
			try {
				try {
					cameraZoom = EntityRenderer.class.getDeclaredField("cameraZoom");
				} catch (Exception exception) {
					cameraZoom = EntityRenderer.class.getDeclaredField("field_78503_V");
				}
				cameraZoom.setAccessible(true);
			} catch (Exception exception) {
				logger.info("Failed to get the cameraZoom field; scopes wont work!");
				exception.printStackTrace();
			}
		}
		
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		creativeTabModifications = new GunCusCreativeTab("GunCus.modifications", null);
		creativeTabBullets = new GunCusCreativeTab("GunCus.bullets", null);
		quickKnife = new ItemKnife();
		
		enableBlockDamage = config.get("Gun Customization", "enableBlockDamage", true).getBoolean(true);
		enableExplosives = config.get("Gun Customization", "enableExplosives", true).getBoolean(true);
		enableOfficialGuns = config.get("Gun Customization", "enableOfficialGuns", true).getBoolean(true);
		logging_enableNetwork = config.get("Gun Customization", "enableNetworkLogs", false).getBoolean(false);
		
		config.save();
		
		blockWeapon = new BlockWeapon();
		blockMag = new BlockMag();
		blockBullet = new BlockBullet();
		blockAmmo = new BlockAmmo();
		blockGun = new BlockGun();
		
		magFill = new ItemMagFiller();
		part = new GunCusItem("guncus:boxpart", "boxpart");
		
		scope = new ItemScope("scope", "scope",
				new ScopePart[] {
					new ScopePart("reflex", 1.0F, 1),
					new ScopePart("kobra", 1.0F, 2),
					new ScopePart("holographic", 1.0F, 3),
					new ScopePart("pka-s", 1.0F, 4),
					new ScopePart("m145", 3.4F, 5),
					new ScopePart("pk-a", 3.4F, 6),
					new ScopePart("acog", 4.0F, 7),
					new ScopePart("pso-1", 4.0F, 8),
					new ScopePart("rifle", 6.0F, 9),
					new ScopePart("pks-07", 7.0F, 10),
					new ScopePart("rifle", 8.0F, 11),
					new ScopePart("ballistic", 12.0F, 12),
					new ScopePart("ballistic", 20.0F, 13) });
		barrel = new ItemMetadata("barrel", "barrel",
				new CustomizationPart[] {
					new CustomizationPart("sln", 1),
					new CustomizationPart("hbl", 2),
					new CustomizationPart("rbl", 3),
					new CustomizationPart("pbl", 4) });
		attachment = new ItemAttachment("attachment", "attachment",
				new CustomizationPart[] {
					new CustomizationPart("spb", 1),
					new CustomizationPart("bpd", 2),
					new CustomizationPart("grp", 3),
					new CustomizationPart("320", 4),
					new CustomizationPart("sss", 5),
					new CustomizationPart("img", 6),
					new CustomizationPart("ptr", 7) });
		
		ammoM320 = new GunCusItem("guncus:ammo_M320", "ammo_M320").setMaxStackSize(8);
		
		if (enableExplosives) {
			mineBlock = new BlockMine();
			GameRegistry.registerBlock(mineBlock, mineBlock.getUnlocalizedName());
			
			mineItem = new ItemMine();
			
			rpgm = new GunCusItem("guncus:explosive/ammo_rpg", "explosive.ammo_rpg");
			smawm = new GunCusItem("guncus:explosive/ammo_smaw", "explosive.ammo_smaw");
			
			rpg = new ItemRPG("guncus:explosive/rpg", "explosive.rpg", rpgm);
			smaw = new ItemRPG("guncus:explosive/smaw", "explosive.smaw", smawm);
			
		}
		
		if (enableOfficialGuns) {
			OfficialGuns.load();
		}
		
		TickHandler tickHandler = new TickHandler();
		FMLCommonHandler.instance().bus().register(tickHandler);
		MinecraftForge.EVENT_BUS.register(tickHandler);
		
		File path = new File(event.getModConfigurationDirectory().getParentFile().getAbsolutePath() + "/GunCus");
		
		if (!path.exists()) {
			path.mkdirs();
			logger.info("Created the GunCus directory!");
			logger.info("You should install some gun packs now!");
		}
		
		loadGunPacks(path);
	}
	
	@Mod.EventHandler
	public void onFMLInitialization(FMLInitializationEvent event) {
		PacketHandler.init();
		
		commonProxy.initRenderingRegistry();
		
		EntityRegistry.registerModEntity(EntityBullet.class, "guncus.bullet", 200, this, 80, 1, true);
		EntityRegistry.registerModEntity(EntityGrenade.class, "guncus.grenade", 201, this, 80, 1, true);
		EntityRegistry.registerModEntity(EntityRocket.class, "guncus.rocket", 202, this, 100, 1, true);
		
		guiHandler = new GuiHandler();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, guiHandler);
		
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
	
	private void loadGunPacks(File fileGunCus) {
		createTemplatePack(fileGunCus);
		
		for (File filePack : fileGunCus.listFiles()) {
			if (filePack.isDirectory() && (!filePack.getName().equalsIgnoreCase("template"))) {
				logger.info("Loading pack " + filePack.getName());
				loadBullets(filePack.getAbsolutePath(), filePack.getName());
				loadGuns(filePack.getAbsolutePath(), filePack.getName());
			}
		}
		gunNames = guns.keySet();
	}
	
	private void createTemplatePack(File fileGunCus) {
		File fileTemplate = new File(fileGunCus, "/template");
		if (!fileTemplate.exists()) {
			fileTemplate.mkdirs();
		}
		
		// create template bullet from default configuration
		File fileBulletConfig = new File(fileTemplate.getAbsolutePath() + "/bullets/default.cfg");
		loadBullet("template", fileBulletConfig);
		
		// create template gun from default configuration
		File fileGunConfig = new File(fileTemplate + "/guns/default.cfg");
		loadGun("template", fileGunConfig);
		
		File fileTextures = new File(fileTemplate.getAbsolutePath() + "/textures");
		if (!fileTextures.exists()) {
			fileTextures.mkdirs();
		}
		File fileItems = new File(fileTextures.getAbsolutePath() + "/items");
		if (!fileItems.exists()) {
			fileItems.mkdirs();
		}
		
		File fileLang = new File(fileTemplate.getAbsolutePath() + "/lang");
		if (!fileLang.exists()) {
			fileLang.mkdirs();
		}
		File fileLangENUS = new File(fileTemplate + "/lang/en_US.lang");
		if (!fileLangENUS.exists()) {
			try {
				FileUtils.writeStringToFile(fileLangENUS, "item._YourPackName_.bullet._BulletUnlocalizedName_.name=.45 ACP"
						+ "\n"
						+ "\nitemGroup._YourPackName_._GunUnlocalizedName_=AK-47"
						+ "\nitem._YourPackName_._GunUnlocalizedName_.name=AK-47"
						+ "\nitem._YourPackName_._GunUnlocalizedName_.magazine.name=AK-47 magazine"
						+ "\n", "utf-8");
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
		
		File sounds = new File(fileTemplate.getAbsolutePath() + "/sounds");
		if (!sounds.exists()) {
			sounds.mkdirs();
		}
		File fileSoundsJSON = new File(fileTemplate + "/sounds.json");
		if (!fileSoundsJSON.exists()) {
			try {
				FileUtils.writeStringToFile(fileSoundsJSON, "{"
						+ "\n   \"_EventNameWithoutNumbers_\": {\"category\": \"master\", \"sounds\": [{\"name\": \"_OGGsoundFileNameWithoutNumbers_\", \"stream\": false}]},"
						+ "\n   \"akfortyseven\": {\"category\": \"master\", \"sounds\": [{\"name\": \"akfortyseven\", \"stream\": false}]},"
						+ "\n   \"akfortysevensilencer\": {\"category\": \"master\", \"sounds\": [{\"name\": \"akfortysevensilencer\", \"stream\": false}]}"
						+ "\n}"
						+ "\n", "utf-8");
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
	}
	
	private void loadBullets(String packPath, String pack) {
		File fileBullets = new File(packPath + "/bullets");
		fileBullets.mkdirs();
		File[] filesFound = fileBullets.listFiles();
		ArrayList<File> files = new ArrayList();
		
		for (File fileFound : filesFound) {
			if (fileFound.getAbsolutePath().endsWith(".cfg")) {
				files.add(fileFound);
			}
		}
		
		for (File file : files) {
			loadBullet(pack, file);
		}
	}
	
	private void loadBullet(String pack, File file) {
		Configuration configBullet = new Configuration(file);
		configBullet.load();
		
		boolean hasError = false; 
		int bulletId = configBullet.get("general", "Bullet ID", 1, "Bullet ID of the bullet (guns refer to bullet by this ID)").getInt();
		if (bulletId <= 0) {
			hasError = true;
			logger.error("[" + pack + "] Bullet " + file.getName() + " has invalid bullet ID. Expecting a strictly positive integer, found " + bulletId + ".");
		}
		if (ItemBullet.bullets.get(pack) != null && ItemBullet.bullets.get(pack).size() > bulletId && ItemBullet.bullets.get(pack).get(bulletId) != null) {
			hasError = true;
			logger.error("[" + pack + "] Bullet " + file.getName() + " defines a duplicated bullet ID. Each bullet needs a unique ID for their pack.");
		}
		
		int ironIngot = configBullet.get("general", "Iron", 1, "How much iron ingots you need to craft this bullet type").getInt();
		if (ironIngot < 0) {
			hasError = true;
			logger.error("[" + pack + "] Bullet " + file.getName() + " has invalid Iron ingots count. Expecting a positive integer, found " + ironIngot + ".");
		}
		
		int gunpowder = configBullet.get("general", "Gunpowder", 3, "How much gunpowder you need to craft this bullet type").getInt();
		if (gunpowder < 0) {
			hasError = true;
			logger.error("[" + pack + "] Bullet " + file.getName() + " has invalid Gunpowder count. Expecting a positive integer, found " + gunpowder + ".");
		}
		if (ironIngot == 0 && gunpowder == 0) {
			hasError = true;
			logger.error("[" + pack + "] Bullet " + file.getName() + " has no cost, need at least 1 Iron ingot or 1 Gunpowder.");
		}
		
		int stackOnCreate = configBullet.get("general", "stackSize", 4, "How much bullets you get at a time by crafting this bullet type").getInt();
		if (stackOnCreate <= 0) {
			hasError = true;
			logger.error("[" + pack + "] Bullet " + file.getName() + " has invalid stackSize. Expecting a strictly positive integer, found " + stackOnCreate + ".");
		}
		
		String name = configBullet.get("general", "Name", "default", "Name of the bullet").getString();;
		if (name == null || name.isEmpty()) {
			hasError = true;
			logger.error("[" + pack + "] Bullet " + file.getName() + " has invalid/missing name.");
		}
		
		String iconName = configBullet.get("general", "Icon", "", "Texture of this bullet. Leave blanc for default").getString();
		
		int split = configBullet.get("general", "Split", 1, "How much bullets are being shot at a time (only one ammo is consumed)").getInt();
		if (split <= 0) {
			hasError = true;
			logger.error("[" + pack + "] Bullet " + file.getName() + " has invalid Split. Expecting a strictly positive integer, found " + split + ".");
		}
		
		int spray = configBullet.get("general", "Spray", 100, "Maximum accuracy by using this bullet in %. 100 is perfect accuracy while 30 is a shotgun spray.").getInt();
		if (spray <= 0 || spray > 100) {
			hasError = true;
			logger.error("[" + pack + "] Bullet " + file.getName() + " has invalid Spray. Expecting a strictly positive integer, up to 100, found " + spray + ".");
		}
		
		int texture = configBullet.get("general", "Texture", 0, "Texture variation of the bullet (0 to 5)."
				+ "\n0 is normal, 1 is poison/rust, 2 is purple, 3 is red, 4 is fat metal, 5 is needle green.").getInt(0);;
		
		String[] effects = configBullet.get("general", "Impact", "", "Semicolon separated list of effects on impact. Example: \"1:3;2:3;4:1.0;5:3.5;7:10\""
				+ "\n'X' and 'Y' are modifiers."
				+ "\n1:X = Poison for X seconds"
				+ "\n2:X = Nausea for X seconds"
				+ "\n3:X = Fire for X seconds"
				+ "\n4:X = Explosion of X Strength (3 is TnT, 7 is RPG, 4.5 is M320)"
				+ "\n5:X = Explosion without Block Damage of X Strength"
				+ "\n6:X = Heal of X points"
				+ "\n7:X = Blindness for X seconds"
				+ "\n8:X = Instant damage (harm) of X damages"
				+ "\n9:X:Y = weaken +Y * 20% damage increase for X seconds"
				+ "\n10:X:Y = knockback +X horizontally +Y vertically.").getString().split(";");
		
		double gravityModifier = configBullet.get("general", "GravityModifier", 0.45D, "Gravity is vertical down acceleration applied every tick.").getDouble();
		
		float damageModifier = (float) configBullet.get("general", "Damage Modifier", 1.0D, "Applied damage is Gun Damage x Damage Modifier").getDouble();
		
		double initialSpeed = configBullet.get("general", "InitialSpeed", 200.0D, "Initial speed of the bullet in m/s. Actual speed decrease with friction and gun's attachment/barret.").getDouble();
		initialSpeed /= 20.0D;	// converts to blocks per tick
		
		double frictionInAir = configBullet.get("general", "FrictionInAir", 0.01D, "Friction factor while in air. Factor is applied every tick. 0.00 means no friction, while 1.00 means instant stop.").getDouble();
		if (frictionInAir <= 0.0D || frictionInAir > 1.0D) {
			hasError = true;
			logger.error("[" + pack + "] Bullet " + file.getName() + " has invalid FrictionInAir. Expecting a strictly positive integer, up to 100, found " + frictionInAir + ".");
		}
		
		double frictionInLiquid = configBullet.get("general", "FrictionInLiquids", 0.5D, "Friction factor while in a liquid. Factor is applied every tick. 0.00 means no friction, while 1.00 means instant stop.").getDouble();
		if (frictionInLiquid <= 0.0D || frictionInLiquid > 1.0D) {
			hasError = true;
			logger.error("[" + pack + "] Bullet " + file.getName() + " has invalid FrictionInLiquids. Expecting a strictly positive integer, up to 100, found " + frictionInLiquid + ".");
		}
		
		if (!ItemBullet.bullets.containsKey(pack)) {
			ItemBullet.bullets.put(pack, new ArrayList());
		}
		
		if (!hasError) {
			if (iconName.equals("") || iconName.equals(" ")) {
				iconName = "guncus:bullet";
			} else {
				iconName = pack + ":bullets/" + iconName;
			}
			
			if (!pack.equalsIgnoreCase("template")) {
				ItemBullet bullet = new ItemBullet(pack, name, bulletId, iconName, texture, gunpowder, ironIngot, stackOnCreate, damageModifier)
					.setSplit(split)
					.setGravityModifier(gravityModifier)
					.setSpray(spray)
					.setSpeedStats(initialSpeed, frictionInAir, frictionInLiquid);
				
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
				
				logger.info("Added bullet #" + bulletId + ": "+ name);
			}
		} else {
			logger.error("[" + pack + "] Something went wrong while initializing the bullet \"" + name + "\"! Ignoring this bullet!");
		}
		
		configBullet.save();
	}
	
	private void loadGuns(final String packPath, final String pack) {
		File fileGuns = new File(packPath + "/guns");
		fileGuns.mkdirs();
		File[] filesFound = fileGuns.listFiles();
		ArrayList<File> files = new ArrayList();
		
		for (File fileFound : filesFound) {
			if (fileFound.getAbsolutePath().endsWith(".cfg")) {
				files.add(fileFound);
			}
		}
		
		for (File file : files) {
			loadGun(pack, file);
		}
	}
	
	private void loadGun(final String pack, File file) {
		Configuration gunConfig = new Configuration(file);
		gunConfig.load();
		
		int shootType = gunConfig.get("general", "Shoot", 2, "Shooting type. Higher values are cumulative."
				+ "\n0 = Single Shooting | 1 = Burst Shooting | 2 = Auto Shooting").getInt();
		
		int delay = gunConfig.get("general", "Delay", 3, "Delay between shots of the gun (in ticks)").getInt();
		
		int magSize = gunConfig.get("general", "Magsize", 1, "Size of the magazines").getInt();
		
		int magIronIngots = gunConfig.get("general", "Mag Ingots", 1, "Number of iron ingots a mag needs to be crafted").getInt();
		
		int gunIronIngots = gunConfig.get("general", "Iron Ingots", 1, "Number of iron ingots this gun needs to be crafted").getInt();
		
		int gunRedstone = gunConfig.get("general", "Redstone", 1, "Number of redstone this gun needs to be crafted").getInt();
		
		String name = gunConfig.get("general", "Name", "default", "Name of the gun").getString();
		
		String[] stringBullets = gunConfig.get("general", "Bullets", "1", "Semicolon separated list of bullet IDs for this gun."
				+ "\nYou may type more than 1 bullet ID if this gun doesn't use magazines!").getString().split(";");
		
		boolean usingMag = gunConfig.get("general", "UsingMags", true, "Does this gun use magazines? False, if the gun is for example a shotgun.").getBoolean();
		
		String stringIcon = gunConfig.get("general", "Texture", "", "Texture of the gun. Leave blanc for default").getString();
		
		double recoilModifier = gunConfig.get("general", "RecoilModifier", 1.0D, "Defines the gun base recoil."
				+ "\nApplied recoil is gun.RecoilModifier x bullet.RecoilModifier.").getDouble();
		
		String sound_normal = gunConfig.get("general", "NormalSound", "Sound_DERP2", "Sound played when shooting."
				+ "\nSelect the sound file in the sounds.json file. Only .ogg files are supported by Minecraft."
				+ "\nLeave blanc for default").getString();
		
		String sound_silenced = gunConfig.get("general", "SilencedSound", "", "Sound event played when shooting while gun has a silencer."
				+ "\nSelect the sound file in the sounds.json file. Only .ogg files are supported by Minecraft."
				+ "\nLeave blanc for default").getString();
		
		double soundModifier = gunConfig.get("general", "SoundModifier", 1.0D, "Modifies the sound volume (does not affect the volume of silenced shots)."
				+ "\nDefault Sound Volume x SoundModifier = Used Sound Volume").getDouble();
		
		String[] stringAttachments = gunConfig.get("general", "Attachments", "1;3;2;6", "Semicolon separated list of attachments valid on this gun."
				+ "\n1 = Straight Pull Bolt | 2 = Bipod | 3 = Foregrip | 4 = M320 | 5 = Strong Spiral Spring"
				+ "\n6 = Improved Grip | 7 = Laser Pointer.").getString().split(";");
		
		String[] stringBarrels = gunConfig.get("general", "Barrels", "1;2;3", "Semicolon separated list of barrels valid on this gun."
				+ "\n1 = Silencer | 2 = Heavy Barrel | 3 = Rifled Barrel | 4 = Polygonal Barrel.").getString().split(";");
		
		String[] stringScopes = gunConfig.get("general", "Scopes", "1;2;3;4;5;6;7;8;9;10;11;12;13", "Semicolon separated list of scopes valid on this gun."
				+ "\n1 = Reflex | 2 = Kobra | 3 = Holographic | 4 = PKA-S | 5 = M145"
				+ "\n6 = PK-A | 7 = ACOG | 8 = PSO-1 | 9 = Rifle 6x | 10 = PKS-07"
				+ "\n11 = Rifle 8x | 12 = Ballistic 12x | 13 = Ballistic 20x.").getString().split(";");
		
		float zoom = (float) gunConfig.get("general", "Zoom", 1.0F, "Zoom factor without any scope. Default 1.0").getDouble();
		
		int damage = gunConfig.get("general", "Damage", 6, "Damage dealth (1 is half a heart)").getInt();
		
		if (soundModifier < 1.E-005D) {
			soundModifier = 1.E-005D;
		}
		
		if (soundModifier > 20.0D) {
			soundModifier = 20.0D;
		}
		
		boolean errored = false;
		if (shootType < 0 || shootType > 2) {
			logger.error("[" + pack + "] [" + name + "] Invalid shootType '" + shootType + "', expecting 0, 1 or 2");
			errored = true;
		}
		if (delay < 0) {
			logger.error("[" + pack + "] [" + name + "] Invalid delay '" + delay + "', expecting a positive or nul value");
			errored = true;
		}
		if (gunIronIngots <= 0) {
			logger.error("[" + pack + "] [" + name + "] Invalid Iron Ingots '" + gunIronIngots + "', expecting at least 1");
			errored = true;
		}
		if (gunRedstone < 0) {
			logger.error("[" + pack + "] [" + name + "] Invalid Redstone '" + gunRedstone + "', expecting a positive or nul value");
			errored = true;
		}
		
		int[] intBullets;
		intBullets = new int[stringBullets.length];
		for (int indexBullet = 0; indexBullet < stringBullets.length; indexBullet++) {
			try {
				intBullets[indexBullet] = Integer.parseInt(stringBullets[indexBullet]);
			} catch (Exception exception) {
				logger.info("[" + pack + "] Something went wrong while initializing bullets of the gun \"" + name
						+ "\"! Caused by: \"" + stringBullets[indexBullet] + "\"!");
				errored = true;
			}
			
			if (!pack.equalsIgnoreCase("template")) {
				if (ItemBullet.bullets.get(pack) == null || ItemBullet.bullets.get(pack).get(intBullets[indexBullet]) == null) {
					logger.error("[" + pack + "] [" + name + "] Can't find a bullet with ID " + intBullets[indexBullet] + "");
					errored = true;
				}
			}
		}
		
		if (stringBullets.length <= 0) {
			logger.error("[" + pack + "] [" + name + "] No bullets are defined?");
			errored = true;
		}
		if (usingMag) {
			if (intBullets.length != 1) {
				logger.info("[" + pack + "] [" + name + "] Invalid Bullets '" + stringBullets[0] + "', expecting a single integer");
				errored = true;
			}
			
			if (magSize < 1) {
				logger.error("[" + pack + "] [" + name + "] Invalid Mag Size '" + magSize + "', expecting at least 1");
				errored = true;
			}
			
			if (magIronIngots < 0) {
				logger.error("[" + pack + "] [" + name + "] Invalid Mag Ingots '" + magIronIngots + "', expecting at least 0");
				errored = true;
			}
		}
		
		if ( (!errored) && (name != null) && (stringIcon != null) ) {
			boolean defaultTexture = false;
			String iconName;
			if (stringIcon.isEmpty() || stringIcon.equals(" ")) {
				if (!pack.equalsIgnoreCase("template")) {
					logger.info("[" + pack + "] [" + name + "] Missing texture '" + name + "'!");
				}
				iconName = "guncus:gun_default/";
				defaultTexture = true;
			} else {
				iconName = pack + ":gun_" + stringIcon + "/";
			}
			try {
				// Create customization parts
				int[] intAttachments;
				if ((stringAttachments.length > 0) && (!stringAttachments[0].replace(" ", "").equals(""))) {
					intAttachments = new int[stringAttachments.length];
					for (int indexAttachment = 0; indexAttachment < stringAttachments.length; indexAttachment++) {
						intAttachments[indexAttachment] = Integer.parseInt(stringAttachments[indexAttachment]);
					}
				} else {
					intAttachments = new int[0];
				}
				int[] intBarrels;
				if ((stringBarrels.length > 0) && (!stringBarrels[0].replace(" ", "").equals(""))) {
					intBarrels = new int[stringBarrels.length];
					for (int indexBarrel = 0; indexBarrel < stringBarrels.length; indexBarrel++) {
						intBarrels[indexBarrel] = Integer.parseInt(stringBarrels[indexBarrel]);
					}
				} else {
					intBarrels = new int[0];
				}
				int[] intScopes;
				if ((stringScopes.length > 0) && (!stringScopes[0].replace(" ", "").equals(""))) {
					intScopes = new int[stringScopes.length];
					for (int indexScope = 0; indexScope < stringScopes.length; indexScope++) {
						intScopes[indexScope] = Integer.parseInt(stringScopes[indexScope]);
					}
				} else {
					intScopes = new int[0];
				}
				
				if (!pack.equalsIgnoreCase("template")) {
					// Create gun and magazine items
					ItemGun gun = new ItemGun(pack, false, name, iconName,
							damage, shootType, delay,
							magSize, magIronIngots, gunIronIngots, gunRedstone,
							intAttachments, intBarrels, intScopes,
							usingMag, intBullets)
							.setRecoilModifier(recoilModifier)
							.setSoundModifier(soundModifier)
							.defaultTexture(defaultTexture)
							.setZoom(zoom);
					GunCusCreativeTab tab = new GunCusCreativeTab((pack + "." + name).replace(" ", "_"), gun);
					gun.setCreativeTab(tab);
					if (gun.mag != null) {
						gun.mag.setCreativeTab(tab);
					}
					
					// Add sounds
					if (!sound_normal.trim().isEmpty()) {
						gun.setNormalSound(pack  + ":" + sound_normal);
					}
					
					if (!sound_silenced.trim().isEmpty()) {
						gun.setSilencedSound(pack  + ":" + sound_silenced);
					}
				}
			} catch (Exception exception) {
				logger.info("[" + pack + "] Error while trying to add the gun \"" + name + "\": ! Pls check the attachments, barrels and scopes of it!");
				exception.printStackTrace();
			}
		} else {
			logger.info("[" + pack + "] Something went wrong while initializing the gun \"" + name + "\"! Ignoring this gun!");
		}
		gunConfig.save();
	}
	
	public static void addChatMessage(final ICommandSender sender, final String message) {
		String[] lines = message.split("\n");
		for (String line : lines) {
			sender.addChatMessage(new ChatComponentText(line));
		}
	}
}
