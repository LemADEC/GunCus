package stuuupiiid.guncus;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GunCusItemGun extends Item {
	public int delay = 3;

	public int shootType = 2;

	protected int actualType = 2;
	protected int burstCounter = 0;
	protected int reloadBurst = 0;
	protected boolean shot = false;
	public String iconName;
	public String name;
	protected IIcon icon;
	protected IIcon[] iconAttach;
	protected IIcon[] iconBar;
	protected IIcon iconScp;
	public GunCusItemMag mag = null;
	public int ingotsMag;
	public int ingots;
	public int field_redstone;
	protected double recModify;
	public double soundModify;
	public boolean isOfficial;
	public boolean usingDefault = false;
	public float zoom = 1.0F;
	public int[] bullets;
	public int actualBullet;
	public int[] attach;
	public int[] barrel;
	public int[] scopes;
	public int factor;
	public int subs;
	public boolean tubing = false;
	public String pack;
	public static List<GunCusItemGun> gunList = new ArrayList();
	public String soundNormal;
	public String soundSilenced;
	public int damage;

	public GunCusItemGun(int parDamage, int parShootType, int parDelay, String parName, String parIconName, int magSize,
			int magId, int bulletType, int parIngotsMag, int parIngots, int parRedstone, String parPack, boolean parIsOfficial,
			int[] parAttach, int[] parBarrel, int[] parScopes, boolean noMag, int[] parBullets) {
		super();
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			MinecraftForgeClient.registerItemRenderer(this, new GunCusInvRenderer());
		}
		isOfficial = parIsOfficial;
		damage = parDamage;
		setHasSubtypes(true);
		maxStackSize = 1;
		setFull3D();
		shootType = parShootType;
		actualType = parShootType;
		delay = parDelay;
		setMaxDamage(0);
		name = parName;
		iconName = parIconName;
		ingots = parIngots;
		field_redstone = parRedstone;
		pack = parPack;
		recModify = 1.0D;
		soundModify = 1.0D;
		attach = parAttach;
		barrel = parBarrel;
		scopes = parScopes;

		factor = ((attach.length + 1) * (scopes.length + 1));

		if (canHaveStraightPullBolt()) {
			soundNormal = "guncus:shoot_sniper";
			shootType = 0;
			actualType = 0;
		} else {
			soundNormal = "guncus:shoot_normal";
		}
		soundSilenced = "guncus:shoot_silenced";

		GunCusCreativeTab tab = new GunCusCreativeTab(parName, this);
		setCreativeTab(tab);

		actualBullet = 0;

		if (noMag) {
			bullets = parBullets;
			mag = null;
		} else {
			mag = new GunCusItemMag(parName, getName(0), magSize, parIconName, bulletType, parPack);
			ingotsMag = parIngotsMag;
			mag.setCreativeTab(tab);
		}

		gunList.add(this);

		int i = this.scopes.length + 1;

		if (this.barrel.length > 0) {
			i *= (this.barrel.length + 1);
		}

		if (this.attach.length > 0) {
			i *= (this.attach.length + 1);
		}

		this.subs = i;

		for (int v1 = 0; v1 < this.subs; v1++) {
			LanguageRegistry.addName(new ItemStack(this, 1, v1), this.name);
		}
	}

	public GunCusItemGun setZoom(float zoom) {
		this.zoom = zoom;
		if (this.zoom < 1.0F) {
			this.zoom = 1.0F;
		}
		return this;
	}

	public GunCusItemGun defaultTexture(boolean flag) {
		this.usingDefault = flag;
		return this;
	}

	public GunCusItemGun setRecoilModifier(double d) {
		this.recModify = d;
		return this;
	}

	public GunCusItemGun setSoundModifier(double d) {
		this.soundModify = d;
		return this;
	}

	public GunCusItemGun setNormalSound(String sound) {
		this.soundNormal = sound;
		return this;
	}

	public GunCusItemGun setSLNSound(String sound) {
		this.soundSilenced = sound;
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
				if ((playerMag != null)
						&& (playerMag.getItem() == mag)
						&& (playerMag.isItemDamaged())
						&& (playerMag.getItemDamage() < playerMag.getMaxDamage())) {
					break;
				}
				playerMag = null;
			}

			if (playerMag == null) {
				// search for a full magazine
				for (int v1 = 0; v1 < entityPlayer.inventory.getSizeInventory(); v1++) {
					playerMag = entityPlayer.inventory.getStackInSlot(v1);
					if ((playerMag != null)
							&& (playerMag.getItem() == mag)
							&& (!playerMag.isItemDamaged())) {
						break;
					}
					playerMag = null;
				}
			}
		}

		if ((GunCus.shootTime <= 0)
				&& (Mouse.isButtonDown(0))
				&& ((client.currentScreen == null) || (Mouse.isButtonDown(1)))
				&& ((entityPlayer.inventory.hasItem(GunCus.ammoM320)) || (entityPlayer.capabilities.isCreativeMode))
				&& (this.tubing)) {
			GunCus.shootTime += 95;
			tube(entityPlayer);
			recoilTube(entityPlayer);
			Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147673_a(new ResourceLocation("guncus:reload_tube")));
		}
		if ((GunCus.shootTime <= 0)
				&& (Mouse.isButtonDown(0))
				&& (!this.shot)
				&& ((client.currentScreen == null) || (Mouse.isButtonDown(1)))
				&& ( (playerMag != null) || (entityPlayer.capabilities.isCreativeMode)
				  || ((this.bullets != null)
					&& (entityPlayer.inventory.hasItem( (GunCusItemBullet.bulletsList.get(this.pack)).get(this.bullets[this.actualBullet]) ))))
				&& (!this.tubing)) {
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

			shoot(entityPlayer);
			GunCusItemBullet bulletItem;
			if (mag != null) {
				bulletItem = GunCusItemBullet.bulletsList.get(pack).get(mag.bulletType);
			} else {
				bulletItem = GunCusItemBullet.bulletsList.get(pack).get(bullets[actualBullet]);
			}

			float damage1 = this.damage * bulletItem.damage;

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

		if (((Keyboard.isKeyDown(29)) || (Keyboard.isKeyDown(157))) && (Keyboard.isKeyDown(47))
				&& (GunCus.switchTime <= 0) && (!canHaveStraightPullBolt())) {
			switch (this.shootType) {
			case 0:
				entityPlayer.addChatMessage("The Fire Mode Of This Gun Can Not Be Changed!");
				break;
			case 1:
				switch (this.actualType) {
				case 0:
					entityPlayer.addChatMessage("Switched To Burst Mode!");
					this.actualType = 1;
					break;
				case 1:
					entityPlayer.addChatMessage("Switched To Single Mode!");
					this.actualType = 0;
				}

				break;
			case 2:
				switch (this.actualType) {
				case 0:
					entityPlayer.addChatMessage("Switched To Burst Mode!");
					this.actualType = 1;
					break;
				case 1:
					entityPlayer.addChatMessage("Switched To Auto Mode!");
					this.actualType = 2;
					break;
				case 2:
					entityPlayer.addChatMessage("Switched To Single Mode!");
					this.actualType = 0;
				}

				break;
			}

			GunCus.switchTime = 20;
		} else if (((Keyboard.isKeyDown(29)) || (Keyboard.isKeyDown(157))) && (Keyboard.isKeyDown(46))
				&& (GunCus.switchTime <= 0) && (hasM320(itemStack.getItemDamage()))) {
			GunCus.switchTime = 20;
			if (this.tubing) {
				entityPlayer.addChatMessage("You are no longer using the M320!");
				this.tubing = false;
			} else {
				entityPlayer.addChatMessage("You are now using the M320!");
				this.tubing = true;
			}
		} else if (((Keyboard.isKeyDown(29)) || (Keyboard.isKeyDown(157))) && (Keyboard.isKeyDown(34))
				&& (GunCus.switchTime <= 0) && (this.bullets != null) && (this.bullets.length > 1)) {
			GunCus.switchTime = 20;
			actualBullet += 1;
			if (actualBullet >= bullets.length) {
				actualBullet = 0;
			}
			entityPlayer.addChatMessage("You are now using \""
					+ ((GunCusItemBullet) ((List) GunCusItemBullet.bulletsList.get(this.pack))
							.get(this.bullets[this.actualBullet])).name + "\" ammunition!");
		}
	}

	private void shoot(EntityPlayer entityPlayer) {
		ByteArrayDataOutput bytes = ByteStreams.newDataOutput();
		bytes.writeInt(1);
		bytes.writeInt(MathHelper.floor_double(GunCus.accuracy));
		if (mag == null) {
			bytes.writeInt(bullets[actualBullet]);
		}
		PacketDispatcher.sendPacketToServer(new Packet250CustomPayload("guncus", bytes.toByteArray()));
	}

	private void tube(EntityPlayer entityPlayer) {
		ByteArrayDataOutput bytes = ByteStreams.newDataOutput();
		bytes.writeInt(8);
		bytes.writeInt(MathHelper.floor_double(GunCus.accuracy));
		PacketDispatcher.sendPacketToServer(new Packet250CustomPayload("guncus", bytes.toByteArray()));
	}

	private void recoil(EntityPlayer entityPlayer, int metadata, boolean scoping, double damage1) {
		float strength = (float) (damage1 / 6.0F * this.recModify);

		if (hasBipod(metadata) && canUseBipod(entityPlayer)) {
			strength /= 3.0F;
		} else if (hasGrip(metadata)) {
			strength *= 0.8F;
		} else if ((!hasImprovedGrip(metadata)) && (canHaveImprovedGrip())) {
			strength *= 1.5F;
		}

		// scoping has no effect
		entityPlayer.rotationPitch = entityPlayer.rotationPitch - strength * (0.8F + 0.4F * entityPlayer.worldObj.rand.nextFloat());
		entityPlayer.rotationYaw = entityPlayer.rotationYaw - strength * (entityPlayer.worldObj.rand.nextBoolean() ? -0.5F : +0.5F) * (0.8F + 0.4F * entityPlayer.worldObj.rand.nextFloat());
	}

	private void recoilTube(EntityPlayer entityPlayer) {
		float strength = 1.5F;

		entityPlayer.rotationPitch -= strength;
		entityPlayer.rotationYaw -= (Item.itemRand.nextBoolean() ? strength / 2.0F : -strength / 2.0F);
	}

	@Override
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
		for (int j = 0; j < this.subs; j++) {
			ItemStack itemStack = new ItemStack(par1, 1, j);
			par3List.add(itemStack);
		}
	}

	public boolean barrelFree(int metadata) {
		for (int v1 = 0; v1 < GunCus.barrel.metadatas.length; v1++) {
			if (testForBarrelId(v1 + 1, metadata)) {
				return false;
			}
		}

		return true;
	}

	public boolean attatchmentFree(int metadata) {
		for (int v1 = 0; v1 < GunCus.attachment.metadatas.length; v1++) {
			if (testForAttachId(v1 + 1, metadata)) {
				return false;
			}
		}

		return true;
	}

	public boolean testIfCanHaveScope(int scope) {
		for (int v1 = 0; v1 < this.scopes.length; v1++) {
			if (this.scopes[v1] == scope) {
				return true;
			}
		}

		return false;
	}

	public boolean testForBarrelId(int barrel1, int metadata) {
		boolean flag = false;

		for (int v1 = 0; v1 < this.barrel.length; v1++) {
			if ((this.barrel[v1] == barrel1) && (metadata >= this.factor * (v1 + 1))
					&& (metadata < this.factor * (v1 + 2))) {
				return true;
			}
		}

		return flag;
	}

	public boolean hasSilencer(int metadata) {
		return testForBarrelId(1, metadata);
	}

	public boolean hasHeavyBarrel(int metadata) {
		return testForBarrelId(2, metadata);
	}

	public boolean hasRifledBarrel(int metadata) {
		return testForBarrelId(3, metadata);
	}

	public boolean hasPolygonalBarrel(int metadata) {
		return testForBarrelId(4, metadata);
	}

	public boolean testForAttachId(int attachToTest, int metadata) {
		for (int attachIndex = 0; attachIndex < this.attach.length; attachIndex++) {
			if (this.attach[attachIndex] == attachToTest) {
				for (int scopeIndex = (this.scopes.length + 1) * (attachIndex + 1); scopeIndex < (this.scopes.length + 1) * (attachIndex + 2); scopeIndex++) {
					for (int barrelIndex = 0; barrelIndex <= this.barrel.length; barrelIndex++) {
						if (metadata == scopeIndex + barrelIndex * this.factor) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public boolean testIfCanHaveE(int attach1) {
		for (int v1 = 0; v1 < this.attach.length; v1++) {
			if (this.attach[v1] == attach1) {
				return true;
			}
		}
		return false;
	}

	public boolean hasStraightPullBolt(int metadata) {
		return testForAttachId(1, metadata);
	}

	public boolean hasBipod(int metadata) {
		return testForAttachId(2, metadata);
	}

	public boolean hasGrip(int metadata) {
		return testForAttachId(3, metadata);
	}

	public boolean hasM320(int metadata) {
		return testForAttachId(4, metadata);
	}

	public boolean hasStrongSpiralSpring(int metadata) {
		return testForAttachId(5, metadata);
	}

	public boolean hasImprovedGrip(int metadata) {
		return testForAttachId(6, metadata);
	}

	public boolean hasLaserPointer(int metadata) {
		return testForAttachId(7, metadata);
	}

	public boolean canHaveStraightPullBolt() {
		return testIfCanHaveE(1);
	}

	public boolean canHaveBipod() {
		return testIfCanHaveE(2);
	}

	public boolean canHaveGrip() {
		return testIfCanHaveE(3);
	}

	public boolean canHaveM320() {
		return testIfCanHaveE(4);
	}

	public boolean canHaveStrongSpiralString() {
		return testIfCanHaveE(5);
	}

	public boolean canHaveImprovedGrip() {
		return testIfCanHaveE(6);
	}

	public boolean canHaveLaserPointer() {
		return testIfCanHaveE(7);
	}

	public int barrelAsMetadataFactor(int barrel1) {
		for (int v1 = 0; v1 < this.barrel.length; v1++) {
			if (this.barrel[v1] == barrel1) {
				return v1 + 1;
			}
		}
		return 0;
	}

	public int attachAsMetadataFactor(int attach1) {
		for (int v1 = 0; v1 < this.attach.length; v1++) {
			if (this.attach[v1] == attach1) {
				return v1 + 1;
			}
		}
		return 0;
	}

	public int getZoom(int metadata) {
		int v1 = metadata;

		while (v1 >= this.scopes.length + 1) {
			v1 -= this.scopes.length + 1;
		}
		if (v1 == 0) {
			return 0;
		}

		return this.scopes[(v1 - 1)];
	}

	public boolean canUseBipod(EntityPlayer entityPlayer) {
		if ((entityPlayer.isSneaking()) && (entityPlayer.motionX == 0.0D) && (entityPlayer.motionZ == 0.0D)) {
			return true;
		}

		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		this.icon = par1IconRegister.registerIcon(this.iconName + "gun");

		this.iconAttach = new IIcon[this.attach.length];

		for (int v1 = 0; v1 < this.attach.length; v1++) {
			this.iconAttach[v1] = par1IconRegister.registerIcon(this.iconName + getAttachIcon("a", this.attach[v1]));
		}

		this.iconBar = new IIcon[this.barrel.length];

		for (int v1 = 0; v1 < this.barrel.length; v1++) {
			this.iconBar[v1] = par1IconRegister.registerIcon(this.iconName + getAttachIcon("b", this.barrel[v1]));
		}

		if (this.scopes.length > 0) {
			this.iconScp = par1IconRegister.registerIcon(this.iconName + "scp");
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
			}

		}

		return string;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int par1) {
		return this.icon;
	}

	public String getName2(int metadata) {
		String front = "";
		String attatchment = "";
		String scope = "";

		if (hasSilencer(metadata)) {
			front = "-sln";
		} else if (hasHeavyBarrel(metadata)) {
			front = "-hbl";
		} else if (hasRifledBarrel(metadata)) {
			front = "-rbl";
		} else if (hasPolygonalBarrel(metadata)) {
			front = "-pbl";
		}

		if (hasStraightPullBolt(metadata)) {
			attatchment = "-spb";
		} else if (hasBipod(metadata)) {
			attatchment = "-bpd";
		} else if (hasGrip(metadata)) {
			attatchment = "-grp";
		} else if (hasM320(metadata)) {
			attatchment = "-320";
		} else if (hasStrongSpiralSpring(metadata)) {
			attatchment = "-sss";
		} else if (hasImprovedGrip(metadata)) {
			attatchment = "-img";
		} else if (hasLaserPointer(metadata)) {
			attatchment = "-ptr";
		}

		if (getZoom(metadata) > 0) {
			scope = "-scp";
		}

		return "gun" + front + attatchment + scope;
	}

	public String getName(int metadata) {
		return this.name + getName2(metadata).replace("gun", "");
	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		return getName(par1ItemStack.getItemDamage()).toLowerCase().replace(" ", "_");
	}

	public float zoomToFloat(int scope) {
		float newZoom = 1.0F;
		if (scope > 0) {
			GunCusScope scope2 = (GunCusScope) GunCus.scope.metadatas[(scope - 1)];
			newZoom = scope2.zoom;
		}

		return newZoom;
	}

	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par2List, boolean par4) {
		par2List.add(infoLine(this.pack));
		int metadata = par1ItemStack.getItemDamage();

		if (metadata > 0) {
			par2List.add(infoLine(""));

			String front = null;
			String attatchment = null;
			String scope = null;

			for (int v1 = 1; v1 <= GunCus.barrel.metadatas.length; v1++) {
				if (testForBarrelId(v1, metadata)) {
					front = GunCus.barrel.metadatas[(v1 - 1)].localized;
				}
			}

			for (int v1 = 1; v1 <= GunCus.attachment.metadatas.length; v1++) {
				if (testForAttachId(v1, metadata)) {
					attatchment = GunCus.attachment.metadatas[(v1 - 1)].localized;
				}
			}

			if (getZoom(metadata) > 0) {
				scope = GunCus.scope.metadatas[(getZoom(metadata) - 1)].localized;
			}

			if (front != null) {
				par2List.add(infoLine(front));
			}
			if (attatchment != null) {
				par2List.add(infoLine(attatchment));
			}
			if (scope != null) {
				par2List.add(infoLine(scope));
			}
		}
	}

	private String infoLine(String s) {
		return s;
	}
}
