package stuuupiiid.guncus;

import stuuupiiid.guncusexplosives.GunCusExplosivesRPG;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetServerHandler;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class GunCusPacketHandler implements IPacketHandler {
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		try {
			DataInputStream data = new DataInputStream(new ByteArrayInputStream(packet.data));
			EntityPlayer entityPlayer = (EntityPlayer) player;
			World world = entityPlayer.worldObj;

			int packetType = data.readInt();
			int acc = data.readInt();

			if (packetType == 1) {// ItemGun shoot(accuracy) client->server
				if ((entityPlayer != null) && (entityPlayer.inventory.getCurrentItem() != null)
						&& ((entityPlayer.inventory.getCurrentItem().getItem() instanceof GunCusItemGun))) {
					GunCusItemGun gun = (GunCusItemGun) entityPlayer.inventory.getCurrentItem().getItem();
					int metadata = entityPlayer.inventory.getCurrentItem().getItemDamage();
					ItemStack mag = null;

					if (gun.magId != -1) {
						for (int v1 = 0; v1 < entityPlayer.inventory.getSizeInventory(); v1++) {
							if ((entityPlayer.inventory.getStackInSlot(v1) != null)
									&& (entityPlayer.inventory.getStackInSlot(v1).getItem() == gun.mag)
									&& (entityPlayer.inventory.getStackInSlot(v1).isItemDamaged())
									&& (entityPlayer.inventory.getStackInSlot(v1).getItemDamage() < entityPlayer.inventory
											.getStackInSlot(v1).getMaxDamage())) {
								mag = entityPlayer.inventory.getStackInSlot(v1);
								break;
							}
						}

						if (mag == null) {
							for (int v1 = 0; v1 < entityPlayer.inventory.getSizeInventory(); v1++) {
								if ((entityPlayer.inventory.getStackInSlot(v1) != null)
										&& (entityPlayer.inventory.getStackInSlot(v1).getItem() == gun.mag)
										&& (!entityPlayer.inventory.getStackInSlot(v1).isItemDamaged())) {
									mag = entityPlayer.inventory.getStackInSlot(v1);
									break;
								}
							}
						}
					}

					int var1 = -1;

					if (gun.mag == null) {
						var1 = data.readInt();
					}

					if ( ((mag != null) && (gun.mag != null))
							|| ((gun.mag == null) && (entityPlayer.inventory.hasItem(GunCusItemBullet.bulletsList.get(gun.pack).get(var1))))
							|| (entityPlayer.capabilities.isCreativeMode)) {
						if ((!entityPlayer.capabilities.isCreativeMode) && (gun.mag != null) && (mag != null)) {
							mag.damageItem(1, entityPlayer);
						} else if ((!entityPlayer.capabilities.isCreativeMode) && (gun.mag == null)) {
							entityPlayer.inventory.consumeInventoryItem(GunCusItemBullet.bulletsList.get(gun.pack).get(var1));
						}

						if (gun.hasSilencer(metadata)) {
							world.playSoundAtEntity(entityPlayer, gun.soundSilenced, 1.0F, 1.0F / (world.rand.nextFloat() * 0.4F + 0.8F));
						} else {
							world.playSoundAtEntity(entityPlayer, gun.soundNormal, 5.0F * (float) gun.soundModify, 1.0F / (world.rand.nextFloat() * 0.4F + 0.8F));
						}
						GunCusItemBullet bulletItem;
						if (gun.mag != null) {
							bulletItem = GunCusItemBullet.bulletsList.get(gun.pack).get(gun.mag.bulletType);
						} else {
							bulletItem = GunCusItemBullet.bulletsList.get(gun.pack).get(var1);
						}

						if (acc > bulletItem.spray) {
							acc = bulletItem.spray;
						}

						float damage = gun.damage * bulletItem.damage;

						if (gun.hasHeavyBarrel(metadata)) {
							damage += 2.0F;
						}
						if (gun.hasStrongSpiralSpring(metadata)) {
							damage += 1.0F;
						}

						for (int v1 = 0; v1 < bulletItem.split; v1++) {
							GunCusEntityBullet bulletEntity = new GunCusEntityBullet(world, entityPlayer, 10.0F, damage, acc)
									.setLowerGravity(gun.hasPolygonalBarrel(metadata))
									.setBullet(bulletItem);
							world.spawnEntityInWorld(bulletEntity);
						}

						if ((!entityPlayer.capabilities.isCreativeMode) && (mag != null) && (mag.getItemDamage() >= mag.getMaxDamage())) {
							// reloading...
							ByteArrayDataOutput bytes = ByteStreams.newDataOutput();
							bytes.writeInt(2);
							bytes.writeInt(0);
							PacketDispatcher.sendPacketToPlayer(new Packet250CustomPayload("guncus", bytes.toByteArray()), (Player) entityPlayer);
						}
					}
				}
			} else if (packetType == 2) {// Reloading (server->client)
				GunCus.reloading = true;
				GunCus.shootTime += 90;
				Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147673_a(new ResourceLocation("guncus:reload")));
			} else if (packetType == 3) {// GuiGun
				GunCusContainerGun container = (GunCusContainerGun) entityPlayer.openContainer;

				if (acc == 0) {
					container.split();
				} else if (acc == 1) {
					container.build();
				}
			} else if (packetType == 4) {// GuiAmmo
				GunCusContainerAmmo container = (GunCusContainerAmmo) entityPlayer.openContainer;

				if (acc == 0) {
					container.fill();
				} else if (acc == 1) {
					container.empty();
				}
			} else if (packetType == 5) {// GuiAmmoMan
				GunCusContainerAmmoMan container = (GunCusContainerAmmoMan) entityPlayer.openContainer;

				if (acc == 0) {
					container.fill();
				} else if (acc == 1) {
					container.empty();
				}
			} else if (packetType == 6) {// GuiMag
				GunCusContainerMag container = (GunCusContainerMag) entityPlayer.openContainer;

				if (acc == 0) {
					container.create();
				} else if (acc == 1) {
					entityPlayer.addChatComponentMessage(new ChatComponentText(container.info()[0]));
					if (container.info()[1] != null) {
						entityPlayer.addChatComponentMessage(new ChatComponentText(container.info()[1]));
					}
				}
			} else if (packetType == 7) {// GuiBullet
				GunCusContainerBullet container = (GunCusContainerBullet) entityPlayer.openContainer;

				if (acc == 0) {
					container.create();
				} else if (acc == 1) {
					entityPlayer.addChatComponentMessage(new ChatComponentText(container.info()[0]));
					if (container.info()[1] != null) {
						entityPlayer.addChatComponentMessage(new ChatComponentText(container.info()[1]));
					}
				}
			} else if (packetType == 8) {// ItemAttachment (tube accurrency)
				if ((entityPlayer != null) && (entityPlayer.inventory.getCurrentItem() != null)) {
					if ((entityPlayer.inventory.getCurrentItem().getItem() instanceof GunCusItemGun)) {
						GunCusItemGun gun = (GunCusItemGun) entityPlayer.inventory.getCurrentItem().getItem();
						int metadata = entityPlayer.inventory.getCurrentItem().getItemDamage();

						if (gun.hasM320(metadata)
								&& ((entityPlayer.inventory.hasItem(GunCus.ammoM320) && entityPlayer.inventory.consumeInventoryItem(GunCus.ammoM320))
										|| entityPlayer.capabilities.isCreativeMode)) {
							GunCusEntityAT rocket = new GunCusEntityAT(world, entityPlayer, acc, 2);
							world.playSoundAtEntity(entityPlayer, "random.explode", 4.0F, 1.0F);
							world.spawnEntityInWorld(rocket);
						}
					} else if ((entityPlayer.inventory.getCurrentItem().getItem() == GunCus.attachment)
							&& (entityPlayer.inventory.getCurrentItem().getItemDamage() == 3)) {
						if (entityPlayer.capabilities.isCreativeMode
								|| (	entityPlayer.inventory.hasItem(GunCus.ammoM320)
										&& entityPlayer.inventory.consumeInventoryItem(GunCus.ammoM320))) {
							GunCusEntityAT rocket = new GunCusEntityAT(world, entityPlayer, acc, 2);
							world.playSoundAtEntity(entityPlayer, "random.explode", 4.0F, 1.0F);
							world.spawnEntityInWorld(rocket);
						}
					} else if ((entityPlayer.inventory.getCurrentItem().getItem() instanceof GunCusExplosivesRPG)) {
						GunCusExplosivesRPG rpg = (GunCusExplosivesRPG) entityPlayer.inventory.getCurrentItem().getItem();
						if ((entityPlayer.capabilities.isCreativeMode)
								|| ((entityPlayer.inventory.hasItem(rpg.ammo)) && (entityPlayer.inventory
										.consumeInventoryItem(rpg.ammo)))) {
							GunCusEntityAT rocket = new GunCusEntityAT(world, entityPlayer, acc, 1);
							world.playSoundAtEntity(entityPlayer, "random.explode", 4.0F, 1.0F);
							world.spawnEntityInWorld(rocket);
						}
					}
				}
			} else if (packetType == 9) {// GuiWeapon
				GunCusContainerWeapon container = (GunCusContainerWeapon) entityPlayer.openContainer;

				if (acc == 1) {
					int actual = data.readInt();
					actual++;
					if (actual >= GunCus.instance.guns.size()) {
						actual = 0;
					}

					if (GunCus.instance.guns.size() > actual) {
						container.actual = actual;
						container.actualItemID = GunCus.instance.guns.get(actual).itemID;
					}

					ByteArrayDataOutput bytes = ByteStreams.newDataOutput();
					bytes.writeInt(15);
					bytes.writeInt(0);
					bytes.writeInt(actual);
					bytes.writeInt(GunCus.instance.guns.get(actual).itemID);
					PacketDispatcher.sendPacketToPlayer(new Packet250CustomPayload("guncus", bytes.toByteArray()), (Player) entityPlayer);
				} else if (acc == 0) {
					entityPlayer.addChatComponentMessage(new ChatComponentText(container.info()[0]));
					if (container.info()[1] != null) {
						entityPlayer.addChatComponentMessage(new ChatComponentText(container.info()[1]));
					}
				} else if (acc == 2) {
					container.create();
				}
			} else if (packetType == 10) {// bullet
				GunCus.hitmarker = 5;
				Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147673_a(new ResourceLocation("guncus:inground")));
			} else if (packetType == 11) {
				float str = acc / 100.0F;
				int x = data.readInt();
				int y = data.readInt();
				int z = data.readInt();

				world.createExplosion(entityPlayer, x, y, z, str, true);
			} else if (packetType == 12) {
				int x = data.readInt();
				int y = data.readInt();
				int z = data.readInt();

				world.destroyBlock(x, y, z, true);
			} else if (packetType == 13) {// doKnife
				world.playSoundAtEntity(entityPlayer, "guncus:knife", 1.0F, 1.0F);
				EntityLiving target = null;
				double acD = 2.0D;

				double x = entityPlayer.posX;
				double y = entityPlayer.posY;
				double z = entityPlayer.posZ;

				for (Iterator i$ = world.loadedEntityList.iterator(); i$.hasNext();) {
					Object entity = i$.next();

					if ((entity instanceof EntityLiving)) {
						double x2 = ((EntityLiving) entity).posX;
						double y2 = ((EntityLiving) entity).posY;
						double z2 = ((EntityLiving) entity).posZ;

						double dx = x2 - x;
						double dy = y2 - y;
						double dz = z2 - z;

						double d = MathHelper.sqrt_double(dx * dx + dy * dy + dz * dz);

						if ((d <= 2.0001D) && (d < acD)
								&& ((!(entity instanceof EntityPlayer)) || ((EntityPlayer) entity != entityPlayer))) {
							target = (EntityLiving) entity;
							acD = d;
						}
					}
				}

				if ((target != null) && (acD <= 2.0001D)) {
					target.attackEntityFrom(DamageSource.causePlayerDamage(entityPlayer), 20.0F);
				}
			} else if (packetType == 14) {// playerLoggedIn
				int packetType2 = data.readShort();

				int var1 = data.readShort();
				int var2 = data.readShort();
				int var3 = data.readShort();
				int var4 = data.readShort();
				int var5 = data.readShort();
				int var6 = data.readShort();

				if (packetType2 == 1) {
					int id = var1;
					int shootType = var2;
					int delay = var3;
					int magId = var4;
					int bullets = var5;
					int recModify = var6;

					GunCusItemGun gun = GunCus.instance.guns.get(id);

					if (   (delay != gun.delay)
						|| (shootType != gun.shootType)
						|| (magId != gun.mag.name)
						|| (bullets != gun.mag.bulletType)
						|| (recModify != gun.recModify)) {
						ByteArrayDataOutput bytes = ByteStreams.newDataOutput();
						bytes.writeShort(14);
						bytes.writeShort(0);
						bytes.writeShort(2);
						bytes.writeShort(0);
						bytes.writeShort(0);
						bytes.writeShort(0);
						bytes.writeShort(0);
						bytes.writeShort(0);
						bytes.writeShort(0);
						PacketDispatcher.sendPacketToServer(new Packet250CustomPayload("guncus2", bytes.toByteArray()));
					}
				}
				if (packetType2 == 2) {
					EntityPlayerMP entityPlayerMp = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(entityPlayer.getDisplayName());
					String s = "You were using modified weapons!";

					if (entityPlayerMp != null) {
						entityPlayerMp.playerNetServerHandler.kickPlayerFromServer(s);
						System.out.println("[GunCus] Kicked player " + entityPlayerMp.getDisplayName() + " because he was using modified weapons!");
					}
				}
			} else if (packetType == 15) {
				GunCus.actual = data.readInt();
				GunCus.actualItemID = data.readInt();
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
}
