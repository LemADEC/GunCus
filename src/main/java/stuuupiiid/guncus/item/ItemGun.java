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
import stuuupiiid.guncus.data.ScopePart;
import stuuupiiid.guncus.network.PacketHandler;
import stuuupiiid.guncus.render.ItemRenderer;

public class ItemGun extends Item {
	public int delay = 3;
	
	public int shootType = 2;
	
	protected int actualType = 2;
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
	public int subs;
	public boolean tubing = false;
	public String pack;
	public String soundNormal;
	public String soundSilenced;
	public int damage;
	
	public ItemGun(String parPack, boolean parIsOfficial, String parName, String parIconBasePath,
			int parDamage, int parShootType, int parDelay,
			int magSize, int intMagBulletId, int parMagIronIngots, int parGunIronIngots, int parGunRedstone,
			int[] parAttach, int[] parBarrel, int[] parScopes,
			boolean noMag, int[] parBullets) {
		super();
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			MinecraftForgeClient.registerItemRenderer(this, new ItemRenderer());
		}
		damage = parDamage;
		setHasSubtypes(true);
		maxStackSize = 1;
		setFull3D();
		shootType = parShootType;
		actualType = parShootType;
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
			actualType = 0;
		} else {
			soundNormal = "guncus:shoot_normal";
		}
		soundSilenced = "guncus:shoot_silenced";
		
		actualBullet = 0;
		
		if (noMag) {
			bullets = parBullets;
			mag = null;
		} else {
			mag = new ItemMag(parPack, parName, parIconBasePath, magSize, intMagBulletId);
			magIronIngots = parMagIronIngots;
		}
		
		subs = (scopes.length + 1) * (barrels.length + 1) * (attachments.length + 1);
		
		GameRegistry.registerItem(this, getUnlocalizedName());
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
		
		if ((GunCus.shootTime <= 0) && (Mouse.isButtonDown(0)) && ((client.currentScreen == null) || (Mouse.isButtonDown(1)))
				&& (entityPlayer.inventory.hasItem(GunCus.ammoM320) || entityPlayer.capabilities.isCreativeMode) && tubing) {
			GunCus.shootTime += 95;
			PacketHandler.sendToServer_playerAction_tube();
			recoilTube(entityPlayer);
			Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147673_a(new ResourceLocation("guncus:reload_tube")));
		}
		if ((GunCus.shootTime <= 0)
				&& (Mouse.isButtonDown(0))
				&& (!this.shot)
				&& ((client.currentScreen == null) || (Mouse.isButtonDown(1)))
				&& ((playerMag != null) || (entityPlayer.capabilities.isCreativeMode) || ((this.bullets != null) && (entityPlayer.inventory.hasItem((ItemBullet.bulletsList.get(pack))
						.get(bullets[actualBullet]))))) && (!this.tubing)) {
			GunCus.shootTime += this.delay;
			this.reloadBurst = 0;
			
			if (this.actualType == 0) {
				this.shot = true;
			} else if (this.actualType == 1) {
				if (this.burstCounter < 2) {
					this.burstCounter += 1;
				} else {
					this.burstCounter = 0;
					this.shot = true;
				}
			}
			
			PacketHandler.sendToServer_playerAction_shoot(entityPlayer, mag, bullets, actualBullet);
			ItemBullet bulletItem;
			if (mag != null) {
				bulletItem = ItemBullet.bulletsList.get(pack).get(mag.bulletId);
			} else {
				bulletItem = ItemBullet.bulletsList.get(pack).get(bullets[actualBullet]);
			}
			
			float damage1 = this.damage * bulletItem.damageModifier;
			
			double newAccuracy = damage1;
			if (Mouse.isButtonDown(1)) {
				newAccuracy *= 0.9D;
			} else if (hasLaserPointer(itemStack.getItemDamage())) {
				newAccuracy *= 0.9D;
			}
			
			if (hasRifledBarrel(itemStack.getItemDamage())) {
				newAccuracy *= 0.8D;
			}
			
			if ((hasBipod(itemStack.getItemDamage())) && (canUseBipod(entityPlayer))) {
				newAccuracy *= 0.65D;
			}
			
			if (GunCus.accuracy > newAccuracy) {
				GunCus.accuracy -= newAccuracy;
			}
			
			recoil(entityPlayer, itemStack.getItemDamage(), Mouse.isButtonDown(1), damage1);
		}
		
		if ((this.shot) && (!Mouse.isButtonDown(0))) {
			this.shot = false;
		}
		
		if (!hasM320(itemStack.getItemDamage())) {
			this.tubing = false;
		}
		
		if ((this.burstCounter > 0) && (!Mouse.isButtonDown(0))) {
			this.reloadBurst += 1;
			if (this.reloadBurst >= this.delay * 3) {
				this.burstCounter = 0;
				this.reloadBurst = 0;
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
				actualType = 0;
				break;
			
			case 1:
				switch (actualType) {
				case 0:
					entityPlayer.addChatComponentMessage(new ChatComponentText("Switched To Burst Mode!"));
					actualType = 1;
					break;
					
				case 1:
				default:
					entityPlayer.addChatComponentMessage(new ChatComponentText("Switched To Single Mode!"));
					actualType = 0;
					break;
				}
				break;
			
			case 2:
				switch (actualType) {
				case 0:
					entityPlayer.addChatComponentMessage(new ChatComponentText("Switched To Burst Mode!"));
					actualType = 1;
					break;
					
				case 1:
					entityPlayer.addChatComponentMessage(new ChatComponentText("Switched To Auto Mode!"));
					actualType = 2;
					break;
					
				case 2:
				default:
					entityPlayer.addChatComponentMessage(new ChatComponentText("Switched To Single Mode!"));
					actualType = 0;
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
			if (this.tubing) {
				entityPlayer.addChatComponentMessage(new ChatComponentText("You are no longer using the M320!"));
				this.tubing = false;
			} else {
				entityPlayer.addChatComponentMessage(new ChatComponentText("You are now using the M320!"));
				this.tubing = true;
			}
			
			// Switching bullets
		} else if ( (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))
			     && Keyboard.isKeyDown(Keyboard.KEY_G)
			     && (GunCus.switchTime <= 0)
			     && (this.bullets != null)
			     && (this.bullets.length > 1) ) {
			GunCus.switchTime = 20;
			actualBullet += 1;
			if (actualBullet >= bullets.length) {
				actualBullet = 0;
			}
			ItemStack itemStackBullet = new ItemStack(ItemBullet.bulletsList.get(pack).get(bullets[actualBullet]));
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
		for (int subsIndex = 0; subsIndex < subs; subsIndex++) {
			ItemStack itemStack = new ItemStack(item, 1, subsIndex);
			list.add(itemStack);
		}
	}
	
	public boolean hasNoBarrel(int metadata) {
		for (int v1 = 0; v1 < GunCus.barrel.customizationParts.length; v1++) {
			if (hasBarrel(v1 + 1, metadata)) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean hasNoAttachment(int metadata) {
		for (int attachmentIndex = 0; attachmentIndex < GunCus.attachment.customizationParts.length; attachmentIndex++) {
			if (hasAttachment(attachmentIndex + 1, metadata)) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean canHaveScope(int scope) {
		for (int scopeIndex = 0; scopeIndex < scopes.length; scopeIndex++) {
			if (scopes[scopeIndex] == scope) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean hasBarrel(int barrel, int metadata) {
		boolean flag = false;
		
		for (int barrelIndex = 0; barrelIndex < barrels.length; barrelIndex++) {
			if ((barrels[barrelIndex] == barrel) && (metadata >= barrelFactor * (barrelIndex + 1)) && (metadata < barrelFactor * (barrelIndex + 2))) {
				return true;
			}
		}
		
		return flag;
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
	
	public boolean hasAttachment(int attachment, int metadata) {
		for (int attachmentIndex = 0; attachmentIndex < attachments.length; attachmentIndex++) {
			if (attachments[attachmentIndex] == attachment) {
				// TODO: check the logic here
				for (int scopeIndex = (scopes.length + 1) * (attachmentIndex + 1); scopeIndex < (scopes.length + 1) * (attachmentIndex + 2); scopeIndex++) {
					for (int barrelIndex = 0; barrelIndex <= barrels.length; barrelIndex++) {
						if (metadata == scopeIndex + barrelIndex * barrelFactor) {
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}
	
	public boolean canHaveExtra(int attachment) {
		for (int v1 = 0; v1 < attachments.length; v1++) {
			if (attachments[v1] == attachment) {
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
		return canHaveExtra(1);
	}
	
	public boolean canHaveBipod() {
		return canHaveExtra(2);
	}
	
	public boolean canHaveGrip() {
		return canHaveExtra(3);
	}
	
	public boolean canHaveM320() {
		return canHaveExtra(4);
	}
	
	public boolean canHaveStrongSpiralString() {
		return canHaveExtra(5);
	}
	
	public boolean canHaveImprovedGrip() {
		return canHaveExtra(6);
	}
	
	public boolean canHaveLaserPointer() {
		return canHaveExtra(7);
	}
	
	public int barrelAsMetadataFactor(int barrel) {
		for (int barrelIndex = 0; barrelIndex < barrels.length; barrelIndex++) {
			if (barrels[barrelIndex] == barrel) {
				return barrelIndex + 1;
			}
		}
		return 0;
	}
	
	public int attachAsMetadataFactor(int attachment) {
		for (int attachmentIndex = 0; attachmentIndex < attachments.length; attachmentIndex++) {
			if (attachments[attachmentIndex] == attachment) {
				return attachmentIndex + 1;
			}
		}
		return 0;
	}
	
	// get the glocal scope index
	public int getScopeIndex(int metadata) {
		int scopeIndex = metadata;
		
		// find scope in the gun's accepted list
		while (scopeIndex >= scopes.length + 1) {
			scopeIndex -= scopes.length + 1;
		}
		if (scopeIndex == 0) {
			return 0;
		}
		
		// return the global index of that scope
		return scopes[scopeIndex - 1];
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
		GunCus.logger.error("Registering icons for " + this);
		String iconToRegisterName = iconBasePath + "gun";
		icon = par1IconRegister.registerIcon(iconToRegisterName);
		if (icon == null) {
			GunCus.logger.error("Failed to register icon '" + iconToRegisterName + "'");
		}
		
		iconsAttachment = new IIcon[attachments.length];
		for (int attachIndex = 0; attachIndex < attachments.length; attachIndex++) {
			iconToRegisterName = iconBasePath + getAttachIcon("a", attachments[attachIndex]);
			iconsAttachment[attachIndex] = par1IconRegister.registerIcon(iconToRegisterName);
			if (iconsAttachment[attachIndex] == null) {
				GunCus.logger.error("Failed to register icon '" + iconToRegisterName + "'");
			}
		}
		
		iconsBarrel = new IIcon[barrels.length];
		for (int barrelIndex = 0; barrelIndex < barrels.length; barrelIndex++) {
			iconToRegisterName = iconBasePath + getAttachIcon("b", barrels[barrelIndex]);
			iconsBarrel[barrelIndex] = par1IconRegister.registerIcon(iconToRegisterName);
			if (iconsBarrel[barrelIndex] == null) {
				GunCus.logger.error("Failed to register icon '" + iconToRegisterName + "'");
			}
		}
		
		if (scopes.length > 0) {
			iconToRegisterName = iconBasePath + "scp";
			iconScope = par1IconRegister.registerIcon(iconToRegisterName);
			if (iconScope == null) {
				GunCus.logger.error("Failed to register icon '" + iconToRegisterName + "'");
			}
		}
	}
	
	public String getAttachIcon(String type, int attach1) {
		String string = "-";
		
		if (type.toLowerCase().startsWith("a")) {
			switch (attach1) {
			case 1:
				string = "spb";
				break;
			case 2:
				string = "bpd";
				break;
			case 3:
				string = "grp";
				break;
			case 4:
				string = "320";
				break;
			case 5:
				string = "sss";
				break;
			case 6:
				string = "img";
				break;
			case 7:
				string = "ptr";
				break;
			default:
				string = "!bad!";
				break;
			}
			
		} else if (type.toLowerCase().startsWith("b")) {
			switch (attach1) {
			case 1:
				string = "sln";
				break;
			case 2:
				string = "hbl";
				break;
			case 3:
				string = "rbl";
				break;
			case 4:
				string = "pbl";
				break;
			default:
				string = "!bad!";
				break;
			}
		}
		
		return string;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int metadata) {
		return icon;
	}
	
	public float getZoomFromScope(int scope) {
		float newZoom = 1.0F;
		if (scope > 0) {
			ScopePart scopePart = (ScopePart) GunCus.scope.customizationParts[(scope - 1)];
			newZoom = scopePart.zoom;
		}
		
		return newZoom;
	}
	
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par2List, boolean par4) {
		int metadata = par1ItemStack.getItemDamage();
		
		if (metadata > 0) {
			String front = null;
			String attachment = null;
			String scope = null;
			
			for (int v1 = 1; v1 <= GunCus.barrel.customizationParts.length; v1++) {
				if (hasBarrel(v1, metadata)) {
					front = GunCus.barrel.customizationParts[(v1 - 1)].localized;
				}
			}
			
			for (int v1 = 1; v1 <= GunCus.attachment.customizationParts.length; v1++) {
				if (hasAttachment(v1, metadata)) {
					attachment = GunCus.attachment.customizationParts[(v1 - 1)].localized;
				}
			}
			
			int scopeIndex = getScopeIndex(metadata);
			if (scopeIndex >= 0) {
				scope = GunCus.scope.customizationParts[scopeIndex].localized;
			}
			
			if (front != null) {
				par2List.add(front);
			}
			if (attachment != null) {
				par2List.add(attachment);
			}
			if (scope != null) {
				par2List.add(scope);
			}
			
			par2List.add("");
		}
		par2List.add("Pack: " + pack);
	}
}
