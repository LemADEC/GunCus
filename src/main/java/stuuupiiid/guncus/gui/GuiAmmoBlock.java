package stuuupiiid.guncus.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import stuuupiiid.guncus.block.ContainerAmmo;
import stuuupiiid.guncus.item.ItemBullet;
import stuuupiiid.guncus.item.ItemMag;
import stuuupiiid.guncus.network.PacketHandler;

public class GuiAmmoBlock extends AbstractGuiContainer {
	public GuiAmmoBlock(InventoryPlayer inventory, World world, int x, int y, int z) {
		
		super(new ContainerAmmo(inventory, world, x, y, z));
		
		// default size
		xSize = 176;
		ySize = 166;
	}
	
	@Override
	public void initGui(){
		super.initGui();
		
		buttonList.add(new ButtonWithTooltip(0, guiLeft + 10, guiTop + 40, 40, 20, "Fill", "Fill the magazine"));
		buttonList.add(new ButtonWithTooltip(1, guiLeft + 125, guiTop + 40, 40, 20, "Empty", "Empty the magazine"));
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		// (ancestor is empty)
		
		RenderHelper.disableStandardItemLighting();
		fontRendererObj.drawString(I18n.format("container.ammobox", new Object[0]), 8, ySize - 160, 0x404040);
		fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, ySize - 96 + 2, 0x404040);
		
		// draw buttons tooltip
		for (Object guibutton : buttonList) {
			if (((GuiButton) guibutton).func_146115_a()) {
				((GuiButton) guibutton).func_146111_b(mouseX - guiLeft, mouseY - guiTop);
				break;
			}
		}
		
		// draw bullet tooltip
		ItemBullet itemBullet = getBullet();
		if (itemBullet != null) {
			if (mouseX >= guiLeft + 101 && mouseY >= guiTop + 14 + 40 && mouseX < guiLeft + 101 + 16 && mouseY < guiTop + 14 + 16 + 40) {
				renderToolTip(new ItemStack(itemBullet), mouseX - guiLeft, mouseY - guiTop);
			}
		}
		
		RenderHelper.enableGUIStandardItemLighting();
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
		// (ancestor is abstract)
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(new ResourceLocation("guncus:textures/gui/gui_ammo.png"));
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		ItemStack itemStack = null;
		ItemBullet itemBullet = getBullet();
		if (itemBullet != null) {
			itemStack = new ItemStack(itemBullet);
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
		PacketHandler.sendToServer_GUIaction(GuiHandler.ammoBlock, button.id);
	}
}
