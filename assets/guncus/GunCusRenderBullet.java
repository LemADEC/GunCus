/*    */ package assets.guncus;
/*    */ 
/*    */ import cpw.mods.fml.relauncher.Side;
/*    */ import cpw.mods.fml.relauncher.SideOnly;
/*    */ import net.minecraft.client.renderer.Tessellator;
/*    */ import net.minecraft.client.renderer.entity.RenderArrow;
/*    */ import net.minecraft.entity.Entity;
/*    */ import net.minecraft.entity.projectile.EntityArrow;
/*    */ import net.minecraft.util.MathHelper;
/*    */ import net.minecraft.util.ResourceLocation;
/*    */ import org.lwjgl.opengl.GL11;
/*    */ 
/*    */ @SideOnly(Side.CLIENT)
/*    */ public class GunCusRenderBullet extends RenderArrow
/*    */ {
/*    */   public void renderArrow(GunCusEntityBullet par1EntityArrow, double par2, double par4, double par6, float par8, float par9)
/*    */   {
/* 20 */     GL11.glPushMatrix();
/* 21 */     GL11.glTranslatef((float)par2, (float)par4, (float)par6);
/* 22 */     GL11.glRotatef(par1EntityArrow.prevRotationYaw + (par1EntityArrow.rotationYaw - par1EntityArrow.prevRotationYaw) * par9 - 90.0F, 0.0F, 1.0F, 0.0F);
/* 23 */     GL11.glRotatef(par1EntityArrow.prevRotationPitch + (par1EntityArrow.rotationPitch - par1EntityArrow.prevRotationPitch) * par9, 0.0F, 0.0F, 1.0F);
/* 24 */     Tessellator tessellator = Tessellator.instance;
/* 25 */     byte b0 = 0;
/* 26 */     float f2 = 0.0F;
/* 27 */     float f3 = 0.5F;
/* 28 */     float f4 = (0 + b0 * 10) / 32.0F;
/* 29 */     float f5 = (5 + b0 * 10) / 32.0F;
/* 30 */     float f6 = 0.0F;
/* 31 */     float f7 = 0.15625F;
/* 32 */     float f8 = (5 + b0 * 10) / 32.0F;
/* 33 */     float f9 = (10 + b0 * 10) / 32.0F;
/* 34 */     float f10 = 0.05625F;
/* 35 */     GL11.glEnable(32826);
/* 36 */     float f11 = par1EntityArrow.arrowShake - par9;
/*    */ 
/* 38 */     if (f11 > 0.0F)
/*    */     {
/* 40 */       float f12 = -MathHelper.sin(f11 * 3.0F) * f11;
/* 41 */       GL11.glRotatef(f12, 0.0F, 0.0F, 1.0F);
/*    */     }
/*    */ 
/* 44 */     GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
/* 45 */     GL11.glScalef(f10, f10, f10);
/* 46 */     GL11.glTranslatef(-4.0F, 0.0F, 0.0F);
/* 47 */     GL11.glNormal3f(f10, 0.0F, 0.0F);
/* 48 */     tessellator.startDrawingQuads();
/* 49 */     tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, f6, f8);
/* 50 */     tessellator.addVertexWithUV(-7.0D, -2.0D, 2.0D, f7, f8);
/* 51 */     tessellator.addVertexWithUV(-7.0D, 2.0D, 2.0D, f7, f9);
/* 52 */     tessellator.addVertexWithUV(-7.0D, 2.0D, -2.0D, f6, f9);
/* 53 */     tessellator.draw();
/* 54 */     GL11.glNormal3f(-f10, 0.0F, 0.0F);
/* 55 */     tessellator.startDrawingQuads();
/* 56 */     tessellator.addVertexWithUV(-7.0D, 2.0D, -2.0D, f6, f8);
/* 57 */     tessellator.addVertexWithUV(-7.0D, 2.0D, 2.0D, f7, f8);
/* 58 */     tessellator.addVertexWithUV(-7.0D, -2.0D, 2.0D, f7, f9);
/* 59 */     tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, f6, f9);
/* 60 */     tessellator.draw();
/*    */ 
/* 62 */     for (int i = 0; i < 4; i++)
/*    */     {
/* 64 */       GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
/* 65 */       GL11.glNormal3f(0.0F, 0.0F, f10);
/* 66 */       tessellator.startDrawingQuads();
/* 67 */       tessellator.addVertexWithUV(-8.0D, -2.0D, 0.0D, f2, f4);
/* 68 */       tessellator.addVertexWithUV(8.0D, -2.0D, 0.0D, f3, f4);
/* 69 */       tessellator.addVertexWithUV(8.0D, 2.0D, 0.0D, f3, f5);
/* 70 */       tessellator.addVertexWithUV(-8.0D, 2.0D, 0.0D, f2, f5);
/* 71 */       tessellator.draw();
/*    */     }
/*    */ 
/* 74 */     GL11.glDisable(32826);
/* 75 */     GL11.glPopMatrix();
/*    */   }
/*    */ 
/*    */   protected ResourceLocation getEntityTexture(Entity par1Entity)
/*    */   {
/* 81 */     return new ResourceLocation("guncus:textures/entity/bullet.png");
/*    */   }
/*    */ 
/*    */   public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
/*    */   {
/* 86 */     if (((GunCusEntityBullet)par1Entity).ticks >= 2)
/*    */     {
/* 88 */       renderArrow((GunCusEntityBullet)par1Entity, par2, par4, par6, par8, par9);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Nate\Desktop\Mod\GunCusClass.zip
 * Qualified Name:     assets.guncus.GunCusRenderBullet
 * JD-Core Version:    0.6.2
 */