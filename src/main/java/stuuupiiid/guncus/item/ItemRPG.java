package stuuupiiid.guncus.item;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.input.Mouse;

public class ItemRPG extends ItemBase {
	public Item itemAmmo;
	
	public ItemRPG(String unlocalizedName, Item itemAmmo) {
		super(unlocalizedName);
		setFull3D();
		this.itemAmmo = itemAmmo;
	}
	
	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int par1, boolean flag) {
		if (world.isRemote) {
			doUpdate(itemStack, world, entity, par1, flag);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void doUpdate(ItemStack itemStack, World world, Entity entity, int par1, boolean flag) {
		Minecraft client = FMLClientHandler.instance().getClient();
		EntityPlayer entityPlayer = client.thePlayer;
		if ((entityPlayer != null) && (entityPlayer.inventory.getCurrentItem() != null) && (entityPlayer.inventory.getCurrentItem().getItem() == this)) {
			
			if ( (GunCus.shootTime <= 0) && (Mouse.isButtonDown(0))
			  && (client.currentScreen == null)
			  && (GunCus.holdFireAfterClosingGUIcounter <= 0)
			  && (entityPlayer.inventory.hasItem(itemAmmo) || entityPlayer.capabilities.isCreativeMode)) {
				GunCus.shootTime += 140;
				GunCus.reloading = true;
				PacketHandler.sendToServer_playerAction_tube();
				applyTubeRecoil(entityPlayer);
				Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("guncus:reload_rpg")));
			}
		}
	}
}
