package stuuupiiid.guncus.network;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.block.ContainerAmmo;
import stuuupiiid.guncus.block.ContainerAmmoMan;
import stuuupiiid.guncus.block.ContainerBullet;
import stuuupiiid.guncus.block.ContainerGun;
import stuuupiiid.guncus.block.ContainerMag;
import stuuupiiid.guncus.block.ContainerWeapon;
import stuuupiiid.guncus.entity.EntityGrenade;
import stuuupiiid.guncus.entity.EntityBullet;
import stuuupiiid.guncus.gui.GuiHandler;
import stuuupiiid.guncus.item.ItemBullet;
import stuuupiiid.guncus.item.ItemGun;
import stuuupiiid.guncus.item.ItemMag;
import stuuupiiid.guncus.item.ItemRPG;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
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

public class PacketHandler {
	public static final SimpleNetworkWrapper simpleNetworkManager = NetworkRegistry.INSTANCE.newSimpleChannel(GunCus.MODID);
	private static Method EntityTrackerEntry_getPacketForThisEntity;
	
	public static void init() {
		// Forge packets
		simpleNetworkManager.registerMessage(MessageReloading.class , MessageReloading.class , 2, Side.CLIENT);	// legacy 2
		
		simpleNetworkManager.registerMessage(MessageTubeShoot.class , MessageTubeShoot.class , 101, Side.SERVER);	// legacy 1
		simpleNetworkManager.registerMessage(MessageTubeShoot.class , MessageTubeShoot.class , 108, Side.SERVER);	// legacy 8
	}

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		try {
			DataInputStream data = new DataInputStream(new ByteArrayInputStream(packet.data));
			EntityPlayer entityPlayer = (EntityPlayer) player;
			World world = entityPlayer.worldObj;

			int packetType = data.readInt();
			int packetData = data.readInt();

			if (packetType == 1) {// ItemGun shoot(accuracy) client->server	=> MessageGunShoot
				/*
				if ((entityPlayer != null) && (entityPlayer.inventory.getCurrentItem() != null)
						&& ((entityPlayer.inventory.getCurrentItem().getItem() instanceof ItemGun))) {
					ItemGun gun = (ItemGun) entityPlayer.inventory.getCurrentItem().getItem();
					int metadata = entityPlayer.inventory.getCurrentItem().getItemDamage();
					ItemStack mag = null;

					if (gun.mag != null) {
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
							|| ((gun.mag == null) && (entityPlayer.inventory.hasItem(ItemBullet.bulletsList.get(gun.pack).get(var1))))
							|| (entityPlayer.capabilities.isCreativeMode)) {
						if ((!entityPlayer.capabilities.isCreativeMode) && (gun.mag != null) && (mag != null)) {
							mag.damageItem(1, entityPlayer);
						} else if ((!entityPlayer.capabilities.isCreativeMode) && (gun.mag == null)) {
							entityPlayer.inventory.consumeInventoryItem(ItemBullet.bulletsList.get(gun.pack).get(var1));
						}

						if (gun.hasSilencer(metadata)) {
							world.playSoundAtEntity(entityPlayer, gun.soundSilenced, 1.0F, 1.0F / (world.rand.nextFloat() * 0.4F + 0.8F));
						} else {
							world.playSoundAtEntity(entityPlayer, gun.soundNormal, 5.0F * (float) gun.soundModify, 1.0F / (world.rand.nextFloat() * 0.4F + 0.8F));
						}
						ItemBullet bulletItem;
						if (gun.mag != null) {
							bulletItem = ItemBullet.bulletsList.get(gun.pack).get(gun.mag.bulletType);
						} else {
							bulletItem = ItemBullet.bulletsList.get(gun.pack).get(var1);
						}

						if (packetData > bulletItem.spray) {
							packetData = bulletItem.spray;
						}

						float damage = gun.damage * bulletItem.damage;

						if (gun.hasHeavyBarrel(metadata)) {
							damage += 2.0F;
						}
						if (gun.hasStrongSpiralSpring(metadata)) {
							damage += 1.0F;
						}

						for (int v1 = 0; v1 < bulletItem.split; v1++) {
							EntityBullet bulletEntity = new EntityBullet(world, entityPlayer, 10.0F, damage, packetData)
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
				/**/
			} else if (packetType == 2) {// Reloading (server->client)	MessageReloading
				/*
				GunCus.reloading = true;
				GunCus.shootTime += 90;
				Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147673_a(new ResourceLocation("guncus:reload")));
				/**/
			} else if (packetType == 8) {// ItemAttachment (tube accuracy) (client->server)	MessageTubeShoot
				/*
				if ((entityPlayer != null) && (entityPlayer.inventory.getCurrentItem() != null)) {
					if ((entityPlayer.inventory.getCurrentItem().getItem() instanceof ItemGun)) {
						ItemGun gun = (ItemGun) entityPlayer.inventory.getCurrentItem().getItem();
						int metadata = entityPlayer.inventory.getCurrentItem().getItemDamage();

						if (gun.hasM320(metadata)
								&& ((entityPlayer.inventory.hasItem(GunCus.ammoM320) && entityPlayer.inventory.consumeInventoryItem(GunCus.ammoM320))
										|| entityPlayer.capabilities.isCreativeMode)) {
							EntityGrenade rocket = new EntityGrenade(world, entityPlayer, packetData, 2);
							world.playSoundAtEntity(entityPlayer, "random.explode", 4.0F, 1.0F);
							world.spawnEntityInWorld(rocket);
						}
					} else if ((entityPlayer.inventory.getCurrentItem().getItem() == GunCus.attachment)
							&& (entityPlayer.inventory.getCurrentItem().getItemDamage() == 3)) {
						if (entityPlayer.capabilities.isCreativeMode
								|| (	entityPlayer.inventory.hasItem(GunCus.ammoM320)
										&& entityPlayer.inventory.consumeInventoryItem(GunCus.ammoM320))) {
							EntityGrenade rocket = new EntityGrenade(world, entityPlayer, packetData, 2);
							world.playSoundAtEntity(entityPlayer, "random.explode", 4.0F, 1.0F);
							world.spawnEntityInWorld(rocket);
						}
					} else if ((entityPlayer.inventory.getCurrentItem().getItem() instanceof ItemRPG)) {
						ItemRPG rpg = (ItemRPG) entityPlayer.inventory.getCurrentItem().getItem();
						if ((entityPlayer.capabilities.isCreativeMode)
								|| ((entityPlayer.inventory.hasItem(rpg.ammo)) && (entityPlayer.inventory
										.consumeInventoryItem(rpg.ammo)))) {
							EntityGrenade rocket = new EntityGrenade(world, entityPlayer, packetData, 1);
							world.playSoundAtEntity(entityPlayer, "random.explode", 4.0F, 1.0F);
							world.spawnEntityInWorld(rocket);
						}
					}
				}
				/**/
			} else if (packetType == 10) {// bullet sound (server -> client)
				GunCus.hitmarker = 5;
				Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147673_a(new ResourceLocation("guncus:inground")));
			} else if (packetType == 11) {
				float str = packetData / 100.0F;
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
				/*
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
				/**/
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

					ItemGun gun = GunCus.instance.guns.get(id);

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
						GunCus.logger.info("[GunCus] Kicked player " + entityPlayerMp.getDisplayName() + " because he was using modified weapons!");
					}
				}
			} else if (packetType == 15) {// weapon selection in weapon box (server -> client)
				GunCus.actualIndex = data.readInt();
				GunCus.actualItem = data.readInt();
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	public static void sendPacket10(EntityPlayer entityPlayer) {
		ByteArrayDataOutput bytes = ByteStreams.newDataOutput();
		bytes.writeInt(10);
		bytes.writeInt(0);
		PacketDispatcher.sendPacketToPlayer(new Packet250CustomPayload("guncus", bytes.toByteArray()), (Player) playerEntity);
	}

	public static void sendToServer_GUIaction(final int guiId, final int buttonId) {
		sendToServer_GUIaction(guiId, buttonId, 0);
	}

	public static void sendToServer_GUIaction(final int guiId, final int buttonId, final int currentGun) {
		ByteArrayDataOutput bytes = ByteStreams.newDataOutput();
		bytes.writeInt(100 + guiId);
		bytes.writeInt(buttonId);
		PacketDispatcher.sendPacketToServer(new Packet250CustomPayload("guncus", bytes.toByteArray()));
	}
	
	public static void read_GUIaction(EntityPlayer entityPlayer, final int guiId, final int buttonId, final int currentGun) {
		int packetType = guiId - 100;
		
		if (packetType == GuiHandler.gunBlock) {// 3 GuiGun (client -> server)
			ContainerGun container = (ContainerGun) entityPlayer.openContainer;
	
			if (buttonId == 0) {
				container.split();
			} else if (buttonId == 1) {
				container.build();
			}
		} else if (packetType == GuiHandler.ammoBlock) {// 4 GuiAmmo (client -> server)
			ContainerAmmo container = (ContainerAmmo) entityPlayer.openContainer;
	
			if (buttonId == 0) {
				container.fill();
			} else if (buttonId == 1) {
				container.empty();
			}
		} else if (packetType == GuiHandler.magItem) {// 5 GuiAmmoMan (client -> server)
			ContainerAmmoMan container = (ContainerAmmoMan) entityPlayer.openContainer;
	
			if (buttonId == 0) {
				container.fill();
			} else if (buttonId == 1) {
				container.empty();
			}
		} else if (packetType == GuiHandler.magBlock) {// 6 GuiMag (client -> server)
			ContainerMag container = (ContainerMag) entityPlayer.openContainer;
	
			if (buttonId == 0) {
				container.create();
			} else if (buttonId == 1) {
				entityPlayer.addChatComponentMessage(new ChatComponentText(container.info()[0]));
				if (container.info()[1] != null) {
					entityPlayer.addChatComponentMessage(new ChatComponentText(container.info()[1]));
				}
			}
		} else if (packetType == GuiHandler.bulletBlock) {// 7 GuiBullet (client -> server)
			ContainerBullet container = (ContainerBullet) entityPlayer.openContainer;
	
			if (buttonId == 0) {
				container.create();
			} else if (buttonId == 1) {
				entityPlayer.addChatComponentMessage(new ChatComponentText(container.info()[0]));
				if (container.info()[1] != null) {
					entityPlayer.addChatComponentMessage(new ChatComponentText(container.info()[1]));
				}
			}
		} else if (packetType == GuiHandler.weaponBlock) {// 9 GuiWeapon
			ContainerWeapon container = (ContainerWeapon) entityPlayer.openContainer;
			
			if (buttonId == 1) {
				int actual = currentGun;
				actual++;
				if (actual >= GunCus.instance.guns.size()) {
					actual = 0;
				}
				
				container.actualGunIndex = actual;
				container.actualGunItem = GunCus.instance.guns.get(actual);
				
				ByteArrayDataOutput bytes = ByteStreams.newDataOutput();
				bytes.writeInt(15);
				bytes.writeInt(0);
				bytes.writeInt(actual);
				bytes.writeUTF(GunCus.instance.guns.get(actual).getUnlocalizedName());
				PacketDispatcher.sendPacketToPlayer(new Packet250CustomPayload("guncus", bytes.toByteArray()), entityPlayer);
			} else if (buttonId == 0) {
				entityPlayer.addChatComponentMessage(new ChatComponentText(container.info()[0]));
				if (container.info()[1] != null) {
					entityPlayer.addChatComponentMessage(new ChatComponentText(container.info()[1]));
				}
			} else if (buttonId == 2) {
				container.create();
			}
		}
	}
	
	public static void sendToServer_playerAction_shoot(EntityPlayer entityPlayer, ItemMag mag, int[] bullets, int actualBullet) {
		MessageGunShoot gunShootMessage = new MessageGunShoot(MathHelper.floor_double(GunCus.accuracy), (mag == null) ? bullets[actualBullet] : -1);
		PacketHandler.simpleNetworkManager.sendToServer(gunShootMessage);
	}
	
	public static void sendToServer_playerAction_tube() {
		MessageTubeShoot tubeShootMessage = new MessageTubeShoot(MathHelper.floor_double(GunCus.accuracy));
		PacketHandler.simpleNetworkManager.sendToServer(tubeShootMessage);
	}
	
	public static void sendToServer_playerAction_knife() {
		MessageKnife knifeMessage = new MessageKnife();
		PacketHandler.simpleNetworkManager.sendToServer(knifeMessage);
		bytes.writeInt(13);
		bytes.writeInt(0);
	}
}
