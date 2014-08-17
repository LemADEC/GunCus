/*     */ package assets.guncus;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.entity.player.InventoryPlayer;
/*     */ import net.minecraft.inventory.Container;
/*     */ import net.minecraft.inventory.InventoryCrafting;
/*     */ import net.minecraft.inventory.Slot;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.world.World;
/*     */ 
/*     */ public class GunCusContainerAmmoMan extends Container
/*     */ {
/*  19 */   public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
/*     */   private World worldObj;
/*     */   public int posX;
/*     */   public int posY;
/*     */   public int posZ;
/*     */ 
/*     */   public GunCusContainerAmmoMan(InventoryPlayer par1InventoryPlayer, World par2World, int par3, int par4, int par5)
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
/*  43 */     for (var6 = 0; var6 < 9; var6++)
/*     */     {
/*  45 */       addSlotToContainer(new Slot(par1InventoryPlayer, var6, 8 + var6 * 18, 142));
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
/*  75 */       GunCusItemBullet bullet = (GunCusItemBullet)ammo.getItem();
/*     */ 
/*  77 */       if (bulletType == bullet.bulletType)
/*     */       {
/*  79 */         int damage = mag.getItemDamage();
/*  80 */         int size = ammo.stackSize;
/*  81 */         size--;
/*  82 */         damage--;
/*     */ 
/*  84 */         ((Slot)this.inventorySlots.get(0)).putStack(new ItemStack(mag1, 1, damage));
/*     */ 
/*  86 */         if (size > 0)
/*     */         {
/*  88 */           ((Slot)this.inventorySlots.get(1)).putStack(new ItemStack(bullet, size));
/*     */         }
/*     */         else
/*     */         {
/*  92 */           ((Slot)this.inventorySlots.get(1)).putStack(null);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void empty()
/*     */   {
/* 100 */     ItemStack mag = ((Slot)this.inventorySlots.get(0)).getStack();
/* 101 */     ItemStack ammo = ((Slot)this.inventorySlots.get(1)).getStack();
/*     */ 
/* 103 */     if ((mag != null) && (mag.getItem() != null) && ((mag.getItem() instanceof GunCusItemMag)) && ((ammo == null) || ((ammo != null) && (ammo.getItem() != null) && ((ammo.getItem() instanceof GunCusItemBullet)))) && (mag.getItemDamage() < mag.getMaxDamage()))
/*     */     {
/* 105 */       GunCusItemMag mag1 = (GunCusItemMag)mag.getItem();
/* 106 */       int bulletType = mag1.bulletType;
/* 107 */       int bulletType2 = bulletType;
/* 108 */       GunCusItemBullet bullet = null;
/*     */ 
/* 110 */       if (ammo != null)
/*     */       {
/* 112 */         bullet = (GunCusItemBullet)ammo.getItem();
/* 113 */         bulletType2 = bullet.bulletType;
/*     */       }
/*     */ 
/* 116 */       if (bulletType == bulletType2)
/*     */       {
/* 118 */         int damage = mag.getItemDamage();
/* 119 */         int size = 0;
/* 120 */         if (ammo != null)
/*     */         {
/* 122 */           size = ammo.stackSize;
/*     */         }
/*     */ 
/* 125 */         size++;
/* 126 */         damage++;
/*     */ 
/* 128 */         if (bullet == null)
/*     */         {
/* 130 */           bullet = (GunCusItemBullet)((List)GunCusItemBullet.bulletsList.get(mag1.pack)).get(bulletType);
/*     */         }
/*     */ 
/* 133 */         ((Slot)this.inventorySlots.get(0)).putStack(new ItemStack(mag1, 1, damage));
/*     */ 
/* 135 */         if (size > 0)
/*     */         {
/* 137 */           ((Slot)this.inventorySlots.get(1)).putStack(new ItemStack(bullet, size));
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean canInteractWith(EntityPlayer par1EntityPlayer)
/*     */   {
/* 145 */     return true;
/*     */   }
/*     */ 
/*     */   public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
/*     */   {
/* 150 */     return null;
/*     */   }
/*     */ }

/* Location:           C:\Users\Nate\Desktop\Mod\GunCusClass.zip
 * Qualified Name:     assets.guncus.GunCusContainerAmmoMan
 * JD-Core Version:    0.6.2
 */