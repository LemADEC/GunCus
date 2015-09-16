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

public class ContainerWeapon extends Container {
	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	private World worldObj;
	public int posX;
	public int posY;
	public int posZ;
	public ItemGun actualGunItem;
	public int actualGunIndex;
	
	public ContainerWeapon(InventoryPlayer inventoryPlayer, World world, int x, int y, int z) {
		this.worldObj = world;
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		
		actualGunIndex = 0;
		actualGunItem = null;
		if (GunCus.instance.guns.size() > 0) {
			actualGunItem = GunCus.instance.guns.get(0);
		}
		
		addSlotToContainer(new Slot(craftMatrix, 0, 59, 35));
		addSlotToContainer(new Slot(craftMatrix, 1, 80, 35));
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
	
	public String[] info() {
		String info1 = "Oops! Something went wrong!";
		String info2 = null;
		if (GunCus.instance.guns.size() <= 0) {
			info1 = "No guns defined!";
			info2 = "Enable official pack or install an GunCus addon, then try again";
		} else if (actualGunItem == null) {
			info1 = "No actual gun!";
			info2 = "Click next to select a gun, then try again";
		} else {
			info1 = "In pack '" + actualGunItem.pack + "', gun '" + actualGunItem.getItemStackDisplayName(null) + "' requires";
			info2 = " " + (actualGunItem.gunIronIngots > 0 ? actualGunItem.gunIronIngots + " iron ingot" + (actualGunItem.gunIronIngots > 1 ? "s " : " no iron ingot") : "")
					+ " and "
					+ (actualGunItem.gunRedstone > 0 ? actualGunItem.gunRedstone + " redstone" : " no redstone");
		}
		return new String[] { info1, info2 };
	}
	
	public void create() {
		if (GunCus.instance.guns.size() > 0) {
			ItemStack itemStackIronIngotsSlot = ((Slot) inventorySlots.get(0)).getStack();
			ItemStack itemStackRedstoneSlot = ((Slot) inventorySlots.get(1)).getStack();
			
			if (actualGunItem != null) {
				if (((itemStackIronIngotsSlot != null) && (itemStackIronIngotsSlot.stackSize >= actualGunItem.gunIronIngots) && (itemStackIronIngotsSlot.getItem() == Items.iron_ingot))
						|| ((actualGunItem.gunIronIngots <= 0)
				  && (((itemStackRedstoneSlot != null) && (itemStackRedstoneSlot.stackSize >= actualGunItem.gunRedstone) && (itemStackRedstoneSlot.getItem() == Items.redstone))
						|| (actualGunItem.gunRedstone <= 0)))) {
					((Slot) inventorySlots.get(2)).putStack(new ItemStack(actualGunItem, 1, 0));
					if (itemStackIronIngotsSlot != null) {
						((Slot) inventorySlots.get(0)).decrStackSize(actualGunItem.gunIronIngots);
					}
					if (itemStackRedstoneSlot != null) {
						((Slot) inventorySlots.get(1)).decrStackSize(actualGunItem.gunRedstone);
					}
				}
			}
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
		return worldObj.getBlock(posX, posY, posZ) == GunCus.blockWeapon;
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
		return null;
	}
}
