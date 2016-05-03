package stuuupiiid.guncus.block;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.item.ItemBullet;
import stuuupiiid.guncus.item.ItemMagazine;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ContainerAmmoBox extends Container {
	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	private World worldObj;
	public int posX;
	public int posY;
	public int posZ;
	
	public ContainerAmmoBox(InventoryPlayer inventoryPlayer, World world, int x, int y, int z) {
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
			ItemStack itemStackSlot = craftMatrix.removeStackFromSlot(slotIndex);
			if (itemStackSlot != null) {
				if (entityPlayer != null) {
					entityPlayer.entityDropItem(itemStackSlot, 0.5F);
				} else {
					EntityItem entityItem = new EntityItem(worldObj, posX, posY + 0.5F, posZ, itemStackSlot);
					entityItem.setDefaultPickupDelay();
					worldObj.spawnEntityInWorld(entityItem);
				}
			}
		}
	}
	
	public void fill() {
		ItemStack itemStackMag = inventorySlots.get(0).getStack();
		ItemStack itemStackAmmo = inventorySlots.get(1).getStack();
		
		if ( (itemStackMag != null) && (itemStackMag.getItem() instanceof ItemMagazine)
		  && (itemStackAmmo != null) && (itemStackAmmo.getItem() instanceof ItemBullet)
		  && (itemStackMag.getItemDamage() > 0) ) {
			ItemMagazine itemMag = (ItemMagazine) itemStackMag.getItem();
			int magBulletId = itemMag.bulletIds[0];	// FIXME: add supports for varied bullets in magazine
			String magPack = itemMag.packName;
			ItemBullet ammoItemBullet = (ItemBullet) itemStackAmmo.getItem();
			
			if ((magBulletId == ammoItemBullet.bulletId) && magPack.equals(ammoItemBullet.packName)) {
				int damage = itemStackMag.getItemDamage();
				int size = itemStackAmmo.stackSize;
				for (; (size > 0) && (damage > 0); size--) {
					damage--;
				}
				
				inventorySlots.get(0).putStack(new ItemStack(itemMag, 1, damage));
				
				if (size > 0) {
					inventorySlots.get(1).putStack(new ItemStack(ammoItemBullet, size));
				} else {
					inventorySlots.get(1).putStack(null);
				}
			}
		}
	}
	
	public void empty() {
		ItemStack itemStackMag = inventorySlots.get(0).getStack();
		ItemStack itemStackBullet = inventorySlots.get(1).getStack();
		
		if ( (itemStackMag != null) && (itemStackMag.getItem() instanceof ItemMagazine)
		  && ((itemStackBullet == null) || (itemStackBullet.getItem() instanceof ItemBullet))
		  && (itemStackMag.getItemDamage() < itemStackMag.getMaxDamage())) {
			ItemMagazine itemMag = (ItemMagazine) itemStackMag.getItem();
			int magBulletId = itemMag.bulletIds[0];	// FIXME: add supports for varied bullets in magazine
			int ammoBulletId = magBulletId;
			String magPack = itemMag.packName;
			String bulletPack = magPack;
			
			if (itemStackBullet != null) {
				ItemBullet itemBullet = (ItemBullet) itemStackBullet.getItem();
				ammoBulletId = itemBullet.bulletId;
				bulletPack = itemBullet.packName;
			}
			
			if ((magBulletId == ammoBulletId) && magPack.equals(bulletPack)) {
				int damage = itemStackMag.getItemDamage();
				int size = 0;
				
				if (itemStackBullet != null) {
					size = itemStackBullet.stackSize;
				}
				int maxBulletStackSize = ItemBullet.bullets.get(itemMag.packName).get(magBulletId).getItemStackLimit(null);
				while ((size < maxBulletStackSize) && (damage < itemStackMag.getMaxDamage())) {
					size++;
					damage++;
				}
				
				inventorySlots.get(0).putStack(new ItemStack(itemMag, 1, damage));
				
				if (size > 0) {
					try {
						inventorySlots.get(1).putStack(new ItemStack(ItemBullet.bullets.get(itemMag.packName).get(magBulletId), size));
					} catch (NullPointerException exception) {
						exception.printStackTrace();
					}
				}
			}
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer entityPlayer) {
		return worldObj.getBlockState(new BlockPos(posX, posY, posZ)).getBlock() == GunCus.blockAmmoBox;
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
		return null;
	}
}
