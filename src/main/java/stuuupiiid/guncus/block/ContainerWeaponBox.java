package stuuupiiid.guncus.block;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.item.ItemGun;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ContainerWeaponBox extends Container {
	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	private World worldObj;
	public int posX;
	public int posY;
	public int posZ;
	public String actualGunName = null;
	
	public ContainerWeaponBox(InventoryPlayer inventoryPlayer, World world, int x, int y, int z) {
		this.worldObj = world;
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		
		if (GunCus.guns.size() > 0) {
			if (actualGunName == null || actualGunName.isEmpty() || GunCus.guns.get(actualGunName) != null) {
				actualGunName = GunCus.gunNames.toArray(new String[0])[0];
			}
		} else {
			actualGunName = null;
		}
		
		addSlotToContainer(new Slot(craftMatrix, 0,  59, 35));
		addSlotToContainer(new Slot(craftMatrix, 1,  80, 35));
		addSlotToContainer(new Slot(craftMatrix, 2, 101, 35));
		
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
	
	public void create() {
		if (GunCus.instance.guns.size() > 0) {
			ItemStack itemStackIronIngotsSlot = inventorySlots.get(0).getStack();
			ItemStack itemStackRedstoneSlot = inventorySlots.get(1).getStack();
			
			ItemGun itemGun = GunCus.instance.guns.get(actualGunName);
			if (itemGun != null) {
				if (((itemStackIronIngotsSlot != null) && (itemStackIronIngotsSlot.stackSize >= itemGun.gunIronIngots) && (itemStackIronIngotsSlot.getItem() == Items.iron_ingot))
						|| ((itemGun.gunIronIngots <= 0)
				  && (((itemStackRedstoneSlot != null) && (itemStackRedstoneSlot.stackSize >= itemGun.gunRedstone) && (itemStackRedstoneSlot.getItem() == Items.redstone))
						|| (itemGun.gunRedstone <= 0)))) {
					inventorySlots.get(2).putStack(new ItemStack(itemGun, 1, 0));
					if (itemStackIronIngotsSlot != null) {
						inventorySlots.get(0).decrStackSize(itemGun.gunIronIngots);
					}
					if (itemStackRedstoneSlot != null) {
						inventorySlots.get(1).decrStackSize(itemGun.gunRedstone);
					}
				}
			}
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer entityPlayer) {
		return worldObj.getBlockState(new BlockPos(posX, posY, posZ)).getBlock() == GunCus.blockWeaponBox;
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
		return null;
	}
}
