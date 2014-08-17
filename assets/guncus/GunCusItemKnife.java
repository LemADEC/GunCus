/*    */ package assets.guncus;
/*    */ 
/*    */ import cpw.mods.fml.common.FMLCommonHandler;
/*    */ import cpw.mods.fml.relauncher.Side;
/*    */ import net.minecraft.entity.Entity;
/*    */ import net.minecraft.item.ItemStack;
/*    */ import net.minecraft.world.World;
/*    */ 
/*    */ public class GunCusItemKnife extends GunCusItem
/*    */ {
/*    */   public GunCusItemKnife(int par1)
/*    */   {
/* 12 */     super(par1, "guncus:knife", "Knife", "gcKnife");
/*    */   }
/*    */ 
/*    */   public void onUpdate(ItemStack itemStack, World world, Entity entity, int par1, boolean flag)
/*    */   {
/* 18 */     if (FMLCommonHandler.instance().getEffectiveSide().isClient())
/*    */     {
/* 20 */       GunCus.doKnife();
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Nate\Desktop\Mod\GunCusClass.zip
 * Qualified Name:     assets.guncus.GunCusItemKnife
 * JD-Core Version:    0.6.2
 */