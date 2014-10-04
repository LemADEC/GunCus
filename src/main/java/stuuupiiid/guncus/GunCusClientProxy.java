package stuuupiiid.guncus;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.EventBus;

import org.lwjgl.input.Mouse;

public class GunCusClientProxy extends GunCusCommonProxy {
	private int previousShootTime = 0;
    private int startedShootTime = 0;
    
    private static int colorGradient(float gradient, int start, int end) {
    	return Math.max(0, Math.min(255, start + Math.round(gradient * (end - start))));
    }

	@Override
	public void render() {
		RenderingRegistry.registerEntityRenderingHandler(GunCusEntityBullet.class, new GunCusRenderBullet());
		RenderingRegistry.registerEntityRenderingHandler(GunCusEntityAT.class, new GunCusRenderAT());
	}

	@Override
	public void sound() {
		MinecraftForge.EVENT_BUS.register(new GunCusSound());
	}

	@Override
	public void sight() {
		Minecraft client = FMLClientHandler.instance().getClient();
		if (client.thePlayer != null) {
			EntityPlayer entityPlayer = client.thePlayer;
			boolean hasGunInHand = (entityPlayer.inventory.getCurrentItem() != null) && (entityPlayer.inventory.getCurrentItem().getItem() instanceof GunCusItemGun);
			ScaledResolution scale = new ScaledResolution(client.gameSettings, client.displayWidth,	client.displayHeight);
			int scaledWidth = scale.getScaledWidth();
			int scaledHeight = scale.getScaledHeight();
			int xCenter = scaledWidth / 2;
			int offset = scaledHeight * 2;
			// draw sight
			if (hasGunInHand && Mouse.isButtonDown(1) && (client.gameSettings.thirdPersonView == 0) && (client.currentScreen == null)) {
				GunCusItemGun gun = (GunCusItemGun) entityPlayer.inventory.getCurrentItem().getItem();
				int scopeId = gun.getZoom(entityPlayer.inventory.getCurrentItem().getItemDamage());

				String path;
				float newZoom = gun.zoom + 0.1F;
				if (scopeId > 0) {
					GunCusScope scope = (GunCusScope) GunCus.scope.metadatas[(scopeId - 1)];
					newZoom = scope.zoom + 0.1F;
					path = "guncus:textures/sights/" + scope.sight + ".png";
				} else if (gun.usingDefault) {
						path = "guncus:textures/sights/default.png";
				} else {
					path = gun.iconName.replace("minecraft:gun_", "minecraft:textures/items/gun_") + "sight.png";
				}

				if (GunCus.zoomLevel < newZoom) {
					GunCus.zoomLevel = (float) (GunCus.zoomLevel + 2.5D);
				}
				if (GunCus.zoomLevel > newZoom) {
					GunCus.zoomLevel = newZoom;
				}
				ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, client.entityRenderer, GunCus.zoomLevel, new String[] { "cameraZoom", GunCus.cameraZoom });
				client.getTextureManager().bindTexture(new ResourceLocation(path));
				Tessellator tessellator = Tessellator.instance;
				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(xCenter - offset, scaledHeight, -90.0D, 0.0D, 1.0D);
				tessellator.addVertexWithUV(xCenter + offset, scaledHeight, -90.0D, 1.0D, 1.0D);
				tessellator.addVertexWithUV(xCenter + offset,         0.0D, -90.0D, 1.0D, 0.0D);
				tessellator.addVertexWithUV(xCenter - offset,         0.0D, -90.0D, 0.0D, 0.0D);
				tessellator.draw();
			} else {
				if (GunCus.zoomLevel > 1.0F) {
					GunCus.zoomLevel = GunCus.zoomLevel - 2.5F;
				}
				if (GunCus.zoomLevel < 1.0F) {
					GunCus.zoomLevel = 1.0F;
				}
				ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, client.entityRenderer, GunCus.zoomLevel, new String[] { "cameraZoom", GunCus.cameraZoom });
			}

			// draw hit marker
			if (hasGunInHand && (GunCus.hitmarker > 0) && (client.currentScreen == null)) {
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

			// draw reloading overlay
			Minecraft	mc = Minecraft.getMinecraft();
			if (hasGunInHand && (client.currentScreen == null) && GunCus.reloading) {
		        String text = "Reloading";
		        int textX = (scaledWidth - mc.fontRenderer.getStringWidth(text)) / 2;
		        float progress = Math.min(1.0F, Math.max(0.0F, 1.0F - GunCus.shootTime / 95F));
		        int color = (colorGradient(progress, 0xFF, 0x00) << 16) + (colorGradient(progress, 0x40, 0xFF) << 8) + colorGradient(progress, 0x00, 0x00);

				mc.fontRenderer.drawString(text, textX, scaledHeight / 2 + 8, color, true);
			}

			// draw delay for long downtime
			if (hasGunInHand && (client.currentScreen == null) && !GunCus.reloading) {
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
			        float progress = Math.min(1.0F, Math.max(0.0F, 1.0F - GunCus.shootTime / (float)startedShootTime));

		        	mc.fontRenderer.drawString(text, textX, scaledHeight / 2 +  8, 0xFF0000, true);
		        	mc.fontRenderer.drawString(text.substring(0, Math.round(text.length() * progress)), textX, scaledHeight / 2 + 8, 0x40FF00, true);
		        	
		        	previousShootTime = GunCus.shootTime;
		        }
			}
		}
	}
}
