package stuuupiiid.guncus.block;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.data.ModifierPart;
import stuuupiiid.guncus.item.ItemGun;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ContainerGunBox extends Container {
	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	private World worldObj;
	public int posX;
	public int posY;
	public int posZ;
	
	public ContainerGunBox(InventoryPlayer inventoryPlayer, World par2World, int par3, int par4, int par5) {
		worldObj = par2World;
		posX = par3;
		posY = par4;
		posZ = par5;
		
		addSlotToContainer(new Slot(craftMatrix, 0, 80, 56));
		addSlotToContainer(new Slot(craftMatrix, 1, 80, 14));
		addSlotToContainer(new Slot(craftMatrix, 2, 59, 35));
		addSlotToContainer(new Slot(craftMatrix, 3, 101, 35));
		addSlotToContainer(new Slot(craftMatrix, 4, 80, 35));
		
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
	
	public void split() {
		ItemStack itemStackGunSlot = inventorySlots.get(4).getStack();
		
		if ((itemStackGunSlot != null) && (itemStackGunSlot.getItem() instanceof ItemGun)) {
			ItemGun gun = (ItemGun) itemStackGunSlot.getItem();
			int metadata = itemStackGunSlot.getItemDamage();
			
			ModifierPart customizationPart;
			customizationPart = gun.getScopePart(metadata);
			if (customizationPart != null) {
				ItemStack itemStackScope = new ItemStack(GunCus.itemScope, 1, customizationPart.id);
				if (inventorySlots.get(1).getStack() == null) {
					metadata -= itemStackScope.getItemDamage();
					inventorySlots.get(1).putStack(itemStackScope);
				}
			}
			
			customizationPart = gun.getAttachmentPart(metadata);
			if (customizationPart != null) {
				ItemStack itemStackAttachment = new ItemStack(GunCus.itemAttachment, 1, customizationPart.id);
				if (inventorySlots.get(0).getStack() == null) {
					metadata -= itemStackAttachment.getItemDamage() * (GunCus.itemScope.idMax + 1);
					inventorySlots.get(0).putStack(itemStackAttachment);
				}
			}
			
			customizationPart = gun.getBarrelPart(metadata);
			if (customizationPart != null) {
				ItemStack itemStackBarrel = new ItemStack(GunCus.itemBarrel, 1, customizationPart.id);
				if (inventorySlots.get(2).getStack() == null) {
					metadata -= itemStackBarrel.getItemDamage() * (GunCus.itemScope.idMax + 1) * (GunCus.itemAttachment.idMax + 1);
					inventorySlots.get(2).putStack(itemStackBarrel);
				}
			}
			
			inventorySlots.get(4).putStack(new ItemStack(gun, 1, metadata));
		}
	}
	
	public void build(EntityPlayerMP entityPlayer) {
		ItemStack itemStackGunSlot = inventorySlots.get(4).getStack();
		
		if (itemStackGunSlot != null && (itemStackGunSlot.getItem() instanceof ItemGun)) {
			ItemGun gun = (ItemGun) itemStackGunSlot.getItem();
			
			int metadata = itemStackGunSlot.getItemDamage();
			
			ItemStack itemStackScopeSlot = inventorySlots.get(1).getStack();
			if (itemStackScopeSlot != null) {
				Item itemScope = itemStackScopeSlot.getItem();
				if (itemScope == GunCus.itemScope) {
					int scopeId = itemStackScopeSlot.getItemDamage();
					if (gun.getScopePart(metadata) != null) {
						GunCus.addChatMessage(entityPlayer, "This " + itemStackGunSlot.getDisplayName() + " already has a scope.");
					} else if (!gun.canHaveScope(scopeId)) {
						GunCus.addChatMessage(entityPlayer, itemStackScopeSlot.getDisplayName() + " isn't supported on " + itemStackGunSlot.getDisplayName() + ".");
					} else {
						metadata += scopeId;
						inventorySlots.get(1).decrStackSize(1);
					}
				}
			}
			
			ItemStack itemStackAttachmentSlot = inventorySlots.get(0).getStack();
			if (itemStackAttachmentSlot != null) {
				Item itemAttachment = itemStackAttachmentSlot.getItem();
				if (itemAttachment == GunCus.itemAttachment) {
					int attachmentId = itemStackAttachmentSlot.getItemDamage();
					if (gun.getAttachmentPart(metadata) != null) {
						GunCus.addChatMessage(entityPlayer, "This " + itemStackGunSlot.getDisplayName() + " already has an attachment.");
					} else if (!gun.canHaveAttachment(attachmentId)) {
						GunCus.addChatMessage(entityPlayer, itemStackAttachmentSlot.getDisplayName() + " isn't supported on " + itemStackGunSlot.getDisplayName() + ".");
					} else {
						metadata += attachmentId * (GunCus.itemScope.idMax + 1);
						inventorySlots.get(0).decrStackSize(1);
					}
				}
			}
			
			ItemStack itemStackBarrelSlot = inventorySlots.get(2).getStack();
			if (itemStackBarrelSlot != null) {
				Item itemBarrel = itemStackBarrelSlot.getItem();
				if (itemBarrel == GunCus.itemBarrel) {
					int barrelId = itemStackBarrelSlot.getItemDamage();
					if (gun.getBarrelPart(metadata) != null) {
						GunCus.addChatMessage(entityPlayer, "This " + itemStackGunSlot.getDisplayName() + " already has an barrel.");
					} else if (!gun.canHaveBarrel(barrelId)) {
						GunCus.addChatMessage(entityPlayer, itemStackBarrelSlot.getDisplayName() + " isn't supported on " + itemStackGunSlot.getDisplayName() + ".");
					} else {
						metadata += barrelId * (GunCus.itemScope.idMax + 1) * (GunCus.itemAttachment.idMax + 1);
						inventorySlots.get(2).decrStackSize(1);
					}
				}
			}
			
			inventorySlots.get(4).putStack(new ItemStack(gun, 1, metadata));
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
		return worldObj.getBlockState(new BlockPos(posX, posY, posZ)).getBlock() == GunCus.blockGunBox;
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
		return null;
	}
}
