/*    */ package assets.guncus;
/*    */ 
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
/*    */ public class GunCusItemAttachment extends GunCusItemMetadata
/*    */ {
/*    */   public GunCusItemAttachment(int par1, String unlocalized, String iconName, GunCusCustomizationPart[] metadatas)
/*    */   {
/* 26 */     super(par1, unlocalized, iconName, metadatas);
/*    */   }
/*    */ 
/*    */   public void onUpdate(ItemStack itemStack, World world, Entity entity, int par1, boolean flag)
/*    */   {
/* 32 */     if ((FMLCommonHandler.instance().getEffectiveSide().isClient()) && (itemStack.getItemDamage() == 3))
/*    */     {
/* 34 */       doUpdate(itemStack, world, entity, par1, flag);
/*    */     }
/*    */   }
/*    */ 
/*    */   @SideOnly(Side.CLIENT)
/*    */   public void doUpdate(ItemStack itemStack, World world, Entity entity, int par1, boolean flag)
/*    */   {
/* 41 */     Minecraft client = FMLClientHandler.instance().getClient();
/* 42 */     EntityPlayer entityPlayer = client.thePlayer;
/* 43 */     if ((entityPlayer != null) && (entityPlayer.inventory.getCurrentItem() != null) && (entityPlayer.inventory.getCurrentItem().getItem() == this))
/*    */     {
/* 45 */       if ((entityPlayer.inventory.getCurrentItem().getItemDamage() == 3) && (GunCus.shootTime <= 0) && (Mouse.isButtonDown(0)) && ((client.currentScreen == null) || (Mouse.isButtonDown(1))) && ((entityPlayer.inventory.hasItem(GunCus.ammoM320.itemID)) || (entityPlayer.capabilities.isCreativeMode)))
/*    */       {
/* 47 */         GunCus.shootTime += 95;
/* 48 */         tube(entityPlayer);
/* 49 */         recoilTube(entityPlayer);
/* 50 */         Minecraft.getMinecraft().sndManager.playSoundFX("guncus:reload_tube", 1.0F, 1.0F);
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   private void tube(EntityPlayer entityPlayer)
/*    */   {
/* 57 */     ByteArrayDataOutput bytes = ByteStreams.newDataOutput();
/* 58 */     bytes.writeInt(8);
/* 59 */     bytes.writeInt(MathHelper.floor_double(GunCus.accuracy));
/* 60 */     PacketDispatcher.sendPacketToServer(new Packet250CustomPayload("guncus", bytes.toByteArray()));
/*    */   }
/*    */ 
/*    */   private void recoilTube(EntityPlayer entityPlayer)
/*    */   {
/* 65 */     float strength = 1.5F;
/*    */ 
/* 67 */     entityPlayer.rotationPitch -= strength;
/* 68 */     entityPlayer.rotationYaw -= (Item.itemRand.nextBoolean() ? strength / 2.0F : -strength / 2.0F);
/*    */   }
/*    */ }

/* Location:           C:\Users\Nate\Desktop\Mod\GunCusClass.zip
 * Qualified Name:     assets.guncus.GunCusItemAttachment
 * JD-Core Version:    0.6.2
 */