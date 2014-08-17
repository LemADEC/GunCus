/*    */ package assets.guncus;
/*    */ 
/*    */ import cpw.mods.fml.common.registry.LanguageRegistry;
/*    */ import java.util.Random;
/*    */ import net.minecraft.block.Block;
/*    */ import net.minecraft.block.material.Material;
/*    */ 
/*    */ public class GunCusBlock extends Block
/*    */ {
/*    */   public GunCusBlock(int par1, Material par2Material, String unlocalized, String name)
/*    */   {
/* 14 */     super(par1, par2Material);
/* 15 */     setUnlocalizedName(unlocalized);
/* 16 */     LanguageRegistry.addName(this, name);
/* 17 */     setCreativeTab(GunCus.gcTab);
/* 18 */     setHardness(2.0F);
/* 19 */     setResistance(5.0F);
/*    */   }
/*    */ 
/*    */   public int idDropped(int par1, Random par2Random, int par3)
/*    */   {
/* 24 */     return this.blockID;
/*    */   }
/*    */ }

/* Location:           C:\Users\Nate\Desktop\Mod\GunCusClass.zip
 * Qualified Name:     assets.guncus.GunCusBlock
 * JD-Core Version:    0.6.2
 */