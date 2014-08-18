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
			if ((Mouse.isButtonDown(1)) && (entityPlayer.inventory.getCurrentItem() != null)
					&& ((entityPlayer.inventory.getCurrentItem().getItem() instanceof GunCusItemGun))
					&& (client.currentScreen == null) && (client.gameSettings.thirdPersonView == 0)) {
				GunCusItemGun gun = (GunCusItemGun) entityPlayer.inventory.getCurrentItem().getItem();
				int scopeId = gun.getZoom(entityPlayer.inventory.getCurrentItem().getItemDamage());
				String scope = "";
				float newZoom = gun.zoom + 0.1F;
				if (scopeId > 0) {
					GunCusScope scope2 = (GunCusScope) GunCus.scope.metadatas[(scopeId - 1)];
					newZoom = scope2.zoom + 0.1F;
					scope = scope2.sight;
				}
				String path;
				if (gun.isOfficial) {
					path = "guncus:textures/sights/" + scope;
				} else {
					path = "guncus:textures/sights/" + scope;
				}

				if (GunCus.zoomLevel < newZoom) {
					GunCus.zoomLevel = (float) (GunCus.zoomLevel + 2.5D);
				}
				if (GunCus.zoomLevel > newZoom) {
					GunCus.zoomLevel = newZoom;
				}
				ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, client.entityRenderer,
						Float.valueOf(GunCus.zoomLevel), new String[] { "cameraZoom", GunCus.cameraZoom });
				ScaledResolution scale = new ScaledResolution(client.gameSettings, client.displayWidth,
						client.displayHeight);
				int xCenter = scale.getScaledWidth() / 2;
				int offset = scale.getScaledHeight() * 2;
				client.getTextureManager().bindTexture(
						new ResourceLocation((!gun.usingDefault ? path : "guncus:textures/sights/default") + ".png"));
				Tessellator tessellator = Tessellator.instance;
				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(xCenter - offset, scale.getScaledHeight(), -100.0D, 0.0D, 1.0D);
				tessellator.addVertexWithUV(xCenter + offset, scale.getScaledHeight(), -100.0D, 1.0D, 1.0D);
				tessellator.addVertexWithUV(xCenter + offset, 0.0D, -100.0D, 1.0D, 0.0D);
				tessellator.addVertexWithUV(xCenter - offset, 0.0D, -100.0D, 0.0D, 0.0D);
				tessellator.draw();
			} else {
				if (GunCus.zoomLevel > 1.0D) {
					GunCus.zoomLevel = (float) (GunCus.zoomLevel - 2.5D);
				}
				if (GunCus.zoomLevel < 1.0D) {
					GunCus.zoomLevel = 1.0F;
				}
				ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, client.entityRenderer,
						Float.valueOf(GunCus.zoomLevel), new String[] { "cameraZoom", GunCus.cameraZoom });
			}

			if ((entityPlayer.inventory.getCurrentItem() != null)
					&& (entityPlayer.inventory.getCurrentItem().getItem() != null)
					&& ((entityPlayer.inventory.getCurrentItem().getItem() instanceof GunCusItemGun))
					&& (GunCus.hitmarker > 0)) {
				GunCus.hitmarker -= 1;
				ScaledResolution scale = new ScaledResolution(client.gameSettings, client.displayWidth,
						client.displayHeight);
				int xCenter = scale.getScaledWidth() / 2;
				int offset = scale.getScaledHeight() * 2;
				client.getTextureManager().bindTexture(new ResourceLocation("guncus:textures/sights/hitmarker.png"));
				Tessellator tessellator = Tessellator.instance;
				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(xCenter - offset, scale.getScaledHeight(), -100.0D, 0.0D, 1.0D);
				tessellator.addVertexWithUV(xCenter + offset, scale.getScaledHeight(), -100.0D, 1.0D, 1.0D);
				tessellator.addVertexWithUV(xCenter + offset, 0.0D, -100.0D, 1.0D, 0.0D);
				tessellator.addVertexWithUV(xCenter - offset, 0.0D, -100.0D, 0.0D, 0.0D);
				tessellator.draw();
			}
		}
	}
}
