package stuuupiiid.guncus.render;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import stuuupiiid.guncus.entity.EntityRocket;

@SideOnly(Side.CLIENT)
public class RenderRocket extends RenderArrow {
	private static final ResourceLocation rocketTextures = new ResourceLocation("guncus:textures/entity/rocket.png");
	
	public RenderRocket(RenderManager renderManagerIn) {
		super(renderManagerIn);
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
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldRenderer = tessellator.getWorldRenderer();
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
		worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		worldRenderer.pos(-7.0D, -2.0D, -2.0D).tex(uBackMin, vMin).endVertex();
		worldRenderer.pos(-7.0D, -2.0D,  2.0D).tex(uBackMax, vMin).endVertex();
		worldRenderer.pos(-7.0D,  2.0D,  2.0D).tex(uBackMax, vMax).endVertex();
		worldRenderer.pos(-7.0D,  2.0D, -2.0D).tex(uBackMin, vMax).endVertex();
		tessellator.draw();
		
		// tail front face
		GL11.glNormal3f(-scale, 0.0F, 0.0F);
		worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		worldRenderer.pos( 4.0D,  2.0D, -2.0D).tex(uFrontMin, vMin).endVertex();
		worldRenderer.pos( 4.0D,  2.0D,  2.0D).tex(uFrontMax, vMin).endVertex();
		worldRenderer.pos( 4.0D, -2.0D,  2.0D).tex(uFrontMax, vMax).endVertex();
		worldRenderer.pos( 4.0D, -2.0D, -2.0D).tex(uFrontMin, vMax).endVertex();
		tessellator.draw();
		
		// 4 sides
		for (int i = 0; i < 4; i++) {
			GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
			GL11.glNormal3f(0.0F, 0.0F, scale);
			worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
			worldRenderer.pos(-8.0D, -2.0D, 0.0D).tex(uSideMin, vMin).endVertex();
			worldRenderer.pos( 8.0D, -2.0D, 0.0D).tex(uSideMax, vMin).endVertex();
			worldRenderer.pos( 8.0D,  2.0D, 0.0D).tex(uSideMax, vMax).endVertex();
			worldRenderer.pos(-8.0D,  2.0D, 0.0D).tex(uSideMin, vMax).endVertex();
			tessellator.draw();
		}
		
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityArrow entityArrow) {
		return rocketTextures;
	}
	
	@Override
	public void doRender(EntityArrow entityArrow, double x, double y, double z, float par8, float par9) {
		renderRocket((EntityRocket) entityArrow, x, y, z, par8, par9);
	}
}
