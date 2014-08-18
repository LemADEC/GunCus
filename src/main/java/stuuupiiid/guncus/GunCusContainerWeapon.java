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

public class GunCusContainerWeapon extends Container {
	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	private World worldObj;
	public int posX;
	public int posY;
	public int posZ;
	public int actualItemID;
	public int actual;

	public GunCusContainerWeapon(InventoryPlayer par1InventoryPlayer, World par2World, int par3, int par4, int par5) {
		this.actual = 0;
		this.actualItemID = 0;
		if (GunCusItemGun.gunList.size() > 0) {
			this.actualItemID = GunCusItemGun.gunList.get(0).itemID;
		}

		this.worldObj = par2World;
		this.posX = par3;
		this.posY = par4;
		this.posZ = par5;

		addSlotToContainer(new Slot(this.craftMatrix, 0, 59, 35));
		addSlotToContainer(new Slot(this.craftMatrix, 1, 80, 35));
		addSlotToContainer(new Slot(this.craftMatrix, 2, 101, 35));

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

	public String[] info() {
		if (GunCusItemGun.gunList.size() > 0) {
			GunCusItemGun gun = (GunCusItemGun) Item.itemsList[this.actualItemID];

			String rtn = "Oops! Something went wrong!";
			String rtn2 = null;

			if (gun != null) {
				int iron = gun.ingots;
				int redst = gun.field_77767_aC;
				rtn = "Gun = \"" + gun.name + "\", Pack = \"" + gun.pack + "\"";
				rtn2 = "-> " + (iron > 0 ? iron + " iron ingot" + (iron > 1 ? "s, " : ", ") : "")
						+ (redst > 0 ? redst + " redstone" : "");
			}
			return new String[] { rtn, rtn2 };
		}
		return new String[] { "Oops! Something went wrong!", null };
	}

	public void create() {
		if (GunCusItemGun.gunList.size() > 0) {
			ItemStack ir = ((Slot) this.inventorySlots.get(0)).getStack();
			ItemStack re = ((Slot) this.inventorySlots.get(1)).getStack();

			GunCusItemGun gun = (GunCusItemGun) Item.itemsList[this.actualItemID];

			if (gun != null) {
				int reqIr = gun.ingots;
				int reqRe = gun.field_77767_aC;

				if (((ir != null) && (ir.stackSize >= reqIr) && (ir.getItem().itemID == Item.ingotIron.itemID))
						|| ((reqIr <= 0) && (((re != null) && (re.stackSize >= reqRe) && (re.getItem().itemID == Item.redstone.itemID)) || (reqRe <= 0)))) {
					((Slot) this.inventorySlots.get(2)).putStack(new ItemStack(gun, 1, 0));
					if (ir != null) {
						((Slot) this.inventorySlots.get(0)).decrStackSize(reqIr);
					}
					if (re != null) {
						((Slot) this.inventorySlots.get(1)).decrStackSize(reqRe);
					}
				}
			}
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
		return this.worldObj.getBlockId(this.posX, this.posY, this.posZ) == GunCus.blockWeapon.blockID;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
		return null;
	}
}
