package stuuupiiid.guncus.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.input.Mouse;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.data.ScopePart;
import stuuupiiid.guncus.item.ItemGun;
import stuuupiiid.guncus.item.ItemRPG;

public class RenderGameOverlay {
	
	// general state
	private static boolean hasGunInHand = false;
	private static boolean hasRPGinHand = false;
	private static boolean hasM320inHand = false;
	private static boolean drawSight = false;
	
	// progress bar state
	private int previousShootTime = 0;
	private int startedShootTime = 0;
	
	// Called when a new frame is displayed (See FPS)
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRenderTick(TickEvent.RenderTickEvent event) {
		if (event.phase == Phase.START) {
			renderTickStart();
		} else if (event.phase == Phase.END) {
			renderTickEnd();
		}
	}
	
	// Client side
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
		if (drawSight) {
			switch (event.type) {
			case HELMET:
			case AIR:
			case ARMOR:
			case EXPERIENCE:
			case FOOD:
			case HEALTH:
			case HEALTHMOUNT:
			case HOTBAR:
			case CHAT:
			case TEXT:
				// Don't render other GUI parts
				if (event.isCancelable()) {
					event.setCanceled(true);
				}
				break;
				
			case CROSSHAIRS: // workaround for what looks like a Forge bug
				Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.icons);
				event.setCanceled(true);
				break;
				
			case BOSSHEALTH:
			default:
				break;
			}
		}
	}
	
	private static int colorGradient(float gradient, int start, int end) {
		return Math.max(0, Math.min(255, start + Math.round(gradient * (end - start))));
	}
	
	@SideOnly(Side.CLIENT)
	public void renderTickStart() {
		Minecraft client = FMLClientHandler.instance().getClient();
		if (client.thePlayer == null) {
			return;
		}
		EntityPlayer entityPlayer = client.thePlayer;
		
		hasGunInHand = (entityPlayer.inventory.getCurrentItem() != null) && (entityPlayer.inventory.getCurrentItem().getItem() instanceof ItemGun);
		hasRPGinHand = (entityPlayer.inventory.getCurrentItem() != null) && (entityPlayer.inventory.getCurrentItem().getItem() instanceof ItemRPG);
		hasM320inHand = (entityPlayer.inventory.getCurrentItem() != null)
				&& (entityPlayer.inventory.getCurrentItem().getItem() == GunCus.itemAttachment) && (entityPlayer.inventory.getCurrentItem().getItemDamage() == 4);
		drawSight = hasGunInHand && Mouse.isButtonDown(1) && (client.gameSettings.thirdPersonView == 0) && (client.currentScreen == null);
	}
	
	@SideOnly(Side.CLIENT)
	public void renderTickEnd() {
		Minecraft client = FMLClientHandler.instance().getClient();
		if (client.thePlayer == null) {
			return;
		}
		EntityPlayer entityPlayer = client.thePlayer;
		
		ScaledResolution scale = new ScaledResolution(client);
		int scaledWidth = scale.getScaledWidth();
		int scaledHeight = scale.getScaledHeight();
		int xCenter = scaledWidth / 2;
		int offset = scaledHeight * 2;
		// draw sight
		if (drawSight) {
			ItemGun gun = (ItemGun) entityPlayer.inventory.getCurrentItem().getItem();
			ScopePart scopePart = gun.getScopePart(entityPlayer.inventory.getCurrentItem().getItemDamage());
			String sightTextureName;
			float newZoom = gun.zoom + 0.1F;
			if (scopePart != null) {
				// scope, always use originals
				newZoom = scopePart.zoom + 0.1F;
				sightTextureName = "guncus:textures/sights/" + scopePart.unlocalizedName + ".png";
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
			if (GunCus.cameraZoom != null) {
				try {
					GunCus.cameraZoom.set(client.entityRenderer, GunCus.zoomLevel);
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
			
			client.getTextureManager().bindTexture(new ResourceLocation(sightTextureName));
			Tessellator tessellator = Tessellator.getInstance();
			WorldRenderer worldRenderer = tessellator.getWorldRenderer();
			
			worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
			worldRenderer.pos(xCenter - offset, scaledHeight, -90.0D).tex(0.0D, 1.0D).endVertex();
			worldRenderer.pos(xCenter + offset, scaledHeight, -90.0D).tex(1.0D, 1.0D).endVertex();
			worldRenderer.pos(xCenter + offset,         0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
			worldRenderer.pos(xCenter - offset,         0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
			tessellator.draw();
		} else {
			float newZoom = 1.0F;
			
			// Progressively adjust zoom
			if (GunCus.zoomLevel < newZoom) {
				GunCus.zoomLevel = Math.min(newZoom, GunCus.zoomLevel + 2.5F);
			} else if (GunCus.zoomLevel > newZoom) {
				GunCus.zoomLevel = Math.max(newZoom, GunCus.zoomLevel - 2.5F);
			}
			if (GunCus.cameraZoom != null) {
				try {
					GunCus.cameraZoom.set(client.entityRenderer, GunCus.zoomLevel);
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		}
		
		// draw hit marker
		if ((hasGunInHand || hasRPGinHand || hasM320inHand) && (GunCus.hitmarker > 0) && (client.currentScreen == null)) {
			GunCus.hitmarker -= 1;
			client.getTextureManager().bindTexture(new ResourceLocation("guncus:textures/sights/hitmarker.png"));
			Tessellator tessellator = Tessellator.getInstance();
			WorldRenderer worldRenderer = tessellator.getWorldRenderer();
			
			worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
			worldRenderer.pos(xCenter - offset, scaledHeight, -100.0D).tex(0.0D, 1.0D).endVertex();
			worldRenderer.pos(xCenter + offset, scaledHeight, -100.0D).tex(1.0D, 1.0D).endVertex();
			worldRenderer.pos(xCenter + offset,         0.0D, -100.0D).tex(1.0D, 0.0D).endVertex();
			worldRenderer.pos(xCenter - offset,         0.0D, -100.0D).tex(0.0D, 0.0D).endVertex();
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
			int textX = (scaledWidth - mc.fontRendererObj.getStringWidth(text)) / 2;
			float progress = Math.min(1.0F, Math.max(0.0F, 1.0F - GunCus.shootTime / 95F));
			int color = (colorGradient(progress, 0xFF, 0x00) << 16) + (colorGradient(progress, 0x40, 0xFF) << 8) + colorGradient(progress, 0x00, 0x00);
			
			mc.fontRendererObj.drawString(text, textX, scaledHeight / 2 + 8, color, true);
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
				int textX = (scaledWidth - mc.fontRendererObj.getStringWidth(text)) / 2;
				float progress = Math.min(1.0F, Math.max(0.0F, 1.0F - GunCus.shootTime / (float) startedShootTime));
				
				mc.fontRendererObj.drawString(text, textX, scaledHeight / 2 + 8, 0xFF0000, true);
				mc.fontRendererObj.drawString(text.substring(0, Math.round(text.length() * progress)), textX, scaledHeight / 2 + 8, 0x40FF00, true);
				
				previousShootTime = GunCus.shootTime;
			}
		}
	}
}
