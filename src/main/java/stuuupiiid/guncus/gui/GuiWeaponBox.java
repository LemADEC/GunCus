package stuuupiiid.guncus.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.block.ContainerWeaponBox;
import stuuupiiid.guncus.item.ItemGun;
import stuuupiiid.guncus.network.PacketHandler;

public class GuiWeaponBox extends AbstractGuiContainer {
	public GuiWeaponBox(InventoryPlayer inventory, World world, int x, int y, int z) {
		super(new ContainerWeaponBox(inventory, world, x, y, z));
		
		if (GunCus.guns.size() > 0) {
			if (GunCus.clientGUI_actualGunName == null || GunCus.clientGUI_actualGunName.isEmpty() || GunCus.guns.get(GunCus.clientGUI_actualGunName) == null) {
				GunCus.clientGUI_actualGunName = GunCus.gunNames.toArray(new String[0])[0];
			}
		} else {
			GunCus.clientGUI_actualGunName = null;
		}
		
		// default size
		xSize = 176;
		ySize = 166;
	}
	
	@Override
	public void initGui() {
		super.initGui();
		
		buttonList.add(new ButtonWithTooltip(0, guiLeft + 32, guiTop + 33, 20, 20, "<", "Previous weapon"));
		buttonList.add(new ButtonWithTooltip(1, guiLeft + 124, guiTop + 33, 20, 20, ">", "Next weapon"));
		buttonList.add(new ButtonWithTooltip(2, guiLeft + 68, guiTop + 55, 40, 20, "Craft", "Craft this weapon"));
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		// (ancestor is empty)
		
		RenderHelper.disableStandardItemLighting();
		fontRendererObj.drawString(I18n.format("container.weaponbox", new Object[0]), 8, ySize - 160, 0x404040);
		fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, ySize - 96 + 2, 0x404040);
		
		// draw buttons tooltip
		for (Object guibutton : buttonList) {
			if (((GuiButton) guibutton).isMouseOver()) {
				((GuiButton) guibutton).drawButtonForegroundLayer(mouseX - guiLeft, mouseY - guiTop);
				break;
			}
		}
		
		// draw weapon tooltip
		if ((GunCus.clientGUI_actualGunName != null) && GunCus.guns.get(GunCus.clientGUI_actualGunName) != null) {
			if (mouseX >= guiLeft + 101 && mouseY >= guiTop + 14 && mouseX < guiLeft + 101 + 16 && mouseY < guiTop + 14 + 16) {
				ItemGun itemGun = GunCus.guns.get(GunCus.clientGUI_actualGunName);
				renderToolTip(new ItemStack(itemGun), mouseX - guiLeft, mouseY - guiTop);
			}
		}
		
		RenderHelper.enableGUIStandardItemLighting();
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
		// (ancestor is abstract)
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(new ResourceLocation("guncus:textures/gui/gui_weapon.png"));
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		ItemStack itemStack = null;
		if ((GunCus.clientGUI_actualGunName != null) && GunCus.guns.get(GunCus.clientGUI_actualGunName) != null) {
			ItemGun itemGun = GunCus.guns.get(GunCus.clientGUI_actualGunName);
			itemStack = new ItemStack(Items.iron_ingot, itemGun.gunIronIngots);
			drawItemStack(itemStack, guiLeft + 59, guiTop + 14, "");
			
			itemStack = new ItemStack(Items.redstone, itemGun.gunRedstone);
			drawItemStack(itemStack, guiLeft + 80, guiTop + 14, "");
			
			itemStack = new ItemStack(itemGun);
			drawItemStack(itemStack, guiLeft + 101, guiTop + 14, "");
		}
	}
	
	@Override
	protected void actionPerformed(GuiButton button) {
		PacketHandler.sendToServer_GUIaction(GuiHandler.weaponBox, button.id, GunCus.clientGUI_actualGunName);
	}
}
