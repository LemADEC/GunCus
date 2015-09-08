package stuuupiiid.guncus.item;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.network.PacketHandler;

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

public class ItemRPG extends GunCusItem {
	public Item ammo;

	public ItemRPG(String iconName, String name, String unlocalized, Item parAmmo) {
		super(iconName, name, unlocalized);
		setFull3D();
		ammo = parAmmo;
	}

	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int par1, boolean flag) {
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			doUpdate(itemStack, world, entity, par1, flag);
		}
	}

	@SideOnly(Side.CLIENT)
	public void doUpdate(ItemStack itemStack, World world, Entity entity, int par1, boolean flag) {
		Minecraft client = FMLClientHandler.instance().getClient();
		EntityPlayer entityPlayer = client.thePlayer;
		if ((entityPlayer != null) && (entityPlayer.inventory.getCurrentItem() != null)
				&& (entityPlayer.inventory.getCurrentItem().getItem() == this)) {

			if ((GunCus.shootTime <= 0) && (Mouse.isButtonDown(0))
					&& ((client.currentScreen == null) || (Mouse.isButtonDown(1)))
					&& ((entityPlayer.inventory.hasItem(this.ammo)) || (entityPlayer.capabilities.isCreativeMode))) {
				GunCus.shootTime += 90;
				PacketHandler.sendToServer_playerAction_tube();
				recoilTube(entityPlayer);
				Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147673_a(new ResourceLocation("guncus:reload_rpg")));
			}
		}
	}

	private void recoilTube(EntityPlayer entityPlayer) {
		float strength = 1.5F;

		entityPlayer.rotationPitch -= strength;
		entityPlayer.rotationYaw -= (Item.itemRand.nextBoolean() ? strength / 2.0F : -strength / 2.0F);
	}
}
