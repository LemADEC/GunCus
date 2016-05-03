package stuuupiiid.guncus;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.command.ICommandSender;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import stuuupiiid.guncus.block.BlockAmmoBox;
import stuuupiiid.guncus.block.BlockBulletBox;
import stuuupiiid.guncus.block.BlockGunBox;
import stuuupiiid.guncus.block.BlockMagazineBox;
import stuuupiiid.guncus.block.BlockMine;
import stuuupiiid.guncus.block.BlockWeaponBox;
import stuuupiiid.guncus.data.ModifierPart;
import stuuupiiid.guncus.data.ScopePart;
import stuuupiiid.guncus.entity.EntityGrenade;
import stuuupiiid.guncus.entity.EntityBullet;
import stuuupiiid.guncus.entity.EntityRocket;
import stuuupiiid.guncus.event.TickHandler;
import stuuupiiid.guncus.gui.GuiHandler;
import stuuupiiid.guncus.item.ItemBase;
import stuuupiiid.guncus.item.ItemAttachmentPart;
import stuuupiiid.guncus.item.ItemBullet;
import stuuupiiid.guncus.item.ItemGun;
import stuuupiiid.guncus.item.ItemQuickKnife;
import stuuupiiid.guncus.item.ItemMagazineFiller;
import stuuupiiid.guncus.item.ItemModifierPart;
import stuuupiiid.guncus.item.ItemRPG;
import stuuupiiid.guncus.item.ItemScope;
import stuuupiiid.guncus.network.PacketHandler;

/**
 * @author LemADEC
 */
@Mod(modid = GunCus.MODID, name = "Gun Customization", version = GunCus.VERSION, dependencies = "")
public class GunCus {
	public static final String MODID = "GunCus";
	public static final String ASSETID = MODID.toLowerCase();
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
	public static int holdFireAfterClosingGUIcounter = 0;
	public static int breathingCounter = 0;
	public static int holdingBreathCounter = 0;
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
	public static ItemQuickKnife itemQuickKnife;
	public static CreativeTabs creativeTabModifications;
	public static CreativeTabs creativeTabBullets;
	public static BlockWeaponBox blockWeaponBox;
	public static BlockMagazineBox blockMagazineBox;
	public static BlockBulletBox blockBulletBox;
	public static BlockAmmoBox blockAmmoBox;
	public static BlockGunBox blockGunBox;
	public static ItemMagazineFiller itemMagazineFiller;
	public static ItemBase itemBoxpart;
	public static ItemScope itemScope;
	public static ItemModifierPart itemBarrel;
	public static ItemAttachmentPart itemAttachment;
	public static ItemBase itemAmmoM320;
	
	public static int knifeTime = 0;
	public static int knifeCooldown = 30;
	public static float knifeDamage = 20.0F;
	public static float knifeRange = 2.0F;
	
	// explosives extension
	public static ItemBase itemRPGmagazine;
	public static ItemRPG itemRPG;
	public static ItemBase itemSMAWmagazine;
	public static ItemRPG itemSMAW;
	public static BlockMine blockMine;
	
	// logging options
	public static boolean logging_enableNetwork = false;
	public static boolean logging_enableDamageData = false;
	public static boolean logging_enableErrors = false;
	
	public static Logger logger;
	
	@Mod.EventHandler
	public void onFMLPreInitialization(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		instance = this;
		
		if (event.getSide().isClient()) {
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
		itemQuickKnife = new ItemQuickKnife("quickKnife");
		
		enableBlockDamage = config.get("Gun Customization", "enableBlockDamage", true, "Disable this to prevent block damage from explosives and bullet damage to glass.").getBoolean(true);
		enableExplosives = config.get("Gun Customization", "enableExplosives", true, "Disable this to remove the explosive parts of Gun Customization.").getBoolean(true);
		logging_enableNetwork = config.get("Gun Customization", "enableNetworkLogs", false, "Enable this to show client/server packet transfers.").getBoolean(false);
		logging_enableDamageData = config.get("Gun Customization", "enableDamageData", false, "Enable this to show damage, range, accuracy, and what was hit.").getBoolean(false);
		logging_enableErrors = config.get("Gun Customization", "enableErrorLogs", false, "Enable this to show some more errors. Shouldn't really need this.").getBoolean(false);
		knifeDamage = (float) config.get("Gun Customization", "knifeDamage", 5.0, "This sets the damage the knife does in 1/2 hearts. Can be set with decimals.").getDouble();
		knifeRange = (float) config.get("Gun Customization", "knifeRange", 2.0, "This sets the range the knife can hit in blocks. Can be set with decimals.").getDouble();
		knifeCooldown = config.get("Gun Customization", "knifeCooldown", 30, "This sets the knife cooldown in ticks. 20 ticks = 1 second. Whole numbers only.").getInt();
		
		config.save();
		
		blockWeaponBox = new BlockWeaponBox();
		blockMagazineBox = new BlockMagazineBox();
		blockBulletBox = new BlockBulletBox();
		blockAmmoBox = new BlockAmmoBox();
		blockGunBox = new BlockGunBox();
		
		itemMagazineFiller = new ItemMagazineFiller("magazineFiller");
		itemBoxpart = (ItemBase) new ItemBase("boxpart").setMaxStackSize(16);
		
		itemScope = new ItemScope("scope",
				new ScopePart[] {
					new ScopePart( 1, "reflex", 1.0F),
					new ScopePart( 2, "kobra", 1.0F),
					new ScopePart( 3, "holographic", 1.0F),
					new ScopePart( 4, "pka-s", 1.0F),
					new ScopePart( 5, "m145", 3.4F),
					new ScopePart( 6, "pk-a", 3.4F),
					new ScopePart( 7, "acog", 4.0F),
					new ScopePart( 8, "pso-1", 4.0F),
					new ScopePart( 9, "rifle6", 6.0F),
					new ScopePart(10, "pks-07", 7.0F),
					new ScopePart(11, "rifle8", 8.0F),
					new ScopePart(12, "ballistic12", 12.0F),
					new ScopePart(13, "ballistic20", 20.0F) });
		itemBarrel = new ItemModifierPart("barrel",
				new ModifierPart[] {
					new ModifierPart(1, "silencer"),
					new ModifierPart(2, "heavy"),
					new ModifierPart(3, "rifled"),
					new ModifierPart(4, "polygonal") });
		itemAttachment = new ItemAttachmentPart("attachment",
				new ModifierPart[] {
					new ModifierPart(1, "straightPullBolt"),
					new ModifierPart(2, "bipod"),
					new ModifierPart(3, "foregrip"),
					new ModifierPart(4, "m320"),
					new ModifierPart(5, "strongSpiralSpring"),
					new ModifierPart(6, "improvedGrip"),
					new ModifierPart(7, "laserPointer") });
		
		itemAmmoM320 = (ItemBase) new ItemBase("ammo_M320").setMaxStackSize(8);
		
		if (enableExplosives) {
			blockMine = new BlockMine();
			
			itemRPGmagazine = new ItemBase("explosive.ammo_rpg");
			itemRPG = new ItemRPG("explosive.rpg", itemRPGmagazine);
			
			itemSMAWmagazine = new ItemBase("explosive.ammo_smaw");
			itemSMAW = new ItemRPG("explosive.smaw", itemSMAWmagazine);
		}
		
		TickHandler tickHandler = new TickHandler();
		MinecraftForge.EVENT_BUS.register(tickHandler);
		
		File path = new File(event.getModConfigurationDirectory().getParentFile().getAbsolutePath() + "/GunCus");
		
		if (!path.exists()) {
			path.mkdirs();
			logger.info("Created the GunCus directory!");
			logger.info("You should install some gun packs now!");
		}
		
		loadGunPacks(path);
		
		commonProxy.onForgePreInit();
	}
	
	@Mod.EventHandler
	public void onFMLInitialization(FMLInitializationEvent event) {
		PacketHandler.init();
		
		EntityRegistry.registerModEntity(EntityBullet.class, "guncus.bullet", 200, this, 80, 1, true);
		EntityRegistry.registerModEntity(EntityGrenade.class, "guncus.grenade", 201, this, 80, 1, true);
		EntityRegistry.registerModEntity(EntityRocket.class, "guncus.rocket", 202, this, 100, 1, true);
		
		guiHandler = new GuiHandler();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, guiHandler);
		
		GameRegistry.addShapedRecipe(new ItemStack(itemBoxpart),
				"ABA", "BCB", "ABA",
					'A', new ItemStack(Items.iron_ingot),
					'B', new ItemStack(Items.redstone),
					'C', new ItemStack(Items.gold_ingot) );
		GameRegistry.addShapedRecipe(new ItemStack(itemMagazineFiller),
				"ABA", "BAB", "ABA",
					'B', new ItemStack(Items.iron_ingot),
					'A', new ItemStack(Items.redstone) );
		GameRegistry.addShapedRecipe(new ItemStack(blockAmmoBox),
				"BBB", "ABA", "BCB",
					'A', new ItemStack(itemBoxpart),
					'B', new ItemStack(Items.iron_ingot),
					'C', new ItemStack(Blocks.iron_block) );
		GameRegistry.addShapedRecipe(new ItemStack(blockBulletBox),
				"BAB", "AAA", "BCB",
					'A', new ItemStack(itemBoxpart),
					'B', new ItemStack(Items.iron_ingot),
					'C', new ItemStack(Blocks.iron_block) );
		GameRegistry.addShapedRecipe(new ItemStack(blockMagazineBox),
				"BAB", "ABA", "BCB",
					'A', new ItemStack(itemBoxpart),
					'B', new ItemStack(Items.iron_ingot),
					'C', new ItemStack(Blocks.iron_block) );
		GameRegistry.addShapedRecipe(new ItemStack(blockGunBox),
				"BAB", "AAA", "BAB",
					'A', new ItemStack(itemBoxpart),
					'B', new ItemStack(Items.iron_ingot) );
		GameRegistry.addShapedRecipe(new ItemStack(blockWeaponBox),
				"ABA", "ABA", "BCB",
					'A', new ItemStack(itemBoxpart),
					'B', new ItemStack(Items.iron_ingot),
					'C', new ItemStack(Blocks.iron_block) );
		GameRegistry.addShapedRecipe(
				new ItemStack(itemScope, 1, 0),
				" IG", "IRI",
					'G', new ItemStack(Blocks.glass_pane, 1),
					'I', new ItemStack(Items.iron_ingot, 1),
					'R', new ItemStack(Items.redstone, 1) );
		GameRegistry.addShapedRecipe(
				new ItemStack(itemScope, 1, 1),
				"IG ", "IRI",
					'G', new ItemStack(Blocks.glass_pane, 1),
					'I', new ItemStack(Items.iron_ingot, 1),
					'R', new ItemStack(Items.redstone, 1) );
		GameRegistry.addShapedRecipe(new ItemStack(itemScope, 1, 2),
				" I ", "GRG", "I I",
					'G', new ItemStack(Blocks.glass_pane, 1),
					'I', new ItemStack(Items.iron_ingot, 1),
					'R', new ItemStack(Items.redstone, 1) );
		GameRegistry.addShapedRecipe(new ItemStack(itemScope, 1, 3),
				"I I", "GRG", " I ",
					'G', new ItemStack(Blocks.glass_pane, 1),
					'I', new ItemStack(Items.iron_ingot, 1),
					'R', new ItemStack(Items.redstone, 1) );
		GameRegistry.addShapedRecipe(new ItemStack(itemScope, 1, 4),
				" I ", "GDG", "I I",
					'G', new ItemStack(Blocks.glass_pane, 1),
					'I', new ItemStack(Items.iron_ingot, 1),
					'D', new ItemStack(Items.diamond, 1) );
		GameRegistry.addShapedRecipe(new ItemStack(itemScope, 1, 5),
				"I I", "GDG", " I ",
					'G', new ItemStack(Blocks.glass_pane, 1),
					'I', new ItemStack(Items.iron_ingot, 1),
					'D', new ItemStack(Items.diamond, 1) );
		GameRegistry.addShapedRecipe(new ItemStack(itemScope, 1, 6),
				"I I", "GDG", "I I",
					'G', new ItemStack(Blocks.glass_pane, 1),
					'I', new ItemStack(Items.iron_ingot, 1),
					'D', new ItemStack(Items.diamond, 1) );
		GameRegistry.addShapedRecipe(new ItemStack(itemScope, 1, 7),
				"I I", "GDG", " II",
					'G', new ItemStack(Blocks.glass_pane, 1),
					'I', new ItemStack(Items.iron_ingot, 1),
					'D', new ItemStack(Items.diamond, 1) );
		GameRegistry.addShapedRecipe(new ItemStack(itemScope, 1, 8),
				"III", "GDG", "I I",
					'G', new ItemStack(Blocks.glass_pane, 1),
					'I', new ItemStack(Items.iron_ingot, 1),
					'D', new ItemStack(Items.diamond, 1) );
		GameRegistry.addShapedRecipe(new ItemStack(itemScope, 1, 9),
				"I I", "D8G", "I I",
					'G', new ItemStack(Blocks.glass_pane, 1),
					'I', new ItemStack(Items.iron_ingot, 1),
					'D', new ItemStack(Items.diamond, 1),
					'8', new ItemStack(itemScope, 1, 7) );
		GameRegistry.addShapedRecipe(
				new ItemStack(itemScope, 1, 10),
				"D9G", " I ",
					'G', new ItemStack(Blocks.glass_pane, 1),
					'I', new ItemStack(Items.iron_ingot, 1),
					'D', new ItemStack(Items.diamond, 1),
					'9', new ItemStack(itemScope, 1, 8) );
		GameRegistry.addShapedRecipe(new ItemStack(itemScope, 1, 11),
				"GIG", "DDD", "III",
					'G', new ItemStack(Blocks.glass_pane, 1),
					'I', new ItemStack(Items.iron_ingot, 1),
					'D', new ItemStack(Items.diamond, 1) );
		GameRegistry.addShapedRecipe(new ItemStack(itemScope, 1, 12),
				" I ", "DBG", "I I",
					'G', new ItemStack(Blocks.glass_pane, 1),
					'I', new ItemStack(Items.iron_ingot, 1),
					'D', new ItemStack(Items.diamond, 1),
					'B', new ItemStack(itemScope, 1, 11) );
		
		GameRegistry.addShapedRecipe(
				new ItemStack(itemAmmoM320),
				"GI ", "IGI", " IG",
					'I', new ItemStack(Items.iron_ingot, 1),
					'G', new ItemStack(Items.gunpowder, 1) );
		GameRegistry.addShapedRecipe(new ItemStack(itemAttachment, 1, 0),
				"I  ", " I ", "I I",
					'I', new ItemStack(Items.iron_ingot, 1) );
		GameRegistry.addShapedRecipe(new ItemStack(itemAttachment, 1, 1),
				" I ", "I I", "I I",
					'I', new ItemStack(Items.iron_ingot, 1) );
		GameRegistry.addShapedRecipe(new ItemStack(itemAttachment, 1, 2),
				"II ", " I ", " II",
					'I', new ItemStack(Items.iron_ingot, 1) );
		GameRegistry.addShapedRecipe(
				new ItemStack(itemAttachment, 1, 3),
				" II", "IRR", "I I",
					'I', new ItemStack(Items.iron_ingot, 1),
					'R', new ItemStack(Items.redstone, 1) );
		GameRegistry.addShapedRecipe(
				new ItemStack(itemAttachment, 1, 4),
				"I  ", "IGI", "  I",
					'I', new ItemStack(Items.iron_ingot, 1),
					'G', new ItemStack(Items.gold_ingot, 1) );
		GameRegistry.addShapedRecipe(
				new ItemStack(itemAttachment, 1, 5),
				" L ", "LGL", " L ",
					'L', new ItemStack(Items.leather, 1),
					'G', new ItemStack(itemAttachment, 1, 2) );
		GameRegistry.addShapedRecipe(
				new ItemStack(itemAttachment, 1, 6),
				"II ", "RRI", "II ",
					'I', new ItemStack(Items.iron_ingot, 1),
					'R', new ItemStack(Items.redstone, 1) );

		GameRegistry.addShapedRecipe(
				new ItemStack(itemBarrel, 1, 0),
				"SI ", "ISI", " IS",
					'I', new ItemStack(Items.iron_ingot, 1),
					'S', new ItemStack(Items.slime_ball, 1) );
		GameRegistry.addShapedRecipe(new ItemStack(itemBarrel, 1, 1),
				"II ", "II ", "  I",
					'I', new ItemStack(Items.iron_ingot, 1) );
		GameRegistry.addShapedRecipe(
				new ItemStack(itemBarrel, 1, 2),
				"GI ", "IGI", " IG",
					'I', new ItemStack(Items.iron_ingot, 1),
					'G', new ItemStack(Items.gold_ingot, 1) );
		GameRegistry.addShapedRecipe(
				new ItemStack(itemBarrel, 1, 3),
				"II ", "IDI", " II",
					'I', new ItemStack(Items.iron_ingot, 1),
					'D', new ItemStack(Items.diamond, 1) );
		
		if (enableExplosives) {
			GameRegistry.addRecipe(
					new ItemStack(itemRPG, 1),
					"IIR", "IWR", "RRW",
						Character.valueOf('I'), new ItemStack(Items.iron_ingot),
						Character.valueOf('W'), new ItemStack(Blocks.planks),
						Character.valueOf('R'), new ItemStack(Items.redstone) );
			GameRegistry.addRecipe(
					new ItemStack(itemSMAW, 1),
					"RI ", "IRI", " IR",
						Character.valueOf('I'), new ItemStack(Items.iron_ingot),
						Character.valueOf('R'), new ItemStack(Items.redstone) );
			
			GameRegistry.addRecipe(
					new ItemStack(itemRPGmagazine, 2),
					"II ", "IG ", "  G",
						Character.valueOf('I'), new ItemStack(Items.iron_ingot),
						Character.valueOf('G'), new ItemStack(Items.gunpowder) );
			GameRegistry.addRecipe(
					new ItemStack(itemSMAWmagazine, 2),
					"G  ", " GI", " II",
						Character.valueOf('I'), new ItemStack(Items.iron_ingot),
						Character.valueOf('G'), new ItemStack(Items.gunpowder) );
		}
	}
	
	/*
	 * Copy a default configuration file from the mod's resources to the specified configuration folder
	 */
	public static void unpackResourceToFolder(final String filename, final String sourceResourcePath, File targetFolder) {
		// targetFolder is already created by caller
		
		String resourceName = sourceResourcePath + "/" + filename;
		
		File destination = new File(targetFolder, filename);
		
		try {
			InputStream inputStream = GunCus.class.getClassLoader().getResourceAsStream(resourceName);
			BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(destination));
			
			byte[] byteBuffer = new byte[Math.max(8192, inputStream.available())];
			int bytesRead;
			while ((bytesRead = inputStream.read(byteBuffer)) >= 0) {
				outputStream.write(byteBuffer, 0, bytesRead);
			}
			
			inputStream.close();
			outputStream.close();
		} catch (Exception exception) {
			GunCus.logger.error("Failed to unpack resource \'" + resourceName + "\' into " + destination);
				if (GunCus.logging_enableErrors) {
					exception.printStackTrace();
				}
		}
	}
	
	private void loadGunPacks(File fileGunCus) {
		createTemplatePack(fileGunCus);
		createOfficialPack(fileGunCus);
		
		for (File filePack : fileGunCus.listFiles()) {
			if (filePack.isDirectory() && (!filePack.getName().equalsIgnoreCase("template")) && (!filePack.getName().equalsIgnoreCase("default"))) {
				logger.info("Loading pack " + filePack.getName());
				loadBullets(filePack.getAbsolutePath(), filePack.getName());
				loadGuns(filePack.getAbsolutePath(), filePack.getName());
			}
		}
		gunNames = guns.keySet();
	}
	
	private void createOfficialPack(File fileGunCus) {
		File fileOfficial = new File(fileGunCus, "/Official");
		
		// official gun pack creation
		File officialDir;
		final String[] attachmentSniperTextureArray = {
			"gun.png",
			"hbl.png",
			"magazine.png",
			"rbl.png",
			"scp.png",
			"sight.png",
			"sln.png",
			"bpd.png",
			"spb.png" };
		final String[] attachmentPistolTextureArray = {
			"gun.png",
			"hbl.png",
			"img.png",
			"magazine.png",
			"rbl.png",
			"scp.png",
			"sight.png",
			"sln.png",
			"sss.png" };
		final String[] attachmentARTextureArray = {
			"gun.png",
			"hbl.png",
			"magazine.png",
			"rbl.png",
			"scp.png",
			"sight.png",
			"sln.png",
			"grp.png",
			"320.png" };
		final String[] bulletCfgArray = {
			"bullet0_acp.cfg",
			"bullet1_nato.cfg",
			"bullet2_wp.cfg",
		    "bullet3_parabellum.cfg",
		    "bullet4_natoHeavy.cfg",
		    "bullet5_wpHeavy.cfg",
		    "bullet6_rHeavy.cfg" };
		final String[] gunCfgArray = {	    
			"aek971.cfg",
		    "g17.cfg",
		    "g18.cfg",
		    "m16a3.cfg",
		    "l96.cfg",
		    "sv98.cfg" };
		final String[] langArray = {	
			"en_US.lang",
		    "ru_RU.lang", };
		final String[] bulletTextureArray = {
			"bullet.png" };			
		final String[] gunSniperNameArray = {
			"gun_l96",
			"gun_sv98" };			
		final String[] gunPistolNameArray = {
			"gun_g17",
			"gun_g18" };			
		final String[] gunARNameArray = {
			"gun_m16a3",
			"gun_aek-971" };
		
		if (!fileOfficial.exists()) {
			fileOfficial.mkdirs();
						
			for (String gunName : gunSniperNameArray) {
				File dirGun = new File(fileGunCus + "/Official/textures/items/" + gunName);
				dirGun.mkdirs();
				for (String gunAttachment : attachmentSniperTextureArray) {
						unpackResourceToFolder(gunAttachment, "assets/guncusofficial/textures/items/" + gunName, dirGun);
				}
			}
			
			for (String gunName : gunPistolNameArray) {
				File dirGun = new File(fileGunCus + "/Official/textures/items/" + gunName);
				dirGun.mkdirs();
				for (String gunAttachment : attachmentPistolTextureArray) {
						unpackResourceToFolder(gunAttachment, "assets/guncusofficial/textures/items/" + gunName, dirGun);
				}
			}
			
			for (String gunName : gunARNameArray) {
				File dirGun = new File(fileGunCus + "/Official/textures/items/" + gunName);
				dirGun.mkdirs();
				for (String gunAttachment : attachmentARTextureArray) {
						unpackResourceToFolder(gunAttachment, "assets/guncusofficial/textures/items/" + gunName, dirGun);
				}
			}
			
			officialDir = new File(fileGunCus + "/Official/bullets");
			officialDir.mkdirs();		
			for (String bulletCfg : bulletCfgArray) {
				unpackResourceToFolder(bulletCfg, "assets/guncusofficial/bullets", officialDir);
			}
			
			officialDir = new File(fileGunCus + "/Official/guns");
			officialDir.mkdirs();
			for (String gunCfg : gunCfgArray) {
				unpackResourceToFolder(gunCfg, "assets/guncusofficial/guns", officialDir);
			}
			
			officialDir = new File(fileGunCus + "/Official/lang");
			officialDir.mkdirs();
			for (String langFile : langArray) {
				unpackResourceToFolder(langFile, "assets/guncusofficial/lang", officialDir);
			}
			
			officialDir = new File(fileGunCus + "/Official/sounds");
			officialDir.mkdirs();
			
			officialDir = new File(fileGunCus + "/Official");
			officialDir.mkdirs();
			unpackResourceToFolder("sounds.json", "assets/guncusofficial", officialDir);
			
			officialDir = new File(fileGunCus + "/Official/textures/items/bullets");
			officialDir.mkdirs();
			for (String bulletTexture : bulletTextureArray) {
				unpackResourceToFolder(bulletTexture, "assets/guncusofficial/textures/items/bullets", officialDir);
			}
		}
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
		boolean enabled = configBullet.get("general", "Enabled", true, "Set this to false to disable this ammo."
				+ "\nNote: Disabling ammo will also disable any guns that use this ammo.").getBoolean();
		
		int bulletId = configBullet.get("general", "Bullet ID", 1, "Bullet ID of the bullet (guns refer to bullet by this ID)").getInt();
		
		if (bulletId < 0) {
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
		
		String name = configBullet.get("general", "Name", "default", "Name of the bullet").getString();
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
		
		String blockHit = configBullet.get("general", "Block Hit", "", "Sound played when a bullet hits a block."
				+ "\nSelect the sound file in the sounds.json file. Only .ogg files are supported by Minecraft."
				+ "\nIf using sounds from other packs, type the pack name and sound seperated by a colon."
				+ "\nLeave blanc for default").getString();
		
		String entityHit = configBullet.get("general", "Entity Hit", "", "Sound played when a bullet hits an entity."
				+ "\nSelect the sound file in the sounds.json file. Only .ogg files are supported by Minecraft."
				+ "\nIf using sounds from other packs, type the pack name and sound seperated by a colon."
				+ "\nLeave blanc for default").getString();
		
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
				+ "\n10:X:Y = knockback +X horizontally +Y * 0.1 vertically. Vanilla knockback is cancelled when this effect is defined.").getString().split(";");
		
		double gravityModifier = configBullet.get("general", "GravityModifier", 0.45D, "Gravity is vertical down acceleration applied every tick.").getDouble();
		
		float damageModifier = (float) configBullet.get("general", "Damage Modifier", 1.0D, "Applied damage is Gun Damage x Damage Modifier").getDouble();
		
		double initialSpeed = configBullet.get("general", "InitialSpeed", 200.0D, "Initial speed of the bullet in m/s. Actual speed decrease with friction and gun's attachment/barret.").getDouble();
		initialSpeed /= 20.0D;	// converts to blocks per tick
		
		double frictionInAir = configBullet.get("general", "FrictionInAir", 0.01D, "Friction factor while in air. Factor is applied every tick. 0.00 means no friction, while 1.00 means instant stop.").getDouble();
		if (frictionInAir <= 0.0D || frictionInAir > 1.0D) {
			hasError = true;
			logger.error("[" + pack + "] Bullet " + file.getName() + " has invalid FrictionInAir. Expecting a strictly positive integer, up to 100, found " + frictionInAir + ".");
		}
		
		float playerInaccuracyMultiplier = (float) configBullet.get("general", "Player Inaccuracy Multiplier", 1.0D, "Effects player accuracy. 0.0 means perfect accuracy and only the effects of spray will move the bullet, while 1.0 means normal. You may get some weird effets.").getDouble();
		
		double frictionInLiquid = configBullet.get("general", "FrictionInLiquids", 0.5D, "Friction factor while in a liquid. Factor is applied every tick. 0.00 means no friction, while 1.00 means instant stop.").getDouble();
		if (frictionInLiquid <= 0.0D || frictionInLiquid > 1.0D) {
			hasError = true;
			logger.error("[" + pack + "] Bullet " + file.getName() + " has invalid FrictionInLiquids. Expecting a strictly positive integer, up to 100, found " + frictionInLiquid + ".");
		}
		
		if (!enabled){
			logger.info("The ammo " + name + " in pack " + pack + " with ID " + bulletId + " is disabled in the configuration file.");
		}		
		
		if (!ItemBullet.bullets.containsKey(pack)) {
			ItemBullet.bullets.put(pack, new ArrayList());
		}
		
		if ((!hasError) && (enabled)) {
			if (iconName.isEmpty()) {
				iconName = "guncus:bullet";
			} else {
				iconName = pack + ":bullets/" + iconName;
			}
			
			if (!pack.equalsIgnoreCase("template")) {
				ItemBullet bullet = new ItemBullet(pack, name, bulletId, texture, gunpowder, ironIngot, stackOnCreate, damageModifier)
					.setSplit(split)
					.setGravityModifier(gravityModifier)
					.setAccuracyModifiers(spray, playerInaccuracyMultiplier)
					.setSpeedStats(initialSpeed, frictionInAir, frictionInLiquid);
				
				// Add sounds
				if (!blockHit.isEmpty()) {
					if (blockHit.contains(":")) {
						bullet.setBlockHit(blockHit);
					} else {
					bullet.setBlockHit(pack + ":" + blockHit);
					}
				} else {
					blockHit = "guncus:inground";
				}
				
				if (!entityHit.isEmpty()) {
					if (entityHit.contains(":")){
						bullet.setEntityHit(entityHit);
					} else {
					bullet.setEntityHit(pack + ":" + entityHit);
					} 
				} else {
					entityHit = "guncus:inground";
				}
				
				// Add effects
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
		Configuration configGun = new Configuration(file);
		configGun.load();		
		
		boolean enabled = configGun.get("general", "Enabled", true, "Set this to false to disable this gun.").getBoolean();

		int shootType = configGun.get("general", "Shoot", 2, "Shooting type. Higher values are cumulative."
				+ "\n0 = Single Shooting | 1 = Burst Shooting | 2 = Auto Shooting").getInt();
		
		int delay = configGun.get("general", "Delay", 3, "Delay between shots of the gun (in ticks)").getInt();
		
		int magSize = configGun.get("general", "Magsize", 1, "Size of the magazines").getInt();
		
		int magIronIngots = configGun.get("general", "Mag Ingots", 1, "Number of iron ingots a mag needs to be crafted").getInt();
		
		int gunIronIngots = configGun.get("general", "Iron Ingots", 1, "Number of iron ingots this gun needs to be crafted").getInt();
		
		boolean scopedReloading = configGun.get("general", "Scope Reloading", true, "Set this to false to disable reloading while aiming down the sights.").getBoolean();
		
		int gunRedstone = configGun.get("general", "Redstone", 1, "Number of redstone this gun needs to be crafted").getInt();
		
		String name = configGun.get("general", "Name", "default", "Name of the gun").getString();
		
		String[] stringBullets = configGun.get("general", "Bullets", "1", "Semicolon separated list of bullet IDs for this gun."
				+ "\nYou may type more than 1 bullet ID if this gun doesn't use magazines!").getString().split(";");
		
		boolean usingMag = configGun.get("general", "UsingMags", true, "Does this gun use magazines? False, if the gun is for example a shotgun.").getBoolean();
		
		String stringIcon = configGun.get("general", "Texture", "", "Texture of the gun. Leave blanc for default").getString();
		
		double recoilModifier = configGun.get("general", "RecoilModifier", 1.0D, "Defines the gun base recoil."
				+ "\nApplied recoil is Damage x RecoilModifier x AttachmentModifier.").getDouble();
		
		String soundNormal = configGun.get("general", "NormalSound", "", "Sound played when shooting."
				+ "\nSelect the sound file in the sounds.json file. Only .ogg files are supported by Minecraft."
				+ "\nLeave blanc for default").getString();
		
		String soundSilenced = configGun.get("general", "SilencedSound", "", "Sound event played when shooting while gun has a silencer."
				+ "\nSelect the sound file in the sounds.json file. Only .ogg files are supported by Minecraft."
				+ "\nLeave blanc for default").getString();
		
		double soundModifier = configGun.get("general", "SoundModifier", 1.0D, "Modifies the sound volume (does not affect the volume of silenced shots)."
				+ "\nDefault Sound Volume x SoundModifier = Used Sound Volume").getDouble();
		
		String[] stringAttachments = configGun.get("general", "Attachments", "1;3;2;6", "Semicolon separated list of attachments valid on this gun."
				+ "\n1 = Straight Pull Bolt | 2 = Bipod | 3 = Foregrip | 4 = M320 | 5 = Strong Spiral Spring"
				+ "\n6 = Improved Grip | 7 = Laser Pointer.").getString().split(";");
		
		String[] stringBarrels = configGun.get("general", "Barrels", "1;2;3", "Semicolon separated list of barrels valid on this gun."
				+ "\n1 = Silencer | 2 = Heavy Barrel | 3 = Rifled Barrel | 4 = Polygonal Barrel.").getString().split(";");
		
		String[] stringScopes = configGun.get("general", "Scopes", "1;2;3;4;5;6;7;8;9;10;11;12;13", "Semicolon separated list of scopes valid on this gun."
				+ "\n1 = Reflex | 2 = Kobra | 3 = Holographic | 4 = PKA-S | 5 = M145"
				+ "\n6 = PK-A | 7 = ACOG | 8 = PSO-1 | 9 = Rifle 6x | 10 = PKS-07"
				+ "\n11 = Rifle 8x | 12 = Ballistic 12x | 13 = Ballistic 20x.").getString().split(";");
		
		float zoom = (float) configGun.get("general", "Zoom", 1.0F, "Zoom factor without any scope. Default 1.0").getDouble();
		
		int damage = configGun.get("general", "Damage", 6, "Damage dealth (1 is half a heart)").getInt();
		
		if (soundModifier < 1.E-005D) {
			soundModifier = 1.E-005D;
		}
		
		if (soundModifier > 20.0D) {
			soundModifier = 20.0D;
		}
		
		if (!enabled){
			logger.info("The gun " + name + " in pack " + pack + " is disabled in the configuration file.");
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
		
		if ( (!errored) && (name != null) && (stringIcon != null) && (enabled)) {
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
							.setZoom(zoom)
							.setReloading(scopedReloading);
					GunCusCreativeTab tab = new GunCusCreativeTab((pack + "." + name).replace(" ", "_"), gun);
					gun.setCreativeTab(tab);
					if (gun.mag != null) {
						gun.mag.setCreativeTab(tab);
					}
					
					// Add sounds
					if (!soundNormal.trim().isEmpty()) {
						if (soundNormal.contains(":")) {
								gun.setNormalSound(soundNormal);
					    } else {
					    	gun.setNormalSound(pack  + ":" + soundNormal);
					    }
					}
					else {
						gun.setNormalSound("guncus:shoot_sound");
					}
					
					if (!soundSilenced.trim().isEmpty()) {
						if (soundSilenced.contains(":")) {
							gun.setSilencedSound(soundSilenced);
						} else {
							gun.setSilencedSound(pack  + ":" + soundSilenced);
						}
					}
					else {
						gun.setSilencedSound("guncus:shoot_silenced");
					}
				}
			} catch (Exception exception) {
				logger.info("[" + pack + "] Error while trying to add the gun \"" + name + "\": ! Pls check the attachments, barrels and scopes of it!");
				exception.printStackTrace();
			}
		} else {
			logger.info("[" + pack + "] Something went wrong while initializing the gun \"" + name + "\"! Ignoring this gun!");
		}
		configGun.save();
	}
	
	public static void addChatMessage(final ICommandSender sender, final String message) {
		String[] lines = message.split("\n");
		for (String line : lines) {
			sender.addChatMessage(new ChatComponentText(line));
		}
	}
	
	// add tooltip information with text formating and line splitting
	// will ensure it fits on minimum screen width
	public static void addTooltip(List list, String tooltip) {
		tooltip = tooltip.replace("§", "" + (char)167).replace("\\n", "\n").replace("|", "\n");
		
		String[] split = tooltip.split("\n");
		for (String line : split) {
			String lineRemaining = line;
			while (lineRemaining.length() > 38) {
				int index = lineRemaining.substring(0, 38).lastIndexOf(' ');
				if (index == -1) {
					list.add(lineRemaining);
					lineRemaining = "";
				} else {
					list.add(lineRemaining.substring(0, index));
					lineRemaining = lineRemaining.substring(index + 1);
				}
			}
			
			list.add(lineRemaining);
		}
	}
}
