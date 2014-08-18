package stuuupiiid.guncusexplosives;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.GunCusItem;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

@Mod(modid = "GunCusExplosives", name = "Gun Customization Explosives", version = "1.7.10 BETA v1.1")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class GunCusExplosives {
	public static Item rpgm;
	public static Item rpg;
	public static Item smawm;
	public static Item smaw;
	public static Block mineBlock;
	public static Item mineItem;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent preEvent) {
		preEvent.getModMetadata().parent = "GunCus";

		Configuration config = GunCus.config;

		config.load();
		boolean enable = config.get("Gun Customization", "Enable Explosives", true).getBoolean(true);
		config.save();

		if (!enable) {
			return;
		}

		config.load();
		int mineBlockID = config.get("Explosives IDs", "Anti Living Entity Mine (Block)", 499).getInt(499);
		int mineItemID = config.get("Explosives IDs", "Anti Living Entity Mine (Item)", 19999).getInt(19999);

		int rpgmID = config.get("Explosives IDs", "GC PG-7VL", 20000).getInt(20000);
		int rpgID = config.get("Explosives IDs", "GC RPG-7V2", 20001).getInt(20001);

		int smawmID = config.get("Explosives IDs", "GC HEDP Rocket", 20002).getInt(20002);
		int smawID = config.get("Explosives IDs", "GC SMAW", 20003).getInt(20003);
		config.save();

		mineBlock = new GunCusExplosivesMine(mineBlockID);
		mineItem = new GunCusExplosivesMineItem(mineItemID);

		rpgm = new GunCusItem(rpgmID, "guncusexplosives:rpgm", "GC PG-7VL Rocket", "gcrpgm");
		smawm = new GunCusItem(smawmID, "guncusexplosives:smawm", "GC HEDP Rocket", "gcsmawm");

		rpg = new GunCusExplosivesRPG(rpgID, "guncusexplosives:rpg", "GC RPG-7V2", "gcrpg", rpgm.itemID);
		smaw = new GunCusExplosivesRPG(smawID, "guncusexplosives:smaw", "GC SMAW", "gcsmaw", smawm.itemID);

		GameRegistry.registerBlock(mineBlock, mineBlock.getUnlocalizedName());
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		GameRegistry.addRecipe(
				new ItemStack(rpg, 1),
				new Object[] { "IIR", "IWR", "RRW", Character.valueOf('I'), new ItemStack(Item.ingotIron), Character.valueOf('W'), new ItemStack(Block.planks), Character.valueOf('R'), new ItemStack(Item.redstone) });
		GameRegistry.addRecipe(new ItemStack(smaw, 1), new Object[] { "RI ", "IRI", " IR", Character.valueOf('I'),
				new ItemStack(Item.ingotIron), Character.valueOf('R'), new ItemStack(Item.redstone) });

		GameRegistry.addRecipe(new ItemStack(rpgm, 2), new Object[] { "II ", "IG ", "  G", Character.valueOf('I'),
				new ItemStack(Item.ingotIron), Character.valueOf('G'), new ItemStack(Item.gunpowder) });
		GameRegistry.addRecipe(new ItemStack(smawm, 2), new Object[] { "G  ", " GI", " II", Character.valueOf('I'),
				new ItemStack(Item.ingotIron), Character.valueOf('G'), new ItemStack(Item.gunpowder) });
	}
}
