package stuuupiiid.guncus.block;

import cpw.mods.fml.common.FMLCommonHandler;
import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.data.CustomizationPart;
import stuuupiiid.guncus.item.ItemGun;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ContainerGun extends Container {
	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	private World worldObj;
	public int posX;
	public int posY;
	public int posZ;
	
	public ContainerGun(InventoryPlayer inventoryPlayer, World par2World, int par3, int par4, int par5) {
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
	
	public void split() {
		ItemStack itemStackGunSlot = ((Slot) inventorySlots.get(4)).getStack();
		
		if ((itemStackGunSlot != null) && (itemStackGunSlot.getItem() instanceof ItemGun)) {
			ItemGun gun = (ItemGun) itemStackGunSlot.getItem();
			int metadata = itemStackGunSlot.getItemDamage();
			
			CustomizationPart customizationPart;
			customizationPart = gun.getScopePart(metadata);
			if (customizationPart != null) {
				ItemStack itemStackScope = new ItemStack(GunCus.scope, 1, customizationPart.id);
				if (((Slot) inventorySlots.get(1)).getStack() == null) {
					metadata -= itemStackScope.getItemDamage();
					((Slot) inventorySlots.get(1)).putStack(itemStackScope);
				}
			}
			
			customizationPart = gun.getAttachmentPart(metadata);
			if (customizationPart != null) {
				ItemStack itemStackAttachment = new ItemStack(GunCus.attachment, 1, customizationPart.id);
				if (((Slot) inventorySlots.get(0)).getStack() == null) {
					metadata -= itemStackAttachment.getItemDamage() * (GunCus.scope.maxId + 1);
					((Slot) inventorySlots.get(0)).putStack(itemStackAttachment);
				}
			}
			
			customizationPart = gun.getBarrelPart(metadata);
			if (customizationPart != null) {
				ItemStack itemStackBarrel = new ItemStack(GunCus.barrel, 1, customizationPart.id);
				if (((Slot) inventorySlots.get(2)).getStack() == null) {
					metadata -= itemStackBarrel.getItemDamage() * (GunCus.scope.maxId + 1) * (GunCus.attachment.maxId + 1);
					((Slot) inventorySlots.get(2)).putStack(itemStackBarrel);
				}
			}
			
			((Slot) inventorySlots.get(4)).putStack(new ItemStack(gun, 1, metadata));
		}
	}
	
	public void build(EntityPlayerMP entityPlayer) {
		ItemStack itemStackGunSlot = ((Slot) inventorySlots.get(4)).getStack();
		
		if (itemStackGunSlot != null && (itemStackGunSlot.getItem() instanceof ItemGun)) {
			ItemGun gun = (ItemGun) itemStackGunSlot.getItem();
			
			int metadata = itemStackGunSlot.getItemDamage();
			
			ItemStack itemStackScopeSlot = ((Slot) inventorySlots.get(1)).getStack();
			if (itemStackScopeSlot != null) {
				Item itemScope = itemStackScopeSlot.getItem();
				if (itemScope == GunCus.scope) {
					int scopeId = itemStackScopeSlot.getItemDamage();
					if (gun.getScopePart(metadata) != null) {
						GunCus.addChatMessage(entityPlayer, "This " + itemStackGunSlot.getDisplayName() + " already has a scope.");
					} else if (!gun.canHaveScope(scopeId)) {
						GunCus.addChatMessage(entityPlayer, itemStackScopeSlot.getDisplayName() + " isn't supported on " + itemStackGunSlot.getDisplayName() + ".");
					} else {
						metadata += scopeId;
						((Slot) inventorySlots.get(1)).decrStackSize(1);
					}
				}
			}
			
			ItemStack itemStackAttachmentSlot = ((Slot) inventorySlots.get(0)).getStack();
			if (itemStackAttachmentSlot != null) {
				Item itemAttachment = itemStackAttachmentSlot.getItem();
				if (itemAttachment == GunCus.attachment) {
					int attachmentId = itemStackAttachmentSlot.getItemDamage();
					if (gun.getAttachmentPart(metadata) != null) {
						GunCus.addChatMessage(entityPlayer, "This " + itemStackGunSlot.getDisplayName() + " already has an attachment.");
					} else if (!gun.canHaveAttachment(attachmentId)) {
						GunCus.addChatMessage(entityPlayer, itemStackAttachmentSlot.getDisplayName() + " isn't supported on " + itemStackGunSlot.getDisplayName() + ".");
					} else {
						metadata += attachmentId * (GunCus.scope.maxId + 1);
						((Slot) inventorySlots.get(0)).decrStackSize(1);
					}
				}
			}
			
			ItemStack itemStackBarrelSlot = ((Slot) inventorySlots.get(2)).getStack();
			if (itemStackBarrelSlot != null) {
				Item itemBarrel = itemStackBarrelSlot.getItem();
				if (itemBarrel == GunCus.barrel) {
					int barrelId = itemStackBarrelSlot.getItemDamage();
					if (gun.getBarrelPart(metadata) != null) {
						GunCus.addChatMessage(entityPlayer, "This " + itemStackGunSlot.getDisplayName() + " already has an barrel.");
					} else if (!gun.canHaveBarrel(barrelId)) {
						GunCus.addChatMessage(entityPlayer, itemStackBarrelSlot.getDisplayName() + " isn't supported on " + itemStackGunSlot.getDisplayName() + ".");
					} else {
						metadata += barrelId * (GunCus.scope.maxId + 1) * (GunCus.attachment.maxId + 1);
						((Slot) inventorySlots.get(2)).decrStackSize(1);
					}
				}
			}
			
			((Slot) inventorySlots.get(4)).putStack(new ItemStack(gun, 1, metadata));
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
		return worldObj.getBlock(posX, posY, posZ) == GunCus.blockGun;
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
		return null;
	}
}
