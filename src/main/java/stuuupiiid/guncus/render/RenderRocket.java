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

import stuuupiiid.guncus.entity.EntityRocket;

@SideOnly(Side.CLIENT)
public class RenderRocket extends RenderArrow {
	private static final ResourceLocation rocketTextures = new ResourceLocation("guncus:textures/entity/rocket.png");
	
	public RenderRocket() {
		shadowSize = 0.0F;
	}
	
	private static final float scale = 0.05625F;
	private static final float uSideMin  =  0 / 32.0F;
	private static final float uSideMax  = 17 / 32.0F;
	private static final float uBackMin  = 17 / 32.0F;
	private static final float uBackMax  = 22 / 32.0F;
	private static final float uFrontMin = 22 / 32.0F;
	private static final float uFrontMax = 27 / 32.0F;
	private static final float vOffset   =  5 / 32.0F;
	public void renderRocket(EntityRocket entityRocket, double x, double y, double z, float par8, float par9) {
		if (entityRocket.ticksExisted < 20 && x < 1.0D && y < 1.0D && z < 1.0D) {
			return;
		}
		
		bindEntityTexture(entityRocket);
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) y, (float) z);
		GL11.glRotatef(entityRocket.prevRotationYaw + (entityRocket.rotationYaw - entityRocket.prevRotationYaw) * par9 - 90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(entityRocket.prevRotationPitch + (entityRocket.rotationPitch - entityRocket.prevRotationPitch) * par9, 0.0F, 0.0F, 1.0F);
		Tessellator tessellator = Tessellator.instance;
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		
		if (entityRocket.state == entityRocket.STATE_FLYING) {
			GL11.glRotatef((entityRocket.ticksExisted * 20 + par9) * 0.1F, 1.0F, 0.0F, 0.0F);
		}
		
		final float vMin =  0F * vOffset;
		final float vMax =  vMin + vOffset;
		
		GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
		GL11.glScalef(scale, scale, scale);
		GL11.glTranslatef(-4.0F, 0.0F, 0.0F);
		
		// tail back face
		GL11.glNormal3f(scale, 0.0F, 0.0F);
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, uBackMin, vMin);
		tessellator.addVertexWithUV(-7.0D, -2.0D,  2.0D, uBackMax, vMin);
		tessellator.addVertexWithUV(-7.0D,  2.0D,  2.0D, uBackMax, vMax);
		tessellator.addVertexWithUV(-7.0D,  2.0D, -2.0D, uBackMin, vMax);
		tessellator.draw();
		
		// tail front face
		GL11.glNormal3f(-scale, 0.0F, 0.0F);
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV( 4.0D,  2.0D, -2.0D, uFrontMin, vMin);
		tessellator.addVertexWithUV( 4.0D,  2.0D,  2.0D, uFrontMax, vMin);
		tessellator.addVertexWithUV( 4.0D, -2.0D,  2.0D, uFrontMax, vMax);
		tessellator.addVertexWithUV( 4.0D, -2.0D, -2.0D, uFrontMin, vMax);
		tessellator.draw();
		
		// 4 sides
		for (int i = 0; i < 4; i++) {
			GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
			GL11.glNormal3f(0.0F, 0.0F, scale);
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(-8.0D, -2.0D, 0.0D, uSideMin, vMin);
			tessellator.addVertexWithUV( 8.0D, -2.0D, 0.0D, uSideMax, vMin);
			tessellator.addVertexWithUV( 8.0D,  2.0D, 0.0D, uSideMax, vMax);
			tessellator.addVertexWithUV(-8.0D,  2.0D, 0.0D, uSideMin, vMax);
			tessellator.draw();
		}
		
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}
	
	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return rocketTextures;
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityArrow entityArrow) {
		return rocketTextures;
	}
	
	@Override
	public void doRender(Entity par1Entity, double x, double y, double z, float par8, float par9) {
		renderRocket((EntityRocket) par1Entity, x, y, z, par8, par9);
	}
}
