/*    */ package assets.guncus;
/*    */ 
/*    */ import com.google.common.io.ByteArrayDataOutput;
/*    */ import com.google.common.io.ByteStreams;
/*    */ import cpw.mods.fml.common.network.IConnectionHandler;
/*    */ import cpw.mods.fml.common.network.PacketDispatcher;
/*    */ import cpw.mods.fml.common.network.Player;
/*    */ import net.minecraft.network.INetworkManager;
/*    */ import net.minecraft.network.NetLoginHandler;
/*    */ import net.minecraft.network.packet.NetHandler;
/*    */ import net.minecraft.network.packet.Packet1Login;
/*    */ import net.minecraft.network.packet.Packet250CustomPayload;
/*    */ import net.minecraft.server.MinecraftServer;
/*    */ 
/*    */ public class GunCusConnectionHandler
/*    */   implements IConnectionHandler
/*    */ {
/*    */   public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager)
/*    */   {
/* 21 */     for (int v1 = 0; v1 < GunCus.instance.guns.length; v1++)
/*    */     {
/* 23 */       if (GunCus.instance.guns[v1] == 1)
/*    */       {
/* 35 */         int id = v1;
/* 36 */         int shootType = GunCus.instance.gunShoots[v1];
/* 37 */         int delay = GunCus.instance.gunDelays[v1];
/* 38 */         int magId = GunCus.instance.gunMags[v1];
/* 39 */         int bullets = GunCus.instance.gunBullets[v1];
/* 40 */         int rec = GunCus.instance.gunRecoils[v1];
/*    */ 
/* 42 */         ByteArrayDataOutput bytes = ByteStreams.newDataOutput();
/* 43 */         bytes.writeShort(14);
/* 44 */         bytes.writeShort(0);
/* 45 */         bytes.writeShort(1);
/* 46 */         bytes.writeShort(id);
/* 47 */         bytes.writeShort(shootType);
/* 48 */         bytes.writeShort(delay);
/* 49 */         bytes.writeShort(magId);
/* 50 */         bytes.writeShort(bullets);
/* 51 */         bytes.writeShort(rec);
/* 52 */         PacketDispatcher.sendPacketToPlayer(new Packet250CustomPayload("guncus", bytes.toByteArray()), player);
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager)
/*    */   {
/* 60 */     return null;
/*    */   }
/*    */ 
/*    */   public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void connectionClosed(INetworkManager manager)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login)
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\Users\Nate\Desktop\Mod\GunCusClass.zip
 * Qualified Name:     assets.guncus.GunCusConnectionHandler
 * JD-Core Version:    0.6.2
 */