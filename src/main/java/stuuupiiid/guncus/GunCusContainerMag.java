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

public class GunCusContainerMag extends Container {
	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	private World worldObj;
	public int posX;
	public int posY;
	public int posZ;

	public GunCusContainerMag(InventoryPlayer par1InventoryPlayer, World par2World, int par3, int par4, int par5) {
		this.worldObj = par2World;
		this.posX = par3;
		this.posY = par4;
		this.posZ = par5;

		addSlotToContainer(new Slot(this.craftMatrix, 0, 80, 14));
		addSlotToContainer(new Slot(this.craftMatrix, 1, 59, 35));
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
				if ((var3 != null) && (par1EntityPlayer != null)) {
					par1EntityPlayer.dropPlayerItem(var3);
				}
			}
		}
	}

	public void create() {
		ItemStack down = ((Slot) this.inventorySlots.get(0)).getStack();
		ItemStack left = ((Slot) this.inventorySlots.get(1)).getStack();
		ItemStack right = ((Slot) this.inventorySlots.get(2)).getStack();

		GunCusItemGun gun = null;
		int ingotsRequired = 0;

		if ((down != null) && (down.getItem() != null) && ((down.getItem() instanceof GunCusItemGun))) {
			gun = (GunCusItemGun) down.getItem();
			ingotsRequired = gun.ingotsMag;
		}

		if ((gun != null) && (left != null) && (left.getItem() != null)
				&& (left.getItem().itemID == Item.ingotIron.itemID) && (right == null) && (ingotsRequired > 0)
				&& (left.stackSize >= ingotsRequired)) {
			int stackSize = left.stackSize;

			stackSize -= ingotsRequired;

			((Slot) this.inventorySlots.get(1)).putStack(new ItemStack(Item.ingotIron, stackSize));
			((Slot) this.inventorySlots.get(2)).putStack(new ItemStack(Item.itemsList[gun.magId], 1,
					Item.itemsList[gun.magId].getMaxDamage()));
		}
	}

	public String[] info() {
		ItemStack down = ((Slot) this.inventorySlots.get(0)).getStack();

		GunCusItemGun gun = null;
		String rtn = "Oops! Something went wrong!";
		String rtn2 = null;

		if ((down != null) && (down.getItem() != null) && ((down.getItem() instanceof GunCusItemGun))) {
			gun = (GunCusItemGun) down.getItem();
		}

		if (gun != null) {
			int iron = gun.ingotsMag;
			rtn = "Gun = \"" + gun.name + "\", Pack = \"" + gun.pack + "\"";
			rtn2 = "-> " + (iron > 0 ? iron + " iron ingot" + (iron > 1 ? "s " : " ") : "");
		}
		return new String[] { rtn, rtn2 };
	}

	@Override
	public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
		return this.worldObj.getBlockId(this.posX, this.posY, this.posZ) == GunCus.blockMag.blockID;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
		return null;
	}
}
