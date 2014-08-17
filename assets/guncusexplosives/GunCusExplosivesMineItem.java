/*    */ package assets.guncusexplosives;
/*    */ 
/*    */ import assets.guncus.GunCusItem;
/*    */ import net.minecraft.block.Block;
/*    */ import net.minecraft.entity.player.EntityPlayer;
/*    */ import net.minecraft.item.ItemStack;
/*    */ import net.minecraft.world.World;
/*    */ 
/*    */ public class GunCusExplosivesMineItem extends GunCusItem
/*    */ {
/*    */   public GunCusExplosivesMineItem(int par1)
/*    */   {
/* 15 */     super(par1, "guncusexplosives:mine", "Mine", "gcmineitem");
/*    */   }
/*    */ 
/*    */   public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
/*    */   {
/* 20 */     if (par7 != 1)
/*    */     {
/* 22 */       return false;
/*    */     }
/*    */ 
/* 26 */     par5++;
/* 27 */     Block block = GunCusExplosives.mineBlock;
/*    */ 
/* 29 */     if ((par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack)) && (par2EntityPlayer.canPlayerEdit(par4, par5 + 1, par6, par7, par1ItemStack)))
/*    */     {
/* 31 */       if (!block.canPlaceBlockAt(par3World, par4, par5, par6))
/*    */       {
/* 33 */         return false;
/*    */       }
/*    */ 
/* 37 */       par3World.setBlock(par4, par5, par6, GunCusExplosives.mineBlock.blockID);
/* 38 */       par1ItemStack.stackSize -= 1;
/* 39 */       return true;
/*    */     }
/*    */ 
/* 44 */     return false;
/*    */   }
/*    */ }

/* Location:           C:\Users\Nate\Desktop\Mod\GunCusClass.zip
 * Qualified Name:     assets.guncusexplosives.GunCusExplosivesMineItem
 * JD-Core Version:    0.6.2
 */