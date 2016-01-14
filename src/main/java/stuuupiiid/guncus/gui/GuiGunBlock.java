package stuuupiiid.guncus.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stuuupiiid.guncus.block.ContainerGun;
import stuuupiiid.guncus.network.PacketHandler;

public class GuiGunBlock extends GuiContainer {
	public GuiGunBlock(InventoryPlayer inventory, World world, int x, int y, int z) {
		
		super(new ContainerGun(inventory, world, x, y, z));
		
		// default size
		xSize = 176;
		ySize = 166;		
	}
	
	@Override
	public void initGui() {
		super.initGui();
		
		buttonList.add(new ButtonWithTooltip(0, guiLeft + 10, guiTop + 40, 40, 20, "Split", "Remove all attatchments."));
		buttonList.add(new ButtonWithTooltip(1, guiLeft + 125, guiTop + 40, 40, 20, "Build", "Add attachments to weapon."));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		// (ancestor is empty)
		
		RenderHelper.disableStandardItemLighting();
		fontRendererObj.drawString(I18n.format("container.gunbox", new Object[0]), 8, this.ySize - 160, 0x404040);
		fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 0x404040);

		// draw buttons tooltip
		for (Object guibutton : buttonList) {
			if (((GuiButton) guibutton).func_146115_a()) {
				((GuiButton) guibutton).func_146111_b(mouseX - guiLeft, mouseY - guiTop);
				break;
			}
		}
		//draw slots tooltips
		if (mouseX >= guiLeft + 80 && mouseY >= guiTop + 14 && mouseX < guiLeft + 96 && mouseY < guiTop + 30) {
			mc.fontRenderer.drawString("Scopes", mouseX - guiLeft + 8, mouseY - guiTop, 0xFFFFFF, true);		
		}
		if (mouseX >= guiLeft + 59 && mouseY >= guiTop + 35 && mouseX < guiLeft + 75 && mouseY < guiTop + 51) {
			mc.fontRenderer.drawString("Barrels", mouseX - guiLeft + 8, mouseY - guiTop, 0xFFFFFF, true);		
		}
		if (mouseX >= guiLeft + 80 && mouseY >= guiTop + 56 && mouseX < guiLeft + 96 && mouseY < guiTop + 72) {
			mc.fontRenderer.drawString("Under Barrel", mouseX - guiLeft + 8, mouseY - guiTop, 0xFFFFFF, true);	

		}
		
		RenderHelper.enableStandardItemLighting();
		
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
		//ancestor is abstract)
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(new ResourceLocation("guncus:textures/gui/gui_gun.png"));
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize);
		

	}

	@Override
	protected void actionPerformed(GuiButton button) {
		PacketHandler.sendToServer_GUIaction(GuiHandler.gunBlock, button.id);
	}
	
	@SideOnly(Side.CLIENT)
	class ButtonWithTooltip extends GuiButton {
		private String helpString = "";
		
		public ButtonWithTooltip(int id, int xPosition, int yPosition, int width, int height, String displayString, String helpString) {
			super(id, xPosition, yPosition, width, height, displayString);
			this.helpString = helpString;
		}
		
		@Override
		public void func_146111_b(int mouseX, int mouseY) {
			drawCreativeTabHoveringText(I18n.format(helpString, new Object[0]), mouseX, mouseY);
		}
	}
	

}
