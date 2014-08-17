/*    */ package assets.guncusexplosives;
/*    */ 
/*    */ import assets.guncus.GunCus;
/*    */ import assets.guncus.GunCusItem;
/*    */ import com.google.common.io.ByteArrayDataOutput;
/*    */ import com.google.common.io.ByteStreams;
/*    */ import cpw.mods.fml.client.FMLClientHandler;
/*    */ import cpw.mods.fml.common.FMLCommonHandler;
/*    */ import cpw.mods.fml.common.network.PacketDispatcher;
/*    */ import cpw.mods.fml.relauncher.Side;
/*    */ import cpw.mods.fml.relauncher.SideOnly;
/*    */ import java.util.Random;
/*    */ import net.minecraft.client.Minecraft;
/*    */ import net.minecraft.client.audio.SoundManager;
/*    */ import net.minecraft.entity.Entity;
/*    */ import net.minecraft.entity.player.EntityPlayer;
/*    */ import net.minecraft.entity.player.InventoryPlayer;
/*    */ import net.minecraft.entity.player.PlayerCapabilities;
/*    */ import net.minecraft.item.Item;
/*    */ import net.minecraft.item.ItemStack;
/*    */ import net.minecraft.network.packet.Packet250CustomPayload;
/*    */ import net.minecraft.util.MathHelper;
/*    */ import net.minecraft.world.World;
/*    */ import org.lwjgl.input.Mouse;
/*    */ 
/*    */ public class GunCusExplosivesRPG extends GunCusItem
/*    */ {
/*    */   public int ammo;
/*    */ 
/*    */   public GunCusExplosivesRPG(int par1, String iconName, String name, String unlocalized, int ammo)
/*    */   {
/* 32 */     super(par1, iconName, name, unlocalized);
/* 33 */     setFull3D();
/* 34 */     this.ammo = ammo;
/*    */   }
/*    */ 
/*    */   public void onUpdate(ItemStack itemStack, World world, Entity entity, int par1, boolean flag)
/*    */   {
/* 40 */     if (FMLCommonHandler.instance().getEffectiveSide().isClient())
/*    */     {
/* 42 */       doUpdate(itemStack, world, entity, par1, flag);
/*    */     }
/*    */   }
/*    */ 
/*    */   @SideOnly(Side.CLIENT)
/*    */   public void doUpdate(ItemStack itemStack, World world, Entity entity, int par1, boolean flag)
/*    */   {
/* 49 */     Minecraft client = FMLClientHandler.instance().getClient();
/* 50 */     EntityPlayer entityPlayer = client.thePlayer;
/* 51 */     if ((entityPlayer != null) && (entityPlayer.inventory.getCurrentItem() != null) && (entityPlayer.inventory.getCurrentItem().getItem() == this))
/*    */     {
/* 53 */       ItemStack mag = null;
/*    */ 
/* 55 */       if ((GunCus.shootTime <= 0) && (Mouse.isButtonDown(0)) && ((client.currentScreen == null) || (Mouse.isButtonDown(1))) && ((entityPlayer.inventory.hasItem(this.ammo)) || (entityPlayer.capabilities.isCreativeMode)))
/*    */       {
/* 57 */         GunCus.shootTime += 90;
/* 58 */         tube(entityPlayer);
/* 59 */         recoilTube(entityPlayer);
/* 60 */         Minecraft.getMinecraft().sndManager.playSoundFX("guncus:reload_rpg", 1.0F, 1.0F);
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   private void tube(EntityPlayer entityPlayer)
/*    */   {
/* 67 */     ByteArrayDataOutput bytes = ByteStreams.newDataOutput();
/* 68 */     bytes.writeInt(8);
/* 69 */     bytes.writeInt(MathHelper.floor_double(GunCus.accuracy));
/* 70 */     PacketDispatcher.sendPacketToServer(new Packet250CustomPayload("guncus", bytes.toByteArray()));
/*    */   }
/*    */ 
/*    */   private void recoilTube(EntityPlayer entityPlayer)
/*    */   {
/* 75 */     float strength = 1.5F;
/*    */ 
/* 77 */     entityPlayer.rotationPitch -= strength;
/* 78 */     entityPlayer.rotationYaw -= (Item.itemRand.nextBoolean() ? strength / 2.0F : -strength / 2.0F);
/*    */   }
/*    */ }

/* Location:           C:\Users\Nate\Desktop\Mod\GunCusClass.zip
 * Qualified Name:     assets.guncusexplosives.GunCusExplosivesRPG
 * JD-Core Version:    0.6.2
 */