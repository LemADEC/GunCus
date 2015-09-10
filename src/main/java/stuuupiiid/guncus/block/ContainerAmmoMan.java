package stuuupiiid.guncus.block;

import stuuupiiid.guncus.item.ItemBullet;
import stuuupiiid.guncus.item.ItemMag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ContainerAmmoMan extends Container {
	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	private World worldObj;
	public int posX;
	public int posY;
	public int posZ;
	
	public ContainerAmmoMan(InventoryPlayer par1InventoryPlayer, World world, int x, int y, int z) {
		this.worldObj = world;
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		
		addSlotToContainer(new Slot(craftMatrix, 0, 59, 35));
		addSlotToContainer(new Slot(craftMatrix, 1, 101, 35));
		
		for (int rowIndex = 0; rowIndex < 3; rowIndex++) {
			for (int columnIndex = 0; columnIndex < 9; columnIndex++) {
				addSlotToContainer(new Slot(par1InventoryPlayer, columnIndex + rowIndex * 9 + 9, 8 + columnIndex * 18, 84 + rowIndex * 18));
			}
		}
		for (int columnIndex = 0; columnIndex < 9; columnIndex++) {
			addSlotToContainer(new Slot(par1InventoryPlayer, columnIndex, 8 + columnIndex * 18, 142));
		}
		onCraftMatrixChanged(craftMatrix);
	}
	
	@Override
	public void onContainerClosed(EntityPlayer par1EntityPlayer) {
		super.onContainerClosed(par1EntityPlayer);
		if (!this.worldObj.isRemote) {
			for (int var2 = 0; var2 < 9; var2++) {
				ItemStack var3 = craftMatrix.getStackInSlotOnClosing(var2);
				if (var3 != null) {
					par1EntityPlayer.dropItem(var3.getItem(), var3.stackSize);
				}
			}
		}
	}
	
	public void fill() {
		ItemStack mag = ((Slot) inventorySlots.get(0)).getStack();
		ItemStack ammo = ((Slot) inventorySlots.get(1)).getStack();
		
		if ( (mag != null) && (mag.getItem() != null) && (mag.getItem() instanceof ItemMag)
		  && (ammo != null) && (ammo.getItem() != null) && (ammo.getItem() instanceof ItemBullet)
		  && (mag.getItemDamage() > 0)) {
			ItemMag mag1 = (ItemMag) mag.getItem();
			int bulletType = mag1.bulletType;
			ItemBullet bullet = (ItemBullet) ammo.getItem();
			
			if (bulletType == bullet.bulletType) {
				int damage = mag.getItemDamage();
				int size = ammo.stackSize;
				size--;
				damage--;
				
				((Slot) inventorySlots.get(0)).putStack(new ItemStack(mag1, 1, damage));
				
				if (size > 0) {
					((Slot) inventorySlots.get(1)).putStack(new ItemStack(bullet, size));
				} else {
					((Slot) inventorySlots.get(1)).putStack(null);
				}
			}
		}
	}
	
	public void empty() {
		ItemStack mag = ((Slot) inventorySlots.get(0)).getStack();
		ItemStack ammo = ((Slot) inventorySlots.get(1)).getStack();
		
		if ( (mag != null) && (mag.getItem() != null) && (mag.getItem() instanceof ItemMag)
		  && ((ammo == null) || ((ammo.getItem() != null) && (ammo.getItem() instanceof ItemBullet)))
		  && (mag.getItemDamage() < mag.getMaxDamage())) {
			ItemMag mag1 = (ItemMag) mag.getItem();
			int bulletType = mag1.bulletType;
			int bulletType2 = bulletType;
			ItemBullet bullet = null;
			
			if (ammo != null) {
				bullet = (ItemBullet) ammo.getItem();
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
					bullet = ItemBullet.bulletsList.get(mag1.pack).get(bulletType);
				}
				
				((Slot) inventorySlots.get(0)).putStack(new ItemStack(mag1, 1, damage));
				
				if (size > 0) {
					((Slot) inventorySlots.get(1)).putStack(new ItemStack(bullet, size));
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
