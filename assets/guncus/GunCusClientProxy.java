/*    */ package assets.guncus;
/*    */ 
/*    */ import cpw.mods.fml.client.FMLClientHandler;
/*    */ import cpw.mods.fml.client.registry.RenderingRegistry;
/*    */ import cpw.mods.fml.common.ObfuscationReflectionHelper;
/*    */ import net.minecraft.client.Minecraft;
/*    */ import net.minecraft.client.gui.ScaledResolution;
/*    */ import net.minecraft.client.renderer.EntityRenderer;
/*    */ import net.minecraft.client.renderer.Tessellator;
/*    */ import net.minecraft.client.renderer.texture.TextureManager;
/*    */ import net.minecraft.client.settings.GameSettings;
/*    */ import net.minecraft.entity.player.EntityPlayer;
/*    */ import net.minecraft.entity.player.InventoryPlayer;
/*    */ import net.minecraft.item.ItemStack;
/*    */ import net.minecraft.util.ResourceLocation;
/*    */ import net.minecraftforge.common.MinecraftForge;
/*    */ import net.minecraftforge.event.EventBus;
/*    */ import org.lwjgl.input.Mouse;
/*    */ 
/*    */ public class GunCusClientProxy extends GunCusCommonProxy
/*    */ {
/*    */   public void render()
/*    */   {
/* 29 */     RenderingRegistry.registerEntityRenderingHandler(GunCusEntityBullet.class, new GunCusRenderBullet());
/* 30 */     RenderingRegistry.registerEntityRenderingHandler(GunCusEntityAT.class, new GunCusRenderAT());
/*    */   }
/*    */ 
/*    */   public void sound()
/*    */   {
/* 35 */     MinecraftForge.EVENT_BUS.register(new GunCusSound());
/*    */   }
/*    */ 
/*    */   public void sight()
/*    */   {
/* 40 */     Minecraft client = FMLClientHandler.instance().getClient();
/* 41 */     if (client.thePlayer != null)
/*    */     {
/* 43 */       EntityPlayer entityPlayer = client.thePlayer;
/* 44 */       if ((Mouse.isButtonDown(1)) && (entityPlayer.inventory.getCurrentItem() != null) && ((entityPlayer.inventory.getCurrentItem().getItem() instanceof GunCusItemGun)) && (client.currentScreen == null) && (client.gameSettings.thirdPersonView == 0))
/*    */       {
/* 46 */         GunCusItemGun gun = (GunCusItemGun)entityPlayer.inventory.getCurrentItem().getItem();
/* 47 */         int scopeId = gun.getZoom(entityPlayer.inventory.getCurrentItem().getItemDamage());
/* 48 */         String scope = "";
/* 49 */         float newZoom = gun.zoom + 0.1F;
/* 50 */         if (scopeId > 0)
/*    */         {
/* 52 */           GunCusScope scope2 = (GunCusScope)GunCus.scope.metadatas[(scopeId - 1)];
/* 53 */           newZoom = scope2.zoom + 0.1F;
/* 54 */           scope = scope2.sight;
/*    */         }
/*    */         String path;
/* 59 */         if (gun.isOfficial)
/*    */         {
/* 61 */           path = "guncus:textures/sights/" + scope;
/*    */         }
/*    */         else
/*    */         {
/* 65 */           path = "guncus:textures/sights/" + scope;
/*    */         }
/*    */ 
/* 68 */         if (GunCus.zoomLevel < newZoom)
/*    */         {
/* 70 */           GunCus.zoomLevel = (float)(GunCus.zoomLevel + 2.5D);
/*    */         }
/* 72 */         if (GunCus.zoomLevel > newZoom)
/*    */         {
/* 74 */           GunCus.zoomLevel = newZoom;
/*    */         }
/* 76 */         ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, client.entityRenderer, Float.valueOf(GunCus.zoomLevel), new String[] { "cameraZoom", GunCus.cameraZoom });
/* 77 */         ScaledResolution scale = new ScaledResolution(client.gameSettings, client.displayWidth, client.displayHeight);
/* 78 */         int xCenter = scale.getScaledWidth() / 2;
/* 79 */         int offset = scale.getScaledHeight() * 2;
/* 80 */         client.getTextureManager().bindTexture(new ResourceLocation((!gun.usingDefault ? path : "guncus:textures/items/gun_default/sight") + ".png"));
/* 81 */         Tessellator tessellator = Tessellator.instance;
/* 82 */         tessellator.startDrawingQuads();
/* 83 */         tessellator.addVertexWithUV(xCenter - offset, scale.getScaledHeight(), -100.0D, 0.0D, 1.0D);
/* 84 */         tessellator.addVertexWithUV(xCenter + offset, scale.getScaledHeight(), -100.0D, 1.0D, 1.0D);
/* 85 */         tessellator.addVertexWithUV(xCenter + offset, 0.0D, -100.0D, 1.0D, 0.0D);
/* 86 */         tessellator.addVertexWithUV(xCenter - offset, 0.0D, -100.0D, 0.0D, 0.0D);
/* 87 */         tessellator.draw();
/*    */       }
/*    */       else
/*    */       {
/* 91 */         if (GunCus.zoomLevel > 1.0D)
/*    */         {
/* 93 */           GunCus.zoomLevel = (float)(GunCus.zoomLevel - 2.5D);
/*    */         }
/* 95 */         if (GunCus.zoomLevel < 1.0D)
/*    */         {
/* 97 */           GunCus.zoomLevel = 1.0F;
/*    */         }
/* 99 */         ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, client.entityRenderer, Float.valueOf(GunCus.zoomLevel), new String[] { "cameraZoom", GunCus.cameraZoom });
/*    */       }
/*    */ 
/* 102 */       if ((entityPlayer.inventory.getCurrentItem() != null) && (entityPlayer.inventory.getCurrentItem().getItem() != null) && ((entityPlayer.inventory.getCurrentItem().getItem() instanceof GunCusItemGun)) && (GunCus.hitmarker > 0))
/*    */       {
/* 104 */         GunCusItemGun gun = (GunCusItemGun)entityPlayer.inventory.getCurrentItem().getItem();
/* 105 */         GunCus.hitmarker -= 1;
/* 106 */         ScaledResolution scale = new ScaledResolution(client.gameSettings, client.displayWidth, client.displayHeight);
/* 107 */         int xCenter = scale.getScaledWidth() / 2;
/* 108 */         int offset = scale.getScaledHeight() * 2;
/* 109 */         client.getTextureManager().bindTexture(new ResourceLocation("guncus:textures/sights/hitmarker.png"));
/* 110 */         Tessellator tessellator = Tessellator.instance;
/* 111 */         tessellator.startDrawingQuads();
/* 112 */         tessellator.addVertexWithUV(xCenter - offset, scale.getScaledHeight(), -100.0D, 0.0D, 1.0D);
/* 113 */         tessellator.addVertexWithUV(xCenter + offset, scale.getScaledHeight(), -100.0D, 1.0D, 1.0D);
/* 114 */         tessellator.addVertexWithUV(xCenter + offset, 0.0D, -100.0D, 1.0D, 0.0D);
/* 115 */         tessellator.addVertexWithUV(xCenter - offset, 0.0D, -100.0D, 0.0D, 0.0D);
/* 116 */         tessellator.draw();
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Nate\Desktop\Mod\GunCusClass.zip
 * Qualified Name:     assets.guncus.GunCusClientProxy
 * JD-Core Version:    0.6.2
 */