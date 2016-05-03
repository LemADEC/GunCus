package stuuupiiid.guncus.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.input.Mouse;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.data.ModifierPart;
import stuuupiiid.guncus.network.PacketHandler;

public class ItemAttachmentPart extends ItemModifierPart {
	public ItemAttachmentPart(String unlocalizedName, ModifierPart[] modifierParts) {
		super(unlocalizedName, modifierParts);
	}
	
	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int par1, boolean flag) {
		if (world.isRemote && (itemStack.getItemDamage() == 4) && entity instanceof EntityPlayer) {
			doUpdate(itemStack, world, entity, par1, flag);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void doUpdate(ItemStack itemStack, World world, Entity entity, int par1, boolean flag) {
		Minecraft client = FMLClientHandler.instance().getClient();
		EntityPlayer entityPlayer = client.thePlayer;
		if ( (entityPlayer != null)
		  && (entityPlayer.inventory.getCurrentItem() != null)
		  && (entityPlayer.inventory.getCurrentItem().getItem() == GunCus.itemAttachment)
		  && (entityPlayer.inventory.getCurrentItem().getItemDamage() == 4)
		  && (GunCus.shootTime <= 0)
		  && (Mouse.isButtonDown(0))
		  && (client.currentScreen == null)
		  && (GunCus.holdFireAfterClosingGUIcounter <= 0)
		  && (entityPlayer.inventory.hasItem(GunCus.itemAmmoM320) || entityPlayer.capabilities.isCreativeMode) ) {
			GunCus.shootTime += 95;
			GunCus.reloading = true;
			PacketHandler.sendToServer_playerAction_tube();
			applyTubeRecoil(entityPlayer);
			Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("guncus:reload_tube"))); // FIXME: pre-load the sound resource
		}
	}
}
