package stuuupiiid.guncus;

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
		for (int id = 0; id < GunCus.instance.guns.length; id++) {
			if (GunCus.instance.guns[id] == 1) {
				int shootType = GunCus.instance.gunShoots[id];
				int delay = GunCus.instance.gunDelays[id];
				int magId = GunCus.instance.gunMags[id];
				int bullets = GunCus.instance.gunBullets[id];
				int rec = GunCus.instance.gunRecoils[id];

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
