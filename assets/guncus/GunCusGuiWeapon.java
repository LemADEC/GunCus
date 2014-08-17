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
/*    */ import net.minecraft.client.renderer.entity.RenderItem;
/*    */ import net.minecraft.client.renderer.texture.TextureManager;
/*    */ import net.minecraft.entity.player.InventoryPlayer;
/*    */ import net.minecraft.item.Item;
/*    */ import net.minecraft.item.ItemStack;
/*    */ import net.minecraft.network.packet.Packet250CustomPayload;
/*    */ import net.minecraft.util.ResourceLocation;
/*    */ import net.minecraft.util.StatCollector;
/*    */ import net.minecraft.world.World;
/*    */ import org.lwjgl.opengl.GL11;
/*    */ 
/*    */ public class GunCusGuiWeapon extends GuiContainer
/*    */ {
/*    */   public GunCusGuiWeapon(InventoryPlayer inventory, World world, int x, int y, int z)
/*    */   {
/* 26 */     super(new GunCusContainerWeapon(inventory, world, x, y, z));
/* 27 */     GunCus.actual = 0;
/* 28 */     GunCus.actualItemID = 0;
/* 29 */     if (GunCusItemGun.gunList.size() > 0)
/*    */     {
/* 31 */       GunCus.actualItemID = ((GunCusItemGun)GunCusItemGun.gunList.get(0)).itemID;
/*    */     }
/*    */   }
/*    */ 
/*    */   protected void drawGuiContainerForegroundLayer(int par1, int par2)
/*    */   {
/* 37 */     this.fontRenderer.drawString("Weapon Box", 8, this.ySize - 160, 4210752);
/* 38 */     this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
/* 39 */     this.buttonList.clear();
/* 40 */     int var5 = (this.width - this.xSize) / 2;
/* 41 */     int var6 = (this.height - this.ySize) / 2;
/* 42 */     this.buttonList.add(new GuiButton(0, var5 + 10, var6 + 40, 40, 20, "Info"));
/* 43 */     this.buttonList.add(new GuiButton(1, var5 + 125, var6 + 40, 40, 20, "Next"));
/* 44 */     this.buttonList.add(new GuiButton(2, var5 + 65, var6 + 55, 46, 20, "Create"));
/*    */   }
/*    */ 
/*    */   protected void drawGuiContainerBackgroundLayer(float f, int x, int y)
/*    */   {
/* 49 */     GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
/* 50 */     this.mc.getTextureManager().bindTexture(new ResourceLocation("guncus:textures/gui/gui_weapon.png"));
/* 51 */     int var5 = (this.width - this.xSize) / 2;
/* 52 */     int var6 = (this.height - this.ySize) / 2;
/* 53 */     drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
/* 54 */     ItemStack itemStack = null;
/* 55 */     if ((GunCus.actualItemID > 0) && (Item.itemsList[GunCus.actualItemID] != null) && ((Item.itemsList[GunCus.actualItemID] instanceof GunCusItemGun)))
/*    */     {
/* 57 */       int k = (this.width - this.xSize) / 2;
/* 58 */       int l = (this.height - this.ySize) / 2;
/* 59 */       itemStack = new ItemStack(Item.itemsList[GunCus.actualItemID]);
/* 60 */       GuiContainer.itemRenderer.renderItemAndEffectIntoGUI(this.fontRenderer, this.mc.renderEngine, itemStack, k + 80, l + 14);
/* 61 */       GuiContainer.itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.renderEngine, itemStack, k + 80, l + 14);
/*    */     }
/*    */   }
/*    */ 
/*    */   protected void actionPerformed(GuiButton button)
/*    */   {
/* 67 */     switch (button.id)
/*    */     {
/*    */     case 1:
/* 70 */       if (GunCusItemGun.gunList.size() > 0)
/*    */       {
/* 72 */         ByteArrayDataOutput bytes3 = ByteStreams.newDataOutput();
/* 73 */         bytes3.writeInt(9);
/* 74 */         bytes3.writeInt(1);
/* 75 */         bytes3.writeInt(GunCus.actual);
/* 76 */         PacketDispatcher.sendPacketToServer(new Packet250CustomPayload("guncus", bytes3.toByteArray()));
/* 77 */       }break;
/*    */     case 0:
/* 80 */       ByteArrayDataOutput bytes = ByteStreams.newDataOutput();
/* 81 */       bytes.writeInt(9);
/* 82 */       bytes.writeInt(0);
/* 83 */       PacketDispatcher.sendPacketToServer(new Packet250CustomPayload("guncus", bytes.toByteArray()));
/* 84 */       break;
/*    */     case 2:
/* 86 */       ByteArrayDataOutput bytes2 = ByteStreams.newDataOutput();
/* 87 */       bytes2.writeInt(9);
/* 88 */       bytes2.writeInt(2);
/* 89 */       PacketDispatcher.sendPacketToServer(new Packet250CustomPayload("guncus", bytes2.toByteArray()));
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Nate\Desktop\Mod\GunCusClass.zip
 * Qualified Name:     assets.guncus.GunCusGuiWeapon
 * JD-Core Version:    0.6.2
 */