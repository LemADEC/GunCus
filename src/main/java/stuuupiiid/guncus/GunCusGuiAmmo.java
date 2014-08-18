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
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class GunCusGuiAmmo extends GuiContainer {
	public GunCusGuiAmmo(InventoryPlayer inventory, World world, int x, int y, int z) {
		super(new GunCusContainerAmmo(inventory, world, x, y, z));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		this.fontRenderer.drawString("Ammo Box", 8, this.ySize - 160, 4210752);
		this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2,
				4210752);
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
		switch (button.id) {
		case 0:
			ByteArrayDataOutput bytes = ByteStreams.newDataOutput();
			bytes.writeInt(4);
			bytes.writeInt(0);
			PacketDispatcher.sendPacketToServer(new Packet250CustomPayload("guncus", bytes.toByteArray()));
			break;
		case 1:
			ByteArrayDataOutput bytes2 = ByteStreams.newDataOutput();
			bytes2.writeInt(4);
			bytes2.writeInt(1);
			PacketDispatcher.sendPacketToServer(new Packet250CustomPayload("guncus", bytes2.toByteArray()));
		}
	}
}
