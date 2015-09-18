package stuuupiiid.guncus.event;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.data.ScopePart;
import stuuupiiid.guncus.item.ItemGun;
import stuuupiiid.guncus.network.MessageClientValidation;
import stuuupiiid.guncus.network.PacketHandler;

public class TickHandler {
	
	// Called when a new frame is displayed (See FPS)
	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent event) {
		if (event.phase == Phase.START) {
			GunCus.commonProxy.renderTickStart();
		} else if (event.phase == Phase.END) {
			GunCus.commonProxy.renderTickEnd();
		}
	}
	
	// Client side
	@SubscribeEvent
	public void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
		if (GunCus.commonProxy.preRenderGameOverlay(event.type)) {
			if (event.isCancelable()) {
				event.setCanceled(true);
			}
		}
	}
	
	// Called when the server ticks. Usually 20 ticks a second.
	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event) {
		
	}
	
	// Called when the client ticks.
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		if (GunCus.switchTime > 0) {
			GunCus.switchTime -= 1;
		}
		
		if ((!Mouse.isButtonDown(0)) && (GunCus.accuracyReset > 0) && (GunCus.accuracy < 100.0D)) {
			GunCus.accuracyReset -= 1;
		} else if ((!Mouse.isButtonDown(0)) && (GunCus.accuracyReset == 0) && (GunCus.accuracy < 100.0D)) {
			GunCus.accuracyReset = 5;
			GunCus.accuracy = 100.0D;
		} else {
			GunCus.accuracyReset = 5;
		}
		
		if (GunCus.accuracy < 0.0D) {
			GunCus.accuracy = 0.0D;
		} else if (GunCus.accuracy > 100.0D) {
			GunCus.accuracy = 100.0D;
		}
		
		if (GunCus.knifeTime > 0) {
			GunCus.knifeTime -= 1;
		}
		
		if (FMLClientHandler.instance().getClient().thePlayer != null) {
			EntityPlayer entityPlayer = FMLClientHandler.instance().getClient().thePlayer;
			
			if (GunCus.shootTime > 0) {
				if (entityPlayer.inventory.getCurrentItem() == null) {
					GunCus.shootTime -= 1;
				} else if (!(entityPlayer.inventory.getCurrentItem().getItem() instanceof ItemGun)) {
					GunCus.shootTime -= 1;
				} else {
					ItemGun gun = (ItemGun) entityPlayer.inventory.getCurrentItem().getItem();
					
					if (gun.canHaveStraightPullBolt()) {
						if (Mouse.isButtonDown(1)) {
							if (gun.hasStraightPullBolt(entityPlayer.inventory.getCurrentItem().getItemDamage())) {
								GunCus.shootTime -= 1;
								if (GunCus.shootTime <= 0) {
									Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147673_a(new ResourceLocation("guncus:click")));
								}
							}
						} else {
							GunCus.shootTime -= 1;
							if (GunCus.shootTime <= 0) {
								Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147673_a(new ResourceLocation("guncus:click")));
							}
						}
					} else {
						GunCus.shootTime -= 1;
					}
				}
			}
			if (GunCus.shootTime <= 0) {
				GunCus.reloading = false;
			}
			
			if ((entityPlayer.motionY + 0.07840000152587891D != 0.0D) && (GunCus.accuracy > 35.0D)) {
				GunCus.accuracy = 35.0D;
			} else if (entityPlayer.isSprinting() && (GunCus.accuracy > 40.0D)) {
				GunCus.accuracy = 40.0D;
			} else if (((entityPlayer.motionX != 0.0D) || (entityPlayer.motionZ != 0.0D)) && (GunCus.accuracy > 70.0D)) {
				if (Mouse.isButtonDown(1) && (GunCus.accuracy > 70.0D)) {
					GunCus.accuracy = 75.0D;
				} else {
					GunCus.accuracy = 70.0D;
				}
			} else if ((!Mouse.isButtonDown(1)) && (GunCus.accuracy > 85.0D)) {
				if ( (entityPlayer.inventory.getCurrentItem() == null)
				  || (!(entityPlayer.inventory.getCurrentItem().getItem() instanceof ItemGun))
				  || (!((ItemGun) entityPlayer.inventory.getCurrentItem().getItem())
								.hasLaserPointer(entityPlayer.inventory.getCurrentItem().getItemDamage()))) {
					GunCus.accuracy = 85.0D;
				} else if (GunCus.accuracy > 92.5D) {
					GunCus.accuracy = 92.5D;
				}
			}
			
			if (entityPlayer.capabilities.isCreativeMode) {
				GunCus.accuracy = 100.0D;
			}
			
			if ( (entityPlayer.inventory.getCurrentItem() != null)
			  && (entityPlayer.inventory.getCurrentItem().getItem() instanceof ItemGun)
			  && Mouse.isButtonDown(1)
			  && entityPlayer.openContainer instanceof net.minecraft.inventory.ContainerPlayer) {
				ItemGun gun = (ItemGun) entityPlayer.inventory.getCurrentItem().getItem();
				if ( Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && (GunCus.counter <= 0)
				  && (!gun.hasBipod(entityPlayer.inventory.getCurrentItem().getItemDamage()))
				  && (!gun.hasImprovedGrip(entityPlayer.inventory.getCurrentItem().getItemDamage()))) {
					if (!GunCus.startedBreathing) {
						entityPlayer.playSound("random.breath", 1.0F, 1.0F);
						GunCus.startedBreathing = true;
						GunCus.breathing = true;
					}
				} else if ( (!GunCus.breathing)
					     && ( (!gun.hasBipod(entityPlayer.inventory.getCurrentItem().getItemDamage()))
					       || (!gun.canUseBipod(entityPlayer)))
					     && (!gun.hasImprovedGrip(entityPlayer.inventory.getCurrentItem().getItemDamage()))) {
					GunCus.breathCounter = 0;
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
			if (GunCus.breathing) {
				GunCus.breathCounter += 1;
				if (GunCus.breathCounter > 50) {
					GunCus.breathCounter = 0;
					GunCus.breathing = false;
				}
			}
			if ((!Mouse.isButtonDown(1)) && (GunCus.maxX >= 0.0F) && (GunCus.maxY >= 0.0F)) {
				GunCus.maxX = 0.0F;
				GunCus.maxY = 0.0F;
			}
			if (GunCus.startedBreathing) {
				GunCus.counter += 1;
				if (GunCus.counter >= 300) {
					GunCus.startedBreathing = false;
					GunCus.counter = 0;
				}
			}
		}
	}
	
	// Server side
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event){
		if (event.entity instanceof EntityPlayer) {
			GunCus.logger.info("onEntityJoinWorld " + event.entity);
			if (!event.world.isRemote) {
				for (int gunIndex = 0; gunIndex < GunCus.instance.guns.size(); gunIndex++) {
					MessageClientValidation clientConnectionMessage = new MessageClientValidation(gunIndex);
					PacketHandler.simpleNetworkManager.sendTo(clientConnectionMessage, (EntityPlayerMP) event.entity);
				}
			}
		}
	}
}
