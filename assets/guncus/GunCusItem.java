/*    */ package assets.guncus;
/*    */ 
/*    */ import cpw.mods.fml.common.registry.LanguageRegistry;
/*    */ import cpw.mods.fml.relauncher.Side;
/*    */ import cpw.mods.fml.relauncher.SideOnly;
/*    */ import java.util.List;
/*    */ import net.minecraft.client.renderer.texture.IconRegister;
/*    */ import net.minecraft.entity.player.EntityPlayer;
/*    */ import net.minecraft.item.Item;
/*    */ import net.minecraft.item.ItemStack;
/*    */ 
/*    */ public class GunCusItem extends Item
/*    */ {
/*    */   String iconName;
/*    */ 
/*    */   public GunCusItem(int par1, String iconName, String name, String unlocalized)
/*    */   {
/* 20 */     super(par1);
/* 21 */     setCreativeTab(GunCus.gcTab);
/* 22 */     this.iconName = iconName;
/* 23 */     setUnlocalizedName(unlocalized);
/* 24 */     LanguageRegistry.addName(this, name);
/*    */   }
/*    */ 
/*    */   public GunCusItem(int par1)
/*    */   {
/* 29 */     super(par1);
/* 30 */     setCreativeTab(GunCus.gcTab);
/*    */   }
/*    */ 
/*    */   @SideOnly(Side.CLIENT)
/*    */   public void registerIcons(IconRegister iconRegister)
/*    */   {
/* 37 */     this.itemIcon = iconRegister.registerIcon(this.iconName);
/*    */   }
/*    */ 
/*    */   public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par2List, boolean par4)
/*    */   {
/* 43 */     String info = "GunCusDebugInfo";
/*    */ 
/* 45 */     if (getUnlocalizedName(par1ItemStack).contains("scope"))
/*    */     {
/* 47 */       info = "Scope (Top)";
/*    */     }
/* 49 */     else if (getUnlocalizedName(par1ItemStack).contains("attachment"))
/*    */     {
/* 51 */       info = "Attachment (Bottom)";
/*    */     }
/* 53 */     else if (getUnlocalizedName(par1ItemStack).contains("barrel"))
/*    */     {
/* 55 */       info = "Barrel (Left)";
/*    */     }
/*    */ 
/* 58 */     if (!info.equals("GunCusDebugInfo"))
/*    */     {
/* 60 */       par2List.add(infoLine(info));
/*    */     }
/*    */   }
/*    */ 
/*    */   private String infoLine(String s)
/*    */   {
/* 66 */     return s;
/*    */   }
/*    */ }

/* Location:           C:\Users\Nate\Desktop\Mod\GunCusClass.zip
 * Qualified Name:     assets.guncus.GunCusItem
 * JD-Core Version:    0.6.2
 */