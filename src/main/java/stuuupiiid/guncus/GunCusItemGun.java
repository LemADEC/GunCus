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

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
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
	protected Icon icon;
	protected Icon[] iconAttach;
	protected Icon[] iconBar;
	protected Icon iconScp;
	public int magId = 0;
	public int ingotsMag;
	public int ingots;
	public int field_77767_aC;
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
	public String soundN;
	public String soundS;
	public int damage;

	public GunCusItemGun(int id, int damage, int shootType, int delay, String name, String iconName, int magSize,
			int magId, int bulletType, int ingotsMag, int ingots, int redstone, String pack, boolean isOfficial,
			int[] attach, int[] barrel, int[] scopes, boolean noMag, int[] bullets) {
		super(id);
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			MinecraftForgeClient.registerItemRenderer(this.itemID, new GunCusInvRenderer());
		this.isOfficial = isOfficial;
		this.damage = damage;
		setHasSubtypes(true);
		this.maxStackSize = 1;
		setFull3D();
		this.shootType = shootType;
		this.actualType = shootType;
		this.delay = delay;
		setMaxDamage(0);
		this.name = name;
		this.iconName = iconName;
		this.ingots = ingots;
		this.field_77767_aC = redstone;
		this.pack = pack;
		this.recModify = 1.0D;
		this.soundModify = 1.0D;

		this.attach = attach;
		this.barrel = barrel;
		this.scopes = scopes;

		this.factor = ((this.attach.length + 1) * (this.scopes.length + 1));

		if (canHaveStraightPullBolt()) {
			this.soundN = "guncus:shoot_sniper";
			this.shootType = 0;
			this.actualType = 0;
		} else {
			this.soundN = "guncus:shoot_normal";
		}
		this.soundS = "guncus:shoot_silenced";

		GunCusCreativeTab tab = new GunCusCreativeTab(name, this.itemID);
		setCreativeTab(tab);

		this.actualBullet = 0;

		if (noMag) {
			this.bullets = bullets;
			this.magId = -1;
		} else {
			Item mag = new GunCusItemMag(magId, name, getName(0), magSize, iconName, bulletType, pack);
			this.magId = mag.itemID;
			this.ingotsMag = ingotsMag;
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
		this.soundN = sound;
		return this;
	}

	public GunCusItemGun setSLNSound(String sound) {
		this.soundS = sound;
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
		ItemStack mag = null;

		if (this.magId != -1) {
			// search for a damaged magazine first
			for (int v1 = 0; v1 < entityPlayer.inventory.getSizeInventory(); v1++) {
				mag = entityPlayer.inventory.getStackInSlot(v1);
				if ((mag != null)
						&& (mag.getItem().itemID == this.magId)
						&& (mag.isItemDamaged())
						&& (mag.getItemDamage() < mag.getMaxDamage())) {
					break;
				}
				mag = null;
			}

			if (mag == null) {
				// search for a full magazine
				for (int v1 = 0; v1 < entityPlayer.inventory.getSizeInventory(); v1++) {
					mag = entityPlayer.inventory.getStackInSlot(v1);
					if ((mag != null)
							&& (mag.getItem().itemID == this.magId)
							&& (!mag.isItemDamaged())) {
						break;
					}
					mag = null;
				}
			}
		}

		if ((GunCus.shootTime <= 0)
				&& (Mouse.isButtonDown(0))
				&& ((client.currentScreen == null) || (Mouse.isButtonDown(1)))
				&& ((entityPlayer.inventory.hasItem(GunCus.ammoM320.itemID)) || (entityPlayer.capabilities.isCreativeMode))
				&& (this.tubing)) {
			GunCus.shootTime += 95;
			tube(entityPlayer);
			recoilTube(entityPlayer);
			Minecraft.getMinecraft().sndManager.playSoundFX("guncus:reload_tube", 1.0F, 1.0F);
		}
		if ((GunCus.shootTime <= 0)
				&& (Mouse.isButtonDown(0))
				&& (!this.shot)
				&& ((client.currentScreen == null) || (Mouse.isButtonDown(1)))
				&& ( (mag != null) || (entityPlayer.capabilities.isCreativeMode)
				  || ((this.bullets != null)
					&& (entityPlayer.inventory.hasItem( (GunCusItemBullet.bulletsList.get(this.pack)).get(this.bullets[this.actualBullet]).itemID ))))
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
			if (this.magId != -1) {
				GunCusItemMag mag2 = (GunCusItemMag) Item.itemsList[this.magId];
				bulletItem = GunCusItemBullet.bulletsList.get(this.pack).get(mag2.bulletType);
			} else {
				bulletItem = GunCusItemBullet.bulletsList.get(this.pack).get(this.bullets[this.actualBullet]);
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
			this.actualBullet += 1;
			if (this.actualBullet >= this.bullets.length) {
				this.actualBullet = 0;
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
		if (this.magId == -1) {
			bytes.writeInt(this.bullets[this.actualBullet]);
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
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
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
	public void registerIcons(IconRegister par1IconRegister) {
		this.icon = par1IconRegister.registerIcon(this.iconName + "gun");

		this.iconAttach = new Icon[this.attach.length];

		for (int v1 = 0; v1 < this.attach.length; v1++) {
			this.iconAttach[v1] = par1IconRegister.registerIcon(this.iconName + getAttachIcon("a", this.attach[v1]));
		}

		this.iconBar = new Icon[this.barrel.length];

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
	public Icon getIconFromDamage(int par1) {
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
