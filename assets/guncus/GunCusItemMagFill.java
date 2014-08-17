/*    */ package assets.guncus;
/*    */ 
/*    */ import net.minecraft.entity.Entity;
/*    */ import net.minecraft.entity.player.EntityPlayer;
/*    */ import net.minecraft.item.ItemStack;
/*    */ import net.minecraft.util.MathHelper;
/*    */ import net.minecraft.world.World;
/*    */ 
/*    */ public class GunCusItemMagFill extends GunCusItem
/*    */ {
/*    */   public GunCusItemMagFill(int par1)
/*    */   {
/* 12 */     super(par1, "guncus:magFiller", "Manual Mag Filler", "magFiller");
/*    */   }
/*    */ 
/*    */   public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
/*    */   {
/* 17 */     par3EntityPlayer.openGui(GunCus.instance, 2, par2World, MathHelper.floor_double(par3EntityPlayer.posX), MathHelper.floor_double(par3EntityPlayer.posY), MathHelper.floor_double(par3EntityPlayer.posZ));
/* 18 */     return par1ItemStack;
/*    */   }
/*    */ }

/* Location:           C:\Users\Nate\Desktop\Mod\GunCusClass.zip
 * Qualified Name:     assets.guncus.GunCusItemMagFill
 * JD-Core Version:    0.6.2
 */