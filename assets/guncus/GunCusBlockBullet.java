/*    */ package assets.guncus;
/*    */ 
/*    */ import cpw.mods.fml.relauncher.Side;
/*    */ import cpw.mods.fml.relauncher.SideOnly;
/*    */ import net.minecraft.block.Block;
/*    */ import net.minecraft.block.material.Material;
/*    */ import net.minecraft.client.renderer.texture.IconRegister;
/*    */ import net.minecraft.entity.player.EntityPlayer;
/*    */ import net.minecraft.util.Icon;
/*    */ import net.minecraft.world.World;
/*    */ 
/*    */ public class GunCusBlockBullet extends GunCusBlock
/*    */ {
/*    */ 
/*    */   @SideOnly(Side.CLIENT)
/*    */   private Icon field_94385_a;
/*    */ 
/*    */   @SideOnly(Side.CLIENT)
/*    */   private Icon field_94384_b;
/*    */ 
/*    */   protected GunCusBlockBullet(int par1)
/*    */   {
/* 24 */     super(par1, Material.iron, "blockBullet", "Bullet Box");
/*    */   }
/*    */ 
/*    */   @SideOnly(Side.CLIENT)
/*    */   public Icon getIcon(int par1, int par2)
/*    */   {
/* 30 */     return par1 == 0 ? this.field_94384_b : par1 == 1 ? this.field_94385_a : this.blockIcon;
/*    */   }
/*    */ 
/*    */   @SideOnly(Side.CLIENT)
/*    */   public void registerIcons(IconRegister par1IconRegister)
/*    */   {
/* 37 */     this.blockIcon = par1IconRegister.registerIcon("guncus:side");
/* 38 */     this.field_94385_a = par1IconRegister.registerIcon("guncus:bullet");
/* 39 */     this.field_94384_b = par1IconRegister.registerIcon("guncus:bot");
/*    */   }
/*    */ 
/*    */   public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
/*    */   {
/* 44 */     par5EntityPlayer.openGui(GunCus.instance, 4, par1World, par2, par3, par4);
/* 45 */     return true;
/*    */   }
/*    */ }

/* Location:           C:\Users\Nate\Desktop\Mod\GunCusClass.zip
 * Qualified Name:     assets.guncus.GunCusBlockBullet
 * JD-Core Version:    0.6.2
 */