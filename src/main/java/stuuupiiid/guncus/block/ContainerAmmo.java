package stuuupiiid.guncus.block;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.item.ItemBullet;
import stuuupiiid.guncus.item.ItemMag;
import net.minecraft.entity.item.EntityItem;
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
	
	public ContainerAmmo(InventoryPlayer inventoryPlayer, World world, int x, int y, int z) {
		this.worldObj = world;
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		
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
		if (entityPlayer != null && entityPlayer.worldObj.isRemote) {
			return;
		}
		for (int slotIndex = 0; slotIndex < 9; slotIndex++) {
			ItemStack itemStackSlot = craftMatrix.getStackInSlotOnClosing(slotIndex);
			if (itemStackSlot != null) {
				if (entityPlayer != null) {
					entityPlayer.entityDropItem(itemStackSlot, 0.5F);
				} else {
					EntityItem entityItem = new EntityItem(worldObj, posX, posY + 0.5F, posZ, itemStackSlot);
					entityItem.delayBeforeCanPickup = 10;
					worldObj.spawnEntityInWorld(entityItem);
				}
			}
		}
	}
	
	public void fill() {
		ItemStack itemStackMag = ((Slot) inventorySlots.get(0)).getStack();
		ItemStack itemStackAmmo = ((Slot) inventorySlots.get(1)).getStack();
		
		if ( (itemStackMag != null) && (itemStackMag.getItem() instanceof ItemMag)
		  && (itemStackAmmo != null) && (itemStackAmmo.getItem() instanceof ItemBullet)
		  && (itemStackMag.getItemDamage() > 0) ) {
			ItemMag itemMag = (ItemMag) itemStackMag.getItem();
			int magBulletId = itemMag.bulletId;
			String magPack = itemMag.pack;
			ItemBullet ammoItemBullet = (ItemBullet) itemStackAmmo.getItem();
			
			if ((magBulletId == ammoItemBullet.bulletId) && magPack.equals(ammoItemBullet.pack)) {
				int damage = itemStackMag.getItemDamage();
				int size = itemStackAmmo.stackSize;
				for (; (size > 0) && (damage > 0); size--) {
					damage--;
				}
				
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
		ItemStack itemStackBullet = ((Slot) inventorySlots.get(1)).getStack();
		
		if ( (itemStackMag != null) && (itemStackMag.getItem() instanceof ItemMag)
		  && ((itemStackBullet == null) || (itemStackBullet.getItem() instanceof ItemBullet))
		  && (itemStackMag.getItemDamage() < itemStackMag.getMaxDamage())) {
			ItemMag itemMag = (ItemMag) itemStackMag.getItem();
			int magBulletId = itemMag.bulletId;
			int ammoBulletId = magBulletId;
			String magPack = itemMag.pack;
			String bulletPack = magPack;
			
			if (itemStackBullet != null) {
				ItemBullet itemBullet = (ItemBullet) itemStackBullet.getItem();
				ammoBulletId = itemBullet.bulletId;
				bulletPack = itemBullet.pack;
			}
			
			if ((magBulletId == ammoBulletId) && magPack.equals(bulletPack)) {
				int damage = itemStackMag.getItemDamage();
				int size = 0;
				
				if (itemStackBullet != null) {
					size = itemStackBullet.stackSize;
				}
				int maxBulletStackSize = ItemBullet.bullets.get(itemMag.pack).get(magBulletId).getItemStackLimit(null);
				while ((size < maxBulletStackSize) && (damage < itemStackMag.getMaxDamage())) {
					size++;
					damage++;
				}
				
				((Slot) inventorySlots.get(0)).putStack(new ItemStack(itemMag, 1, damage));
				
				if (size > 0) {
					try {
						((Slot) inventorySlots.get(1)).putStack(new ItemStack(ItemBullet.bullets.get(itemMag.pack).get(magBulletId), size));
					} catch (NullPointerException exception) {
						exception.printStackTrace();
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
