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
/*     */ public class GunCusContainerMag extends Container
/*     */ {
/*  20 */   public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
/*     */   private World worldObj;
/*     */   public int posX;
/*     */   public int posY;
/*     */   public int posZ;
/*     */ 
/*     */   public GunCusContainerMag(InventoryPlayer par1InventoryPlayer, World par2World, int par3, int par4, int par5)
/*     */   {
/*  28 */     this.worldObj = par2World;
/*  29 */     this.posX = par3;
/*  30 */     this.posY = par4;
/*  31 */     this.posZ = par5;
/*     */ 
/*  34 */     addSlotToContainer(new Slot(this.craftMatrix, 0, 80, 14));
/*  35 */     addSlotToContainer(new Slot(this.craftMatrix, 1, 59, 35));
/*  36 */     addSlotToContainer(new Slot(this.craftMatrix, 2, 101, 35));
/*     */ 
/*  38 */     for (int var6 = 0; var6 < 3; var6++)
/*     */     {
/*  40 */       for (int var7 = 0; var7 < 9; var7++)
/*     */       {
/*  42 */         addSlotToContainer(new Slot(par1InventoryPlayer, var7 + var6 * 9 + 9, 8 + var7 * 18, 84 + var6 * 18));
/*     */       }
/*     */     }
/*  45 */     for (var6 = 0; var6 < 9; var6++)
/*     */     {
/*  47 */       addSlotToContainer(new Slot(par1InventoryPlayer, var6, 8 + var6 * 18, 142));
/*     */     }
/*  49 */     onCraftMatrixChanged(this.craftMatrix);
/*     */   }
/*     */ 
/*     */   public void onContainerClosed(EntityPlayer par1EntityPlayer)
/*     */   {
/*  54 */     super.onContainerClosed(par1EntityPlayer);
/*  55 */     if (!this.worldObj.isRemote)
/*     */     {
/*  57 */       for (int var2 = 0; var2 < 9; var2++)
/*     */       {
/*  59 */         ItemStack var3 = this.craftMatrix.getStackInSlotOnClosing(var2);
/*  60 */         if ((var3 != null) && (par1EntityPlayer != null))
/*     */         {
/*  62 */           par1EntityPlayer.dropPlayerItem(var3);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void create()
/*     */   {
/*  70 */     ItemStack down = ((Slot)this.inventorySlots.get(0)).getStack();
/*  71 */     ItemStack left = ((Slot)this.inventorySlots.get(1)).getStack();
/*  72 */     ItemStack right = ((Slot)this.inventorySlots.get(2)).getStack();
/*     */ 
/*  74 */     GunCusItemGun gun = null;
/*  75 */     int ingotsRequired = 0;
/*     */ 
/*  77 */     if ((down != null) && (down.getItem() != null) && ((down.getItem() instanceof GunCusItemGun)))
/*     */     {
/*  79 */       gun = (GunCusItemGun)down.getItem();
/*  80 */       ingotsRequired = gun.ingotsMag;
/*     */     }
/*     */ 
/*  83 */     if ((gun != null) && (left != null) && (left.getItem() != null) && (left.getItem().itemID == Item.ingotIron.itemID) && (right == null) && (ingotsRequired > 0) && (left.stackSize >= ingotsRequired))
/*     */     {
/*  85 */       int stackSize = left.stackSize;
/*     */ 
/*  87 */       stackSize -= ingotsRequired;
/*     */ 
/*  89 */       ((Slot)this.inventorySlots.get(1)).putStack(new ItemStack(Item.ingotIron, stackSize));
/*  90 */       ((Slot)this.inventorySlots.get(2)).putStack(new ItemStack(Item.itemsList[gun.magId], 1, Item.itemsList[gun.magId].getMaxDamage()));
/*     */     }
/*     */   }
/*     */ 
/*     */   public String[] info()
/*     */   {
/*  96 */     ItemStack down = ((Slot)this.inventorySlots.get(0)).getStack();
/*     */ 
/*  98 */     GunCusItemGun gun = null;
/*  99 */     String rtn = "Oops! Something went wrong!";
/* 100 */     String rtn2 = null;
/*     */ 
/* 102 */     if ((down != null) && (down.getItem() != null) && ((down.getItem() instanceof GunCusItemGun)))
/*     */     {
/* 104 */       gun = (GunCusItemGun)down.getItem();
/*     */     }
/*     */ 
/* 107 */     if (gun != null)
/*     */     {
/* 109 */       int iron = gun.ingotsMag;
/* 110 */       rtn = "Gun = \"" + gun.name + "\", Pack = \"" + gun.pack + "\"";
/* 111 */       rtn2 = "-> " + (iron > 0 ? iron + " iron ingot" + (iron > 1 ? "s " : " ") : "");
/*     */     }
/* 113 */     return new String[] { rtn, rtn2 };
/*     */   }
/*     */ 
/*     */   public boolean canInteractWith(EntityPlayer par1EntityPlayer)
/*     */   {
/* 118 */     return this.worldObj.getBlockId(this.posX, this.posY, this.posZ) == GunCus.blockMag.blockID;
/*     */   }
/*     */ 
/*     */   public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
/*     */   {
/* 123 */     return null;
/*     */   }
/*     */ }

/* Location:           C:\Users\Nate\Desktop\Mod\GunCusClass.zip
 * Qualified Name:     assets.guncus.GunCusContainerMag
 * JD-Core Version:    0.6.2
 */