package stuuupiiid.guncus.block;

import java.util.List;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.item.ItemBullet;
import stuuupiiid.guncus.item.ItemMag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ContainerAmmo extends Container {
	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	private World worldObj;
	public int posX;
	public int posY;
	public int posZ;

	public ContainerAmmo(InventoryPlayer par1InventoryPlayer, World par2World, int par3, int par4, int par5) {
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
					par1EntityPlayer.dropItem(var3.getItem(), var3.stackSize);
				}
			}
		}
	}

	public void fill() {
		ItemStack mag = ((Slot) this.inventorySlots.get(0)).getStack();
		ItemStack ammo = ((Slot) this.inventorySlots.get(1)).getStack();

		if ((mag != null) && (ammo != null) && (mag.getItem() != null) && ((mag.getItem() instanceof ItemMag))
				&& (ammo.getItem() != null) && ((ammo.getItem() instanceof ItemBullet))
				&& (mag.getItemDamage() > 0)) {
			ItemMag mag1 = (ItemMag) mag.getItem();
			int bulletType = mag1.bulletType;
			String pack = mag1.pack;
			ItemBullet bullet = (ItemBullet) ammo.getItem();

			if ((bulletType == bullet.bulletType) && (pack.equals(bullet.pack))) {
				int damage = mag.getItemDamage();
				int size = ammo.stackSize;
				for (; (size > 0) && (damage > 0); size--) {
					damage--;
				}

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
				&& ((mag.getItem() instanceof ItemMag))
				&& ((ammo == null) || ((ammo != null) && (ammo.getItem() != null) && ((ammo.getItem() instanceof ItemBullet))))
				&& (mag.getItemDamage() < mag.getMaxDamage())) {
			ItemMag mag1 = (ItemMag) mag.getItem();
			int bulletType = mag1.bulletType;
			int bulletType2 = bulletType;
			String pack = mag1.pack;
			String pack2 = pack;
			ItemBullet bullet = null;

			if (ammo != null) {
				bullet = (ItemBullet) ammo.getItem();
				bulletType2 = bullet.bulletType;
				pack2 = bullet.pack;
			}

			if ((bulletType == bulletType2) && (pack.equals(pack2))) {
				int damage = mag.getItemDamage();
				int size = 0;
				if (ammo != null)
					for (size = ammo.stackSize; (size < 64) && (damage < mag.getMaxDamage()); size++) {
						damage++;
					}

				if (bullet == null) {
					bullet = (ItemBullet) ((List) ItemBullet.bulletsList.get(mag1.pack)).get(bulletType);
				}

				((Slot) this.inventorySlots.get(0)).putStack(new ItemStack(mag1, 1, damage));

				if (size > 0) {
					try {
						((Slot) this.inventorySlots.get(1)).putStack(new ItemStack(bullet, size));
					} catch (NullPointerException e) {
					}
				}
			}
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
		return worldObj.getBlock(posX, posY, posZ) == GunCus.blockAmmo;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
		return null;
	}
}