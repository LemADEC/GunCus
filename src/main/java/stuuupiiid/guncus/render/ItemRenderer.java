package stuuupiiid.guncus.render;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.item.ItemGun;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;

public class ItemRenderer implements IItemRenderer {
	private static RenderItem renderItem = new RenderItem();
	
	@Override
	public boolean handleRenderType(ItemStack itemStack, IItemRenderer.ItemRenderType type) {
		return (type == IItemRenderer.ItemRenderType.INVENTORY) && (itemStack.getItem() != null) && ((itemStack.getItem() instanceof ItemGun));
	}
	
	@Override
	public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item, IItemRenderer.ItemRendererHelper helper) {
		return false;
	}
	
	@Override
	public void renderItem(IItemRenderer.ItemRenderType type, ItemStack itemStack, Object... data) {
		ItemGun gun = (ItemGun) itemStack.getItem();
		IIcon icon = itemStack.getIconIndex();
		GunCus.logger.info("icon " + icon + " itemStack " + itemStack + " gun " + gun);
		if (icon == null) {
			icon = Items.fire_charge.getIconFromDamage(0);
		}
		renderItem.renderIcon(0, 0, icon, 16, 16);
		
		for (int v1 = 0; v1 < gun.barrel.length; v1++) {
			if ((gun.testForBarrelId(gun.barrel[v1], itemStack.getItemDamage())) && (gun.iconsBarrel[v1] != null)) {
				renderItem.renderIcon(0, 0, gun.iconsBarrel[v1], 16, 16);
			}
		}
		
		if (gun.getZoom(itemStack.getItemDamage()) > 0) {
			renderItem.renderIcon(0, 0, gun.iconScope, 16, 16);
		}
		
		for (int v1 = 0; v1 < gun.attach.length; v1++) {
			if ((gun.testForAttachId(gun.attach[v1], itemStack.getItemDamage())) && (gun.iconsAttachment[v1] != null)) {
				renderItem.renderIcon(0, 0, gun.iconsAttachment[v1], 16, 16);
			}
		}
	}
}
