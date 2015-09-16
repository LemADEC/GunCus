package stuuupiiid.guncus.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import stuuupiiid.guncus.data.CustomizationPart;
import stuuupiiid.guncus.item.ItemGun;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;

public class RenderGun implements IItemRenderer {
	private static RenderItem renderItem = new RenderItem();
	
	@Override
	public boolean handleRenderType(ItemStack itemStack, IItemRenderer.ItemRenderType type) {
		return (type == IItemRenderer.ItemRenderType.INVENTORY) && (itemStack.getItem() instanceof ItemGun);
	}
	
	@Override
	public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item, IItemRenderer.ItemRendererHelper helper) {
		return false;
	}
	
	@Override
	public void renderItem(IItemRenderer.ItemRenderType type, ItemStack itemStack, Object... data) {
		ItemGun gun = (ItemGun) itemStack.getItem();
		IIcon icon = itemStack.getIconIndex();
		if (icon == null) {
			icon = Items.fire_charge.getIconFromDamage(0);
		}
		
		if (type == ItemRenderType.INVENTORY) {
			GL11.glPushMatrix();
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			
			renderItem.renderIcon(0, 0, icon, 16, 16);
			
			CustomizationPart customizationPart = gun.getBarrelPart(itemStack.getItemDamage());
			if (customizationPart != null && gun.iconsBarrel[customizationPart.id] != null) {
				renderItem.renderIcon(0, 0, gun.iconsBarrel[customizationPart.id], 16, 16);
			}
			
			if (gun.getScopePart(itemStack.getItemDamage()) != null) {
				renderItem.renderIcon(0, 0, gun.iconScope, 16, 16);
			}
			
			customizationPart = gun.getAttachmentPart(itemStack.getItemDamage());
			if (customizationPart != null && gun.iconsAttachment[customizationPart.id] != null) {
				renderItem.renderIcon(0, 0, gun.iconsAttachment[customizationPart.id], 16, 16);
			}
			
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			GL11.glPopMatrix();
		}
	}
}
