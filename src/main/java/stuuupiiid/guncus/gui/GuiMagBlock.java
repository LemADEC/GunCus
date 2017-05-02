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

import stuuupiiid.guncus.block.ContainerMag;
import stuuupiiid.guncus.item.ItemGun;
import stuuupiiid.guncus.network.PacketHandler;

public class GuiMagBlock extends AbstractGuiContainer {
	public GuiMagBlock(InventoryPlayer inventory, World world, int x, int y, int z) {
		
		super(new ContainerMag(inventory, world, x, y, z));
		
		// default size
		xSize = 176;
		ySize = 166;
	}
	
	@Override
	public void initGui(){
		super.initGui();
		
		this.buttonList.add(new ButtonWithTooltip(0, guiLeft + 125, guiTop + 33, 40, 20, "Craft", "Create a magazine"));
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.fontRendererObj.drawString(I18n.format("container.magbox", new Object[0]), 8, this.ySize - 160, 0x404040);
		this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 0x404040);
		// draw buttons tooltip
		for (Object guibutton : buttonList) {
			if (((GuiButton) guibutton).func_146115_a()) {
				((GuiButton) guibutton).func_146111_b(mouseX - guiLeft, mouseY - guiTop);
				break;
			}
		}
		
		// draw weapon tooltip
		ItemGun itemGun = getGun();
		if (itemGun != null) {
			if (mouseX >= guiLeft + 101 && mouseY >= guiTop + 14 && mouseX < guiLeft + 101 + 16 && mouseY < guiTop + 14 + 16) {
				renderToolTip(new ItemStack(itemGun), mouseX - guiLeft, mouseY - guiTop);
			}
		}
		
		RenderHelper.enableGUIStandardItemLighting();
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(new ResourceLocation("guncus:textures/gui/gui_mag.png"));
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize);
		
		ItemStack itemStack = null;
		ItemGun itemGun = getGun();
		if (itemGun != null) {
			itemStack = new ItemStack(Items.iron_ingot, itemGun.magIronIngots);
			GuiContainer.itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.renderEngine, itemStack, guiLeft + 59, guiTop + 14 + 40);
			GuiContainer.itemRender.renderItemOverlayIntoGUI(fontRendererObj, mc.renderEngine, itemStack, guiLeft + 59, guiTop + 14 + 40);
						
			itemStack = new ItemStack(itemGun.mag);
			GuiContainer.itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.renderEngine, itemStack, guiLeft + 101, guiTop + 14 + 40);
			GuiContainer.itemRender.renderItemOverlayIntoGUI(fontRendererObj, mc.renderEngine, itemStack, guiLeft + 101, guiTop + 14 + 40);
		}
	}
	
	protected ItemGun getGun() {
		if (inventorySlots.getSlot(0).getStack() != null && inventorySlots.getSlot(0).getStack().getItem() instanceof ItemGun) {
			ItemGun itemGun= (ItemGun) inventorySlots.getSlot(0).getStack().getItem();
			if (itemGun.mag != null) {
				return itemGun;
			}
		}
		return null;
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		PacketHandler.sendToServer_GUIaction(GuiHandler.magBlock, button.id);
	}
}
