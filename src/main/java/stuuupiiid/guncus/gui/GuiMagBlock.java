package stuuupiiid.guncus.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import stuuupiiid.guncus.block.ContainerMag;
import stuuupiiid.guncus.network.PacketHandler;

public class GuiMagBlock extends GuiContainer {
	public GuiMagBlock(InventoryPlayer inventory, World world, int x, int y, int z) {
		super(new ContainerMag(inventory, world, x, y, z));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		this.fontRendererObj.drawString("Mag Box", 8, this.ySize - 160, 0x404040);
		this.fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 0x404040);
		this.buttonList.clear();
		int var5 = (this.width - this.xSize) / 2;
		int var6 = (this.height - this.ySize) / 2;
		this.buttonList.add(new GuiButton(0, var5 + 10, var6 + 40, 40, 20, "Create"));
		this.buttonList.add(new GuiButton(1, var5 + 125, var6 + 40, 40, 20, "Info"));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(new ResourceLocation("guncus:textures/gui/gui_mag.png"));
		int var5 = (this.width - this.xSize) / 2;
		int var6 = (this.height - this.ySize) / 2;
		drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		PacketHandler.sendToServer_GUIaction(GuiHandler.magBlock, button.id);
	}
}
