package stuuupiiid.guncus.block;

import cpw.mods.fml.common.FMLCommonHandler;
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

public class ContainerMag extends Container {
	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	private World worldObj;
	public int posX;
	public int posY;
	public int posZ;
	
	public ContainerMag(InventoryPlayer inventoryPlayer, World world, int x, int y, int z) {
		this.worldObj = world;
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		
		addSlotToContainer(new Slot(this.craftMatrix, 0, 80, 14));
		addSlotToContainer(new Slot(this.craftMatrix, 1, 59, 35));
		addSlotToContainer(new Slot(this.craftMatrix, 2, 101, 35));
		
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
				entityPlayer.dropItem(itemStackSlot.getItem(), itemStackSlot.stackSize);
			}
		}
	}
	
	public void create() {
		ItemStack down = ((Slot) inventorySlots.get(0)).getStack();
		ItemStack left = ((Slot) inventorySlots.get(1)).getStack();
		ItemStack right = ((Slot) inventorySlots.get(2)).getStack();
		
		ItemGun gun = null;
		int ingotsRequired = 0;
		
		if ((down != null) && (down.getItem() != null) && ((down.getItem() instanceof ItemGun))) {
			gun = (ItemGun) down.getItem();
			ingotsRequired = gun.magIronIngots;
		}
		
		if ((gun != null) && (left != null) && (left.getItem() != null)
				&& (left.getItem() == Items.iron_ingot) && (right == null) && (ingotsRequired > 0)
				&& (left.stackSize >= ingotsRequired)) {
			int stackSize = left.stackSize;
			
			stackSize -= ingotsRequired;
			
			((Slot) inventorySlots.get(1)).putStack(new ItemStack(Items.iron_ingot, stackSize));
			((Slot) inventorySlots.get(2)).putStack(new ItemStack(gun.mag, 1, gun.mag.getMaxDamage()));
		}
	}
	
	public String[] info() {
		ItemStack itemStackGunSlot = ((Slot) inventorySlots.get(0)).getStack();
		
		ItemGun gun = null;
		String info1 = "Oops! Something went wrong!";
		String info2 = null;
		
		if ((itemStackGunSlot == null) || (itemStackGunSlot.getItem() == null)) {
			info1 = "Empty gun slot!";
			info2 = "Place a GunCus gun in the gun slot and try again...";
		} else if (itemStackGunSlot.getItem() instanceof ItemGun) {
			gun = (ItemGun) itemStackGunSlot.getItem();
			info1 = "In pack '" + gun.pack + "', gun '" + itemStackGunSlot.getDisplayName() + "' magazine requires";
			info2 = " " + (gun.magIronIngots > 0 ? gun.magIronIngots + " iron ingot" + (gun.magIronIngots > 1 ? "s " : " ") : "");
		} else {
			info1 = "Invalid item detected in gun slot!";
			info2 = "Place a GunCus gun in the gun slot and try again...";
		}
		
		return new String[] { info1, info2 };
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
		return worldObj.getBlock(posX, posY, posZ) == GunCus.blockMag;
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
		return null;
	}
}
