/*    */ package assets.guncus;
/*    */ 
/*    */ import cpw.mods.fml.common.registry.LanguageRegistry;
/*    */ import cpw.mods.fml.relauncher.Side;
/*    */ import cpw.mods.fml.relauncher.SideOnly;
/*    */ import java.util.List;
/*    */ import net.minecraft.client.renderer.texture.IconRegister;
/*    */ import net.minecraft.creativetab.CreativeTabs;
/*    */ import net.minecraft.item.ItemStack;
/*    */ import net.minecraft.util.Icon;
/*    */ 
/*    */ public class GunCusItemMetadata extends GunCusItem
/*    */ {
/*    */   public GunCusCustomizationPart[] metadatas;
/*    */   public Icon[] icons;
/*    */   public String iconName;
/*    */   public String unlocalized;
/*    */ 
/*    */   public GunCusItemMetadata(int par1, String unlocalized, String iconName, GunCusCustomizationPart[] metadatas)
/*    */   {
/* 24 */     super(par1);
/*    */ 
/* 26 */     this.unlocalized = unlocalized;
/* 27 */     this.iconName = iconName;
/* 28 */     this.metadatas = metadatas;
/* 29 */     setHasSubtypes(true);
/*    */ 
/* 31 */     for (int v1 = 0; v1 < this.metadatas.length; v1++)
/*    */     {
/* 33 */       LanguageRegistry.addName(new ItemStack(this, 1, v1), this.metadatas[v1].localized);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
/*    */   {
/* 40 */     for (int j = 0; j < this.metadatas.length; j++)
/*    */     {
/* 42 */       ItemStack itemStack = new ItemStack(par1, 1, j);
/* 43 */       par3List.add(itemStack);
/*    */     }
/*    */   }
/*    */ 
/*    */   @SideOnly(Side.CLIENT)
/*    */   public void registerIcons(IconRegister par1IconRegister)
/*    */   {
/* 51 */     this.icons = new Icon[this.metadatas.length];
/*    */ 
/* 53 */     for (int i = 0; i < this.metadatas.length; i++)
/*    */     {
/* 55 */       this.icons[i] = par1IconRegister.registerIcon("guncus:" + this.iconName + i);
/*    */     }
/*    */   }
/*    */ 
/*    */   @SideOnly(Side.CLIENT)
/*    */   public Icon getIconFromDamage(int par1)
/*    */   {
/* 63 */     return this.icons[par1];
/*    */   }
/*    */ 
/*    */   public String getUnlocalizedName(ItemStack par1ItemStack)
/*    */   {
/* 69 */     return this.unlocalized + par1ItemStack.getItemDamage();
/*    */   }
/*    */ }

/* Location:           C:\Users\Nate\Desktop\Mod\GunCusClass.zip
 * Qualified Name:     assets.guncus.GunCusItemMetadata
 * JD-Core Version:    0.6.2
 */