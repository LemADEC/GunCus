package stuuupiiid.guncus.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.block.ContainerWeapon;
import stuuupiiid.guncus.item.ItemGun;
import stuuupiiid.guncus.network.PacketHandler;

public class GuiWeaponBlock extends GuiContainer {
	public GuiWeaponBlock(InventoryPlayer inventory, World world, int x, int y, int z) {
		super(new ContainerWeapon(inventory, world, x, y, z));
		GunCus.actualIndex = 0;
		GunCus.actualItem = null;
		if (GunCus.instance.guns.size() > 0) {
			GunCus.actualItem = GunCus.instance.guns.get(0);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		this.fontRendererObj.drawString("Weapon Box", 8, this.ySize - 160, 0x404040);
		this.fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 0x404040);
		this.buttonList.clear();
		int var5 = (this.width - this.xSize) / 2;
		int var6 = (this.height - this.ySize) / 2;
		this.buttonList.add(new GuiButton(0, var5 + 10, var6 + 40, 40, 20, "Info"));
		this.buttonList.add(new GuiButton(1, var5 + 125, var6 + 40, 40, 20, "Next"));
		this.buttonList.add(new GuiButton(2, var5 + 65, var6 + 55, 46, 20, "Create"));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(new ResourceLocation("guncus:textures/gui/gui_weapon.png"));
		int var5 = (this.width - this.xSize) / 2;
		int var6 = (this.height - this.ySize) / 2;
		drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
		ItemStack itemStack = null;
		if ( (GunCus.actualItem != null) && (GunCus.actualItem instanceof ItemGun) ) {
			int k = (this.width - this.xSize) / 2;
			int l = (this.height - this.ySize) / 2;
			itemStack = new ItemStack(GunCus.actualItem);
			GuiContainer.itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.renderEngine, itemStack, k + 80, l + 14);
			GuiContainer.itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, this.mc.renderEngine, itemStack, k + 80, l + 14);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		PacketHandler.sendToServer_GUIaction(GuiHandler.weaponBlock, button.id, GunCus.actualIndex);
	}
}
