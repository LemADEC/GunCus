/*    */ package assets.guncus;
/*    */ 
/*    */ import com.google.common.io.ByteArrayDataOutput;
/*    */ import com.google.common.io.ByteStreams;
/*    */ import cpw.mods.fml.common.network.PacketDispatcher;
/*    */ import java.util.List;
/*    */ import net.minecraft.client.Minecraft;
/*    */ import net.minecraft.client.gui.FontRenderer;
/*    */ import net.minecraft.client.gui.GuiButton;
/*    */ import net.minecraft.client.gui.GuiScreen;
/*    */ import net.minecraft.client.gui.inventory.GuiContainer;
/*    */ import net.minecraft.client.renderer.texture.TextureManager;
/*    */ import net.minecraft.entity.player.InventoryPlayer;
/*    */ import net.minecraft.network.packet.Packet250CustomPayload;
/*    */ import net.minecraft.util.ResourceLocation;
/*    */ import net.minecraft.util.StatCollector;
/*    */ import net.minecraft.world.World;
/*    */ import org.lwjgl.opengl.GL11;
/*    */ 
/*    */ public class GunCusGuiAmmo extends GuiContainer
/*    */ {
/*    */   public GunCusGuiAmmo(InventoryPlayer inventory, World world, int x, int y, int z)
/*    */   {
/* 23 */     super(new GunCusContainerAmmo(inventory, world, x, y, z));
/*    */   }
/*    */ 
/*    */   protected void drawGuiContainerForegroundLayer(int par1, int par2)
/*    */   {
/* 28 */     this.fontRenderer.drawString("Ammo Box", 8, this.ySize - 160, 4210752);
/* 29 */     this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
/* 30 */     this.buttonList.clear();
/* 31 */     int var5 = (this.width - this.xSize) / 2;
/* 32 */     int var6 = (this.height - this.ySize) / 2;
/* 33 */     this.buttonList.add(new GuiButton(0, var5 + 10, var6 + 40, 40, 20, "Fill Up"));
/* 34 */     this.buttonList.add(new GuiButton(1, var5 + 125, var6 + 40, 40, 20, "Empty"));
/*    */   }
/*    */ 
/*    */   protected void drawGuiContainerBackgroundLayer(float f, int x, int y)
/*    */   {
/* 39 */     GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
/* 40 */     this.mc.getTextureManager().bindTexture(new ResourceLocation("guncus:textures/gui/gui_ammo.png"));
/* 41 */     int var5 = (this.width - this.xSize) / 2;
/* 42 */     int var6 = (this.height - this.ySize) / 2;
/* 43 */     drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
/*    */   }
/*    */ 
/*    */   protected void actionPerformed(GuiButton button)
/*    */   {
/* 48 */     switch (button.id)
/*    */     {
/*    */     case 0:
/* 51 */       ByteArrayDataOutput bytes = ByteStreams.newDataOutput();
/* 52 */       bytes.writeInt(4);
/* 53 */       bytes.writeInt(0);
/* 54 */       PacketDispatcher.sendPacketToServer(new Packet250CustomPayload("guncus", bytes.toByteArray()));
/* 55 */       break;
/*    */     case 1:
/* 57 */       ByteArrayDataOutput bytes2 = ByteStreams.newDataOutput();
/* 58 */       bytes2.writeInt(4);
/* 59 */       bytes2.writeInt(1);
/* 60 */       PacketDispatcher.sendPacketToServer(new Packet250CustomPayload("guncus", bytes2.toByteArray()));
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Nate\Desktop\Mod\GunCusClass.zip
 * Qualified Name:     assets.guncus.GunCusGuiAmmo
 * JD-Core Version:    0.6.2
 */