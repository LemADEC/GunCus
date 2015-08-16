package stuuupiiid.guncus.network;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.item.ItemGun;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class GunCusConnectionHandler {
	@SubscribeEvent
	public void PlayerLoggedInEvent(EntityPlayer player) {
		for (ItemGun gun : GunCus.instance.guns) {
			ByteArrayDataOutput bytes = ByteStreams.newDataOutput();
			bytes.writeShort(14);
			bytes.writeShort(0);
			bytes.writeShort(1);
			bytes.writeUTF(gun.name);
			bytes.writeShort(gun.shootType);
			bytes.writeShort(gun.delay);
			bytes.writeUTF(gun.mag.getUnlocalizedName());
			bytes.writeShort(gun.mag.bulletType);
			bytes.writeDouble(gun.recModify);
			PacketDispatcher.sendPacketToPlayer(new Packet250CustomPayload("guncus", bytes.toByteArray()), player);
		}
	}
}
