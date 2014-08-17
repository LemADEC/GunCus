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
/*     */ import net.minecraft.item.Item;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.world.World;
/*     */ 
/*     */ public class GunCusContainerBullet extends Container
/*     */ {
/*  20 */   public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
/*     */   private World worldObj;
/*     */   public int posX;
/*     */   public int posY;
/*     */   public int posZ;
/*     */ 
/*     */   public GunCusContainerBullet(InventoryPlayer par1InventoryPlayer, World par2World, int par3, int par4, int par5)
/*     */   {
/*  28 */     this.worldObj = par2World;
/*  29 */     this.posX = par3;
/*  30 */     this.posY = par4;
/*  31 */     this.posZ = par5;
/*     */ 
/*  34 */     addSlotToContainer(new Slot(this.craftMatrix, 0, 80, 14));
/*  35 */     addSlotToContainer(new Slot(this.craftMatrix, 1, 59, 35));
/*  36 */     addSlotToContainer(new Slot(this.craftMatrix, 2, 101, 35));
/*  37 */     addSlotToContainer(new Slot(this.craftMatrix, 3, 80, 35));
/*     */ 
/*  39 */     for (int var6 = 0; var6 < 3; var6++)
/*     */     {
/*  41 */       for (int var7 = 0; var7 < 9; var7++)
/*     */       {
/*  43 */         addSlotToContainer(new Slot(par1InventoryPlayer, var7 + var6 * 9 + 9, 8 + var7 * 18, 84 + var6 * 18));
/*     */       }
/*     */     }
/*  46 */     for (var6 = 0; var6 < 9; var6++)
/*     */     {
/*  48 */       addSlotToContainer(new Slot(par1InventoryPlayer, var6, 8 + var6 * 18, 142));
/*     */     }
/*  50 */     onCraftMatrixChanged(this.craftMatrix);
/*     */   }
/*     */ 
/*     */   public void onContainerClosed(EntityPlayer par1EntityPlayer)
/*     */   {
/*  55 */     super.onContainerClosed(par1EntityPlayer);
/*  56 */     if (!this.worldObj.isRemote)
/*     */     {
/*  58 */       for (int var2 = 0; var2 < 9; var2++)
/*     */       {
/*  60 */         ItemStack var3 = this.craftMatrix.getStackInSlotOnClosing(var2);
/*  61 */         if (var3 != null)
/*     */         {
/*  63 */           par1EntityPlayer.dropPlayerItem(var3);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void create()
/*     */   {
/*  71 */     ItemStack down = ((Slot)this.inventorySlots.get(0)).getStack();
/*  72 */     ItemStack left = ((Slot)this.inventorySlots.get(1)).getStack();
/*  73 */     ItemStack right = ((Slot)this.inventorySlots.get(2)).getStack();
/*  74 */     ItemStack mid = ((Slot)this.inventorySlots.get(3)).getStack();
/*     */ 
/*  76 */     GunCusItemMag mag = null;
/*  77 */     GunCusItemBullet bullet = null;
/*  78 */     int iron = 0;
/*  79 */     int sulphur = 0;
/*     */ 
/*  81 */     if ((down != null) && (down.getItem() != null) && ((down.getItem() instanceof GunCusItemMag)))
/*     */     {
/*  83 */       mag = (GunCusItemMag)down.getItem();
/*  84 */       bullet = (GunCusItemBullet)((List)GunCusItemBullet.bulletsList.get(mag.pack)).get(mag.bulletType);
/*     */     }
/*     */ 
/*  87 */     if (bullet != null)
/*     */     {
/*  89 */       iron = bullet.iron;
/*  90 */       sulphur = bullet.sulphur;
/*     */     }
/*     */ 
/*  93 */     if ((mag != null) && (sulphur >= 0) && (iron >= 0) && ((sulphur > 0) || (iron > 0)) && (left != null) && (left.getItem() != null) && (left.getItem().itemID == Item.ingotIron.itemID) && (mid != null) && (mid.getItem() != null) && (mid.getItem().itemID == Item.gunpowder.itemID) && ((right == null) || ((right != null) && (right.getItem() != null) && (right.getItem().itemID == bullet.itemID) && (right.stackSize + bullet.stackOnCreate <= 64))) && (bullet.stackOnCreate >= 1) && (left.stackSize >= iron) && (mid.stackSize >= sulphur))
/*     */     {
/*  95 */       int sizeIr = left.stackSize;
/*  96 */       int sizeSu = mid.stackSize;
/*     */ 
/*  98 */       sizeIr -= iron;
/*  99 */       sizeSu -= sulphur;
/*     */ 
/* 101 */       int size = bullet.stackOnCreate + ((right != null) && (right.getItem() != null) && (right.stackSize > 0) ? right.stackSize : 0);
/*     */ 
/* 103 */       ((Slot)this.inventorySlots.get(1)).putStack(new ItemStack(Item.ingotIron, sizeIr));
/* 104 */       ((Slot)this.inventorySlots.get(3)).putStack(new ItemStack(Item.gunpowder, sizeSu));
/* 105 */       ((Slot)this.inventorySlots.get(2)).putStack(new ItemStack(bullet, size));
/*     */     }
/*     */   }
/*     */ 
/*     */   public String[] info()
/*     */   {
/* 111 */     ItemStack down = ((Slot)this.inventorySlots.get(0)).getStack();
/*     */ 
/* 113 */     GunCusItemMag gun = null;
/* 114 */     String rtn = "Oops! Something went wrong!";
/* 115 */     String rtn2 = null;
/*     */ 
/* 117 */     if ((down != null) && (down.getItem() != null) && ((down.getItem() instanceof GunCusItemMag)))
/*     */     {
/* 119 */       gun = (GunCusItemMag)down.getItem();
/*     */     }
/*     */ 
/* 122 */     if (gun != null)
/*     */     {
/* 124 */       GunCusItemBullet bullet = (GunCusItemBullet)((List)GunCusItemBullet.bulletsList.get(gun.pack)).get(gun.bulletType);
/*     */ 
/* 126 */       int sulphur = -1;
/* 127 */       int iron = -1;
/*     */ 
/* 129 */       if (bullet != null)
/*     */       {
/* 131 */         sulphur = bullet.sulphur;
/* 132 */         iron = bullet.iron;
/*     */       }
/*     */ 
/* 135 */       if ((iron >= 0) && (sulphur >= 0) && ((iron > 0) || (sulphur > 0)))
/*     */       {
/* 137 */         rtn = "Gun = \"" + gun.gunName + "\", Bullets = \"" + bullet.name + "\", Pack = \"" + gun.pack + "\"";
/* 138 */         rtn2 = "-> " + (iron > 0 ? iron + " iron ingot" + (iron > 1 ? "s, " : ", ") : "") + (sulphur > 0 ? sulphur + " gunpowder" : "");
/*     */       }
/*     */     }
/* 141 */     return new String[] { rtn, rtn2 };
/*     */   }
/*     */ 
/*     */   public boolean canInteractWith(EntityPlayer par1EntityPlayer)
/*     */   {
/* 146 */     return this.worldObj.getBlockId(this.posX, this.posY, this.posZ) == GunCus.blockBullet.blockID;
/*     */   }
/*     */ 
/*     */   public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
/*     */   {
/* 151 */     return null;
/*     */   }
/*     */ }

/* Location:           C:\Users\Nate\Desktop\Mod\GunCusClass.zip
 * Qualified Name:     assets.guncus.GunCusContainerBullet
 * JD-Core Version:    0.6.2
 */