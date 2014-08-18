package assets.guncus;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;

public class GunCusConnectionHandler implements IConnectionHandler {
	@Override
	public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager) {
		for (int v1 = 0; v1 < GunCus.instance.guns.length; v1++) {
			if (GunCus.instance.guns[v1] == 1) {
				int id = v1;
				int shootType = GunCus.instance.gunShoots[v1];
				int delay = GunCus.instance.gunDelays[v1];
				int magId = GunCus.instance.gunMags[v1];
				int bullets = GunCus.instance.gunBullets[v1];
				int rec = GunCus.instance.gunRecoils[v1];

				ByteArrayDataOutput bytes = ByteStreams.newDataOutput();
				bytes.writeShort(14);
				bytes.writeShort(0);
				bytes.writeShort(1);
				bytes.writeShort(id);
				bytes.writeShort(shootType);
				bytes.writeShort(delay);
				bytes.writeShort(magId);
				bytes.writeShort(bullets);
				bytes.writeShort(rec);
				PacketDispatcher.sendPacketToPlayer(new Packet250CustomPayload("guncus", bytes.toByteArray()), player);
			}
		}
	}

	@Override
	public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager) {
		return null;
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager) {
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager) {
	}

	@Override
	public void connectionClosed(INetworkManager manager) {
	}

	@Override
	public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login) {
	}
}
