package stuuupiiid.guncus.block;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.item.ItemBullet;
import stuuupiiid.guncus.item.ItemMagazine;
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

public class ContainerBulletBox extends Container {
	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	private World worldObj;
	public int posX;
	public int posY;
	public int posZ;

	public ContainerBulletBox(InventoryPlayer inventoryPlayer, World world, int x, int y, int z) {
		this.worldObj = world;
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		
		addSlotToContainer(new Slot(craftMatrix, 0, 80, 14));
		addSlotToContainer(new Slot(craftMatrix, 1, 59, 35));
		addSlotToContainer(new Slot(craftMatrix, 2, 101, 35));
		addSlotToContainer(new Slot(craftMatrix, 3, 80, 35));
		
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
		ItemStack itemStackMagazineSlot = inventorySlots.get(0).getStack();
		ItemStack itemStackIronIngotsSlot = inventorySlots.get(1).getStack();
		ItemStack itemStackOutputSlot = inventorySlots.get(2).getStack();
		ItemStack itemStackGunpowerSlot = inventorySlots.get(3).getStack();
		
		ItemMagazine mag = null;
		ItemBullet bullet = null;
		
		if ((itemStackMagazineSlot != null) && (itemStackMagazineSlot.getItem() != null) && (itemStackMagazineSlot.getItem() instanceof ItemMagazine)) {
			mag = (ItemMagazine) itemStackMagazineSlot.getItem();
			bullet = ItemBullet.bullets.get(mag.packName).get(mag.bulletIds[0]);	// FIXME: add supports for varied bullets in magazine
			
			if ( (bullet.gunpowder >= 0)
			  && (bullet.ironIngots >= 0)
			  && ((bullet.gunpowder > 0) || (bullet.ironIngots > 0))
			  && (itemStackIronIngotsSlot != null)
			  && (itemStackIronIngotsSlot.getItem() != null)
			  && (itemStackIronIngotsSlot.getItem() == Items.iron_ingot)
			  && (itemStackGunpowerSlot != null)
			  && (itemStackGunpowerSlot.getItem() != null)
			  && (itemStackGunpowerSlot.getItem() == Items.gunpowder)
			  && ( (itemStackOutputSlot == null)
			    || ( (itemStackOutputSlot.getItem() != null)
			      && (itemStackOutputSlot.getItem() == bullet) && (itemStackOutputSlot.stackSize + bullet.stackOnCreate <= itemStackOutputSlot.getMaxStackSize())))
			  && (bullet.stackOnCreate >= 1)
			  && (itemStackIronIngotsSlot.stackSize >= bullet.ironIngots)
			  && (itemStackGunpowerSlot.stackSize >= bullet.gunpowder)) {
				int sizeIr = itemStackIronIngotsSlot.stackSize - bullet.ironIngots;
				int sizeSu = itemStackGunpowerSlot.stackSize - bullet.gunpowder;
				
				int size = bullet.stackOnCreate
						+ ((itemStackOutputSlot != null) && (itemStackOutputSlot.getItem() != null) && (itemStackOutputSlot.stackSize > 0) ? itemStackOutputSlot.stackSize : 0);
				
				inventorySlots.get(1).putStack(new ItemStack(Items.iron_ingot, sizeIr));
				inventorySlots.get(3).putStack(new ItemStack(Items.gunpowder, sizeSu));
				inventorySlots.get(2).putStack(new ItemStack(bullet, size));
			}
		}
	}
	
	public String info() {
		ItemStack itemStackMagSlot = inventorySlots.get(0).getStack();
		
		ItemMagazine itemMag = null;
		
		if ((itemStackMagSlot == null) || (itemStackMagSlot.getItem() == null)) {
			return "Empty magazine slot!\n"
					+ "Place a GunCus magazine in the magazine slot and try again...";
		} else if (itemStackMagSlot.getItem() instanceof ItemMagazine) {
			itemMag = (ItemMagazine) itemStackMagSlot.getItem();
			
			ItemBullet bullet = ItemBullet.bullets.get(itemMag.packName).get(itemMag.bulletIds[0]);	// FIXME: add supports for varied bullets in magazine);
			if (bullet == null) {
				return "Invalid bullet defined!\n"
						+ "Please contact the '" + itemMag.packName + "' addon author to fix it";
			} else {
				if ((bullet.ironIngots >= 0) && (bullet.gunpowder >= 0) && ((bullet.ironIngots > 0) || (bullet.gunpowder > 0))) {
					return "Those '" + bullet.getUnlocalizedName() + "' bullets from '" + itemMag.packName + "' requires\n"
							+ " " + (bullet.ironIngots > 0 ? bullet.ironIngots + " iron ingot" + (bullet.ironIngots > 1 ? "s, " : ", ") : "")
							+ (bullet.gunpowder > 0 ? bullet.gunpowder + " gunpowder" : "");
				} else {
					return "Invalid bullet costs detected!\n"
							+ "Please contact the '" + bullet.packName + "' addon author to fix it";
				}
			}
		} else {
			return "Invalid item detected in magazine slot!\n"
					+ "Place a GunCus magazine in the magazine slot and try again...";
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer entityPlayer) {
		return worldObj.getBlockState(new BlockPos(posX, posY, posZ)).getBlock() == GunCus.blockBulletBox;
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
		return null;
	}
}
