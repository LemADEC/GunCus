package stuuupiiid.guncus.event;

import java.lang.reflect.Field;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.data.ScopePart;
import stuuupiiid.guncus.item.ItemAttachmentPart;
import stuuupiiid.guncus.item.ItemGun;
import stuuupiiid.guncus.item.ItemRPG;
import stuuupiiid.guncus.network.MessageClientValidation;
import stuuupiiid.guncus.network.PacketHandler;

public class TickHandler {
	
	// Called when the server ticks. Usually 20 ticks a second.
	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event) {
		
	}
	
	// Minecraft support for disabling swing movement
	private static Field Minecraft_leftClickCounter;
	private static void noSwing(final int counter) {
		try {
			if (Minecraft_leftClickCounter == null) {
				try {
					try {
						Minecraft_leftClickCounter = Class.forName("net.minecraft.client.Minecraft").getDeclaredField("field_71429_W");	// obfuscated
					} catch (NoSuchFieldException exception) {
						Minecraft_leftClickCounter = Class.forName("net.minecraft.client.Minecraft").getDeclaredField("leftClickCounter");	// dev
					}
					Minecraft_leftClickCounter.setAccessible(true);
				} catch (Exception exception) {
					throw new RuntimeException(exception);
				}
			}
			Minecraft_leftClickCounter.set(Minecraft.getMinecraft(), counter);
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}
	// Minecraft support ends here
	
	// Called when the client ticks.
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onClientTick(TickEvent.ClientTickEvent event) {
		if (GunCus.switchTime > 0) {
			GunCus.switchTime -= 1;
		}
		
		// disable shooting right after closing a GUI (in case we've clicked a close button of sort)
		// this is also an additional safety to prevent shooting within a GUI
		Minecraft client = FMLClientHandler.instance().getClient();
		if (client.currentScreen != null) {
			GunCus.holdFireAfterClosingGUIcounter = 15;
		} else {
			if (GunCus.holdFireAfterClosingGUIcounter > 0) {
				if (!Mouse.isButtonDown(0)) {
					GunCus.holdFireAfterClosingGUIcounter = 0;
				} else {
					GunCus.holdFireAfterClosingGUIcounter -= 1;
				}
			}
		}
		
		// restore accuracy after 5 consecutive ticks without firing
		if ((!Mouse.isButtonDown(0)) && (GunCus.accuracyReset > 0) && (GunCus.accuracy < 100.0D)) {
			GunCus.accuracyReset -= 1;
		} else if ((!Mouse.isButtonDown(0)) && (GunCus.accuracyReset == 0) && (GunCus.accuracy < 100.0D)) {
			GunCus.accuracyReset = 5;
			GunCus.accuracy = 100.0D;
		} else {
			GunCus.accuracyReset = 5;
		}
		
		// clamping 0-100%
		if (GunCus.accuracy < 0.0D) {
			GunCus.accuracy = 0.0D;
		} else if (GunCus.accuracy > 100.0D) {
			GunCus.accuracy = 100.0D;
		}
		
		// knife cool-down
		if (GunCus.knifeTime > 0) {
			GunCus.knifeTime -= 1;
		}
		
		// get the player object
		if (FMLClientHandler.instance().getClient().thePlayer == null) {// not supposed to happen, but yeah...
			return;
		}
		EntityPlayer entityPlayer = FMLClientHandler.instance().getClient().thePlayer;
		
		// hold swing animation when holding a gun
		if ( (entityPlayer.inventory.getCurrentItem() != null)
		  && ((entityPlayer.inventory.getCurrentItem().getItem() instanceof ItemGun )
		  || ( entityPlayer.inventory.getCurrentItem().getItem() instanceof ItemRPG )
		  || ( entityPlayer.inventory.getCurrentItem().getItem() instanceof ItemAttachmentPart) ) ) {
			noSwing(10000);
		}
		
		if (GunCus.shootTime > 0) {
			// always cool-down when item isn't in hand, and we're not reloading
			if (entityPlayer.inventory.getCurrentItem() == null) {
				if (!GunCus.reloading) {
					GunCus.shootTime -= 1;
				}
			} else if (!(entityPlayer.inventory.getCurrentItem().getItem() instanceof ItemGun)) {
				if ((!GunCus.reloading) || (entityPlayer.inventory.getCurrentItem().getItem() instanceof ItemRPG) || (entityPlayer.inventory.getCurrentItem().getItem() instanceof ItemAttachmentPart)) {
					GunCus.shootTime -= 1;
				}
			} else {
				ItemGun gun = (ItemGun) entityPlayer.inventory.getCurrentItem().getItem();
				
				// guns with manual pull: engage bullet when idle or scoping with attachment, play click sound at the end
				// guns without: always engage bullet
				// all guns: only reload when not scoping
				if (gun.canHaveStraightPullBolt() && !GunCus.reloading) {
					if (Mouse.isButtonDown(1)) {
						if (gun.hasStraightPullBolt(entityPlayer.inventory.getCurrentItem().getItemDamage())) {
							GunCus.shootTime -= 1;
							if (GunCus.shootTime <= 0) {
								Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("guncus:click")));
							}
						}
					} else {
						GunCus.shootTime -= 1;
						if (GunCus.shootTime <= 0) {
							Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("guncus:click")));
						}
					}
				} else {
					if (!Mouse.isButtonDown(1) || !GunCus.reloading || gun.scopedReloading) {
						GunCus.shootTime -= 1;
					}
				}
			}
		}
		if (GunCus.shootTime <= 0) {
			GunCus.reloading = false;
		}
		
		// apply accuracy cap depending on player state
		double accuracyCap = 100.0D;
		if (entityPlayer.motionY + 0.07840000152587891D != 0.0D) {
			accuracyCap = 35.0D;
		} else if (entityPlayer.isSprinting()) {
			accuracyCap = 40.0D;
		} else if (((entityPlayer.motionX != 0.0D) || (entityPlayer.motionZ != 0.0D)) && (GunCus.accuracy > 70.0D)) {
			if (Mouse.isButtonDown(1)) {
				accuracyCap = 75.0D;
			} else {
				accuracyCap = 70.0D;
			}
		} else if (!Mouse.isButtonDown(1)) {
			if ( (entityPlayer.inventory.getCurrentItem() == null)
			  || (!(entityPlayer.inventory.getCurrentItem().getItem() instanceof ItemGun))
			  || (!((ItemGun) entityPlayer.inventory.getCurrentItem().getItem())
							.hasLaserPointer(entityPlayer.inventory.getCurrentItem().getItemDamage()))) {
				accuracyCap = 85.0D;
			} else {
				accuracyCap = 92.5D;
			}
		}
		if (GunCus.accuracy > accuracyCap) {
			GunCus.accuracy = accuracyCap;
		}
		if (entityPlayer.capabilities.isCreativeMode) {
			GunCus.accuracy = 100.0D;
		}
		
		if ( (client.currentScreen == null)
		  && (entityPlayer.inventory.getCurrentItem() != null)
		  && (entityPlayer.inventory.getCurrentItem().getItem() instanceof ItemGun)
		  && Mouse.isButtonDown(1)
		  && entityPlayer.openContainer instanceof net.minecraft.inventory.ContainerPlayer) {
			ItemGun gun = (ItemGun) entityPlayer.inventory.getCurrentItem().getItem();
			if ( Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && (GunCus.breathingCounter <= 0)
			  && (!gun.hasBipod(entityPlayer.inventory.getCurrentItem().getItemDamage()))
			  && (!gun.hasImprovedGrip(entityPlayer.inventory.getCurrentItem().getItemDamage()))) {
				if (GunCus.breathingCounter <= 0) {
					Random rand = entityPlayer.worldObj.rand;
					entityPlayer.playSound("guncus:breath", (rand.nextFloat() - rand.nextFloat()) * 0.1F + 0.2F, (rand.nextFloat() - rand.nextFloat()) * 0.07F + 0.75F);
					GunCus.breathingCounter = 300;
					GunCus.holdingBreathCounter = 50;
				}
			} else if ( (GunCus.holdingBreathCounter <= 0)
				     && ( (!gun.hasBipod(entityPlayer.inventory.getCurrentItem().getItemDamage()))
				       || (!gun.canUseBipod(entityPlayer)))
				     && (!gun.hasImprovedGrip(entityPlayer.inventory.getCurrentItem().getItemDamage()))) {
				int metadata = entityPlayer.inventory.getCurrentItem().getItemDamage();
				ScopePart scopePart = gun.getScopePart(metadata);
				float zoom = (scopePart == null) ? 1.0F : scopePart.zoom;
				float maxX = 0.05475F / (zoom / 3.0F * 2.0F);
				float maxY = 0.0975F / (zoom / 3.0F * 2.0F);
				float plusX = 0.005F / (zoom / 3.0F * 2.0F);
				float plusY = 0.005F / (zoom / 3.0F * 2.0F);
				
				if (GunCus.scopingX) {
					GunCus.maxX += plusX;
					entityPlayer.rotationYaw += GunCus.maxX;
					
					if (GunCus.maxX >= maxX) {
						GunCus.scopingX = false;
					}
				} else if (!GunCus.scopingX) {
					GunCus.maxX -= plusX;
					entityPlayer.rotationYaw += GunCus.maxX;
					
					if (GunCus.maxX <= -maxX) {
						GunCus.scopingX = true;
					}
				}
				
				if (GunCus.scopingY) {
					GunCus.maxY += plusY;
					entityPlayer.rotationPitch += GunCus.maxY;
					
					if (GunCus.maxY >= maxY) {
						GunCus.scopingY = false;
					}
				} else if (!GunCus.scopingY) {
					GunCus.maxY -= plusY;
					entityPlayer.rotationPitch += GunCus.maxY;
					
					if (GunCus.maxY <= -maxY) {
						GunCus.scopingY = true;
					}
				}
			}
		}
		if (GunCus.holdingBreathCounter > 0) {
			GunCus.holdingBreathCounter -= 1;
		}
		if ((!Mouse.isButtonDown(1)) && (GunCus.maxX >= 0.0F) && (GunCus.maxY >= 0.0F)) {
			GunCus.maxX = 0.0F;
			GunCus.maxY = 0.0F;
		}
		if (GunCus.breathingCounter > 0) {
			GunCus.breathingCounter -= 1;
		}
	}
	
	// Server side
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event){
		if (event.entity instanceof EntityPlayer) {
			// GunCus.logger.info("onEntityJoinWorld " + event.entity);
			if (!event.world.isRemote) {
				for (ItemGun itemGun : GunCus.instance.guns.values()) {
					MessageClientValidation clientConnectionMessage = new MessageClientValidation(itemGun);
					PacketHandler.simpleNetworkManager.sendTo(clientConnectionMessage, (EntityPlayerMP) event.entity);
				}
			}
		}
	}
}
