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
/*     */ public class GunCusContainerGun extends Container
/*     */ {
/*  19 */   public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
/*     */   private World worldObj;
/*     */   public int posX;
/*     */   public int posY;
/*     */   public int posZ;
/*     */ 
/*     */   public GunCusContainerGun(InventoryPlayer par1InventoryPlayer, World par2World, int par3, int par4, int par5)
/*     */   {
/*  27 */     this.worldObj = par2World;
/*  28 */     this.posX = par3;
/*  29 */     this.posY = par4;
/*  30 */     this.posZ = par5;
/*     */ 
/*  33 */     addSlotToContainer(new Slot(this.craftMatrix, 0, 80, 56));
/*  34 */     addSlotToContainer(new Slot(this.craftMatrix, 1, 80, 14));
/*  35 */     addSlotToContainer(new Slot(this.craftMatrix, 2, 59, 35));
/*  36 */     addSlotToContainer(new Slot(this.craftMatrix, 3, 101, 35));
/*  37 */     addSlotToContainer(new Slot(this.craftMatrix, 4, 80, 35));
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
/*     */   public void split()
/*     */   {
/*  77 */     ItemStack down = ((Slot)this.inventorySlots.get(0)).getStack();
/*  78 */     ItemStack top = ((Slot)this.inventorySlots.get(1)).getStack();
/*  79 */     ItemStack left = ((Slot)this.inventorySlots.get(2)).getStack();
/*  80 */     ItemStack right = ((Slot)this.inventorySlots.get(3)).getStack();
/*  81 */     ItemStack mid = ((Slot)this.inventorySlots.get(4)).getStack();
/*     */ 
/*  83 */     ItemStack scope = null;
/*  84 */     ItemStack attatch = null;
/*  85 */     ItemStack extra = null;
/*     */ 
/*  87 */     GunCusItemGun gun = null;
/*     */ 
/*  89 */     if ((mid != null) && (mid.getItem() != null) && ((mid.getItem() instanceof GunCusItemGun)))
/*     */     {
/*  91 */       gun = (GunCusItemGun)mid.getItem();
/*     */     }
/*     */ 
/*  94 */     if (gun != null)
/*     */     {
/*  96 */       int metadata = mid.getItemDamage();
/*     */ 
/*  98 */       int scope1 = 0;
/*     */ 
/* 100 */       if (gun.getZoom(metadata) > 0)
/*     */       {
/* 102 */         scope1 = gun.getZoom(metadata);
/*     */       }
/*     */ 
/* 105 */       if (scope1 > 0)
/*     */       {
/* 107 */         scope = new ItemStack(GunCus.scope, 1, scope1 - 1);
/*     */       }
/*     */ 
/* 110 */       int extra1 = 0;
/*     */ 
/* 112 */       for (int v1 = 1; v1 <= GunCus.attachment.metadatas.length; v1++)
/*     */       {
/* 114 */         if (gun.testForAttachId(v1, metadata))
/*     */         {
/* 116 */           extra1 = v1;
/* 117 */           break;
/*     */         }
/*     */       }
/*     */ 
/* 121 */       if (extra1 > 0)
/*     */       {
/* 123 */         extra = new ItemStack(GunCus.attachment, 1, extra1 - 1);
/*     */       }
/*     */ 
/* 126 */       int bar1 = 0;
/*     */ 
/* 128 */       for (int v1 = 1; v1 <= GunCus.barrel.metadatas.length; v1++)
/*     */       {
/* 130 */         if (gun.testForBarrelId(v1, metadata))
/*     */         {
/* 132 */           bar1 = v1;
/* 133 */           break;
/*     */         }
/*     */       }
/*     */ 
/* 137 */       if (bar1 > 0)
/*     */       {
/* 139 */         attatch = new ItemStack(GunCus.barrel, 1, bar1 - 1);
/*     */       }
/*     */ 
/* 142 */       int extra2 = gun.attachAsMetadataFactor(extra1);
/* 143 */       int bar2 = gun.barrelAsMetadataFactor(bar1);
/*     */ 
/* 145 */       if (top == null)
/*     */       {
/* 147 */         metadata -= scope1;
/* 148 */         ((Slot)this.inventorySlots.get(1)).putStack(scope);
/* 149 */         ((Slot)this.inventorySlots.get(4)).putStack(new ItemStack(gun, 1, metadata));
/*     */       }
/* 151 */       if (left == null)
/*     */       {
/* 153 */         metadata -= bar2 * gun.factor;
/* 154 */         ((Slot)this.inventorySlots.get(2)).putStack(attatch);
/* 155 */         ((Slot)this.inventorySlots.get(4)).putStack(new ItemStack(gun, 1, metadata));
/*     */       }
/* 157 */       if (down == null)
/*     */       {
/* 159 */         metadata -= extra2 * (gun.scopes.length + 1);
/* 160 */         ((Slot)this.inventorySlots.get(0)).putStack(extra);
/* 161 */         ((Slot)this.inventorySlots.get(4)).putStack(new ItemStack(gun, 1, metadata));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void build()
/*     */   {
/* 174 */     ItemStack down = ((Slot)this.inventorySlots.get(0)).getStack();
/* 175 */     ItemStack top = ((Slot)this.inventorySlots.get(1)).getStack();
/* 176 */     ItemStack left = ((Slot)this.inventorySlots.get(2)).getStack();
/* 177 */     ItemStack right = ((Slot)this.inventorySlots.get(3)).getStack();
/* 178 */     ItemStack mid = ((Slot)this.inventorySlots.get(4)).getStack();
/*     */ 
/* 180 */     int scope = 0;
/*     */ 
/* 182 */     if (top != null)
/*     */     {
/* 184 */       if (top.getItem() != null)
/*     */       {
/* 186 */         int scope1 = top.getItem().itemID;
/*     */ 
/* 188 */         if (scope1 == GunCus.scope.itemID)
/*     */         {
/* 190 */           int scopeMeta = top.getItemDamage();
/*     */ 
/* 192 */           scope = scopeMeta + 1;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 197 */     int bar1 = 0;
/*     */ 
/* 199 */     if (left != null)
/*     */     {
/* 201 */       if (left.getItem() != null)
/*     */       {
/* 203 */         int ex = left.getItem().itemID;
/*     */ 
/* 205 */         if (ex == GunCus.barrel.itemID)
/*     */         {
/* 207 */           int attaMeta = left.getItemDamage();
/* 208 */           bar1 = attaMeta + 1;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 213 */     int bar = 0;
/*     */ 
/* 215 */     int extra1 = 0;
/*     */ 
/* 217 */     if (down != null)
/*     */     {
/* 219 */       if (down.getItem() != null)
/*     */       {
/* 221 */         int ex = down.getItem().itemID;
/*     */ 
/* 223 */         if (ex == GunCus.attachment.itemID)
/*     */         {
/* 225 */           int attaMeta = down.getItemDamage();
/*     */ 
/* 227 */           extra1 = attaMeta + 1;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 232 */     int extra = 0;
/* 233 */     GunCusItemGun gun = null;
/*     */ 
/* 235 */     if (mid != null)
/*     */     {
/* 237 */       if ((mid.getItem() != null) && ((mid.getItem() instanceof GunCusItemGun)))
/*     */       {
/* 239 */         gun = (GunCusItemGun)mid.getItem();
/*     */ 
/* 241 */         extra = gun.attachAsMetadataFactor(extra1);
/* 242 */         bar = gun.barrelAsMetadataFactor(bar1);
/*     */       }
/*     */     }
/*     */ 
/* 246 */     if (gun != null)
/*     */     {
/* 248 */       int metadata = mid.getItemDamage();
/*     */ 
/* 250 */       if ((scope > 0) && (gun.getZoom(metadata) <= 0) && (gun.testIfCanHaveScope(scope)))
/*     */       {
/* 252 */         metadata += scope;
/* 253 */         ((Slot)this.inventorySlots.get(1)).decrStackSize(1);
/* 254 */         ((Slot)this.inventorySlots.get(4)).putStack(new ItemStack(gun, 1, metadata));
/*     */       }
/* 256 */       if ((extra > 0) && (gun.attatchmentFree(metadata)))
/*     */       {
/* 258 */         metadata += extra * (gun.scopes.length + 1);
/* 259 */         ((Slot)this.inventorySlots.get(0)).decrStackSize(1);
/* 260 */         ((Slot)this.inventorySlots.get(4)).putStack(new ItemStack(gun, 1, metadata));
/*     */       }
/* 262 */       if ((bar > 0) && (gun.barrelFree(metadata)))
/*     */       {
/* 264 */         metadata += bar * gun.factor;
/* 265 */         ((Slot)this.inventorySlots.get(2)).decrStackSize(1);
/* 266 */         ((Slot)this.inventorySlots.get(4)).putStack(new ItemStack(gun, 1, metadata));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean canInteractWith(EntityPlayer par1EntityPlayer)
/*     */   {
/* 273 */     return this.worldObj.getBlockId(this.posX, this.posY, this.posZ) == GunCus.blockGun.blockID;
/*     */   }
/*     */ 
/*     */   public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
/*     */   {
/* 278 */     return null;
/*     */   }
/*     */ }

/* Location:           C:\Users\Nate\Desktop\Mod\GunCusClass.zip
 * Qualified Name:     assets.guncus.GunCusContainerGun
 * JD-Core Version:    0.6.2
 */