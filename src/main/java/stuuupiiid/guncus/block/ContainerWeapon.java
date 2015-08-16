package stuuupiiid.guncus.block;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.item.ItemGun;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ContainerWeapon extends Container {
	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	private World worldObj;
	public int posX;
	public int posY;
	public int posZ;
	public ItemGun actualGunItem;
	public int actualGunIndex;

	public ContainerWeapon(InventoryPlayer par1InventoryPlayer, World par2World, int par3, int par4, int par5) {
		actualGunIndex = 0;
		actualGunItem = null;
		if (GunCus.instance.guns.size() > 0) {
			actualGunItem = GunCus.instance.guns.get(0);
		}

		worldObj = par2World;
		posX = par3;
		posY = par4;
		posZ = par5;

		addSlotToContainer(new Slot(craftMatrix, 0, 59, 35));
		addSlotToContainer(new Slot(craftMatrix, 1, 80, 35));
		addSlotToContainer(new Slot(craftMatrix, 2, 101, 35));

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
					par1EntityPlayer.dropItem(var3.getItem(), var3.stackSize);
				}
			}
		}
	}

	public String[] info() {
		if (GunCus.instance.guns.size() > 0) {
			String rtn = "Oops! Something went wrong!";
			String rtn2 = null;

			if (actualGunItem != null) {
				int iron = actualGunItem.ingots;
				int redst = actualGunItem.field_redstone;
				rtn = "Gun = \"" + actualGunItem.name + "\", Pack = \"" + actualGunItem.pack + "\"";
				rtn2 = "-> " + (iron > 0 ? iron + " iron ingot" + (iron > 1 ? "s, " : ", ") : "")
						+ (redst > 0 ? redst + " redstone" : "");
			}
			return new String[] { rtn, rtn2 };
		}
		return new String[] { "Oops! Something went wrong!", null };
	}

	public void create() {
		if (GunCus.instance.guns.size() > 0) {
			ItemStack ir = ((Slot) this.inventorySlots.get(0)).getStack();
			ItemStack re = ((Slot) this.inventorySlots.get(1)).getStack();

			if (actualGunItem != null) {
				int reqIr = actualGunItem.ingots;
				int reqRe = actualGunItem.field_redstone;

				if (((ir != null) && (ir.stackSize >= reqIr) && (ir.getItem() == Items.iron_ingot))
						|| ((reqIr <= 0) && (((re != null) && (re.stackSize >= reqRe) && (re.getItem() == Items.redstone)) || (reqRe <= 0)))) {
					((Slot) this.inventorySlots.get(2)).putStack(new ItemStack(actualGunItem, 1, 0));
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
		return this.worldObj.getBlock(posX, posY, posZ) == GunCus.blockWeapon;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
		return null;
	}
}
