package stuuupiiid.guncus.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import stuuupiiid.guncus.entity.EntityGrenade;

@SideOnly(Side.CLIENT)
public class RenderGrenade extends RenderArrow {
	private static final ResourceLocation grenadeTextures = new ResourceLocation("guncus:textures/entity/at.png");
	
	private static final float scale = 0.05625F;
	private static final float uSideMin  =  0 / 32.0F;
	private static final float uSideMax  = 17 / 32.0F;
	private static final float uBackMin  = 17 / 32.0F;
	private static final float uBackMax  = 22 / 32.0F;
	private static final float uFrontMin = 22 / 32.0F;
	private static final float uFrontMax = 27 / 32.0F;
	private static final float vOffset   =  5 / 32.0F;
	public void renderGrenade(EntityGrenade entityGrenade, double x, double y, double z, float par8, float par9) {
		bindEntityTexture(entityGrenade);
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) y, (float) z);
		GL11.glRotatef(entityGrenade.prevRotationYaw + (entityGrenade.rotationYaw - entityGrenade.prevRotationYaw) * par9 - 90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(entityGrenade.prevRotationPitch + (entityGrenade.rotationPitch - entityGrenade.prevRotationPitch) * par9, 0.0F, 0.0F, 1.0F);
		Tessellator tessellator = Tessellator.instance;
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		
		if (entityGrenade.state == entityGrenade.STATE_FLYING) {
			GL11.glRotatef((entityGrenade.ticksExisted * 20 + par9) * 0.1F, 1.0F, 0.0F, 0.0F);
		}
		
		byte b0 = 0;
		float f2 = 0.0F;
		float f3 = 0.5F;
		float f4 = (0 + b0 * 10) / 32.0F;
		float f5 = (5 + b0 * 10) / 32.0F;
		float f6 = 0.0F;
		float f7 = 0.15625F;
		float f8 = (5 + b0 * 10) / 32.0F;
		float f9 = (10 + b0 * 10) / 32.0F;

		GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
		GL11.glScalef(scale, scale, scale);
		GL11.glTranslatef(-4.0F, 0.0F, 0.0F);
		
		// tail back face
		GL11.glNormal3f(scale, 0.0F, 0.0F);
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, f6, f8);
		tessellator.addVertexWithUV(-7.0D, -2.0D,  2.0D, f7, f8);
		tessellator.addVertexWithUV(-7.0D,  2.0D,  2.0D, f7, f9);
		tessellator.addVertexWithUV(-7.0D,  2.0D, -2.0D, f6, f9);
		tessellator.draw();
		
		// tail front face
		GL11.glNormal3f(-scale, 0.0F, 0.0F);
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(-7.0D,  2.0D, -2.0D, f6, f8);
		tessellator.addVertexWithUV(-7.0D,  2.0D,  2.0D, f7, f8);
		tessellator.addVertexWithUV(-7.0D, -2.0D,  2.0D, f7, f9);
		tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, f6, f9);
		tessellator.draw();
		
		// 4 sides
		for (int i = 0; i < 4; i++) {
			GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
			GL11.glNormal3f(0.0F, 0.0F, scale);
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(-8.0D, -2.0D, 0.0D, f2, f4);
			tessellator.addVertexWithUV( 8.0D, -2.0D, 0.0D, f3, f4);
			tessellator.addVertexWithUV( 8.0D,  2.0D, 0.0D, f3, f5);
			tessellator.addVertexWithUV(-8.0D,  2.0D, 0.0D, f2, f5);
			tessellator.draw();
		}
		
		GL11.glDisable(32826);
		GL11.glPopMatrix();
	}
	
	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return grenadeTextures;
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityArrow entityArrow) {
		return grenadeTextures;
	}
	
	@Override
	public void doRender(Entity par1Entity, double x, double y, double z, float par8, float par9) {
		if (((EntityGrenade) par1Entity).ticksExisted >= 2) {
			renderGrenade((EntityGrenade) par1Entity, x, y, z, par8, par9);
		}
	}
}
