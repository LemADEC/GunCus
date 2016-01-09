package stuuupiiid.guncus.item;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.data.CustomizationPart;
import stuuupiiid.guncus.data.ScopePart;
import stuuupiiid.guncus.network.PacketHandler;
import stuuupiiid.guncus.render.RenderGun;

public class ItemGun extends Item {
	public int delay = 3;
	
	public int shootType = 2;
	
	protected int actualShootType = 2;
	protected int burstCounter = 0;
	protected int reloadBurst = 0;
	protected boolean shot = false;
	public String iconBasePath;
	public IIcon icon;
	public IIcon[] iconsAttachment;
	public IIcon[] iconsBarrel;
	public IIcon iconScope;
	public ItemMag mag = null;
	public int magIronIngots;
	public int gunIronIngots;
	public int gunRedstone;
	public double recoilModifier;
	public double soundModifier;
	public boolean usingDefault = false;
	public float zoom = 1.0F;
	public int[] bullets;
	public int actualBullet;
	public int[] attachments;
	public int[] barrels;
	public int[] scopes;
	public int barrelFactor;
	public boolean isInTubingMode = false;
	public String pack;
	public String soundNormal;
	public String soundSilenced;
	public int damage;
	
	public ItemGun(String parPack, boolean parIsOfficial, String parName, String parIconBasePath,
			int parDamage, int parShootType, int parDelay,
			int magSize, int parMagIronIngots, int parGunIronIngots, int parGunRedstone,
			int[] parAttach, int[] parBarrel, int[] parScopes,
			boolean usingMag, int[] parBullets) {
		super();
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			MinecraftForgeClient.registerItemRenderer(this, new RenderGun());
		}
		damage = parDamage;
		setHasSubtypes(true);
		maxStackSize = 1;
		setFull3D();
		shootType = parShootType;
		actualShootType = parShootType;
		delay = parDelay;
		setMaxDamage(0);
		setUnlocalizedName((parPack + "." + parName).replace(" ", "_"));
		iconBasePath = parIconBasePath;
		gunIronIngots = parGunIronIngots;
		gunRedstone = parGunRedstone;
		pack = parPack;
		recoilModifier = 1.0D;
		soundModifier = 1.0D;
		attachments = parAttach;
		barrels = parBarrel;
		scopes = parScopes;
		
		barrelFactor = (attachments.length + 1) * (scopes.length + 1);
		
		if (canHaveStraightPullBolt()) {
			soundNormal = "guncus:shoot_sniper";
			shootType = 0;
			actualShootType = 0;
		} else {
			soundNormal = "guncus:shoot_normal";
		}
		soundSilenced = "guncus:shoot_silenced";
		
		actualBullet = 0;
		
		if (usingMag) {
			mag = new ItemMag(parPack, parName, parIconBasePath, magSize, parBullets[0]);
			magIronIngots = parMagIronIngots;
		} else {
			bullets = parBullets;
			mag = null;
		}
		
		GameRegistry.registerItem(this, getUnlocalizedName());
		
		// Add to gun list
		GunCus.guns.put(this.getUnlocalizedName(), this);
		GunCus.logger.info("Added gun " + parName);
	}
	
	public ItemGun setZoom(float zoom) {
		this.zoom = Math.max(1.0F, zoom);
		return this;
	}
	
	public ItemGun defaultTexture(boolean flag) {
		this.usingDefault = flag;
		return this;
	}
	
	public ItemGun setRecoilModifier(double recoilModifier) {
		this.recoilModifier = recoilModifier;
		return this;
	}
	
	public ItemGun setSoundModifier(double soundModifier) {
		this.soundModifier = soundModifier;
		return this;
	}
	
	public ItemGun setNormalSound(String soundNormal) {
		this.soundNormal = soundNormal;
		return this;
	}
	
	public ItemGun setSilencedSound(String soundSilenced) {
		this.soundSilenced = soundSilenced;
		return this;
	}
	
	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int par1, boolean flag) {
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			doUpdate(itemStack, world, entity, par1, flag);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void doUpdate(ItemStack itemStack, World world, Entity entity, int par1, boolean flag) {
		Minecraft client = FMLClientHandler.instance().getClient();
		EntityPlayer entityPlayer = client.thePlayer;
		if (entityPlayer == null || entityPlayer.getHeldItem() == null || !entityPlayer.getHeldItem().isItemEqual(itemStack)) {
			return;
		}
		ItemStack playerMag = null;
		
		if (mag != null) {
			// search for a damaged magazine first
			for (int v1 = 0; v1 < entityPlayer.inventory.getSizeInventory(); v1++) {
				playerMag = entityPlayer.inventory.getStackInSlot(v1);
				if ((playerMag != null) && (playerMag.getItem() == mag) && (playerMag.isItemDamaged()) && (playerMag.getItemDamage() < playerMag.getMaxDamage())) {
					break;
				}
				playerMag = null;
			}
			
			if (playerMag == null) {
				// search for a full magazine
				for (int v1 = 0; v1 < entityPlayer.inventory.getSizeInventory(); v1++) {
					playerMag = entityPlayer.inventory.getStackInSlot(v1);
					if ((playerMag != null) && (playerMag.getItem() == mag) && (!playerMag.isItemDamaged())) {
						break;
					}
					playerMag = null;
				}
			}
		}
		
		if ( (GunCus.shootTime <= 0)
				&& Mouse.isButtonDown(0)
				&& ((client.currentScreen == null) || Mouse.isButtonDown(1))
				&& (entityPlayer.inventory.hasItem(GunCus.ammoM320) || entityPlayer.capabilities.isCreativeMode)
				&& isInTubingMode) {
			GunCus.shootTime += 120;
			GunCus.reloading = true;
			PacketHandler.sendToServer_playerAction_tube();
			recoilTube(entityPlayer);
			Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147673_a(new ResourceLocation("guncus:reload_tube")));
		}
		if ( (GunCus.shootTime <= 0)
				&& (Mouse.isButtonDown(0))
				&& (!shot)
				&& ((client.currentScreen == null) || Mouse.isButtonDown(1))
				&& ((playerMag != null) || (entityPlayer.capabilities.isCreativeMode)
						|| ((bullets != null)
								&& entityPlayer.inventory.hasItem((ItemBullet.bullets.get(pack)).get(bullets[actualBullet]))))
				&& (!isInTubingMode)) {
			GunCus.shootTime += this.delay;
			this.reloadBurst = 0;
			
			if (actualShootType == 0) {
				shot = true;
			} else if (actualShootType == 1) {
				if (burstCounter < 2) {
					burstCounter += 1;
				} else {
					burstCounter = 0;
					shot = true;
				}
			}
			
			PacketHandler.sendToServer_playerAction_shoot(entityPlayer, mag, bullets, actualBullet);
			ItemBullet bulletItem;
			if (mag != null) {
				bulletItem = ItemBullet.bullets.get(pack).get(mag.bulletId);
			} else {
				bulletItem = ItemBullet.bullets.get(pack).get(bullets[actualBullet]);
			}
			
			float damage1 = (this.damage - 5) * bulletItem.damageModifier;
			if (GunCus.logging_enableNetwork) {
				GunCus.logger.info("Gun damage is " + this.damage);
				GunCus.logger.info("Gun pre accuracy is " + GunCus.accuracy);
			}
			
			double newAccuracy = damage1;
			if (Mouse.isButtonDown(1)) {
				newAccuracy *= 0.75D;
			} else if (hasLaserPointer(itemStack.getItemDamage())) {
				newAccuracy *= 0.75D;
			}
			
			if (hasSilencer(itemStack.getItemDamage())) {
				newAccuracy *= 1.05D;
			}
			
			if (hasRifledBarrel(itemStack.getItemDamage())) {
				newAccuracy *= 0.8D;
			}
			
			if (hasPolygonalBarrel(itemStack.getItemDamage())) {
				newAccuracy *= 0.75D;
			}
			
			if (hasHeavyBarrel(itemStack.getItemDamage())) {
				newAccuracy *= 0.7D;
			}
			
			if ((hasBipod(itemStack.getItemDamage())) && (canUseBipod(entityPlayer))) {
				newAccuracy *= 0.65D;
			}
			
			if (hasGrip(itemStack.getItemDamage())) {
				newAccuracy *= 0.8D;
			}
			
			if (hasImprovedGrip(itemStack.getItemDamage())) {
				newAccuracy *= 0.8D;
			}
			
			if (GunCus.accuracy > newAccuracy) {
				GunCus.accuracy -= newAccuracy;
			}
			
			recoil(entityPlayer, itemStack.getItemDamage(), Mouse.isButtonDown(1), damage1);
		}
		
		if (shot && (!Mouse.isButtonDown(0))) {
			shot = false;
		}
		
		if (!hasM320(itemStack.getItemDamage())) {
			isInTubingMode = false;
		}
		
		if ((burstCounter > 0) && (!Mouse.isButtonDown(0))) {
			reloadBurst += 1;
			if (reloadBurst >= delay * 3) {
				burstCounter = 0;
				reloadBurst = 0;
			}
		}
		
		// Switching Fire mode
		if ( (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))
		  && Keyboard.isKeyDown(Keyboard.KEY_V)
		  && (GunCus.switchTime <= 0)
		  && (!canHaveStraightPullBolt()) ) {
			switch (shootType) {
			case 0:
			default:
				entityPlayer.addChatComponentMessage(new ChatComponentText("The Fire mode of this gun can't be changed!"));
				shootType = 0;
				actualShootType = 0;
				break;
			
			case 1:
				switch (actualShootType) {
				case 0:
					entityPlayer.addChatComponentMessage(new ChatComponentText("Switched To Burst Mode!"));
					actualShootType = 1;
					break;
					
				case 1:
				default:
					entityPlayer.addChatComponentMessage(new ChatComponentText("Switched To Single Mode!"));
					actualShootType = 0;
					break;
				}
				break;
			
			case 2:
				switch (actualShootType) {
				case 0:
					entityPlayer.addChatComponentMessage(new ChatComponentText("Switched To Burst Mode!"));
					actualShootType = 1;
					break;
					
				case 1:
					entityPlayer.addChatComponentMessage(new ChatComponentText("Switched To Auto Mode!"));
					actualShootType = 2;
					break;
					
				case 2:
				default:
					entityPlayer.addChatComponentMessage(new ChatComponentText("Switched To Single Mode!"));
					actualShootType = 0;
					break;
				}
				break;
			}
			
			GunCus.switchTime = 20;
			
			// Switching with grenades
		} else if ( (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))
			     && Keyboard.isKeyDown(Keyboard.KEY_C)
			     && (GunCus.switchTime <= 0)
			     && hasM320(itemStack.getItemDamage()) ) {
			GunCus.switchTime = 20;
			if (isInTubingMode) {
				entityPlayer.addChatComponentMessage(new ChatComponentText("You are no longer using the M320!"));
				isInTubingMode = false;
			} else {
				entityPlayer.addChatComponentMessage(new ChatComponentText("You are now using the M320!"));
				isInTubingMode = true;
			}
			
			// Switching bullets
		} else if ( (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))
			     && Keyboard.isKeyDown(Keyboard.KEY_G)
			     && (GunCus.switchTime <= 0)
			     && (bullets != null)
			     && (bullets.length > 1) ) {
			GunCus.switchTime = 20;
			actualBullet += 1;
			if (actualBullet >= bullets.length) {
				actualBullet = 0;
			}
			ItemStack itemStackBullet = new ItemStack(ItemBullet.bullets.get(pack).get(bullets[actualBullet]));
			entityPlayer.addChatComponentMessage(new ChatComponentText("You are now using " + itemStackBullet.getDisplayName() + " ammunition!"));
		}
	}
	
	private void recoil(EntityPlayer entityPlayer, int metadata, boolean scoping, double parDamage) {
		float strength = (float) (parDamage / 12.0F * recoilModifier);
		
		if (hasBipod(metadata) && canUseBipod(entityPlayer)) {
			strength /= 3.0F;
		} else if (hasGrip(metadata)) {
			strength *= 0.8F;
		} else if ((!hasImprovedGrip(metadata)) && canHaveImprovedGrip()) {
			strength *= 1.5F;
		}
		
		// scoping has no effect
		entityPlayer.rotationPitch -= strength * (0.8F + 0.4F * itemRand.nextFloat());
		entityPlayer.rotationYaw -= strength * (itemRand.nextBoolean() ? -1.0F : +1.0F) * (0.8F + 0.4F * entityPlayer.worldObj.rand.nextFloat());
	}
	
	private void recoilTube(EntityPlayer entityPlayer) {
		entityPlayer.rotationPitch -= 1.25F + itemRand.nextFloat() * 0.5F;
		entityPlayer.rotationYaw += itemRand.nextFloat() * 4.0F - 2.0F;
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs creativeTabs, List list) {
		for (int barrelIndex = 0; barrelIndex <= barrels.length; barrelIndex++) {
			int barrelId = (barrelIndex == 0)? 0 : barrels[barrelIndex - 1];
			
			for (int attachmentIndex = 0; attachmentIndex <= attachments.length; attachmentIndex++) {
				int attachmentId = (attachmentIndex == 0)? 0 : attachments[attachmentIndex - 1];
				
				for (int scopeIndex = 0; scopeIndex <= scopes.length; scopeIndex++) {
					int scopeId = (scopeIndex == 0)? 0 : scopes[scopeIndex - 1];
					
					int metadata = scopeId + (GunCus.scope.maxId + 1) * (attachmentId + (GunCus.attachment.maxId + 1) * barrelId);
					list.add(new ItemStack(item, 1, metadata));
				}
			}
		}
	}
	
	// hasScope() not needed
	
	public boolean canHaveScope(int scopeId) {
		for (int scopeIndex = 0; scopeIndex < scopes.length; scopeIndex++) {
			if (scopes[scopeIndex] == scopeId) {
				return true;
			}
		}
		return false;
	}
	
	
	private boolean hasBarrel(int barrelId, int metadata) {
		CustomizationPart customizationPart = getBarrelPart(metadata);
		if (customizationPart != null) {
			return customizationPart.id == barrelId; 
		} else {
			return 0 == barrelId;
		}
	}
	
	public boolean canHaveBarrel(int barrelId) {
		for (int barrelIndex = 0; barrelIndex < barrels.length; barrelIndex++) {
			if (barrels[barrelIndex] == barrelId) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasSilencer(int metadata) {
		return hasBarrel(1, metadata);
	}
	
	public boolean hasHeavyBarrel(int metadata) {
		return hasBarrel(2, metadata);
	}
	
	public boolean hasRifledBarrel(int metadata) {
		return hasBarrel(3, metadata);
	}
	
	public boolean hasPolygonalBarrel(int metadata) {
		return hasBarrel(4, metadata);
	}
	
	
	private boolean hasAttachment(int attachmentId, int metadata) {
		CustomizationPart customizationPart = getAttachmentPart(metadata);
		if (customizationPart != null) {
			return customizationPart.id == attachmentId; 
		} else {
			return 0 == attachmentId;
		}
	}
	
	public boolean canHaveAttachment(int attachmentId) {
		for (int attachmentIndex = 0; attachmentIndex < attachments.length; attachmentIndex++) {
			if (attachments[attachmentIndex] == attachmentId) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasStraightPullBolt(int metadata) {
		return hasAttachment(1, metadata);
	}
	
	public boolean hasBipod(int metadata) {
		return hasAttachment(2, metadata);
	}
	
	public boolean hasGrip(int metadata) {
		return hasAttachment(3, metadata);
	}
	
	public boolean hasM320(int metadata) {
		return hasAttachment(4, metadata);
	}
	
	public boolean hasStrongSpiralSpring(int metadata) {
		return hasAttachment(5, metadata);
	}
	
	public boolean hasImprovedGrip(int metadata) {
		return hasAttachment(6, metadata);
	}
	
	public boolean hasLaserPointer(int metadata) {
		return hasAttachment(7, metadata);
	}
	
	public boolean canHaveStraightPullBolt() {
		return canHaveAttachment(1);
	}
	
	/*
	public boolean canHaveBipod() {
		return canHaveAttachment(2);
	}
	
	public boolean canHaveGrip() {
		return canHaveAttachment(3);
	}
	
	public boolean canHaveM320() {
		return canHaveAttachment(4);
	}
	
	public boolean canHaveStrongSpiralString() {
		return canHaveAttachment(5);
	}
	/**/
	
	public boolean canHaveImprovedGrip() {
		return canHaveAttachment(6);
	}
	
	/*
	public boolean canHaveLaserPointer() {
		return canHaveAttachment(7);
	}
	/**/
	
	// get the scope part or null
	public ScopePart getScopePart(final int metadata) {
		int scopeId = metadata % (GunCus.scope.maxId + 1);
		
		if (scopeId == 0) {
			return null;
		}
		
		// return the global index of that scope
		return (ScopePart) GunCus.scope.getCustomizationPart(scopeId);
	}
	
	// get the attachment part or null
	public CustomizationPart getAttachmentPart(final int metadata) {
		int attachmentId = (metadata / (GunCus.scope.maxId + 1)) % (GunCus.attachment.maxId + 1);
		
		if (attachmentId == 0) {
			return null;
		}
		
		// return the global index of that scope
		return GunCus.attachment.getCustomizationPart(attachmentId);
	}
	
	// get the barrel part or null
	public CustomizationPart getBarrelPart(final int metadata) {
		int barrelId = (metadata / (GunCus.scope.maxId + 1) / (GunCus.attachment.maxId + 1)) % (GunCus.barrel.maxId + 1);
		
		if (barrelId == 0) {
			return null;
		}
		
		// return the global index of that scope
		return GunCus.barrel.getCustomizationPart(barrelId);
	}
	
	public boolean canUseBipod(EntityPlayer entityPlayer) {
		if ( entityPlayer.isSneaking()
		  && entityPlayer.motionX == 0.0D
		  && entityPlayer.motionZ == 0.0D) {
			return true;
		}
		
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		String iconToRegisterName = iconBasePath + "gun";
		icon = par1IconRegister.registerIcon(iconToRegisterName);
		if (icon == null) {
			GunCus.logger.error("Failed to register icon '" + iconToRegisterName + "'");
		}
		
		if (scopes.length > 0) {
			iconToRegisterName = iconBasePath + "scp";
			iconScope = par1IconRegister.registerIcon(iconToRegisterName);
			if (iconScope == null) {
				GunCus.logger.error("Failed to register icon '" + iconToRegisterName + "'");
			}
		}
		
		iconsAttachment = new IIcon[GunCus.attachment.maxId + 1];
		for (int attachmentId : attachments) {
			iconToRegisterName = iconBasePath + GunCus.attachment.getCustomizationPart(attachmentId).iconName;
			iconsAttachment[attachmentId] = par1IconRegister.registerIcon(iconToRegisterName);
			if (iconsAttachment[attachmentId] == null) {
				GunCus.logger.error("Failed to register icon '" + iconToRegisterName + "'");
			}
		}
		
		iconsBarrel = new IIcon[GunCus.barrel.maxId + 1];
		for (int barrelId : barrels) {
			iconToRegisterName = iconBasePath + GunCus.barrel.getCustomizationPart(barrelId).iconName;
			iconsBarrel[barrelId] = par1IconRegister.registerIcon(iconToRegisterName);
			if (iconsBarrel[barrelId] == null) {
				GunCus.logger.error("Failed to register icon '" + iconToRegisterName + "'");
			}
		}
	}
		
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int metadata) {
		return icon;
	}
	
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
		int metadata = itemStack.getItemDamage();
		
		CustomizationPart customizationPart;
		ItemStack itemStackPart;
		customizationPart = getBarrelPart(metadata);
		if (customizationPart != null) {
			itemStackPart = new ItemStack(GunCus.barrel, 1, customizationPart.id);
			list.add(itemStackPart.getDisplayName());
		} else if (barrels.length > 0) {
			list.add("-");
		}
		customizationPart = getAttachmentPart(metadata);
		if (customizationPart != null) {
			itemStackPart = new ItemStack(GunCus.attachment, 1, customizationPart.id);
			list.add(itemStackPart.getDisplayName());
		} else if (attachments.length > 0) {
			list.add("-");
		}
		customizationPart = getScopePart(metadata);
		if (customizationPart != null) {
			itemStackPart = new ItemStack(GunCus.scope, 1, customizationPart.id);
			list.add(itemStackPart.getDisplayName());
		} else if (scopes.length > 0) {
			list.add("-");
		}
		
		list.add("");
		list.add(pack + " pack");
	}
}
