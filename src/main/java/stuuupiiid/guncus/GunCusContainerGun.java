package stuuupiiid.guncus;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class GunCusContainerGun extends Container {
	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	private World worldObj;
	public int posX;
	public int posY;
	public int posZ;

	public GunCusContainerGun(InventoryPlayer par1InventoryPlayer, World par2World, int par3, int par4, int par5) {
		this.worldObj = par2World;
		this.posX = par3;
		this.posY = par4;
		this.posZ = par5;

		addSlotToContainer(new Slot(this.craftMatrix, 0, 80, 56));
		addSlotToContainer(new Slot(this.craftMatrix, 1, 80, 14));
		addSlotToContainer(new Slot(this.craftMatrix, 2, 59, 35));
		addSlotToContainer(new Slot(this.craftMatrix, 3, 101, 35));
		addSlotToContainer(new Slot(this.craftMatrix, 4, 80, 35));

		for (int var6 = 0; var6 < 3; var6++) {
			for (int var7 = 0; var7 < 9; var7++) {
				addSlotToContainer(new Slot(par1InventoryPlayer, var7 + var6 * 9 + 9, 8 + var7 * 18, 84 + var6 * 18));
			}
		}
		for (int var8 = 0; var8 < 9; var8++) {
			addSlotToContainer(new Slot(par1InventoryPlayer, var8, 8 + var8 * 18, 142));
		}
		onCraftMatrixChanged(this.craftMatrix);
	}

	@Override
	public void onContainerClosed(EntityPlayer par1EntityPlayer) {
		super.onContainerClosed(par1EntityPlayer);
		if (!this.worldObj.isRemote) {
			for (int var2 = 0; var2 < 9; var2++) {
				ItemStack var3 = this.craftMatrix.getStackInSlotOnClosing(var2);
				if (var3 != null) {
					par1EntityPlayer.dropPlayerItem(var3);
				}
			}
		}
	}

	public void split() {
		ItemStack down = ((Slot) this.inventorySlots.get(0)).getStack();
		ItemStack top = ((Slot) this.inventorySlots.get(1)).getStack();
		ItemStack left = ((Slot) this.inventorySlots.get(2)).getStack();
		// ItemStack right = ((Slot)this.inventorySlots.get(3)).getStack();
		ItemStack mid = ((Slot) this.inventorySlots.get(4)).getStack();

		ItemStack scope = null;
		ItemStack attatch = null;
		ItemStack extra = null;

		GunCusItemGun gun = null;

		if ((mid != null) && (mid.getItem() != null) && ((mid.getItem() instanceof GunCusItemGun))) {
			gun = (GunCusItemGun) mid.getItem();
			int metadata = mid.getItemDamage();

			int scope1 = 0;

			if (gun.getZoom(metadata) > 0) {
				scope1 = gun.getZoom(metadata);
			}

			if (scope1 > 0) {
				scope = new ItemStack(GunCus.scope, 1, scope1 - 1);
			}

			int extra1 = 0;

			for (int v1 = 1; v1 <= GunCus.attachment.metadatas.length; v1++) {
				if (gun.testForAttachId(v1, metadata)) {
					extra1 = v1;
					break;
				}
			}

			if (extra1 > 0) {
				extra = new ItemStack(GunCus.attachment, 1, extra1 - 1);
			}

			int bar1 = 0;

			for (int v1 = 1; v1 <= GunCus.barrel.metadatas.length; v1++) {
				if (gun.testForBarrelId(v1, metadata)) {
					bar1 = v1;
					break;
				}
			}

			if (bar1 > 0) {
				attatch = new ItemStack(GunCus.barrel, 1, bar1 - 1);
			}

			int extra2 = gun.attachAsMetadataFactor(extra1);
			int bar2 = gun.barrelAsMetadataFactor(bar1);

			if (top == null) {
				metadata -= scope1;
				((Slot) this.inventorySlots.get(1)).putStack(scope);
				((Slot) this.inventorySlots.get(4)).putStack(new ItemStack(gun, 1, metadata));
			}
			if (left == null) {
				metadata -= bar2 * gun.factor;
				((Slot) this.inventorySlots.get(2)).putStack(attatch);
				((Slot) this.inventorySlots.get(4)).putStack(new ItemStack(gun, 1, metadata));
			}
			if (down == null) {
				metadata -= extra2 * (gun.scopes.length + 1);
				((Slot) this.inventorySlots.get(0)).putStack(extra);
				((Slot) this.inventorySlots.get(4)).putStack(new ItemStack(gun, 1, metadata));
			}
		}
	}

	public void build() {
		ItemStack down = ((Slot) this.inventorySlots.get(0)).getStack();
		ItemStack top = ((Slot) this.inventorySlots.get(1)).getStack();
		ItemStack left = ((Slot) this.inventorySlots.get(2)).getStack();
		// ItemStack right = ((Slot)this.inventorySlots.get(3)).getStack();
		ItemStack mid = ((Slot) this.inventorySlots.get(4)).getStack();

		int scope = 0;

		if (top != null) {
			if (top.getItem() != null) {
				int scope1 = top.getItem().itemID;

				if (scope1 == GunCus.scope.itemID) {
					int scopeMeta = top.getItemDamage();

					scope = scopeMeta + 1;
				}
			}
		}

		int bar1 = 0;

		if (left != null) {
			if (left.getItem() != null) {
				int ex = left.getItem().itemID;

				if (ex == GunCus.barrel.itemID) {
					int attaMeta = left.getItemDamage();
					bar1 = attaMeta + 1;
				}
			}
		}

		int bar = 0;

		int extra1 = 0;

		if (down != null) {
			if (down.getItem() != null) {
				int ex = down.getItem().itemID;

				if (ex == GunCus.attachment.itemID) {
					int attaMeta = down.getItemDamage();

					extra1 = attaMeta + 1;
				}
			}
		}

		int extra = 0;
		GunCusItemGun gun = null;

		if (mid != null && (mid.getItem() != null) && (mid.getItem() instanceof GunCusItemGun)) {
			gun = (GunCusItemGun) mid.getItem();

			extra = gun.attachAsMetadataFactor(extra1);
			bar = gun.barrelAsMetadataFactor(bar1);

			int metadata = mid.getItemDamage();

			if ((scope > 0) && (gun.getZoom(metadata) <= 0) && (gun.testIfCanHaveScope(scope))) {
				metadata += scope;
				((Slot) this.inventorySlots.get(1)).decrStackSize(1);
				((Slot) this.inventorySlots.get(4)).putStack(new ItemStack(gun, 1, metadata));
			}
			if ((extra > 0) && (gun.attatchmentFree(metadata))) {
				metadata += extra * (gun.scopes.length + 1);
				((Slot) this.inventorySlots.get(0)).decrStackSize(1);
				((Slot) this.inventorySlots.get(4)).putStack(new ItemStack(gun, 1, metadata));
			}
			if ((bar > 0) && (gun.barrelFree(metadata))) {
				metadata += bar * gun.factor;
				((Slot) this.inventorySlots.get(2)).decrStackSize(1);
				((Slot) this.inventorySlots.get(4)).putStack(new ItemStack(gun, 1, metadata));
			}
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
		return this.worldObj.getBlockId(this.posX, this.posY, this.posZ) == GunCus.blockGun.blockID;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
		return null;
	}
}
