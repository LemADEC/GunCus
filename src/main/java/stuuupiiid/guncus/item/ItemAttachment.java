package stuuupiiid.guncus.item;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.input.Mouse;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.data.CustomizationPart;
import stuuupiiid.guncus.network.PacketHandler;

public class ItemAttachment extends ItemMetadata {
	public ItemAttachment(String unlocalized, String iconName, CustomizationPart[] metadatas) {
		super(unlocalized, iconName, metadatas);
	}

	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int par1, boolean flag) {
		if ((FMLCommonHandler.instance().getEffectiveSide().isClient()) && (itemStack.getItemDamage() == 3)) {
			doUpdate(itemStack, world, entity, par1, flag);
		}
	}

	@SideOnly(Side.CLIENT)
	public void doUpdate(ItemStack itemStack, World world, Entity entity, int par1, boolean flag) {
		Minecraft client = FMLClientHandler.instance().getClient();
		EntityPlayer entityPlayer = client.thePlayer;
		if ((entityPlayer != null) && (entityPlayer.inventory.getCurrentItem() != null)
				&& (entityPlayer.inventory.getCurrentItem().getItem() == this)) {
			if ((entityPlayer.inventory.getCurrentItem().getItemDamage() == 3)
					&& (GunCus.shootTime <= 0)
					&& (Mouse.isButtonDown(0))
					&& ((client.currentScreen == null) || (Mouse.isButtonDown(1)))
					&& ((entityPlayer.inventory.hasItem(GunCus.ammoM320)) || (entityPlayer.capabilities.isCreativeMode))) {
				GunCus.shootTime += 95;
				PacketHandler.sendToServer_playerAction_tube();
				recoilTube(entityPlayer);
				Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147673_a(new ResourceLocation("guncus:reload_tube"))); // FIXME: pre-load the sound resource
			}
		}
	}

	private void recoilTube(EntityPlayer entityPlayer) {
		float strength = 1.5F;

		entityPlayer.rotationPitch -= strength;
		entityPlayer.rotationYaw -= (Item.itemRand.nextBoolean() ? strength / 2.0F : -strength / 2.0F);
	}
}
