package stuuupiiid.guncusexplosives;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.GunCusItem;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.input.Mouse;

public class GunCusExplosivesRPG extends GunCusItem {
	public int ammo;

	public GunCusExplosivesRPG(int par1, String iconName, String name, String unlocalized, int ammo) {
		super(par1, iconName, name, unlocalized);
		setFull3D();
		this.ammo = ammo;
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
				tube(entityPlayer);
				recoilTube(entityPlayer);
				Minecraft.getMinecraft().sndManager.playSoundFX("guncus:reload_rpg", 1.0F, 1.0F);
			}
		}
	}

	private void tube(EntityPlayer entityPlayer) {
		ByteArrayDataOutput bytes = ByteStreams.newDataOutput();
		bytes.writeInt(8);
		bytes.writeInt(MathHelper.floor_double(GunCus.accuracy));
		PacketDispatcher.sendPacketToServer(new Packet250CustomPayload("guncus", bytes.toByteArray()));
	}

	private void recoilTube(EntityPlayer entityPlayer) {
		float strength = 1.5F;

		entityPlayer.rotationPitch -= strength;
		entityPlayer.rotationYaw -= (Item.itemRand.nextBoolean() ? strength / 2.0F : -strength / 2.0F);
	}
}
