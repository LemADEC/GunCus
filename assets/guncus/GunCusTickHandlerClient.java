/*     */ package assets.guncus;
/*     */ 
/*     */ import cpw.mods.fml.client.FMLClientHandler;
/*     */ import cpw.mods.fml.common.IScheduledTickHandler;
/*     */ import cpw.mods.fml.common.TickType;
/*     */ import java.util.EnumSet;
/*     */ import net.minecraft.client.Minecraft;
/*     */ import net.minecraft.client.audio.SoundManager;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.entity.player.InventoryPlayer;
/*     */ import net.minecraft.entity.player.PlayerCapabilities;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import org.lwjgl.input.Keyboard;
/*     */ import org.lwjgl.input.Mouse;
/*     */ 
/*     */ public class GunCusTickHandlerClient
/*     */   implements IScheduledTickHandler
/*     */ {
/*     */   public void tickStart(EnumSet<TickType> type, Object[] tickData)
/*     */   {
/*  22 */     if (GunCus.switchTime > 0)
/*     */     {
/*  24 */       GunCus.switchTime -= 1;
/*     */     }
/*     */ 
/*  27 */     if ((!Mouse.isButtonDown(0)) && (GunCus.accuracyReset > 0) && (GunCus.accuracy < 100.0D))
/*     */     {
/*  29 */       GunCus.accuracyReset -= 1;
/*     */     }
/*  31 */     else if ((!Mouse.isButtonDown(0)) && (GunCus.accuracyReset == 0) && (GunCus.accuracy < 100.0D))
/*     */     {
/*  33 */       GunCus.accuracyReset = 5;
/*  34 */       GunCus.accuracy = 100.0D;
/*     */     }
/*     */     else
/*     */     {
/*  38 */       GunCus.accuracyReset = 5;
/*     */     }
/*     */ 
/*  41 */     if (GunCus.accuracy < 0.0D)
/*     */     {
/*  43 */       GunCus.accuracy = 0.0D;
/*     */     }
/*  45 */     else if (GunCus.accuracy > 100.0D)
/*     */     {
/*  47 */       GunCus.accuracy = 100.0D;
/*     */     }
/*     */ 
/*  50 */     if (GunCus.knifeTime > 0)
/*     */     {
/*  52 */       GunCus.knifeTime -= 1;
/*     */     }
/*     */ 
/*  55 */     if (FMLClientHandler.instance().getClient().thePlayer != null)
/*     */     {
/*  57 */       EntityPlayer entityPlayer = FMLClientHandler.instance().getClient().thePlayer;
/*     */ 
/*  59 */       if (GunCus.shootTime > 0)
/*     */       {
/*  61 */         if (entityPlayer.inventory.getCurrentItem() == null)
/*     */         {
/*  63 */           GunCus.shootTime -= 1;
/*     */         }
/*  65 */         else if (!(entityPlayer.inventory.getCurrentItem().getItem() instanceof GunCusItemGun))
/*     */         {
/*  67 */           GunCus.shootTime -= 1;
/*     */         }
/*     */         else
/*     */         {
/*  71 */           GunCusItemGun gun = (GunCusItemGun)entityPlayer.inventory.getCurrentItem().getItem();
/*     */ 
/*  73 */           if (gun.canHaveStraightPullBolt())
/*     */           {
/*  75 */             if (Mouse.isButtonDown(1))
/*     */             {
/*  77 */               if (gun.hasStraightPullBolt(entityPlayer.inventory.getCurrentItem().getItemDamage()))
/*     */               {
/*  79 */                 GunCus.shootTime -= 1;
/*  80 */                 if (GunCus.shootTime <= 0)
/*     */                 {
/*  82 */                   Minecraft.getMinecraft().sndManager.playSoundFX("guncus:click", 1.0F, 1.0F);
/*     */                 }
/*     */               }
/*     */             }
/*     */             else
/*     */             {
/*  88 */               GunCus.shootTime -= 1;
/*  89 */               if (GunCus.shootTime <= 0)
/*     */               {
/*  91 */                 Minecraft.getMinecraft().sndManager.playSoundFX("guncus:click", 1.0F, 1.0F);
/*     */               }
/*     */             }
/*     */           }
/*     */           else
/*     */           {
/*  97 */             GunCus.shootTime -= 1;
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 102 */       if ((entityPlayer.motionY + 0.07840000152587891D != 0.0D) && (GunCus.accuracy > 35.0D))
/*     */       {
/* 104 */         GunCus.accuracy = 35.0D;
/*     */       }
/* 106 */       else if ((entityPlayer.isSprinting()) && (GunCus.accuracy > 40.0D))
/*     */       {
/* 108 */         GunCus.accuracy = 40.0D;
/*     */       }
/* 110 */       else if (((entityPlayer.motionX != 0.0D) || (entityPlayer.motionZ != 0.0D)) && (GunCus.accuracy > 70.0D))
/*     */       {
/* 112 */         if ((Mouse.isButtonDown(1)) && (GunCus.accuracy > 70.0D))
/*     */         {
/* 114 */           GunCus.accuracy = 75.0D;
/*     */         }
/*     */         else
/*     */         {
/* 118 */           GunCus.accuracy = 70.0D;
/*     */         }
/*     */       }
/* 121 */       else if ((!Mouse.isButtonDown(1)) && (GunCus.accuracy > 85.0D))
/*     */       {
/* 123 */         if ((entityPlayer.inventory.getCurrentItem() == null) || (!(entityPlayer.inventory.getCurrentItem().getItem() instanceof GunCusItemGun)) || (!((GunCusItemGun)entityPlayer.inventory.getCurrentItem().getItem()).hasLaserPointer(entityPlayer.inventory.getCurrentItem().getItemDamage())))
/*     */         {
/* 125 */           GunCus.accuracy = 85.0D;
/*     */         }
/* 127 */         else if (GunCus.accuracy > 92.5D)
/*     */         {
/* 129 */           GunCus.accuracy = 92.5D;
/*     */         }
/*     */       }
/*     */ 
/* 133 */       if (entityPlayer.capabilities.isCreativeMode)
/*     */       {
/* 135 */         GunCus.accuracy = 100.0D;
/*     */       }
/*     */ 
/* 138 */       if ((entityPlayer.inventory.getCurrentItem() != null) && ((entityPlayer.inventory.getCurrentItem().getItem() instanceof GunCusItemGun)) && (Mouse.isButtonDown(1)))
/*     */       {
/* 140 */         GunCusItemGun gun = (GunCusItemGun)entityPlayer.inventory.getCurrentItem().getItem();
/* 141 */         if ((Keyboard.isKeyDown(42)) && (GunCus.counter <= 0) && (!gun.hasBipod(entityPlayer.inventory.getCurrentItem().getItemDamage())) && (!gun.hasImprovedGrip(entityPlayer.inventory.getCurrentItem().getItemDamage())))
/*     */         {
/* 143 */           if (!GunCus.startedBreathing)
/*     */           {
/* 145 */             entityPlayer.playSound("random.breath", 1.0F, 1.0F);
/* 146 */             GunCus.startedBreathing = true;
/* 147 */             GunCus.breathing = true;
/*     */           }
/*     */         }
/* 150 */         else if ((!GunCus.breathing) && ((!gun.hasBipod(entityPlayer.inventory.getCurrentItem().getItemDamage())) || (!gun.canUseBipod(entityPlayer))) && (!gun.hasImprovedGrip(entityPlayer.inventory.getCurrentItem().getItemDamage())))
/*     */         {
/* 152 */           GunCus.breathCounter = 0;
/* 153 */           int metadata = entityPlayer.inventory.getCurrentItem().getItemDamage();
/* 154 */           float maxX = 0.05475F / (gun.zoomToFloat(gun.getZoom(metadata)) / 3.0F * 2.0F);
/* 155 */           float maxY = 0.0975F / (gun.zoomToFloat(gun.getZoom(metadata)) / 3.0F * 2.0F);
/* 156 */           float plusX = 0.005F / (gun.zoomToFloat(gun.getZoom(metadata)) / 3.0F * 2.0F);
/* 157 */           float plusY = 0.005F / (gun.zoomToFloat(gun.getZoom(metadata)) / 3.0F * 2.0F);
/*     */ 
/* 159 */           if (GunCus.scopingX)
/*     */           {
/* 161 */             GunCus.maxX += plusX;
/* 162 */             entityPlayer.rotationYaw += GunCus.maxX;
/*     */ 
/* 164 */             if (GunCus.maxX >= maxX)
/*     */             {
/* 166 */               GunCus.scopingX = false;
/*     */             }
/*     */           }
/* 169 */           else if (!GunCus.scopingX)
/*     */           {
/* 171 */             GunCus.maxX -= plusX;
/* 172 */             entityPlayer.rotationYaw += GunCus.maxX;
/*     */ 
/* 174 */             if (GunCus.maxX <= -maxX)
/*     */             {
/* 176 */               GunCus.scopingX = true;
/*     */             }
/*     */           }
/*     */ 
/* 180 */           if (GunCus.scopingY)
/*     */           {
/* 182 */             GunCus.maxY += plusY;
/* 183 */             entityPlayer.rotationPitch += GunCus.maxY;
/*     */ 
/* 185 */             if (GunCus.maxY >= maxY)
/*     */             {
/* 187 */               GunCus.scopingY = false;
/*     */             }
/*     */           }
/* 190 */           else if (!GunCus.scopingY)
/*     */           {
/* 192 */             GunCus.maxY -= plusY;
/* 193 */             entityPlayer.rotationPitch += GunCus.maxY;
/*     */ 
/* 195 */             if (GunCus.maxY <= -maxY)
/*     */             {
/* 197 */               GunCus.scopingY = true;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 202 */       if (GunCus.breathing)
/*     */       {
/* 204 */         GunCus.breathCounter += 1;
/* 205 */         if (GunCus.breathCounter > 50)
/*     */         {
/* 207 */           GunCus.breathCounter = 0;
/* 208 */           GunCus.breathing = false;
/*     */         }
/*     */       }
/* 211 */       if ((!Mouse.isButtonDown(1)) && (GunCus.maxX >= 0.0F) && (GunCus.maxY >= 0.0F))
/*     */       {
/* 213 */         GunCus.maxX = 0.0F;
/* 214 */         GunCus.maxY = 0.0F;
/*     */       }
/* 216 */       if (GunCus.startedBreathing)
/*     */       {
/* 218 */         GunCus.counter += 1;
/* 219 */         if (GunCus.counter >= 300)
/*     */         {
/* 221 */           GunCus.startedBreathing = false;
/* 222 */           GunCus.counter = 0;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void tickEnd(EnumSet<TickType> type, Object[] tickData)
/*     */   {
/*     */   }
/*     */ 
/*     */   public EnumSet<TickType> ticks()
/*     */   {
/* 234 */     return EnumSet.of(TickType.CLIENT);
/*     */   }
/*     */ 
/*     */   public String getLabel()
/*     */   {
/* 240 */     return null;
/*     */   }
/*     */ 
/*     */   public int nextTickSpacing()
/*     */   {
/* 246 */     return 1;
/*     */   }
/*     */ }

/* Location:           C:\Users\Nate\Desktop\Mod\GunCusClass.zip
 * Qualified Name:     assets.guncus.GunCusTickHandlerClient
 * JD-Core Version:    0.6.2
 */