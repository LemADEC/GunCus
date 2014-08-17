/*     */ package assets.guncus;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import net.minecraft.block.Block;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.entity.player.InventoryPlayer;
/*     */ import net.minecraft.inventory.Container;
/*     */ import net.minecraft.inventory.InventoryCrafting;
/*     */ import net.minecraft.inventory.Slot;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.world.World;
/*     */ 
/*     */ public class GunCusContainerAmmo extends Container
/*     */ {
/*  19 */   public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
/*     */   private World worldObj;
/*     */   public int posX;
/*     */   public int posY;
/*     */   public int posZ;
/*     */ 
/*     */   public GunCusContainerAmmo(InventoryPlayer par1InventoryPlayer, World par2World, int par3, int par4, int par5)
/*     */   {
/*  27 */     this.worldObj = par2World;
/*  28 */     this.posX = par3;
/*  29 */     this.posY = par4;
/*  30 */     this.posZ = par5;
/*     */ 
/*  33 */     addSlotToContainer(new Slot(this.craftMatrix, 0, 59, 35));
/*  34 */     addSlotToContainer(new Slot(this.craftMatrix, 1, 101, 35));
/*     */ 
/*  36 */     for (int var6 = 0; var6 < 3; var6++)
/*     */     {
/*  38 */       for (int var7 = 0; var7 < 9; var7++)
/*     */       {
/*  40 */         addSlotToContainer(new Slot(par1InventoryPlayer, var7 + var6 * 9 + 9, 8 + var7 * 18, 84 + var6 * 18));
/*     */       }
/*     */     }
/*  43 */     for (int var8 = 0; var8 < 9; var8++)
/*     */     {
/*  45 */       addSlotToContainer(new Slot(par1InventoryPlayer, var8, 8 + var8 * 18, 142));
/*     */     }
/*  47 */     onCraftMatrixChanged(this.craftMatrix);
/*     */   }
/*     */ 
/*     */   public void onContainerClosed(EntityPlayer par1EntityPlayer)
/*     */   {
/*  52 */     super.onContainerClosed(par1EntityPlayer);
/*  53 */     if (!this.worldObj.isRemote)
/*     */     {
/*  55 */       for (int var2 = 0; var2 < 9; var2++)
/*     */       {
/*  57 */         ItemStack var3 = this.craftMatrix.getStackInSlotOnClosing(var2);
/*  58 */         if (var3 != null)
/*     */         {
/*  60 */           par1EntityPlayer.dropPlayerItem(var3);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void fill()
/*     */   {
/*  68 */     ItemStack mag = ((Slot)this.inventorySlots.get(0)).getStack();
/*  69 */     ItemStack ammo = ((Slot)this.inventorySlots.get(1)).getStack();
/*     */ 
/*  71 */     if ((mag != null) && (ammo != null) && (mag.getItem() != null) && ((mag.getItem() instanceof GunCusItemMag)) && (ammo.getItem() != null) && ((ammo.getItem() instanceof GunCusItemBullet)) && (mag.getItemDamage() > 0))
/*     */     {
/*  73 */       GunCusItemMag mag1 = (GunCusItemMag)mag.getItem();
/*  74 */       int bulletType = mag1.bulletType;
/*  75 */       String pack = mag1.pack;
/*  76 */       GunCusItemBullet bullet = (GunCusItemBullet)ammo.getItem();
/*     */ 
/*  78 */       if ((bulletType == bullet.bulletType) && (pack.equals(bullet.pack)))
/*     */       {
/*  80 */         int damage = mag.getItemDamage();
				int size = ammo.stackSize;
/*  81 */         for (; 
/*  82 */           (size > 0) && (damage > 0); size--)
/*     */         {
/*  84 */           damage--;
/*     */         }
/*     */ 
/*  87 */         ((Slot)this.inventorySlots.get(0)).putStack(new ItemStack(mag1, 1, damage));
/*     */ 
/*  89 */         if (size > 0)
/*     */         {
/*  91 */           ((Slot)this.inventorySlots.get(1)).putStack(new ItemStack(bullet, size));
/*     */         }
/*     */         else
/*     */         {
/*  95 */           ((Slot)this.inventorySlots.get(1)).putStack(null);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void empty()
/*     */   {
/* 103 */     ItemStack mag = ((Slot)this.inventorySlots.get(0)).getStack();
/* 104 */     ItemStack ammo = ((Slot)this.inventorySlots.get(1)).getStack();
/*     */ 
/* 106 */     if ((mag != null) && (mag.getItem() != null) && ((mag.getItem() instanceof GunCusItemMag)) && ((ammo == null) || ((ammo != null) && (ammo.getItem() != null) && ((ammo.getItem() instanceof GunCusItemBullet)))) && (mag.getItemDamage() < mag.getMaxDamage()))
/*     */     {
/* 108 */       GunCusItemMag mag1 = (GunCusItemMag)mag.getItem();
/* 109 */       int bulletType = mag1.bulletType;
/* 110 */       int bulletType2 = bulletType;
/* 111 */       String pack = mag1.pack;
/* 112 */       String pack2 = pack;
/* 113 */       GunCusItemBullet bullet = null;
/*     */ 
/* 115 */       if (ammo != null)
/*     */       {
/* 117 */         bullet = (GunCusItemBullet)ammo.getItem();
/* 118 */         bulletType2 = bullet.bulletType;
/* 119 */         pack2 = bullet.pack;
/*     */       }
/*     */ 
/* 122 */       if ((bulletType == bulletType2) && (pack.equals(pack2)))
/*     */       {
/* 124 */         int damage = mag.getItemDamage();
/* 125 */         int size = 0;
/* 126 */         if (ammo != null);
/* 128 */         for (size = ammo.stackSize; 
/* 130 */           (size < 64) && (damage < mag.getMaxDamage()); size++)
/*     */         {
/* 132 */           damage++;
/*     */         }
/*     */ 
/* 135 */         if (bullet == null)
/*     */         {
/* 137 */           bullet = (GunCusItemBullet)((List)GunCusItemBullet.bulletsList.get(mag1.pack)).get(bulletType);
/*     */         }
/*     */ 
/* 140 */         ((Slot)this.inventorySlots.get(0)).putStack(new ItemStack(mag1, 1, damage));
/*     */ 
/* 142 */         if (size > 0)
/*     */         {
/*     */           try
/*     */           {
/* 146 */             ((Slot)this.inventorySlots.get(1)).putStack(new ItemStack(bullet, size));
/*     */           }
/*     */           catch (NullPointerException e)
/*     */           {
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean canInteractWith(EntityPlayer par1EntityPlayer)
/*     */   {
/* 159 */     return this.worldObj.getBlockId(this.posX, this.posY, this.posZ) == GunCus.blockAmmo.blockID;
/*     */   }
/*     */ 
/*     */   public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
/*     */   {
/* 164 */     return null;
/*     */   }
/*     */ }

/* Location:           C:\Users\Nate\Desktop\Mod\GunCusClass.zip
 * Qualified Name:     assets.guncus.GunCusContainerAmmo
 * JD-Core Version:    0.6.2
 */