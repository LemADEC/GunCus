/*    */ package assets.guncus;
/*    */ 
/*    */ import cpw.mods.fml.common.network.IGuiHandler;
/*    */ import net.minecraft.entity.player.EntityPlayer;
/*    */ import net.minecraft.world.World;
/*    */ 
/*    */ public class GunCusGuiHandler
/*    */   implements IGuiHandler
/*    */ {
/*    */   public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
/*    */   {
/* 13 */     switch (ID)
/*    */     {
/*    */     case 0:
/* 16 */       return new GunCusGuiGun(player.inventory, world, x, y, z);
/*    */     case 1:
/* 18 */       return new GunCusGuiAmmo(player.inventory, world, x, y, z);
/*    */     case 2:
/* 20 */       return new GunCusGuiAmmoMan(player.inventory, world, x, y, z);
/*    */     case 3:
/* 22 */       return new GunCusGuiMag(player.inventory, world, x, y, z);
/*    */     case 4:
/* 24 */       return new GunCusGuiBullet(player.inventory, world, x, y, z);
/*    */     case 5:
/* 26 */       return new GunCusGuiWeapon(player.inventory, world, x, y, z);
/*    */     }
/*    */ 
/* 29 */     return null;
/*    */   }
/*    */ 
/*    */   public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
/*    */   {
/* 35 */     switch (ID)
/*    */     {
/*    */     case 0:
/* 38 */       return new GunCusContainerGun(player.inventory, world, x, y, z);
/*    */     case 1:
/* 40 */       return new GunCusContainerAmmo(player.inventory, world, x, y, z);
/*    */     case 2:
/* 42 */       return new GunCusContainerAmmoMan(player.inventory, world, x, y, z);
/*    */     case 3:
/* 44 */       return new GunCusContainerMag(player.inventory, world, x, y, z);
/*    */     case 4:
/* 46 */       return new GunCusContainerBullet(player.inventory, world, x, y, z);
/*    */     case 5:
/* 48 */       return new GunCusContainerWeapon(player.inventory, world, x, y, z);
/*    */     }
/*    */ 
/* 51 */     return null;
/*    */   }
/*    */ }

/* Location:           C:\Users\Nate\Desktop\Mod\GunCusClass.zip
 * Qualified Name:     assets.guncus.GunCusGuiHandler
 * JD-Core Version:    0.6.2
 */