package stuuupiiid.guncus.block;

import cpw.mods.fml.common.FMLCommonHandler;
import stuuupiiid.guncus.item.ItemBullet;
import stuuupiiid.guncus.item.ItemMag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerAmmoMan extends Container {
	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	
	public ContainerAmmoMan(InventoryPlayer inventoryPlayer) {
		addSlotToContainer(new Slot(craftMatrix, 0, 59, 35));
		addSlotToContainer(new Slot(craftMatrix, 1, 101, 35));
		
		for (int rowIndex = 0; rowIndex < 3; rowIndex++) {
			for (int columnIndex = 0; columnIndex < 9; columnIndex++) {
				addSlotToContainer(new Slot(inventoryPlayer, columnIndex + rowIndex * 9 + 9, 8 + columnIndex * 18, 84 + rowIndex * 18));
			}
		}
		for (int columnIndex = 0; columnIndex < 9; columnIndex++) {
			addSlotToContainer(new Slot(inventoryPlayer, columnIndex, 8 + columnIndex * 18, 142));
		}
		onCraftMatrixChanged(craftMatrix);
	}
	
	@Override
	public void onContainerClosed(EntityPlayer entityPlayer) {
		super.onContainerClosed(entityPlayer);
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			return;
		}
		for (int slotIndex = 0; slotIndex < 9; slotIndex++) {
			ItemStack itemStackSlot = craftMatrix.getStackInSlotOnClosing(slotIndex);
			if ((itemStackSlot != null) && (entityPlayer != null)) {
				entityPlayer.entityDropItem(itemStackSlot, 0.5F);
			}
		}
	}
	
	public void fill() {
		ItemStack itemStackMag = ((Slot) inventorySlots.get(0)).getStack();
		ItemStack itemStackAmmo = ((Slot) inventorySlots.get(1)).getStack();
		
		if ( (itemStackMag != null) && (itemStackMag.getItem() instanceof ItemMag)
		  && (itemStackAmmo != null) && (itemStackAmmo.getItem() instanceof ItemBullet)
		  && (itemStackMag.getItemDamage() > 0)) {
			ItemMag itemMag = (ItemMag) itemStackMag.getItem();
			int magBulletId = itemMag.bulletId;
			ItemBullet ammoItemBullet = (ItemBullet) itemStackAmmo.getItem();
			
			if (magBulletId == ammoItemBullet.bulletId && itemMag.pack.equals(ammoItemBullet.pack)) {
				int damage = itemStackMag.getItemDamage();
				int size = itemStackAmmo.stackSize;
				size--;
				damage--;
				
				((Slot) inventorySlots.get(0)).putStack(new ItemStack(itemMag, 1, damage));
				
				if (size > 0) {
					((Slot) inventorySlots.get(1)).putStack(new ItemStack(ammoItemBullet, size));
				} else {
					((Slot) inventorySlots.get(1)).putStack(null);
				}
			}
		}
	}
	
	public void empty() {
		ItemStack itemStackMag = ((Slot) inventorySlots.get(0)).getStack();
		ItemStack itemStackAmmo = ((Slot) inventorySlots.get(1)).getStack();
		
		if ( (itemStackMag != null) && (itemStackMag.getItem() instanceof ItemMag)
		  && ((itemStackAmmo == null) || (itemStackAmmo.getItem() instanceof ItemBullet))
		  && (itemStackMag.getItemDamage() < itemStackMag.getMaxDamage()) ) {
			ItemMag itemMag = (ItemMag) itemStackMag.getItem();
			int magBulletId = itemMag.bulletId;
			int ammoBulletId = magBulletId;
			ItemBullet ammoItemBullet = null;
			
			if (itemStackAmmo != null) {
				ammoItemBullet = (ItemBullet) itemStackAmmo.getItem();
				ammoBulletId = ammoItemBullet.bulletId;
			}
			
			if (magBulletId == ammoBulletId && (ammoItemBullet == null || ammoItemBullet.pack.equals(itemMag.pack))) {
				int damage = itemStackMag.getItemDamage();
				int size = 0;
				if (itemStackAmmo != null) {
					size = itemStackAmmo.stackSize;
				}
				
				size++;
				damage++;
				
				if (ammoItemBullet == null) {
					ammoItemBullet = ItemBullet.bullets.get(itemMag.pack).get(magBulletId);
				}
				
				((Slot) inventorySlots.get(0)).putStack(new ItemStack(itemMag, 1, damage));
				
				if (size > 0) {
					((Slot) inventorySlots.get(1)).putStack(new ItemStack(ammoItemBullet, size));
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
