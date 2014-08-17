/*    */ package assets.guncus;
/*    */ 
/*    */ import java.util.List;
/*    */ import net.minecraft.entity.player.EntityPlayer;
/*    */ import net.minecraft.item.ItemStack;
/*    */ 
/*    */ public class GunCusItemMag extends GunCusItem
/*    */ {
/*    */   public int bulletType;
/*    */   public String gunName;
/*    */   public String pack;
/*    */ 
/*    */   public GunCusItemMag(int par1, String weaponName, String unlocalized, int magSize, String weaponIcon, int bulletType, String pack)
/*    */   {
/* 16 */     super(par1, weaponIcon + "magazine", weaponName + " Magazine", unlocalized);
/* 17 */     setMaxDamage(magSize);
/* 18 */     setMaxStackSize(1);
/* 19 */     this.bulletType = bulletType;
/* 20 */     this.gunName = weaponName;
/* 21 */     this.pack = pack;
/*    */   }
/*    */ 
/*    */   public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par2List, boolean par4)
/*    */   {
/* 27 */     par2List.add(this.pack);
/*    */   }
/*    */ }

/* Location:           C:\Users\Nate\Desktop\Mod\GunCusClass.zip
 * Qualified Name:     assets.guncus.GunCusItemMag
 * JD-Core Version:    0.6.2
 */