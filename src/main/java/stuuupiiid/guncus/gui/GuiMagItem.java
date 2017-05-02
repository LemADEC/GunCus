package stuuupiiid.guncus.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import stuuupiiid.guncus.block.ContainerAmmoMan;
import stuuupiiid.guncus.network.PacketHandler;

public class GuiMagItem extends AbstractGuiContainer {
	public GuiMagItem(InventoryPlayer inventory) {
		super(new ContainerAmmoMan(inventory));
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		this.fontRendererObj.drawString(I18n.format("container.manualmagfiller", new Object[0]), 8, this.ySize - 160, 0x404040);
		this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 0x404040);
		this.buttonList.clear();
		int var5 = (this.width - this.xSize) / 2;
		int var6 = (this.height - this.ySize) / 2;
		this.buttonList.add(new GuiButton(0, var5 + 10, var6 + 40, 40, 20, "Fill Up"));
		this.buttonList.add(new GuiButton(1, var5 + 125, var6 + 40, 40, 20, "Empty"));
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(new ResourceLocation("guncus:textures/gui/gui_ammo.png"));
		int var5 = (this.width - this.xSize) / 2;
		int var6 = (this.height - this.ySize) / 2;
		drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) {
		PacketHandler.sendToServer_GUIaction(GuiHandler.magItem, button.id);
	}
}
