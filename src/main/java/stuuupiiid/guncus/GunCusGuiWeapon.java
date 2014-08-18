package stuuupiiid.guncus;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.network.PacketDispatcher;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class GunCusGuiWeapon extends GuiContainer {
	public GunCusGuiWeapon(InventoryPlayer inventory, World world, int x, int y, int z) {
		super(new GunCusContainerWeapon(inventory, world, x, y, z));
		GunCus.actual = 0;
		GunCus.actualItemID = 0;
		if (GunCusItemGun.gunList.size() > 0) {
			GunCus.actualItemID = GunCusItemGun.gunList.get(0).itemID;
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		this.fontRenderer.drawString("Weapon Box", 8, this.ySize - 160, 4210752);
		this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2,
				4210752);
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
		if ((GunCus.actualItemID > 0) && (Item.itemsList[GunCus.actualItemID] != null)
				&& ((Item.itemsList[GunCus.actualItemID] instanceof GunCusItemGun))) {
			int k = (this.width - this.xSize) / 2;
			int l = (this.height - this.ySize) / 2;
			itemStack = new ItemStack(Item.itemsList[GunCus.actualItemID]);
			GuiContainer.itemRenderer.renderItemAndEffectIntoGUI(this.fontRenderer, this.mc.renderEngine, itemStack,
					k + 80, l + 14);
			GuiContainer.itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.renderEngine, itemStack,
					k + 80, l + 14);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
		case 1:
			if (GunCusItemGun.gunList.size() > 0) {
				ByteArrayDataOutput bytes3 = ByteStreams.newDataOutput();
				bytes3.writeInt(9);
				bytes3.writeInt(1);
				bytes3.writeInt(GunCus.actual);
				PacketDispatcher.sendPacketToServer(new Packet250CustomPayload("guncus", bytes3.toByteArray()));
			}
			break;
		case 0:
			ByteArrayDataOutput bytes = ByteStreams.newDataOutput();
			bytes.writeInt(9);
			bytes.writeInt(0);
			PacketDispatcher.sendPacketToServer(new Packet250CustomPayload("guncus", bytes.toByteArray()));
			break;
		case 2:
			ByteArrayDataOutput bytes2 = ByteStreams.newDataOutput();
			bytes2.writeInt(9);
			bytes2.writeInt(2);
			PacketDispatcher.sendPacketToServer(new Packet250CustomPayload("guncus", bytes2.toByteArray()));
		}
	}
}
