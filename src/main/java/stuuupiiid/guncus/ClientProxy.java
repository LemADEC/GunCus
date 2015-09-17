package stuuupiiid.guncus;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Mouse;

import stuuupiiid.guncus.data.ScopePart;
import stuuupiiid.guncus.entity.EntityGrenade;
import stuuupiiid.guncus.entity.EntityBullet;
import stuuupiiid.guncus.item.ItemGun;
import stuuupiiid.guncus.item.ItemRPG;
import stuuupiiid.guncus.render.RenderGrenade;
import stuuupiiid.guncus.render.RenderBullet;

public class ClientProxy extends CommonProxy {
	private int previousShootTime = 0;
	private int startedShootTime = 0;
	
	private static int colorGradient(float gradient, int start, int end) {
		return Math.max(0, Math.min(255, start + Math.round(gradient * (end - start))));
	}
	
	@Override
	public void render() {
		RenderingRegistry.registerEntityRenderingHandler(EntityBullet.class, new RenderBullet());
		RenderingRegistry.registerEntityRenderingHandler(EntityGrenade.class, new RenderGrenade());
	}
	
	@Override
	public void sight() {
		Minecraft client = FMLClientHandler.instance().getClient();
		if (client.thePlayer == null) {
			return;
		}
		EntityPlayer entityPlayer = client.thePlayer;
		
		boolean hasGunInHand = (entityPlayer.inventory.getCurrentItem() != null) && (entityPlayer.inventory.getCurrentItem().getItem() instanceof ItemGun);
		boolean hasRPGinHand = (entityPlayer.inventory.getCurrentItem() != null) && (entityPlayer.inventory.getCurrentItem().getItem() instanceof ItemRPG);
		boolean hasM320inHand = (entityPlayer.inventory.getCurrentItem() != null)
				&& (entityPlayer.inventory.getCurrentItem().getItem() == GunCus.attachment) && (entityPlayer.inventory.getCurrentItem().getItemDamage() == 4);
		ScaledResolution scale = new ScaledResolution(client, client.displayWidth,	client.displayHeight);
		int scaledWidth = scale.getScaledWidth();
		int scaledHeight = scale.getScaledHeight();
		int xCenter = scaledWidth / 2;
		int offset = scaledHeight * 2;
		// draw sight
		if (hasGunInHand && Mouse.isButtonDown(1) && (client.gameSettings.thirdPersonView == 0) && (client.currentScreen == null)) {
			ItemGun gun = (ItemGun) entityPlayer.inventory.getCurrentItem().getItem();
			ScopePart scopePart = gun.getScopePart(entityPlayer.inventory.getCurrentItem().getItemDamage());
			String sightTextureName;
			float newZoom = gun.zoom + 0.1F;
			if (scopePart != null) {
				// scope, always use originals
				newZoom = scopePart.zoom + 0.1F;
				sightTextureName = "guncus:textures/sights/" + scopePart.sight + ".png";
			} else if (gun.usingDefault) {
				// no scope, using default
				sightTextureName = "guncus:textures/sights/default.png";
			} else {
				// no scope, using custom
				sightTextureName = gun.iconBasePath.replace(":gun_", ":textures/items/gun_") + "sight.png";
			}
			
			// Progressively adjust zoom
			if (GunCus.zoomLevel < newZoom) {
				GunCus.zoomLevel = Math.min(newZoom, GunCus.zoomLevel + 0.5F);
			} else if (GunCus.zoomLevel > newZoom) {
				GunCus.zoomLevel = Math.max(newZoom, GunCus.zoomLevel - 0.5F);
			}
			ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, client.entityRenderer, GunCus.zoomLevel, new String[] { "cameraZoom", GunCus.cameraZoom });
			
			client.getTextureManager().bindTexture(new ResourceLocation(sightTextureName));
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(xCenter - offset, scaledHeight, -90.0D, 0.0D, 1.0D);
			tessellator.addVertexWithUV(xCenter + offset, scaledHeight, -90.0D, 1.0D, 1.0D);
			tessellator.addVertexWithUV(xCenter + offset,         0.0D, -90.0D, 1.0D, 0.0D);
			tessellator.addVertexWithUV(xCenter - offset,         0.0D, -90.0D, 0.0D, 0.0D);
			tessellator.draw();
		} else {
			float newZoom = 1.0F;
			
			// Progressively adjust zoom
			if (GunCus.zoomLevel < newZoom) {
				GunCus.zoomLevel = Math.min(newZoom, GunCus.zoomLevel + 2.5F);
			} else if (GunCus.zoomLevel > newZoom) {
				GunCus.zoomLevel = Math.max(newZoom, GunCus.zoomLevel - 2.5F);
			}
			ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, client.entityRenderer, GunCus.zoomLevel, new String[] { "cameraZoom", GunCus.cameraZoom });
		}
		
		// draw hit marker
		if ((hasGunInHand || hasRPGinHand || hasM320inHand) && (GunCus.hitmarker > 0) && (client.currentScreen == null)) {
			GunCus.hitmarker -= 1;
			client.getTextureManager().bindTexture(new ResourceLocation("guncus:textures/sights/hitmarker.png"));
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(xCenter - offset, scaledHeight, -100.0D, 0.0D, 1.0D);
			tessellator.addVertexWithUV(xCenter + offset, scaledHeight, -100.0D, 1.0D, 1.0D);
			tessellator.addVertexWithUV(xCenter + offset,         0.0D, -100.0D, 1.0D, 0.0D);
			tessellator.addVertexWithUV(xCenter - offset,         0.0D, -100.0D, 0.0D, 0.0D);
			tessellator.draw();
		}
		
		// skip remaining if non-chat GUI is open
		if (client.currentScreen != null && !(client.currentScreen instanceof net.minecraft.client.gui.GuiChat)) {
			return;
		}
		
		// draw reloading overlay
		Minecraft mc = Minecraft.getMinecraft();
		if ((hasGunInHand || hasRPGinHand || hasM320inHand) && GunCus.reloading) {
			String text = "Reloading";
			int textX = (scaledWidth - mc.fontRenderer.getStringWidth(text)) / 2;
			float progress = Math.min(1.0F, Math.max(0.0F, 1.0F - GunCus.shootTime / 95F));
			int color = (colorGradient(progress, 0xFF, 0x00) << 16) + (colorGradient(progress, 0x40, 0xFF) << 8) + colorGradient(progress, 0x00, 0x00);
			
			mc.fontRenderer.drawString(text, textX, scaledHeight / 2 + 8, color, true);
		}
		
		// draw delay for long downtime
		if ((hasGunInHand || hasRPGinHand || hasM320inHand) && !GunCus.reloading) {
			if (GunCus.shootTime > previousShootTime) {
				previousShootTime = GunCus.shootTime;
				startedShootTime = Math.max(GunCus.shootTime, startedShootTime);
			}
			if (GunCus.shootTime == 0 && previousShootTime == 0) {
				// go shooting...
				startedShootTime = 0;
			} else if (startedShootTime > 20) {
				String text = ".............";
				int textX = (scaledWidth - mc.fontRenderer.getStringWidth(text)) / 2;
				float progress = Math.min(1.0F, Math.max(0.0F, 1.0F - GunCus.shootTime / (float) startedShootTime));
				
				mc.fontRenderer.drawString(text, textX, scaledHeight / 2 + 8, 0xFF0000, true);
				mc.fontRenderer.drawString(text.substring(0, Math.round(text.length() * progress)), textX, scaledHeight / 2 + 8, 0x40FF00, true);
				
				previousShootTime = GunCus.shootTime;
			}
		}
	}
}
