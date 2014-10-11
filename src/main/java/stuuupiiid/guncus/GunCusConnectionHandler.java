package stuuupiiid.guncus;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerConnectionFromClientEvent;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;

public class GunCusConnectionHandler {
	@Override
	public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager) {
		for (GunCusItemGun gun : GunCus.instance.guns) {
			ByteArrayDataOutput bytes = ByteStreams.newDataOutput();
			bytes.writeShort(14);
			bytes.writeShort(0);
			bytes.writeShort(1);
			bytes.writeUTF(gun.name);
			bytes.writeShort(gun.shootType);
			bytes.writeShort(gun.delay);
			bytes.writeString(gun.mag.name);
			bytes.writeShort(gun.mag.bulletType);
			bytes.writeDouble(gun.recModify);
			PacketDispatcher.sendPacketToPlayer(new Packet250CustomPayload("guncus", bytes.toByteArray()), player);
		}
	}
	
	@SubscribeEvent
	public void onServerConnectionFromClientEvent(ServerConnectionFromClientEvent event) {
		// FIXME: how to get player to send it's configuration?
		// event.
	}
}
