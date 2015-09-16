package stuuupiiid.guncus.block;

import cpw.mods.fml.common.FMLCommonHandler;
import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.item.ItemGun;
import net.minecraft.entity.player.EntityPlayer;
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
				entityPlayer.dropItem(itemStackSlot.getItem(), itemStackSlot.stackSize);
			}
		}
	}
	
	public void split() {
		ItemStack down = ((Slot) inventorySlots.get(0)).getStack();
		ItemStack top = ((Slot) inventorySlots.get(1)).getStack();
		ItemStack left = ((Slot) inventorySlots.get(2)).getStack();
		// ItemStack right = ((Slot)inventorySlots.get(3)).getStack();
		ItemStack mid = ((Slot) inventorySlots.get(4)).getStack();
		
		ItemStack scope = null;
		ItemStack attatch = null;
		ItemStack extra = null;
		
		ItemGun gun = null;
		
		if ((mid != null) && (mid.getItem() != null) && ((mid.getItem() instanceof ItemGun))) {
			gun = (ItemGun) mid.getItem();
			int metadata = mid.getItemDamage();
			
			int scope1 = 0;
			
			if (gun.getScopeIndex(metadata) > 0) {
				scope1 = gun.getScopeIndex(metadata);
			}
			
			if (scope1 > 0) {
				scope = new ItemStack(GunCus.scope, 1, scope1 - 1);
			}
			
			int extra1 = 0;
			
			for (int v1 = 1; v1 <= GunCus.attachment.customizationParts.length; v1++) {
				if (gun.hasAttachment(v1, metadata)) {
					extra1 = v1;
					break;
				}
			}
			
			if (extra1 > 0) {
				extra = new ItemStack(GunCus.attachment, 1, extra1 - 1);
			}
			
			int bar1 = 0;
			
			for (int v1 = 1; v1 <= GunCus.barrel.customizationParts.length; v1++) {
				if (gun.hasBarrel(v1, metadata)) {
					bar1 = v1;
					break;
				}
			}
			
			if (bar1 > 0) {
				attatch = new ItemStack(GunCus.barrel, 1, bar1 - 1);
			}
			
			int extra2 = gun.attachAsMetadataFactor(extra1);
			int bar2 = gun.barrelAsMetadataFactor(bar1);
			
			if (top == null) {
				metadata -= scope1;
				((Slot) inventorySlots.get(1)).putStack(scope);
				((Slot) inventorySlots.get(4)).putStack(new ItemStack(gun, 1, metadata));
			}
			if (left == null) {
				metadata -= bar2 * gun.barrelFactor;
				((Slot) inventorySlots.get(2)).putStack(attatch);
				((Slot) inventorySlots.get(4)).putStack(new ItemStack(gun, 1, metadata));
			}
			if (down == null) {
				metadata -= extra2 * (gun.scopes.length + 1);
				((Slot) inventorySlots.get(0)).putStack(extra);
				((Slot) inventorySlots.get(4)).putStack(new ItemStack(gun, 1, metadata));
			}
		}
	}
	
	public void build() {
		ItemStack itemStackAttachmentSlot = ((Slot) inventorySlots.get(0)).getStack();
		ItemStack itemStackScopeSlot = ((Slot) inventorySlots.get(1)).getStack();
		ItemStack itemStackBarrelSlot = ((Slot) inventorySlots.get(2)).getStack();
		// ItemStack right = ((Slot) inventorySlots.get(3)).getStack();
		ItemStack mid = ((Slot) inventorySlots.get(4)).getStack();
		
		int scopeId = 0;
		
		if (itemStackScopeSlot != null) {
			Item topItem = itemStackScopeSlot.getItem();
			if (topItem != null) {
				if (topItem == GunCus.scope) {
					scopeId = itemStackScopeSlot.getItemDamage();
				}
			}
		}
		
		int barrelId = 0;
		
		if (itemStackBarrelSlot != null) {
			Item leftItem = itemStackBarrelSlot.getItem();
			if (leftItem != null) {
				if (leftItem == GunCus.barrel) {
					barrelId = itemStackBarrelSlot.getItemDamage();
				}
			}
		}
		
		
		int attachmentId = 0;
		
		if (itemStackAttachmentSlot != null) {
			Item downItem = itemStackAttachmentSlot.getItem();
			if (downItem != null) {
				if (downItem == GunCus.attachment) {
					attachmentId = itemStackAttachmentSlot.getItemDamage();
				}
			}
		}
		
		if (mid != null && (mid.getItem() != null) && (mid.getItem() instanceof ItemGun)) {
			ItemGun gun = (ItemGun) mid.getItem();
			
			int attachmentFactor = gun.attachAsMetadataFactor(attachmentId);
			int barrelFactor = gun.barrelAsMetadataFactor(barrelId);
			
			int metadata = mid.getItemDamage();
			
			if ((scopeId >= 0) && (gun.getScopeIndex(metadata) < 0) && gun.canHaveScope(scopeId)) {
				metadata += scopeId;
				((Slot) inventorySlots.get(1)).decrStackSize(1);
				((Slot) inventorySlots.get(4)).putStack(new ItemStack(gun, 1, metadata));
			}
			if ((attachmentFactor > 0) && (gun.hasNoAttachment(metadata))) {
				metadata += attachmentFactor * (gun.scopes.length + 1);
				((Slot) inventorySlots.get(0)).decrStackSize(1);
				((Slot) inventorySlots.get(4)).putStack(new ItemStack(gun, 1, metadata));
			}
			if ((barrelFactor > 0) && (gun.hasNoBarrel(metadata))) {
				metadata += barrelFactor * gun.barrelFactor;
				((Slot) inventorySlots.get(2)).decrStackSize(1);
				((Slot) inventorySlots.get(4)).putStack(new ItemStack(gun, 1, metadata));
			}
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
