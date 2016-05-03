package stuuupiiid.guncus.item;

import java.util.ArrayList;
import java.util.List;

import stuuupiiid.guncus.GunCus;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBase extends Item {
	
	public ItemBase(String unlocalizedName) {
		super();
		setMaxStackSize(1);
		setCreativeTab(GunCus.creativeTabModifications);
		setUnlocalizedName(unlocalizedName.replace(" ", "_"));
		setRegistryName(getUnlocalizedName());
		GameRegistry.registerItem(this);
	}
	
	@SideOnly(Side.CLIENT)
	public void onForgePreinit() {
		onForgePreinit(this);
	}
	
	@SideOnly(Side.CLIENT)
	public static void onForgePreinit(Item item) {
		if (!item.getHasSubtypes()) {
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
		} else {
			List<ItemStack> listItemStacks = new ArrayList(16);
			item.getSubItems(item, item.getCreativeTab(), listItemStacks);
			for (ItemStack itemStack : listItemStacks) {
				String resourceLocation = (item instanceof ItemBase) ? ((ItemBase)item).getRegistryName(itemStack) : item.getRegistryName() + "." + itemStack.getMetadata();
				ModelLoader.setCustomModelResourceLocation(item, itemStack.getMetadata(), new ModelResourceLocation(resourceLocation, "inventory"));
			}
		}
	}
	
	public String getRegistryName(ItemStack itemStack) {
		if (!itemStack.getHasSubtypes()) {
			return getRegistryName() + "." + itemStack.getMetadata();
		} else {
			return getRegistryName();
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean advancedItemTooltips) {
		String tooltip = "";
		if (StatCollector.canTranslate(getUnlocalizedName(itemStack) + ".tooltip")) {
			tooltip = StatCollector.translateToLocal(StatCollector.translateToLocalFormatted(getUnlocalizedName(itemStack) + ".tooltip"));
		} else if (StatCollector.canTranslate(super.getUnlocalizedName(itemStack) + ".tooltip")) {
			tooltip = StatCollector.translateToLocal(StatCollector.translateToLocalFormatted(super.getUnlocalizedName(itemStack) + ".tooltip"));
		}
		
		if (!tooltip.isEmpty()) {
			GunCus.addTooltip(list, tooltip);
		}
		super.addInformation(itemStack, entityPlayer, list, advancedItemTooltips);
	}
	
	protected void applyTubeRecoil(EntityPlayer entityPlayer) {
		entityPlayer.rotationPitch -= 1.25F + itemRand.nextFloat() * 0.5F;
		entityPlayer.rotationYaw += itemRand.nextFloat() * 4.0F - 2.0F;
	}
}
