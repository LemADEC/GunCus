/*    */ package assets.guncus;
/*    */ 
/*    */ import net.minecraft.client.renderer.entity.RenderItem;
/*    */ import net.minecraft.item.ItemStack;
/*    */ import net.minecraft.util.Icon;
/*    */ import net.minecraftforge.client.IItemRenderer;
/*    */ import net.minecraftforge.client.IItemRenderer.ItemRenderType;
/*    */ import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
/*    */ 
/*    */ public class GunCusInvRenderer
/*    */   implements IItemRenderer
/*    */ {
/* 15 */   private static RenderItem renderItem = new RenderItem();
/*    */ 
/*    */   public boolean handleRenderType(ItemStack itemStack, IItemRenderer.ItemRenderType type)
/*    */   {
/* 20 */     return (type == IItemRenderer.ItemRenderType.INVENTORY) && (itemStack.getItem() != null) && ((itemStack.getItem() instanceof GunCusItemGun));
/*    */   }
/*    */ 
/*    */   public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item, IItemRenderer.ItemRendererHelper helper)
/*    */   {
/* 26 */     return false;
/*    */   }
/*    */ 
/*    */   public void renderItem(IItemRenderer.ItemRenderType type, ItemStack itemStack, Object[] data)
/*    */   {
/* 32 */     GunCusItemGun gun = (GunCusItemGun)itemStack.getItem();
/* 33 */     Icon icon = itemStack.getIconIndex();
/* 34 */     renderItem.renderIcon(0, 0, icon, 16, 16);
/*    */ 
/* 36 */     for (int v1 = 0; v1 < gun.barrel.length; v1++)
/*    */     {
/* 38 */       if ((gun.testForBarrelId(gun.barrel[v1], itemStack.getItemDamage())) && (gun.iconBar[v1] != null))
/*    */       {
/* 40 */         renderItem.renderIcon(0, 0, gun.iconBar[v1], 16, 16);
/*    */       }
/*    */     }
/*    */ 
/* 44 */     if (gun.getZoom(itemStack.getItemDamage()) > 0)
/*    */     {
/* 46 */       renderItem.renderIcon(0, 0, gun.iconScp, 16, 16);
/*    */     }
/*    */ 
/* 49 */     for (int v1 = 0; v1 < gun.attach.length; v1++)
/*    */     {
/* 51 */       if ((gun.testForAttachId(gun.attach[v1], itemStack.getItemDamage())) && (gun.iconAttach[v1] != null))
/*    */       {
/* 53 */         renderItem.renderIcon(0, 0, gun.iconAttach[v1], 16, 16);
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Nate\Desktop\Mod\GunCusClass.zip
 * Qualified Name:     assets.guncus.GunCusInvRenderer
 * JD-Core Version:    0.6.2
 */