/*    */ package assets.guncus;
/*    */ 
/*    */ import assets.guncusexplosives.GunCusExplosivesRPG;
/*    */ import com.google.common.io.ByteArrayDataOutput;
/*    */ import com.google.common.io.ByteStreams;
/*    */ import cpw.mods.fml.common.network.IPacketHandler;
/*    */ import cpw.mods.fml.common.network.PacketDispatcher;
/*    */ import cpw.mods.fml.common.network.Player;
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.DataInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.PrintStream;
/*    */ import java.util.HashMap;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import java.util.Random;
/*    */ import net.minecraft.client.Minecraft;
/*    */ import net.minecraft.client.audio.SoundManager;
/*    */ import net.minecraft.entity.Entity;
/*    */ import net.minecraft.entity.EntityLiving;
/*    */ import net.minecraft.entity.player.EntityPlayer;
/*    */ import net.minecraft.entity.player.EntityPlayerMP;
/*    */ import net.minecraft.entity.player.InventoryPlayer;
/*    */ import net.minecraft.entity.player.PlayerCapabilities;
/*    */ import net.minecraft.item.Item;
/*    */ import net.minecraft.item.ItemStack;
/*    */ import net.minecraft.network.INetworkManager;
/*    */ import net.minecraft.network.NetServerHandler;
/*    */ import net.minecraft.network.packet.Packet250CustomPayload;
/*    */ import net.minecraft.server.MinecraftServer;
/*    */ import net.minecraft.server.management.ServerConfigurationManager;
/*    */ import net.minecraft.util.DamageSource;
/*    */ import net.minecraft.util.MathHelper;
/*    */ import net.minecraft.world.World;
/*    */ 
/*    */ public class GunCusPacketHandler
/*    */   implements IPacketHandler
/*    */ {
/*    */   public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
/*    */   {
/*    */     try
/*    */     {
/* 37 */       DataInputStream data = new DataInputStream(new ByteArrayInputStream(packet.data));
/* 38 */       EntityPlayer entityPlayer = (EntityPlayer)player;
/* 39 */       World world = entityPlayer.worldObj;
/*    */ 
/* 41 */       int packetType = data.readInt();
/* 42 */       int acc = data.readInt();
/*    */ 
/* 44 */       if (packetType == 1)
/*    */       {
/* 47 */         if ((entityPlayer != null) && (entityPlayer.inventory.getCurrentItem() != null) && ((entityPlayer.inventory.getCurrentItem().getItem() instanceof GunCusItemGun)))
/*    */         {
/* 49 */           GunCusItemGun gun = (GunCusItemGun)entityPlayer.inventory.getCurrentItem().getItem();
/* 50 */           int metadata = entityPlayer.inventory.getCurrentItem().getItemDamage();
/* 51 */           ItemStack mag = null;
/*    */ 
/* 53 */           if (gun.magId != -1)
/*    */           {
/* 55 */             for (int v1 = 0; v1 < entityPlayer.inventory.getSizeInventory(); v1++)
/*    */             {
/* 57 */               if ((entityPlayer.inventory.getStackInSlot(v1) != null) && (entityPlayer.inventory.getStackInSlot(v1).getItem().itemID == gun.magId) && (entityPlayer.inventory.getStackInSlot(v1).isItemDamaged()) && (entityPlayer.inventory.getStackInSlot(v1).getItemDamage() < entityPlayer.inventory.getStackInSlot(v1).getMaxDamage()))
/*    */               {
/* 59 */                 mag = entityPlayer.inventory.getStackInSlot(v1);
/* 60 */                 break;
/*    */               }
/*    */             }
/*    */ 
/* 64 */             if (mag == null)
/*    */             {
/* 66 */               for (int v1 = 0; v1 < entityPlayer.inventory.getSizeInventory(); v1++)
/*    */               {
/* 68 */                 if ((entityPlayer.inventory.getStackInSlot(v1) != null) && (entityPlayer.inventory.getStackInSlot(v1).getItem().itemID == gun.magId) && (!entityPlayer.inventory.getStackInSlot(v1).isItemDamaged()))
/*    */                 {
/* 70 */                   mag = entityPlayer.inventory.getStackInSlot(v1);
/* 71 */                   break;
/*    */                 }
/*    */               }
/*    */             }
/*    */           }
/*    */ 
/* 77 */           int var1 = -1;
/*    */ 
/* 79 */           if (gun.magId == -1)
/*    */           {
/* 81 */             var1 = data.readInt();
/*    */           }
/*    */ 
/* 84 */           if (((mag != null) && (gun.magId != -1)) || ((gun.magId == -1) && (entityPlayer.inventory.hasItem(((GunCusItemBullet)((List)GunCusItemBullet.bulletsList.get(gun.pack)).get(var1)).itemID))) || (entityPlayer.capabilities.isCreativeMode))
/*    */           {
/* 86 */             if ((!entityPlayer.capabilities.isCreativeMode) && (gun.magId != -1) && (mag != null))
/*    */             {
/* 88 */               mag.damageItem(1, entityPlayer);
/*    */             }
/* 90 */             else if ((!entityPlayer.capabilities.isCreativeMode) && (gun.magId == -1))
/*    */             {
/* 92 */               entityPlayer.inventory.consumeInventoryItem(((GunCusItemBullet)((List)GunCusItemBullet.bulletsList.get(gun.pack)).get(var1)).itemID);
/*    */             }
/*    */ 
/* 95 */             if (gun.hasSilencer(metadata))
/*    */             {
/* 97 */               world.playSoundAtEntity(entityPlayer, gun.soundS, 1.0F, 1.0F / (world.rand.nextFloat() * 0.4F + 0.8F));
/*    */             }
/*    */             else
/*    */             {
/* 101 */               world.playSoundAtEntity(entityPlayer, gun.soundN, 5.0F * (float)gun.soundModify, 1.0F / (world.rand.nextFloat() * 0.4F + 0.8F));
/*    */             }
/*    */             GunCusItemBullet bullet2;
/* 106 */             if (gun.magId != -1)
/*    */             {
/* 108 */               GunCusItemMag mag2 = (GunCusItemMag)Item.itemsList[gun.magId];
/* 109 */               bullet2 = (GunCusItemBullet)((List)GunCusItemBullet.bulletsList.get(gun.pack)).get(mag2.bulletType);
/*    */             }
/*    */             else
/*    */             {
/* 113 */               bullet2 = (GunCusItemBullet)((List)GunCusItemBullet.bulletsList.get(gun.pack)).get(var1);
/*    */             }
/*    */ 
/* 116 */             if (acc > bullet2.spray)
/*    */             {
/* 118 */               acc = bullet2.spray;
/*    */             }
/*    */ 
/* 121 */             float damage = gun.damage * bullet2.damage;
/*    */ 
/* 123 */             if (gun.hasHeavyBarrel(metadata))
/*    */             {
/* 125 */               damage += 2.0F;
/*    */             }
/* 127 */             if (gun.hasStrongSpiralSpring(metadata))
/*    */             {
/* 129 */               damage += 1.0F;
/*    */             }
/*    */ 
/* 132 */             for (int v1 = 0; v1 < bullet2.split; v1++)
/*    */             {
/* 134 */               GunCusEntityBullet bullet = new GunCusEntityBullet(world, entityPlayer, 10.0F, damage, acc).setLowerGravity(gun.hasPolygonalBarrel(metadata)).setGravityMod(bullet2.gravity).setEffects(bullet2.effects, bullet2.effectModifiers);
/* 135 */               world.spawnEntityInWorld(bullet);
/*    */             }
/*    */ 
/* 138 */             if ((!entityPlayer.capabilities.isCreativeMode) && (mag != null) && (mag.getItemDamage() >= mag.getMaxDamage()))
/*    */             {
/* 140 */               ByteArrayDataOutput bytes = ByteStreams.newDataOutput();
/* 141 */               bytes.writeInt(2);
/* 142 */               bytes.writeInt(0);
/* 143 */               PacketDispatcher.sendPacketToPlayer(new Packet250CustomPayload("guncus", bytes.toByteArray()), (Player)entityPlayer);
/*    */             }
/*    */           }
/*    */         }
/*    */       }
/* 148 */       else if (packetType == 2)
/*    */       {
/* 150 */         GunCus.shootTime += 90;
/* 151 */         Minecraft.getMinecraft().sndManager.playSoundFX("guncus:reload", 1.0F, 1.0F);
/*    */       }
/* 153 */       else if (packetType == 3)
/*    */       {
/* 155 */         GunCusContainerGun container = (GunCusContainerGun)entityPlayer.openContainer;
/*    */ 
/* 157 */         if (acc == 0)
/*    */         {
/* 159 */           container.split();
/*    */         }
/* 161 */         else if (acc == 1)
/*    */         {
/* 163 */           container.build();
/*    */         }
/*    */       }
/* 166 */       else if (packetType == 4)
/*    */       {
/* 168 */         GunCusContainerAmmo container = (GunCusContainerAmmo)entityPlayer.openContainer;
/*    */ 
/* 170 */         if (acc == 0)
/*    */         {
/* 172 */           container.fill();
/*    */         }
/* 174 */         else if (acc == 1)
/*    */         {
/* 176 */           container.empty();
/*    */         }
/*    */       }
/* 179 */       else if (packetType == 5)
/*    */       {
/* 181 */         GunCusContainerAmmoMan container = (GunCusContainerAmmoMan)entityPlayer.openContainer;
/*    */ 
/* 183 */         if (acc == 0)
/*    */         {
/* 185 */           container.fill();
/*    */         }
/* 187 */         else if (acc == 1)
/*    */         {
/* 189 */           container.empty();
/*    */         }
/*    */       }
/* 192 */       else if (packetType == 6)
/*    */       {
/* 194 */         GunCusContainerMag container = (GunCusContainerMag)entityPlayer.openContainer;
/*    */ 
/* 196 */         if (acc == 0)
/*    */         {
/* 198 */           container.create();
/*    */         }
/* 200 */         else if (acc == 1)
/*    */         {
/* 202 */           entityPlayer.addChatMessage(container.info()[0]);
/* 203 */           if (container.info()[1] != null)
/*    */           {
/* 205 */             entityPlayer.addChatMessage(container.info()[1]);
/*    */           }
/*    */         }
/*    */       }
/* 209 */       else if (packetType == 7)
/*    */       {
/* 211 */         GunCusContainerBullet container = (GunCusContainerBullet)entityPlayer.openContainer;
/*    */ 
/* 213 */         if (acc == 0)
/*    */         {
/* 215 */           container.create();
/*    */         }
/* 217 */         else if (acc == 1)
/*    */         {
/* 219 */           entityPlayer.addChatMessage(container.info()[0]);
/* 220 */           if (container.info()[1] != null)
/*    */           {
/* 222 */             entityPlayer.addChatMessage(container.info()[1]);
/*    */           }
/*    */         }
/*    */       }
/* 226 */       else if (packetType == 8)
/*    */       {
/* 229 */         if ((entityPlayer != null) && (entityPlayer.inventory.getCurrentItem() != null))
/*    */         {
/* 231 */           if ((entityPlayer.inventory.getCurrentItem().getItem() instanceof GunCusItemGun))
/*    */           {
/* 233 */             GunCusItemGun gun = (GunCusItemGun)entityPlayer.inventory.getCurrentItem().getItem();
/* 234 */             int metadata = entityPlayer.inventory.getCurrentItem().getItemDamage();
/*    */ 
/* 236 */             if ((gun.hasM320(metadata)) && (((entityPlayer.inventory.hasItem(GunCus.ammoM320.itemID)) && (entityPlayer.inventory.consumeInventoryItem(GunCus.ammoM320.itemID))) || (entityPlayer.capabilities.isCreativeMode)))
/*    */             {
/* 238 */               GunCusEntityAT rocket = new GunCusEntityAT(world, entityPlayer, acc, 2);
/* 239 */               world.playSoundAtEntity(entityPlayer, "random.explode", 4.0F, 1.0F);
/* 240 */               world.spawnEntityInWorld(rocket);
/*    */             }
/*    */           }
/* 243 */           else if ((entityPlayer.inventory.getCurrentItem().getItem().itemID == GunCus.attachment.itemID) && (entityPlayer.inventory.getCurrentItem().getItemDamage() == 3))
/*    */           {
/* 245 */             if ((entityPlayer.capabilities.isCreativeMode) || ((entityPlayer.inventory.hasItem(GunCus.ammoM320.itemID)) && (entityPlayer.inventory.consumeInventoryItem(GunCus.ammoM320.itemID))))
/*    */             {
/* 247 */               GunCusEntityAT rocket = new GunCusEntityAT(world, entityPlayer, acc, 2);
/* 248 */               world.playSoundAtEntity(entityPlayer, "random.explode", 4.0F, 1.0F);
/* 249 */               world.spawnEntityInWorld(rocket);
/*    */             }
/*    */           }
/* 252 */           else if ((entityPlayer.inventory.getCurrentItem().getItem() instanceof GunCusExplosivesRPG))
/*    */           {
/* 254 */             GunCusExplosivesRPG rpg = (GunCusExplosivesRPG)entityPlayer.inventory.getCurrentItem().getItem();
/* 255 */             if ((entityPlayer.capabilities.isCreativeMode) || ((entityPlayer.inventory.hasItem(rpg.ammo)) && (entityPlayer.inventory.consumeInventoryItem(rpg.ammo))))
/*    */             {
/* 257 */               GunCusEntityAT rocket = new GunCusEntityAT(world, entityPlayer, acc, 1);
/* 258 */               world.playSoundAtEntity(entityPlayer, "random.explode", 4.0F, 1.0F);
/* 259 */               world.spawnEntityInWorld(rocket);
/*    */             }
/*    */           }
/*    */         }
/*    */       }
/* 264 */       else if (packetType == 9)
/*    */       {
/* 266 */         GunCusContainerWeapon container = (GunCusContainerWeapon)entityPlayer.openContainer;
/*    */ 
/* 268 */         if (acc == 1)
/*    */         {
/* 270 */           int actual = data.readInt();
/* 271 */           actual++;
/* 272 */           if (actual >= GunCusItemGun.gunList.size())
/*    */           {
/* 274 */             actual = 0;
/*    */           }
/*    */ 
/* 277 */           if (GunCusItemGun.gunList.size() > actual)
/*    */           {
/* 279 */             container.actual = actual;
/* 280 */             container.actualItemID = ((GunCusItemGun)GunCusItemGun.gunList.get(actual)).itemID;
/*    */           }
/*    */ 
/* 283 */           ByteArrayDataOutput bytes = ByteStreams.newDataOutput();
/* 284 */           bytes.writeInt(15);
/* 285 */           bytes.writeInt(0);
/* 286 */           bytes.writeInt(actual);
/* 287 */           bytes.writeInt(((GunCusItemGun)GunCusItemGun.gunList.get(actual)).itemID);
/* 288 */           PacketDispatcher.sendPacketToPlayer(new Packet250CustomPayload("guncus", bytes.toByteArray()), (Player)entityPlayer);
/*    */         }
/* 290 */         else if (acc == 0)
/*    */         {
/* 292 */           entityPlayer.addChatMessage(container.info()[0]);
/* 293 */           if (container.info()[1] != null)
/*    */           {
/* 295 */             entityPlayer.addChatMessage(container.info()[1]);
/*    */           }
/*    */         }
/* 298 */         else if (acc == 2)
/*    */         {
/* 300 */           container.create();
/*    */         }
/*    */       }
/* 303 */       else if (packetType == 10)
/*    */       {
/* 305 */         GunCus.hitmarker = 5;
/* 306 */         Minecraft.getMinecraft().sndManager.playSoundFX("guncus:inground", 1.0F, 1.0F);
/*    */       }
/* 308 */       else if (packetType == 11)
/*    */       {
/* 310 */         float str = acc / 100.0F;
/* 311 */         int x = data.readInt();
/* 312 */         int y = data.readInt();
/* 313 */         int z = data.readInt();
/*    */ 
/* 315 */         world.createExplosion(entityPlayer, x, y, z, str, true);
/*    */       }
/* 317 */       else if (packetType == 12)
/*    */       {
/* 319 */         int x = data.readInt();
/* 320 */         int y = data.readInt();
/* 321 */         int z = data.readInt();
/*    */ 
/* 323 */         world.destroyBlock(x, y, z, true);
/*    */       }
/* 325 */       else if (packetType == 13)
/*    */       {
/* 327 */         world.playSoundAtEntity(entityPlayer, "guncus:knife", 1.0F, 1.0F);
/* 328 */         EntityLiving target = null;
/* 329 */         double acD = 2.0D;
/*    */ 
/* 331 */         double x = entityPlayer.posX;
/* 332 */         double y = entityPlayer.posY;
/* 333 */         double z = entityPlayer.posZ;
/*    */ 
/* 335 */         for (Iterator i$ = world.loadedEntityList.iterator(); i$.hasNext(); ) { Object entity = i$.next();
/*    */ 
/* 337 */           if ((entity instanceof EntityLiving))
/*    */           {
/* 339 */             double x2 = ((EntityLiving)entity).posX;
/* 340 */             double y2 = ((EntityLiving)entity).posY;
/* 341 */             double z2 = ((EntityLiving)entity).posZ;
/*    */ 
/* 343 */             double dx = x2 - x;
/* 344 */             double dy = y2 - y;
/* 345 */             double dz = z2 - z;
/*    */ 
/* 347 */             double d = MathHelper.sqrt_double(dx * dx + dy * dy + dz * dz);
/*    */ 
/* 349 */             if ((d <= 2.0001D) && (d < acD) && ((!(entity instanceof EntityPlayer)) || ((EntityPlayer)entity != entityPlayer)))
/*    */             {
/* 351 */               target = (EntityLiving)entity;
/* 352 */               acD = d;
/*    */             }
/*    */           }
/*    */         }
/*    */ 
/* 357 */         if ((target != null) && (acD <= 2.0001D))
/*    */         {
/* 359 */           target.attackEntityFrom(DamageSource.causePlayerDamage(entityPlayer), 20.0F);
/*    */         }
/*    */       }
/* 362 */       else if (packetType == 14)
/*    */       {
/* 364 */         int packetType2 = data.readShort();
/*    */ 
/* 372 */         int var1 = data.readShort();
/* 373 */         int var2 = data.readShort();
/* 374 */         int var3 = data.readShort();
/* 375 */         int var4 = data.readShort();
/* 376 */         int var5 = data.readShort();
/* 377 */         int var6 = data.readShort();
/*    */ 
/* 379 */         if (packetType2 == 1)
/*    */         {
/* 381 */           int id = var1;
/* 382 */           int shootType = var2;
/* 383 */           int delay = var3;
/* 384 */           int magId = var4;
/* 385 */           int bullets = var5;
/* 386 */           int recModify = var6;
/*    */ 
/* 395 */           int shootType2 = GunCus.instance.gunShoots[id];
/* 396 */           int delay2 = GunCus.instance.gunDelays[id];
/* 397 */           int magId2 = GunCus.instance.gunMags[id];
/* 398 */           int bullets2 = GunCus.instance.gunBullets[id];
/* 399 */           int recModify2 = GunCus.instance.gunRecoils[id];
/*    */ 
/* 401 */           if ((delay != delay2) || (shootType != shootType2) || (magId != magId2) || (bullets != bullets2) || (recModify != recModify2))
/*    */           {
/* 403 */             ByteArrayDataOutput bytes = ByteStreams.newDataOutput();
/* 404 */             bytes.writeShort(14);
/* 405 */             bytes.writeShort(0);
/* 406 */             bytes.writeShort(2);
/* 407 */             bytes.writeShort(0);
/* 408 */             bytes.writeShort(0);
/* 409 */             bytes.writeShort(0);
/* 410 */             bytes.writeShort(0);
/* 411 */             bytes.writeShort(0);
/* 412 */             bytes.writeShort(0);
/* 413 */             PacketDispatcher.sendPacketToServer(new Packet250CustomPayload("guncus2", bytes.toByteArray()));
/*    */           }
/*    */         }
/* 416 */         if (packetType2 == 2)
/*    */         {
/* 418 */           EntityPlayerMP entityPlayerMp = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(entityPlayer.username);
/* 419 */           String s = "You were using modified weapons!";
/* 420 */           boolean flag = false;
/*    */ 
/* 422 */           if (entityPlayerMp != null)
/*    */           {
/* 424 */             entityPlayerMp.playerNetServerHandler.kickPlayerFromServer(s);
/* 425 */             System.out.println("[GunCus] Kicked player " + entityPlayerMp.username + " because he was using modified weapons!");
/*    */           }
/*    */         }
/*    */       }
/* 429 */       else if (packetType == 15)
/*    */       {
/* 431 */         GunCus.actual = data.readInt();
/* 432 */         GunCus.actualItemID = data.readInt();
/*    */       }
/*    */     }
/*    */     catch (IOException exception)
/*    */     {
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Nate\Desktop\Mod\GunCusClass.zip
 * Qualified Name:     assets.guncus.GunCusPacketHandler
 * JD-Core Version:    0.6.2
 */