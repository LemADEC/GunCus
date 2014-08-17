/*     */ package assets.guncus;
/*     */ 
/*     */ import com.google.common.io.ByteArrayDataOutput;
/*     */ import com.google.common.io.ByteStreams;
/*     */ import cpw.mods.fml.client.FMLClientHandler;
/*     */ import cpw.mods.fml.common.FMLCommonHandler;
/*     */ import cpw.mods.fml.common.network.PacketDispatcher;
/*     */ import cpw.mods.fml.common.registry.LanguageRegistry;
/*     */ import cpw.mods.fml.relauncher.Side;
/*     */ import cpw.mods.fml.relauncher.SideOnly;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Random;
/*     */ import net.minecraft.client.Minecraft;
/*     */ import net.minecraft.client.audio.SoundManager;
/*     */ import net.minecraft.client.renderer.texture.IconRegister;
/*     */ import net.minecraft.creativetab.CreativeTabs;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.entity.player.InventoryPlayer;
/*     */ import net.minecraft.entity.player.PlayerCapabilities;
/*     */ import net.minecraft.item.Item;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.network.packet.Packet250CustomPayload;
/*     */ import net.minecraft.util.Icon;
/*     */ import net.minecraft.util.MathHelper;
/*     */ import net.minecraft.world.World;
/*     */ import net.minecraftforge.client.MinecraftForgeClient;
/*     */ import org.lwjgl.input.Keyboard;
/*     */ import org.lwjgl.input.Mouse;
/*     */ 
/*     */ public class GunCusItemGun extends Item
/*     */ {
/*  40 */   public int delay = 3;
/*     */ 
/*  43 */   public int shootType = 2;
/*     */ 
/*  45 */   protected int actualType = 2;
/*  46 */   protected int burstCounter = 0;
/*  47 */   protected int reloadBurst = 0;
/*  48 */   protected boolean shot = false;
/*     */   public String iconName;
/*     */   public String name;
/*     */   protected Icon icon;
/*     */   protected Icon[] iconAttach;
/*     */   protected Icon[] iconBar;
/*     */   protected Icon iconScp;
/*  55 */   public int magId = 0;
/*     */   public int ingotsMag;
/*     */   public int ingots;
/*     */   public int field_77767_aC;
/*     */   public String sightTexture;
/*     */   protected double recModify;
/*     */   public double soundModify;
/*     */   public boolean isOfficial;
/*  63 */   public boolean usingDefault = false;
/*  64 */   public float zoom = 1.0F;
/*     */   public int[] bullets;
/*     */   public int actualBullet;
/*     */   public int[] attach;
/*     */   public int[] barrel;
/*     */   public int[] scopes;
/*     */   public int factor;
/*     */   public int subs;
/*  76 */   public boolean tubing = false;
/*     */   public String pack;
/*  79 */   public static List<GunCusItemGun> gunList = new ArrayList();
/*     */   public String soundN;
/*     */   public String soundS;
/*     */   public int damage;
/*     */ 
/*     */   public GunCusItemGun(int id, int damage, int shootType, int delay, String name, String iconName, int magSize, int magId, int bulletType, int ingotsMag, int ingots, int redstone, String pack, boolean isOfficial, int[] attach, int[] barrel, int[] scopes, boolean noMag, int[] bullets)
/*     */   {
/*  88 */     super(id);
/*  89 */     if (FMLCommonHandler.instance().getEffectiveSide().isClient()) MinecraftForgeClient.registerItemRenderer(this.itemID, new GunCusInvRenderer());
/*  90 */     this.isOfficial = isOfficial;
/*  91 */     this.damage = damage;
/*  92 */     setHasSubtypes(true);
/*  93 */     this.maxStackSize = 1;
/*  94 */     setFull3D();
/*  95 */     this.shootType = shootType;
/*  96 */     this.actualType = shootType;
/*  97 */     this.delay = delay;
/*  98 */     setMaxDamage(0);
/*  99 */     this.name = name;
/* 100 */     this.iconName = iconName;
/* 101 */     this.ingots = ingots;
/* 102 */     this.field_77767_aC = redstone;
/* 103 */     this.pack = pack;
/* 104 */     this.recModify = 1.0D;
/* 105 */     this.soundModify = 1.0D;
/*     */ 
/* 107 */     this.attach = attach;
/* 108 */     this.barrel = barrel;
/* 109 */     this.scopes = scopes;
/*     */ 
/* 111 */     this.factor = ((this.attach.length + 1) * (this.scopes.length + 1));
/*     */ 
/* 113 */     if (canHaveStraightPullBolt())
/*     */     {
/* 115 */       this.soundN = "guncus:shoot_sniper";
/* 116 */       this.shootType = 0;
/* 117 */       this.actualType = 0;
/*     */     }
/*     */     else
/*     */     {
/* 121 */       this.soundN = "guncus:shoot_normal";
/*     */     }
/* 123 */     this.soundS = "guncus:shoot_silenced";
/*     */ 
/* 125 */     GunCusCreativeTab tab = new GunCusCreativeTab(name, this.itemID);
/* 126 */     setCreativeTab(tab);
/*     */ 
/* 128 */     this.actualBullet = 0;
/*     */ 
/* 130 */     if (noMag)
/*     */     {
/* 132 */       this.bullets = bullets;
/* 133 */       this.magId = -1;
/*     */     }
/*     */     else
/*     */     {
/* 137 */       Item mag = new GunCusItemMag(magId, name, getName(0), magSize, iconName, bulletType, pack);
/* 138 */       this.magId = mag.itemID;
/* 139 */       this.ingotsMag = ingotsMag;
/* 140 */       mag.setCreativeTab(tab);
/*     */     }
/*     */ 
/* 143 */     gunList.add(this);
/*     */ 
/* 145 */     int i = this.scopes.length + 1;
/*     */ 
/* 147 */     if (this.barrel.length > 0)
/*     */     {
/* 149 */       i *= (this.barrel.length + 1);
/*     */     }
/*     */ 
/* 152 */     if (this.attach.length > 0)
/*     */     {
/* 154 */       i *= (this.attach.length + 1);
/*     */     }
/*     */ 
/* 157 */     this.subs = i;
/*     */ 
/* 159 */     for (int v1 = 0; v1 < this.subs; v1++)
/*     */     {
/* 161 */       LanguageRegistry.addName(new ItemStack(this, 1, v1), this.name);
/*     */     }
/*     */   }
/*     */ 
/*     */   public GunCusItemGun setZoom(float zoom)
/*     */   {
/* 167 */     this.zoom = zoom;
/* 168 */     if (this.zoom < 1.0F)
/*     */     {
/* 170 */       this.zoom = 1.0F;
/*     */     }
/* 172 */     return this;
/*     */   }
/*     */ 
/*     */   public GunCusItemGun defaultTexture(boolean flag)
/*     */   {
/* 177 */     this.usingDefault = flag;
/* 178 */     return this;
/*     */   }
/*     */ 
/*     */   public GunCusItemGun setRecoilModifier(double d)
/*     */   {
/* 183 */     this.recModify = d;
/* 184 */     return this;
/*     */   }
/*     */ 
/*     */   public GunCusItemGun setSoundModifier(double d)
/*     */   {
/* 189 */     this.soundModify = d;
/* 190 */     return this;
/*     */   }
/*     */ 
/*     */   public GunCusItemGun setNormalSound(String sound)
/*     */   {
/* 195 */     this.soundN = sound;
/* 196 */     return this;
/*     */   }
/*     */ 
/*     */   public GunCusItemGun setSLNSound(String sound)
/*     */   {
/* 201 */     this.soundS = sound;
/* 202 */     return this;
/*     */   }
/*     */ 
/*     */   public void onUpdate(ItemStack itemStack, World world, Entity entity, int par1, boolean flag)
/*     */   {
/* 208 */     if (FMLCommonHandler.instance().getEffectiveSide().isClient())
/*     */     {
/* 210 */       doUpdate(itemStack, world, entity, par1, flag);
/*     */     }
/*     */   }
/*     */ 
/*     */   @SideOnly(Side.CLIENT)
/*     */   public void doUpdate(ItemStack itemStack, World world, Entity entity, int par1, boolean flag)
/*     */   {
/* 217 */     Minecraft client = FMLClientHandler.instance().getClient();
/* 218 */     EntityPlayer entityPlayer = client.thePlayer;
/* 219 */     if ((entityPlayer != null) && (entityPlayer.inventory.getCurrentItem() != null) && (entityPlayer.inventory.getCurrentItem().getItem() == this))
/*     */     {
/* 221 */       ItemStack mag = null;
/*     */ 
/* 223 */       if (this.magId != -1)
/*     */       {
/* 225 */         for (int v1 = 0; v1 < entityPlayer.inventory.getSizeInventory(); v1++)
/*     */         {
/* 227 */           if ((entityPlayer.inventory.getStackInSlot(v1) != null) && (entityPlayer.inventory.getStackInSlot(v1).getItem().itemID == this.magId) && (entityPlayer.inventory.getStackInSlot(v1).isItemDamaged()) && (entityPlayer.inventory.getStackInSlot(v1).getItemDamage() < entityPlayer.inventory.getStackInSlot(v1).getMaxDamage()))
/*     */           {
/* 229 */             mag = entityPlayer.inventory.getStackInSlot(v1);
/* 230 */             break;
/*     */           }
/*     */         }
/*     */ 
/* 234 */         if (mag == null)
/*     */         {
/* 236 */           for (int v1 = 0; v1 < entityPlayer.inventory.getSizeInventory(); v1++)
/*     */           {
/* 238 */             if ((entityPlayer.inventory.getStackInSlot(v1) != null) && (entityPlayer.inventory.getStackInSlot(v1).getItem().itemID == this.magId) && (!entityPlayer.inventory.getStackInSlot(v1).isItemDamaged()))
/*     */             {
/* 240 */               mag = entityPlayer.inventory.getStackInSlot(v1);
/* 241 */               break;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 247 */       if ((GunCus.shootTime <= 0) && (Mouse.isButtonDown(0)) && ((client.currentScreen == null) || (Mouse.isButtonDown(1))) && ((entityPlayer.inventory.hasItem(GunCus.ammoM320.itemID)) || (entityPlayer.capabilities.isCreativeMode)) && (this.tubing))
/*     */       {
/* 249 */         GunCus.shootTime += 95;
/* 250 */         tube(entityPlayer);
/* 251 */         recoilTube(entityPlayer);
/* 252 */         Minecraft.getMinecraft().sndManager.playSoundFX("guncus:reload_tube", 1.0F, 1.0F);
/*     */       }
/* 254 */       if ((GunCus.shootTime <= 0) && (Mouse.isButtonDown(0)) && (!this.shot) && ((client.currentScreen == null) || (Mouse.isButtonDown(1))) && ((mag != null) || (entityPlayer.capabilities.isCreativeMode) || ((this.bullets != null) && (entityPlayer.inventory.hasItem(((GunCusItemBullet)((List)GunCusItemBullet.bulletsList.get(this.pack)).get(this.bullets[this.actualBullet])).itemID)))) && (!this.tubing))
/*     */       {
/* 256 */         GunCus.shootTime += this.delay;
/* 257 */         this.reloadBurst = 0;
/*     */ 
/* 259 */         if (this.actualType == 0)
/*     */         {
/* 261 */           this.shot = true;
/*     */         }
/* 263 */         else if (this.actualType == 1)
/*     */         {
/* 265 */           if (this.burstCounter < 2)
/*     */           {
/* 267 */             this.burstCounter += 1;
/*     */           }
/*     */           else
/*     */           {
/* 271 */             this.burstCounter = 0;
/* 272 */             this.shot = true;
/*     */           }
/*     */         }
/*     */ 
/* 276 */         shoot(entityPlayer);
/*     */         GunCusItemBullet bullet2;
/* 279 */         if (this.magId != -1)
/*     */         {
/* 281 */           GunCusItemMag mag2 = (GunCusItemMag)Item.itemsList[this.magId];
/* 282 */           bullet2 = (GunCusItemBullet)((List)GunCusItemBullet.bulletsList.get(this.pack)).get(mag2.bulletType);
/*     */         }
/*     */         else
/*     */         {
/* 286 */           bullet2 = (GunCusItemBullet)((List)GunCusItemBullet.bulletsList.get(this.pack)).get(this.bullets[this.actualBullet]);
/*     */         }
/*     */ 
/* 289 */         float damage = this.damage * bullet2.damage;
/*     */ 
/* 291 */         double newAcc = damage;
/* 292 */         if (Mouse.isButtonDown(1))
/*     */         {
/* 294 */           newAcc *= 0.9D;
/*     */         }
/* 296 */         else if (hasLaserPointer(itemStack.getItemDamage()))
/*     */         {
/* 298 */           newAcc *= 0.9D;
/*     */         }
/*     */ 
/* 301 */         if (hasRifledBarrel(itemStack.getItemDamage()))
/*     */         {
/* 303 */           newAcc *= 0.8D;
/*     */         }
/*     */ 
/* 306 */         if ((hasBipod(itemStack.getItemDamage())) && (canUseBipod(entityPlayer)))
/*     */         {
/* 308 */           newAcc *= 0.65D;
/*     */         }
/*     */ 
/* 311 */         if (GunCus.accuracy > newAcc)
/*     */         {
/* 313 */           GunCus.accuracy -= newAcc;
/*     */         }
/*     */ 
/* 316 */         recoil(entityPlayer, itemStack.getItemDamage(), Mouse.isButtonDown(1), damage);
/*     */       }
/*     */ 
/* 319 */       if ((this.shot) && (!Mouse.isButtonDown(0)))
/*     */       {
/* 321 */         this.shot = false;
/*     */       }
/*     */ 
/* 324 */       if (!hasM320(itemStack.getItemDamage()))
/*     */       {
/* 326 */         this.tubing = false;
/*     */       }
/*     */ 
/* 329 */       if ((this.burstCounter > 0) && (!Mouse.isButtonDown(0)))
/*     */       {
/* 331 */         this.reloadBurst += 1;
/* 332 */         if (this.reloadBurst >= this.delay * 3)
/*     */         {
/* 334 */           this.burstCounter = 0;
/* 335 */           this.reloadBurst = 0;
/*     */         }
/*     */       }
/*     */ 
/* 339 */       if (((Keyboard.isKeyDown(29)) || (Keyboard.isKeyDown(157))) && (Keyboard.isKeyDown(47)) && (GunCus.switchTime <= 0) && (!canHaveStraightPullBolt()))
/*     */       {
/* 341 */         switch (this.shootType)
/*     */         {
/*     */         case 0:
/* 344 */           entityPlayer.addChatMessage("The Fire Mode Of This Gun Can Not Be Changed!");
/* 345 */           break;
/*     */         case 1:
/* 347 */           switch (this.actualType)
/*     */           {
/*     */           case 0:
/* 350 */             entityPlayer.addChatMessage("Switched To Burst Mode!");
/* 351 */             this.actualType = 1;
/* 352 */             break;
/*     */           case 1:
/* 354 */             entityPlayer.addChatMessage("Switched To Single Mode!");
/* 355 */             this.actualType = 0;
/*     */           }
/*     */ 
/* 358 */           break;
/*     */         case 2:
/* 360 */           switch (this.actualType)
/*     */           {
/*     */           case 0:
/* 363 */             entityPlayer.addChatMessage("Switched To Burst Mode!");
/* 364 */             this.actualType = 1;
/* 365 */             break;
/*     */           case 1:
/* 367 */             entityPlayer.addChatMessage("Switched To Auto Mode!");
/* 368 */             this.actualType = 2;
/* 369 */             break;
/*     */           case 2:
/* 371 */             entityPlayer.addChatMessage("Switched To Single Mode!");
/* 372 */             this.actualType = 0;
/*     */           }
/*     */ 
/*     */           break;
/*     */         }
/*     */ 
/* 378 */         GunCus.switchTime = 20;
/*     */       }
/* 380 */       else if (((Keyboard.isKeyDown(29)) || (Keyboard.isKeyDown(157))) && (Keyboard.isKeyDown(46)) && (GunCus.switchTime <= 0) && (hasM320(itemStack.getItemDamage())))
/*     */       {
/* 382 */         GunCus.switchTime = 20;
/* 383 */         if (this.tubing)
/*     */         {
/* 385 */           entityPlayer.addChatMessage("You are no longer using the M320!");
/* 386 */           this.tubing = false;
/*     */         }
/*     */         else
/*     */         {
/* 390 */           entityPlayer.addChatMessage("You are now using the M320!");
/* 391 */           this.tubing = true;
/*     */         }
/*     */       }
/* 394 */       else if (((Keyboard.isKeyDown(29)) || (Keyboard.isKeyDown(157))) && (Keyboard.isKeyDown(34)) && (GunCus.switchTime <= 0) && (this.bullets != null) && (this.bullets.length > 1))
/*     */       {
/* 396 */         GunCus.switchTime = 20;
/* 397 */         this.actualBullet += 1;
/* 398 */         if (this.actualBullet >= this.bullets.length)
/*     */         {
/* 400 */           this.actualBullet = 0;
/*     */         }
/* 402 */         entityPlayer.addChatMessage("You are now using \"" + ((GunCusItemBullet)((List)GunCusItemBullet.bulletsList.get(this.pack)).get(this.bullets[this.actualBullet])).name + "\" ammunition!");
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void shoot(EntityPlayer entityPlayer)
/*     */   {
/* 409 */     ByteArrayDataOutput bytes = ByteStreams.newDataOutput();
/* 410 */     bytes.writeInt(1);
/* 411 */     bytes.writeInt(MathHelper.floor_double(GunCus.accuracy));
/* 412 */     if (this.magId == -1)
/*     */     {
/* 414 */       bytes.writeInt(this.bullets[this.actualBullet]);
/*     */     }
/* 416 */     PacketDispatcher.sendPacketToServer(new Packet250CustomPayload("guncus", bytes.toByteArray()));
/*     */   }
/*     */ 
/*     */   private void tube(EntityPlayer entityPlayer)
/*     */   {
/* 421 */     ByteArrayDataOutput bytes = ByteStreams.newDataOutput();
/* 422 */     bytes.writeInt(8);
/* 423 */     bytes.writeInt(MathHelper.floor_double(GunCus.accuracy));
/* 424 */     PacketDispatcher.sendPacketToServer(new Packet250CustomPayload("guncus", bytes.toByteArray()));
/*     */   }
/*     */ 
/*     */   private void recoil(EntityPlayer entityPlayer, int metadata, boolean scoping, double damage)
/*     */   {
/* 429 */     float strength = (float)damage / 6.0F;
/*     */ 
/* 431 */     if ((hasBipod(metadata)) && (canUseBipod(entityPlayer)))
/*     */     {
/* 433 */       strength /= 3.0F;
/*     */     }
/* 435 */     else if (hasGrip(metadata))
/*     */     {
/* 437 */       strength /= 5.0F;
/* 438 */       strength *= 4.0F;
/*     */     }
/* 440 */     else if ((!hasImprovedGrip(metadata)) && (canHaveImprovedGrip()))
/*     */     {
/* 442 */       strength *= 3.0F;
/* 443 */       strength /= 2.0F;
/*     */     }
/*     */ 
/* 446 */     if (scoping)
/*     */     {
				// FIXME: decompiler failed badly here...
/*     */       // EntityPlayer tmp97_96 = entityPlayer; tmp97_96.rotationPitch = ((float)(tmp97_96.rotationPitch - this.recModify * tmp97_96));
/*     */       // EntityPlayer tmp116_115 = entityPlayer; tmp116_115.rotationYaw = ((float)(tmp116_115.rotationYaw - this.recModify * (Item.itemRand.nextBoolean() ? tmp97_96 / 2.0F : -tmp97_96 / 2.0F)));
				float tmpPitch = entityPlayer.rotationPitch;
				float tmpYaw = entityPlayer.rotationYaw;
/*     */       entityPlayer.rotationPitch = (float)(tmpPitch - this.recModify * tmpPitch);
/*     */       entityPlayer.rotationYaw = (float)(tmpYaw - this.recModify * (Item.itemRand.nextBoolean() ? tmpPitch / 2.0F : - tmpPitch / 2.0F));
/*     */     }
/*     */     else
/*     */     {
				// FIXME: decompiler failed badly here...
/*     */       // EntityPlayer tmp159_158 = entityPlayer; tmp159_158.rotationPitch = ((float)(tmp159_158.rotationPitch - this.recModify * tmp97_96));
/*     */       // EntityPlayer tmp178_177 = entityPlayer; tmp178_177.rotationYaw = ((float)(tmp178_177.rotationYaw - this.recModify * (Item.itemRand.nextBoolean() ? tmp97_96 / 2.0F : -tmp97_96 / 2.0F)));
				float tmpPitch = entityPlayer.rotationPitch;
				float tmpYaw = entityPlayer.rotationYaw;
/*     */       entityPlayer.rotationPitch = (float)(tmpPitch - this.recModify * tmpPitch);
/*     */       entityPlayer.rotationYaw = (float)(tmpYaw - this.recModify * (Item.itemRand.nextBoolean() ? tmpPitch / 2.0F : - tmpPitch / 2.0F));
/*     */     }
/*     */   }
/*     */ 
/*     */   private void recoilTube(EntityPlayer entityPlayer)
/*     */   {
/* 460 */     float strength = 1.5F;
/*     */ 
/* 462 */     entityPlayer.rotationPitch -= strength;
/* 463 */     entityPlayer.rotationYaw -= (Item.itemRand.nextBoolean() ? strength / 2.0F : -strength / 2.0F);
/*     */   }
/*     */ 
/*     */   public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
/*     */   {
/* 469 */     for (int j = 0; j < this.subs; j++)
/*     */     {
/* 471 */       ItemStack itemStack = new ItemStack(par1, 1, j);
/* 472 */       par3List.add(itemStack);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean barrelFree(int metadata)
/*     */   {
/* 478 */     for (int v1 = 0; v1 < GunCus.barrel.metadatas.length; v1++)
/*     */     {
/* 480 */       if (testForBarrelId(v1 + 1, metadata))
/*     */       {
/* 482 */         return false;
/*     */       }
/*     */     }
/*     */ 
/* 486 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean attatchmentFree(int metadata)
/*     */   {
/* 491 */     for (int v1 = 0; v1 < GunCus.attachment.metadatas.length; v1++)
/*     */     {
/* 493 */       if (testForAttachId(v1 + 1, metadata))
/*     */       {
/* 495 */         return false;
/*     */       }
/*     */     }
/*     */ 
/* 499 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean testIfCanHaveScope(int scope)
/*     */   {
/* 504 */     for (int v1 = 0; v1 < this.scopes.length; v1++)
/*     */     {
/* 506 */       if (this.scopes[v1] == scope)
/*     */       {
/* 508 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 512 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean testForBarrelId(int barrel, int metadata)
/*     */   {
/* 517 */     boolean flag = false;
/*     */ 
/* 519 */     for (int v1 = 0; v1 < this.barrel.length; v1++)
/*     */     {
/* 521 */       if ((this.barrel[v1] == barrel) && (metadata >= this.factor * (v1 + 1)) && (metadata < this.factor * (v1 + 2)))
/*     */       {
/* 523 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 527 */     return flag;
/*     */   }
/*     */ 
/*     */   public boolean hasSilencer(int metadata)
/*     */   {
/* 532 */     return testForBarrelId(1, metadata);
/*     */   }
/*     */ 
/*     */   public boolean hasHeavyBarrel(int metadata)
/*     */   {
/* 537 */     return testForBarrelId(2, metadata);
/*     */   }
/*     */ 
/*     */   public boolean hasRifledBarrel(int metadata)
/*     */   {
/* 542 */     return testForBarrelId(3, metadata);
/*     */   }
/*     */ 
/*     */   public boolean hasPolygonalBarrel(int metadata)
/*     */   {
/* 547 */     return testForBarrelId(4, metadata);
/*     */   }
/*     */ 
/*     */   public boolean testForAttachId(int attach, int metadata)
/*     */   {
/* 552 */     for (int v1 = 0; v1 < this.attach.length; v1++)
/*     */     {
/* 554 */       if (this.attach[v1] == attach)
/*     */       {
/* 556 */         for (int v2 = (this.scopes.length + 1) * (v1 + 1); v2 < (this.scopes.length + 1) * (v1 + 2); v2++)
/*     */         {
/* 558 */           for (int v3 = 0; v3 <= this.barrel.length; v3++)
/*     */           {
/* 560 */             if (metadata == v2 + v3 * this.factor)
/*     */             {
/* 562 */               return true;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 569 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean testIfCanHaveE(int attach)
/*     */   {
/* 574 */     for (int v1 = 0; v1 < this.attach.length; v1++)
/*     */     {
/* 576 */       if (this.attach[v1] == attach)
/*     */       {
/* 578 */         return true;
/*     */       }
/*     */     }
/* 581 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean hasStraightPullBolt(int metadata)
/*     */   {
/* 586 */     return testForAttachId(1, metadata);
/*     */   }
/*     */ 
/*     */   public boolean hasBipod(int metadata)
/*     */   {
/* 591 */     return testForAttachId(2, metadata);
/*     */   }
/*     */ 
/*     */   public boolean hasGrip(int metadata)
/*     */   {
/* 596 */     return testForAttachId(3, metadata);
/*     */   }
/*     */ 
/*     */   public boolean hasM320(int metadata)
/*     */   {
/* 601 */     return testForAttachId(4, metadata);
/*     */   }
/*     */ 
/*     */   public boolean hasStrongSpiralSpring(int metadata)
/*     */   {
/* 606 */     return testForAttachId(5, metadata);
/*     */   }
/*     */ 
/*     */   public boolean hasImprovedGrip(int metadata)
/*     */   {
/* 611 */     return testForAttachId(6, metadata);
/*     */   }
/*     */ 
/*     */   public boolean hasLaserPointer(int metadata)
/*     */   {
/* 616 */     return testForAttachId(7, metadata);
/*     */   }
/*     */ 
/*     */   public boolean canHaveStraightPullBolt()
/*     */   {
/* 621 */     return testIfCanHaveE(1);
/*     */   }
/*     */ 
/*     */   public boolean canHaveBipod()
/*     */   {
/* 626 */     return testIfCanHaveE(2);
/*     */   }
/*     */ 
/*     */   public boolean canHaveGrip()
/*     */   {
/* 631 */     return testIfCanHaveE(3);
/*     */   }
/*     */ 
/*     */   public boolean canHaveM320()
/*     */   {
/* 636 */     return testIfCanHaveE(4);
/*     */   }
/*     */ 
/*     */   public boolean canHaveStrongSpiralString()
/*     */   {
/* 641 */     return testIfCanHaveE(5);
/*     */   }
/*     */ 
/*     */   public boolean canHaveImprovedGrip()
/*     */   {
/* 646 */     return testIfCanHaveE(6);
/*     */   }
/*     */ 
/*     */   public boolean canHaveLaserPointer()
/*     */   {
/* 651 */     return testIfCanHaveE(7);
/*     */   }
/*     */ 
/*     */   public int barrelAsMetadataFactor(int barrel)
/*     */   {
/* 656 */     for (int v1 = 0; v1 < this.barrel.length; v1++)
/*     */     {
/* 658 */       if (this.barrel[v1] == barrel)
/*     */       {
/* 660 */         return v1 + 1;
/*     */       }
/*     */     }
/* 663 */     return 0;
/*     */   }
/*     */ 
/*     */   public int attachAsMetadataFactor(int attach)
/*     */   {
/* 668 */     for (int v1 = 0; v1 < this.attach.length; v1++)
/*     */     {
/* 670 */       if (this.attach[v1] == attach)
/*     */       {
/* 672 */         return v1 + 1;
/*     */       }
/*     */     }
/* 675 */     return 0;
/*     */   }
/*     */ 
/*     */   public int getZoom(int metadata)
/*     */   {
/* 680 */     int v1 = metadata;
/*     */ 
/* 682 */     while (v1 >= this.scopes.length + 1)
/*     */     {
/* 684 */       v1 -= this.scopes.length + 1;
/*     */     }
/* 686 */     if (v1 == 0)
/*     */     {
/* 688 */       return 0;
/*     */     }
/*     */ 
/* 692 */     return this.scopes[(v1 - 1)];
/*     */   }
/*     */ 
/*     */   public boolean canUseBipod(EntityPlayer entityPlayer)
/*     */   {
/* 699 */     if ((entityPlayer.isSneaking()) && (entityPlayer.motionX == 0.0D) && (entityPlayer.motionZ == 0.0D))
/*     */     {
/* 701 */       return true;
/*     */     }
/*     */ 
/* 705 */     return false;
/*     */   }
/*     */ 
/*     */   @SideOnly(Side.CLIENT)
/*     */   public void registerIcons(IconRegister par1IconRegister)
/*     */   {
/* 713 */     this.icon = par1IconRegister.registerIcon(this.iconName + "gun");
/*     */ 
/* 715 */     this.iconAttach = new Icon[this.attach.length];
/*     */ 
/* 717 */     for (int v1 = 0; v1 < this.attach.length; v1++)
/*     */     {
/* 719 */       this.iconAttach[v1] = par1IconRegister.registerIcon(this.iconName + getAttachIcon("a", this.attach[v1]));
/*     */     }
/*     */ 
/* 722 */     this.iconBar = new Icon[this.barrel.length];
/*     */ 
/* 724 */     for (int v1 = 0; v1 < this.barrel.length; v1++)
/*     */     {
/* 726 */       this.iconBar[v1] = par1IconRegister.registerIcon(this.iconName + getAttachIcon("b", this.barrel[v1]));
/*     */     }
/*     */ 
/* 729 */     if (this.scopes.length > 0)
/*     */     {
/* 731 */       this.iconScp = par1IconRegister.registerIcon(this.iconName + "scp");
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getAttachIcon(String type, int attach)
/*     */   {
/* 737 */     String string = "-";
/*     */ 
/* 739 */     if (type.toLowerCase().startsWith("a"))
/*     */     {
/* 741 */       switch (attach)
/*     */       {
/*     */       case 1:
/* 744 */         string = "spb";
/* 745 */         break;
/*     */       case 2:
/* 747 */         string = "bpd";
/* 748 */         break;
/*     */       case 3:
/* 750 */         string = "grp";
/* 751 */         break;
/*     */       case 4:
/* 753 */         string = "320";
/* 754 */         break;
/*     */       case 5:
/* 756 */         string = "sss";
/* 757 */         break;
/*     */       case 6:
/* 759 */         string = "img";
/* 760 */         break;
/*     */       case 7:
/* 762 */         string = "ptr";
/*     */       }
/*     */ 
/*     */     }
/* 766 */     else if (type.toLowerCase().startsWith("b"))
/*     */     {
/* 768 */       switch (attach)
/*     */       {
/*     */       case 1:
/* 771 */         string = "sln";
/* 772 */         break;
/*     */       case 2:
/* 774 */         string = "hbl";
/* 775 */         break;
/*     */       case 3:
/* 777 */         string = "rbl";
/* 778 */         break;
/*     */       case 4:
/* 780 */         string = "pbl";
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 785 */     return string;
/*     */   }
/*     */ 
/*     */   @SideOnly(Side.CLIENT)
/*     */   public Icon getIconFromDamage(int par1)
/*     */   {
/* 792 */     return this.icon;
/*     */   }
/*     */ 
/*     */   public String getName2(int metadata)
/*     */   {
/* 797 */     String front = "";
/* 798 */     String attatchment = "";
/* 799 */     String scope = "";
/*     */ 
/* 801 */     if (hasSilencer(metadata))
/*     */     {
/* 803 */       front = "-sln";
/*     */     }
/* 805 */     else if (hasHeavyBarrel(metadata))
/*     */     {
/* 807 */       front = "-hbl";
/*     */     }
/* 809 */     else if (hasRifledBarrel(metadata))
/*     */     {
/* 811 */       front = "-rbl";
/*     */     }
/* 813 */     else if (hasPolygonalBarrel(metadata))
/*     */     {
/* 815 */       front = "-pbl";
/*     */     }
/*     */ 
/* 818 */     if (hasStraightPullBolt(metadata))
/*     */     {
/* 820 */       attatchment = "-spb";
/*     */     }
/* 822 */     else if (hasBipod(metadata))
/*     */     {
/* 824 */       attatchment = "-bpd";
/*     */     }
/* 826 */     else if (hasGrip(metadata))
/*     */     {
/* 828 */       attatchment = "-grp";
/*     */     }
/* 830 */     else if (hasM320(metadata))
/*     */     {
/* 832 */       attatchment = "-320";
/*     */     }
/* 834 */     else if (hasStrongSpiralSpring(metadata))
/*     */     {
/* 836 */       attatchment = "-sss";
/*     */     }
/* 838 */     else if (hasImprovedGrip(metadata))
/*     */     {
/* 840 */       attatchment = "-img";
/*     */     }
/* 842 */     else if (hasLaserPointer(metadata))
/*     */     {
/* 844 */       attatchment = "-ptr";
/*     */     }
/*     */ 
/* 847 */     if (getZoom(metadata) > 0)
/*     */     {
/* 849 */       scope = "-scp";
/*     */     }
/*     */ 
/* 852 */     return "gun" + front + attatchment + scope;
/*     */   }
/*     */ 
/*     */   public String getName(int metadata)
/*     */   {
/* 857 */     return this.name + getName2(metadata).replace("gun", "");
/*     */   }
/*     */ 
/*     */   public String getUnlocalizedName(ItemStack par1ItemStack)
/*     */   {
/* 863 */     return getName(par1ItemStack.getItemDamage()).toLowerCase().replace(" ", "_");
/*     */   }
/*     */ 
/*     */   public float zoomToFloat(int scope)
/*     */   {
/* 868 */     float newZoom = 1.0F;
/* 869 */     if (scope > 0)
/*     */     {
/* 871 */       GunCusScope scope2 = (GunCusScope)GunCus.scope.metadatas[(scope - 1)];
/* 872 */       newZoom = scope2.zoom;
/*     */     }
/*     */ 
/* 875 */     return newZoom;
/*     */   }
/*     */ 
/*     */   public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par2List, boolean par4)
/*     */   {
/* 881 */     par2List.add(infoLine(this.pack));
/* 882 */     int metadata = par1ItemStack.getItemDamage();
/*     */ 
/* 884 */     if (metadata > 0)
/*     */     {
/* 886 */       par2List.add(infoLine(""));
/*     */ 
/* 888 */       String front = null;
/* 889 */       String attatchment = null;
/* 890 */       String scope = null;
/*     */ 
/* 892 */       for (int v1 = 1; v1 <= GunCus.barrel.metadatas.length; v1++)
/*     */       {
/* 894 */         if (testForBarrelId(v1, metadata))
/*     */         {
/* 896 */           front = GunCus.barrel.metadatas[(v1 - 1)].localized;
/*     */         }
/*     */       }
/*     */ 
/* 900 */       for (int v1 = 1; v1 <= GunCus.attachment.metadatas.length; v1++)
/*     */       {
/* 902 */         if (testForAttachId(v1, metadata))
/*     */         {
/* 904 */           attatchment = GunCus.attachment.metadatas[(v1 - 1)].localized;
/*     */         }
/*     */       }
/*     */ 
/* 908 */       if (getZoom(metadata) > 0)
/*     */       {
/* 910 */         scope = GunCus.scope.metadatas[(getZoom(metadata) - 1)].localized;
/*     */       }
/*     */ 
/* 913 */       if (front != null)
/*     */       {
/* 915 */         par2List.add(infoLine(front));
/*     */       }
/* 917 */       if (attatchment != null)
/*     */       {
/* 919 */         par2List.add(infoLine(attatchment));
/*     */       }
/* 921 */       if (scope != null)
/*     */       {
/* 923 */         par2List.add(infoLine(scope));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private String infoLine(String s)
/*     */   {
/* 930 */     return s;
/*     */   }
/*     */ }

/* Location:           C:\Users\Nate\Desktop\Mod\GunCusClass.zip
 * Qualified Name:     assets.guncus.GunCusItemGun
 * JD-Core Version:    0.6.2
 */