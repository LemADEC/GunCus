/*    */ package assets.guncus;
/*    */ 
/*    */ import cpw.mods.fml.common.registry.LanguageRegistry;
/*    */ import net.minecraft.creativetab.CreativeTabs;
/*    */ 
/*    */ public class GunCusCreativeTab extends CreativeTabs
/*    */ {
/*  9 */   private int icon = 0;
/*    */ 
/*    */   public GunCusCreativeTab(String label, int icon)
/*    */   {
/* 13 */     super(label);
/* 14 */     this.icon = icon;
/* 15 */     LanguageRegistry.instance().addStringLocalization("itemGroup." + label, label);
/*    */   }
/*    */ 
/*    */   public int getTabIconItemIndex()
/*    */   {
/* 20 */     return this.icon;
/*    */   }
/*    */ }

/* Location:           C:\Users\Nate\Desktop\Mod\GunCusClass.zip
 * Qualified Name:     assets.guncus.GunCusCreativeTab
 * JD-Core Version:    0.6.2
 */