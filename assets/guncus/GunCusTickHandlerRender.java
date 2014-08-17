/*    */ package assets.guncus;
/*    */ 
/*    */ import cpw.mods.fml.common.IScheduledTickHandler;
/*    */ import cpw.mods.fml.common.TickType;
/*    */ import java.util.EnumSet;
/*    */ 
/*    */ public class GunCusTickHandlerRender
/*    */   implements IScheduledTickHandler
/*    */ {
/*    */   public void tickStart(EnumSet<TickType> type, Object[] tickData)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void tickEnd(EnumSet<TickType> type, Object[] tickData)
/*    */   {
/* 16 */     GunCus.commonProxy.sight();
/*    */   }
/*    */ 
/*    */   public EnumSet<TickType> ticks()
/*    */   {
/* 22 */     return EnumSet.of(TickType.RENDER);
/*    */   }
/*    */ 
/*    */   public String getLabel()
/*    */   {
/* 28 */     return null;
/*    */   }
/*    */ 
/*    */   public int nextTickSpacing()
/*    */   {
/* 34 */     return 1;
/*    */   }
/*    */ }

/* Location:           C:\Users\Nate\Desktop\Mod\GunCusClass.zip
 * Qualified Name:     assets.guncus.GunCusTickHandlerRender
 * JD-Core Version:    0.6.2
 */