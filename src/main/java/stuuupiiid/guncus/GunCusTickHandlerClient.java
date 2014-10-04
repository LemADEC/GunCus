package stuuupiiid.guncus;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GunCusTickHandlerClient implements IScheduledTickHandler {
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
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
				} else if (!(entityPlayer.inventory.getCurrentItem().getItem() instanceof GunCusItemGun)) {
					GunCus.shootTime -= 1;
				} else {
					GunCusItemGun gun = (GunCusItemGun) entityPlayer.inventory.getCurrentItem().getItem();

					if (gun.canHaveStraightPullBolt()) {
						if (Mouse.isButtonDown(1)) {
							if (gun.hasStraightPullBolt(entityPlayer.inventory.getCurrentItem().getItemDamage())) {
								GunCus.shootTime -= 1;
								if (GunCus.shootTime <= 0) {
									Minecraft.getMinecraft().sndManager.playSoundFX("guncus:click", 1.0F, 1.0F);
								}
							}
						} else {
							GunCus.shootTime -= 1;
							if (GunCus.shootTime <= 0) {
								Minecraft.getMinecraft().sndManager.playSoundFX("guncus:click", 1.0F, 1.0F);
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
			} else if ((entityPlayer.isSprinting()) && (GunCus.accuracy > 40.0D)) {
				GunCus.accuracy = 40.0D;
			} else if (((entityPlayer.motionX != 0.0D) || (entityPlayer.motionZ != 0.0D)) && (GunCus.accuracy > 70.0D)) {
				if ((Mouse.isButtonDown(1)) && (GunCus.accuracy > 70.0D)) {
					GunCus.accuracy = 75.0D;
				} else {
					GunCus.accuracy = 70.0D;
				}
			} else if ((!Mouse.isButtonDown(1)) && (GunCus.accuracy > 85.0D)) {
				if ((entityPlayer.inventory.getCurrentItem() == null)
						|| (!(entityPlayer.inventory.getCurrentItem().getItem() instanceof GunCusItemGun))
						|| (!((GunCusItemGun) entityPlayer.inventory.getCurrentItem().getItem())
								.hasLaserPointer(entityPlayer.inventory.getCurrentItem().getItemDamage()))) {
					GunCus.accuracy = 85.0D;
				} else if (GunCus.accuracy > 92.5D) {
					GunCus.accuracy = 92.5D;
				}
			}

			if (entityPlayer.capabilities.isCreativeMode) {
				GunCus.accuracy = 100.0D;
			}

			if ((entityPlayer.inventory.getCurrentItem() != null)
					&& ((entityPlayer.inventory.getCurrentItem().getItem() instanceof GunCusItemGun))
					&& (Mouse.isButtonDown(1))) {
				GunCusItemGun gun = (GunCusItemGun) entityPlayer.inventory.getCurrentItem().getItem();
				if ((Keyboard.isKeyDown(42)) && (GunCus.counter <= 0)
						&& (!gun.hasBipod(entityPlayer.inventory.getCurrentItem().getItemDamage()))
						&& (!gun.hasImprovedGrip(entityPlayer.inventory.getCurrentItem().getItemDamage()))) {
					if (!GunCus.startedBreathing) {
						entityPlayer.playSound("random.breath", 1.0F, 1.0F);
						GunCus.startedBreathing = true;
						GunCus.breathing = true;
					}
				} else if ((!GunCus.breathing)
						&& ((!gun.hasBipod(entityPlayer.inventory.getCurrentItem().getItemDamage())) || (!gun
								.canUseBipod(entityPlayer)))
						&& (!gun.hasImprovedGrip(entityPlayer.inventory.getCurrentItem().getItemDamage()))) {
					GunCus.breathCounter = 0;
					int metadata = entityPlayer.inventory.getCurrentItem().getItemDamage();
					float maxX = 0.05475F / (gun.zoomToFloat(gun.getZoom(metadata)) / 3.0F * 2.0F);
					float maxY = 0.0975F / (gun.zoomToFloat(gun.getZoom(metadata)) / 3.0F * 2.0F);
					float plusX = 0.005F / (gun.zoomToFloat(gun.getZoom(metadata)) / 3.0F * 2.0F);
					float plusY = 0.005F / (gun.zoomToFloat(gun.getZoom(metadata)) / 3.0F * 2.0F);

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

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public int nextTickSpacing() {
		return 1;
	}
}
