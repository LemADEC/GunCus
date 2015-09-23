package stuuupiiid.guncus.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stuuupiiid.guncus.block.ContainerBullet;
import stuuupiiid.guncus.item.ItemBullet;
import stuuupiiid.guncus.item.ItemMag;
import stuuupiiid.guncus.network.PacketHandler;

public class GuiBulletBlock extends GuiContainer {
	public GuiBulletBlock(InventoryPlayer inventory, World world, int x, int y, int z) {
		
		super(new ContainerBullet(inventory, world, x, y, z));
		
		// default size
		xSize = 176;
		ySize = 166;
	}
	
	@Override
	public void initGui() {
		super.initGui();
		
		this.buttonList.add(new ButtonWithTooltip(0, guiLeft + 10, guiTop + 40, 40, 20, "Craft", "Create the ammo"));
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		// (ancestor is empty)
		
		RenderHelper.disableStandardItemLighting();
		this.fontRendererObj.drawString(I18n.format("container.bulletbox", new Object[0]), 8, this.ySize - 160, 0x404040);
		this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 0x40404);
		
		// draw buttons tooltip
		for (Object guibutton : buttonList) {
			if (((GuiButton) guibutton).func_146115_a()) {
				((GuiButton) guibutton).func_146111_b(mouseX - guiLeft, mouseY - guiTop);
				break;
			}
		}
		// draw weapon tooltip
		ItemBullet itemBullet = getBullet();
		if (itemBullet != null) {
			if (mouseX >= guiLeft + 101 && mouseY >= guiTop + 14 && mouseX < guiLeft + 101 + 16 && mouseY < guiTop + 14 + 16) {
				renderToolTip(new ItemStack(itemBullet), mouseX - guiLeft, mouseY - guiTop);
			}
		}
		
		RenderHelper.enableGUIStandardItemLighting();
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(new ResourceLocation("guncus:textures/gui/gui_bullet.png"));
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		ItemStack itemStack = null;
		ItemBullet itemBullet = getBullet();
		if (itemBullet != null) {
			itemStack = new ItemStack(Items.iron_ingot, itemBullet.ironIngots);
			GuiContainer.itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.renderEngine, itemStack, guiLeft + 59, guiTop + 14 + 40);
			GuiContainer.itemRender.renderItemOverlayIntoGUI(fontRendererObj, mc.renderEngine, itemStack, guiLeft + 59, guiTop + 14 + 40);
			
			itemStack = new ItemStack(Items.gunpowder, itemBullet.gunpowder);
			GuiContainer.itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.renderEngine, itemStack, guiLeft + 80, guiTop + 14 + 40);
			GuiContainer.itemRender.renderItemOverlayIntoGUI(fontRendererObj, mc.renderEngine, itemStack, guiLeft + 80, guiTop + 14 + 40);
			
			itemStack = new ItemStack(itemBullet, itemBullet.stackOnCreate);
			GuiContainer.itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.renderEngine, itemStack, guiLeft + 101, guiTop + 14 + 40);
			GuiContainer.itemRender.renderItemOverlayIntoGUI(fontRendererObj, mc.renderEngine, itemStack, guiLeft + 101, guiTop + 14 + 40);
		}
	}
	
	protected ItemBullet getBullet() {
		if (inventorySlots.getSlot(0).getStack() != null && inventorySlots.getSlot(0).getStack().getItem() instanceof ItemMag) {
			ItemMag itemMag = (ItemMag) inventorySlots.getSlot(0).getStack().getItem();
			return ItemBullet.bullets.get(itemMag.pack).get(itemMag.bulletId);
		}
		return null;
	}
	
	@Override
	protected void actionPerformed(GuiButton button) {
		PacketHandler.sendToServer_GUIaction(GuiHandler.bulletBlock, button.id);
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
