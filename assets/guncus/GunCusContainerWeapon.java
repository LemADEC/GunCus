/*     */ package assets.guncus;
/*     */ 
/*     */ import java.util.List;
/*     */ import net.minecraft.block.Block;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.entity.player.InventoryPlayer;
/*     */ import net.minecraft.inventory.Container;
/*     */ import net.minecraft.inventory.InventoryCrafting;
/*     */ import net.minecraft.inventory.Slot;
/*     */ import net.minecraft.item.Item;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.world.World;
/*     */ 
/*     */ public class GunCusContainerWeapon extends Container
/*     */ {
/*  20 */   public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
/*     */   private World worldObj;
/*     */   public int posX;
/*     */   public int posY;
/*     */   public int posZ;
/*     */   public int actualItemID;
/*     */   public int actual;
/*     */ 
/*     */   public GunCusContainerWeapon(InventoryPlayer par1InventoryPlayer, World par2World, int par3, int par4, int par5)
/*     */   {
/*  30 */     this.actual = 0;
/*  31 */     this.actualItemID = 0;
/*  32 */     if (GunCusItemGun.gunList.size() > 0)
/*     */     {
/*  34 */       this.actualItemID = GunCusItemGun.gunList.get(0).itemID;
/*     */     }
/*     */ 
/*  37 */     this.worldObj = par2World;
/*  38 */     this.posX = par3;
/*  39 */     this.posY = par4;
/*  40 */     this.posZ = par5;
/*     */ 
/*  43 */     addSlotToContainer(new Slot(this.craftMatrix, 0, 59, 35));
/*  44 */     addSlotToContainer(new Slot(this.craftMatrix, 1, 80, 35));
/*  45 */     addSlotToContainer(new Slot(this.craftMatrix, 2, 101, 35));
/*     */ 
/*  47 */     for (int var6 = 0; var6 < 3; var6++)
/*     */     {
/*  49 */       for (int var7 = 0; var7 < 9; var7++)
/*     */       {
/*  51 */         addSlotToContainer(new Slot(par1InventoryPlayer, var7 + var6 * 9 + 9, 8 + var7 * 18, 84 + var6 * 18));
/*     */       }
/*     */     }
/*  54 */     for (int var8 = 0; var8 < 9; var8++)
/*     */     {
/*  56 */       addSlotToContainer(new Slot(par1InventoryPlayer, var8, 8 + var8 * 18, 142));
/*     */     }
/*  58 */     onCraftMatrixChanged(this.craftMatrix);
/*     */   }
/*     */ 
/*     */   public void onContainerClosed(EntityPlayer par1EntityPlayer)
/*     */   {
/*  63 */     super.onContainerClosed(par1EntityPlayer);
/*  64 */     if (!this.worldObj.isRemote)
/*     */     {
/*  66 */       for (int var2 = 0; var2 < 9; var2++)
/*     */       {
/*  68 */         ItemStack var3 = this.craftMatrix.getStackInSlotOnClosing(var2);
/*  69 */         if (var3 != null)
/*     */         {
/*  71 */           par1EntityPlayer.dropPlayerItem(var3);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String[] info()
/*     */   {
/*  79 */     if (GunCusItemGun.gunList.size() > 0)
/*     */     {
/*  81 */       GunCusItemGun gun = (GunCusItemGun)Item.itemsList[this.actualItemID];
/*     */ 
/*  83 */       String rtn = "Oops! Something went wrong!";
/*  84 */       String rtn2 = null;
/*     */ 
/*  86 */       if (gun != null)
/*     */       {
/*  88 */         int iron = gun.ingots;
/*  89 */         int redst = gun.field_77767_aC;
/*  90 */         rtn = "Gun = \"" + gun.name + "\", Pack = \"" + gun.pack + "\"";
/*  91 */         rtn2 = "-> " + (iron > 0 ? iron + " iron ingot" + (iron > 1 ? "s, " : ", ") : "") + (redst > 0 ? redst + " redstone" : "");
/*     */       }
/*  93 */       return new String[] { rtn, rtn2 };
/*     */     }
/*  95 */     return new String[] { "Oops! Something went wrong!", null };
/*     */   }
/*     */ 
/*     */   public void create()
/*     */   {
/* 100 */     if (GunCusItemGun.gunList.size() > 0)
/*     */     {
/* 102 */       ItemStack ir = ((Slot)this.inventorySlots.get(0)).getStack();
/* 103 */       ItemStack re = ((Slot)this.inventorySlots.get(1)).getStack();
/*     */ 
/* 105 */       GunCusItemGun gun = (GunCusItemGun)Item.itemsList[this.actualItemID];
/*     */ 
/* 107 */       if (gun != null)
/*     */       {
/* 109 */         int reqIr = gun.ingots;
/* 110 */         int reqRe = gun.field_77767_aC;
/*     */ 
/* 112 */         if (((ir != null) && (ir.stackSize >= reqIr) && (ir.getItem().itemID == Item.ingotIron.itemID)) || ((reqIr <= 0) && (((re != null) && (re.stackSize >= reqRe) && (re.getItem().itemID == Item.redstone.itemID)) || (reqRe <= 0))))
/*     */         {
/* 114 */           ((Slot)this.inventorySlots.get(2)).putStack(new ItemStack(gun, 1, 0));
/* 115 */           if (ir != null)
/*     */           {
/* 117 */             ((Slot)this.inventorySlots.get(0)).decrStackSize(reqIr);
/*     */           }
/* 119 */           if (re != null)
/*     */           {
/* 121 */             ((Slot)this.inventorySlots.get(1)).decrStackSize(reqRe);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean canInteractWith(EntityPlayer par1EntityPlayer)
/*     */   {
/* 130 */     return this.worldObj.getBlockId(this.posX, this.posY, this.posZ) == GunCus.blockWeapon.blockID;
/*     */   }
/*     */ 
/*     */   public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
/*     */   {
/* 135 */     return null;
/*     */   }
/*     */ }

/* Location:           C:\Users\Nate\Desktop\Mod\GunCusClass.zip
 * Qualified Name:     assets.guncus.GunCusContainerWeapon
 * JD-Core Version:    0.6.2
 */