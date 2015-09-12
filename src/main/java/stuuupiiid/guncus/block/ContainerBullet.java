package stuuupiiid.guncus.block;

import java.util.List;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.item.ItemBullet;
import stuuupiiid.guncus.item.ItemMag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ContainerBullet extends Container {
	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	private World worldObj;
	public int posX;
	public int posY;
	public int posZ;

	public ContainerBullet(InventoryPlayer par1InventoryPlayer, World par2World, int par3, int par4, int par5) {
		this.worldObj = par2World;
		this.posX = par3;
		this.posY = par4;
		this.posZ = par5;

		addSlotToContainer(new Slot(craftMatrix, 0, 80, 14));
		addSlotToContainer(new Slot(craftMatrix, 1, 59, 35));
		addSlotToContainer(new Slot(craftMatrix, 2, 101, 35));
		addSlotToContainer(new Slot(craftMatrix, 3, 80, 35));

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

	public void create() {
		ItemStack down = ((Slot) inventorySlots.get(0)).getStack();
		ItemStack left = ((Slot) inventorySlots.get(1)).getStack();
		ItemStack right = ((Slot) inventorySlots.get(2)).getStack();
		ItemStack mid = ((Slot) inventorySlots.get(3)).getStack();

		ItemMag mag = null;
		ItemBullet bullet = null;
		int iron = 0;
		int sulphur = 0;

		if ((down != null) && (down.getItem() != null) && ((down.getItem() instanceof ItemMag))) {
			mag = (ItemMag) down.getItem();
			bullet = (ItemBullet) ((List) ItemBullet.bulletsList.get(mag.pack)).get(mag.bulletType);

			iron = bullet.iron;
			sulphur = bullet.sulphur;
			if ((sulphur >= 0)
					&& (iron >= 0)
					&& ((sulphur > 0) || (iron > 0))
					&& (left != null)
					&& (left.getItem() != null)
					&& (left.getItem() == Items.iron_ingot)
					&& (mid != null)
					&& (mid.getItem() != null)
					&& (mid.getItem() == Items.gunpowder)
					&& ((right == null) || ((right.getItem() != null)
							&& (right.getItem() == bullet) && (right.stackSize + bullet.stackOnCreate <= 64)))
					&& (bullet.stackOnCreate >= 1) && (left.stackSize >= iron) && (mid.stackSize >= sulphur)) {
				int sizeIr = left.stackSize;
				int sizeSu = mid.stackSize;

				sizeIr -= iron;
				sizeSu -= sulphur;

				int size = bullet.stackOnCreate
						+ ((right != null) && (right.getItem() != null) && (right.stackSize > 0) ? right.stackSize : 0);

				((Slot) inventorySlots.get(1)).putStack(new ItemStack(Items.iron_ingot, sizeIr));
				((Slot) inventorySlots.get(3)).putStack(new ItemStack(Items.gunpowder, sizeSu));
				((Slot) inventorySlots.get(2)).putStack(new ItemStack(bullet, size));
			}
		}
	}

	public String[] info() {
		ItemStack down = ((Slot) this.inventorySlots.get(0)).getStack();

		ItemMag gun = null;
		String rtn = "Oops! Something went wrong!";
		String rtn2 = null;

		if ((down != null) && (down.getItem() != null) && ((down.getItem() instanceof ItemMag))) {
			gun = (ItemMag) down.getItem();
		}

		if (gun != null) {
			ItemBullet bullet = ItemBullet.bulletsList.get(gun.pack).get(gun.bulletType);

			String name = "<none>";
			int sulphur = -1;
			int iron = -1;

			if (bullet != null) {
				ItemStack itemStackBullet = new ItemStack(bullet);
				name = itemStackBullet.getDisplayName();
				sulphur = bullet.sulphur;
				iron = bullet.iron;
			}

			if ((iron >= 0) && (sulphur >= 0) && ((iron > 0) || (sulphur > 0))) {
				rtn = "Gun = \"" + gun.gunName + "\", Bullets = \"" + name + "\", Pack = \"" + gun.pack + "\"";
				rtn2 = "-> " + (iron > 0 ? iron + " iron ingot" + (iron > 1 ? "s, " : ", ") : "")
						+ (sulphur > 0 ? sulphur + " gunpowder" : "");
			}
		}
		return new String[] { rtn, rtn2 };
	}

	@Override
	public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
		return this.worldObj.getBlock(posX, posY, posZ) == GunCus.blockBullet;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
		return null;
	}
}
