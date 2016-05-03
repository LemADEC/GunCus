package stuuupiiid.guncus.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class AbstractGuiContainer extends GuiContainer {
	public AbstractGuiContainer(Container container) {
		super(container);
	}
	
	// inspired from parent class method with same name
	protected void drawItemStack(ItemStack stack, int x, int y, String altText) {
		// GlStateManager.translate(0.0F, 0.0F, 32.0F);
		// this.zLevel = 200.0F;
		// this.itemRender.zLevel = 200.0F;
		FontRenderer fontRenderer = null;
		if (stack != null) fontRenderer = stack.getItem().getFontRenderer(stack);
		if (fontRenderer == null) fontRenderer = fontRendererObj;
		itemRender.renderItemAndEffectIntoGUI(stack, x, y);
		itemRender.renderItemOverlayIntoGUI(fontRenderer, stack, x, y, altText);
		// this.zLevel = 0.0F;
		// this.itemRender.zLevel = 0.0F;
	}
	
	@SideOnly(Side.CLIENT)
	class ButtonWithTooltip extends GuiButton {
		private String helpString = "";
		
		public ButtonWithTooltip(int id, int xPosition, int yPosition, int width, int height, String displayString, String helpString) {
			super(id, xPosition, yPosition, width, height, displayString);
			this.helpString = helpString;
		}
		
		@Override
		public void drawButtonForegroundLayer(int mouseX, int mouseY) {
			drawCreativeTabHoveringText(I18n.format(helpString, new Object[0]), mouseX, mouseY);
		}
	}
}
