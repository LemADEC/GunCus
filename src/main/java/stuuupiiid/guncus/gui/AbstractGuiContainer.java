package stuuupiiid.guncus.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class AbstractGuiContainer extends GuiContainer {
	public AbstractGuiContainer(Container container) {
		super(container);
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
