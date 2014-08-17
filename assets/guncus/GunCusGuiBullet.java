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
/*    */ public class GunCusGuiBullet extends GuiContainer
/*    */ {
/*    */   public GunCusGuiBullet(InventoryPlayer inventory, World world, int x, int y, int z)
/*    */   {
/* 25 */     super(new GunCusContainerBullet(inventory, world, x, y, z));
/*    */   }
/*    */ 
/*    */   protected void drawGuiContainerForegroundLayer(int par1, int par2)
/*    */   {
/* 30 */     this.fontRenderer.drawString("Bullet Box", 8, this.ySize - 160, 4210752);
/* 31 */     this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
/* 32 */     this.buttonList.clear();
/* 33 */     int var5 = (this.width - this.xSize) / 2;
/* 34 */     int var6 = (this.height - this.ySize) / 2;
/* 35 */     this.buttonList.add(new GuiButton(0, var5 + 10, var6 + 40, 40, 20, "Create"));
/* 36 */     this.buttonList.add(new GuiButton(1, var5 + 125, var6 + 40, 40, 20, "Info"));
/*    */   }
/*    */ 
/*    */   protected void drawGuiContainerBackgroundLayer(float f, int x, int y)
/*    */   {
/* 41 */     GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
/* 42 */     this.mc.getTextureManager().bindTexture(new ResourceLocation("guncus:textures/gui/gui_bullet.png"));
/* 43 */     int var5 = (this.width - this.xSize) / 2;
/* 44 */     int var6 = (this.height - this.ySize) / 2;
/* 45 */     drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
/*    */   }
/*    */ 
/*    */   protected void actionPerformed(GuiButton button)
/*    */   {
/* 50 */     switch (button.id)
/*    */     {
/*    */     case 0:
/* 53 */       ByteArrayDataOutput bytes = ByteStreams.newDataOutput();
/* 54 */       bytes.writeInt(7);
/* 55 */       bytes.writeInt(0);
/* 56 */       PacketDispatcher.sendPacketToServer(new Packet250CustomPayload("guncus", bytes.toByteArray()));
/* 57 */       break;
/*    */     case 1:
/* 59 */       ByteArrayDataOutput bytes2 = ByteStreams.newDataOutput();
/* 60 */       bytes2.writeInt(7);
/* 61 */       bytes2.writeInt(1);
/* 62 */       PacketDispatcher.sendPacketToServer(new Packet250CustomPayload("guncus", bytes2.toByteArray()));
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Nate\Desktop\Mod\GunCusClass.zip
 * Qualified Name:     assets.guncus.GunCusGuiBullet
 * JD-Core Version:    0.6.2
 */