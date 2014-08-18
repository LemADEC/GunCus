package stuuupiiid.guncus;

import java.util.HashMap;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class GunCusContainerAmmoMan extends Container {
	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	private World worldObj;
	public int posX;
	public int posY;
	public int posZ;

	public GunCusContainerAmmoMan(InventoryPlayer par1InventoryPlayer, World par2World, int par3, int par4, int par5) {
		this.worldObj = par2World;
		this.posX = par3;
		this.posY = par4;
		this.posZ = par5;

		addSlotToContainer(new Slot(this.craftMatrix, 0, 59, 35));
		addSlotToContainer(new Slot(this.craftMatrix, 1, 101, 35));

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

	public void fill() {
		ItemStack mag = ((Slot) this.inventorySlots.get(0)).getStack();
		ItemStack ammo = ((Slot) this.inventorySlots.get(1)).getStack();

		if ((mag != null) && (ammo != null) && (mag.getItem() != null) && ((mag.getItem() instanceof GunCusItemMag))
				&& (ammo.getItem() != null) && ((ammo.getItem() instanceof GunCusItemBullet))
				&& (mag.getItemDamage() > 0)) {
			GunCusItemMag mag1 = (GunCusItemMag) mag.getItem();
			int bulletType = mag1.bulletType;
			GunCusItemBullet bullet = (GunCusItemBullet) ammo.getItem();

			if (bulletType == bullet.bulletType) {
				int damage = mag.getItemDamage();
				int size = ammo.stackSize;
				size--;
				damage--;

				((Slot) this.inventorySlots.get(0)).putStack(new ItemStack(mag1, 1, damage));

				if (size > 0) {
					((Slot) this.inventorySlots.get(1)).putStack(new ItemStack(bullet, size));
				} else {
					((Slot) this.inventorySlots.get(1)).putStack(null);
				}
			}
		}
	}

	public void empty() {
		ItemStack mag = ((Slot) this.inventorySlots.get(0)).getStack();
		ItemStack ammo = ((Slot) this.inventorySlots.get(1)).getStack();

		if ((mag != null)
				&& (mag.getItem() != null)
				&& ((mag.getItem() instanceof GunCusItemMag))
				&& ((ammo == null) || ((ammo != null) && (ammo.getItem() != null) && ((ammo.getItem() instanceof GunCusItemBullet))))
				&& (mag.getItemDamage() < mag.getMaxDamage())) {
			GunCusItemMag mag1 = (GunCusItemMag) mag.getItem();
			int bulletType = mag1.bulletType;
			int bulletType2 = bulletType;
			GunCusItemBullet bullet = null;

			if (ammo != null) {
				bullet = (GunCusItemBullet) ammo.getItem();
				bulletType2 = bullet.bulletType;
			}

			if (bulletType == bulletType2) {
				int damage = mag.getItemDamage();
				int size = 0;
				if (ammo != null) {
					size = ammo.stackSize;
				}

				size++;
				damage++;

				if (bullet == null) {
					bullet = (GunCusItemBullet) ((List) GunCusItemBullet.bulletsList.get(mag1.pack)).get(bulletType);
				}

				((Slot) this.inventorySlots.get(0)).putStack(new ItemStack(mag1, 1, damage));

				if (size > 0) {
					((Slot) this.inventorySlots.get(1)).putStack(new ItemStack(bullet, size));
				}
			}
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
		return null;
	}
}
