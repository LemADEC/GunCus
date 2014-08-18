package stuuupiiid.guncus;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;

public class GunCusInvRenderer implements IItemRenderer {
	private static RenderItem renderItem = new RenderItem();

	@Override
	public boolean handleRenderType(ItemStack itemStack, IItemRenderer.ItemRenderType type) {
		return (type == IItemRenderer.ItemRenderType.INVENTORY) && (itemStack.getItem() != null)
				&& ((itemStack.getItem() instanceof GunCusItemGun));
	}

	@Override
	public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item,
			IItemRenderer.ItemRendererHelper helper) {
		return false;
	}

	@Override
	public void renderItem(IItemRenderer.ItemRenderType type, ItemStack itemStack, Object... data) {
		GunCusItemGun gun = (GunCusItemGun) itemStack.getItem();
		Icon icon = itemStack.getIconIndex();
		renderItem.renderIcon(0, 0, icon, 16, 16);

		for (int v1 = 0; v1 < gun.barrel.length; v1++) {
			if ((gun.testForBarrelId(gun.barrel[v1], itemStack.getItemDamage())) && (gun.iconBar[v1] != null)) {
				renderItem.renderIcon(0, 0, gun.iconBar[v1], 16, 16);
			}
		}

		if (gun.getZoom(itemStack.getItemDamage()) > 0) {
			renderItem.renderIcon(0, 0, gun.iconScp, 16, 16);
		}

		for (int v1 = 0; v1 < gun.attach.length; v1++) {
			if ((gun.testForAttachId(gun.attach[v1], itemStack.getItemDamage())) && (gun.iconAttach[v1] != null)) {
				renderItem.renderIcon(0, 0, gun.iconAttach[v1], 16, 16);
			}
		}
	}
}
