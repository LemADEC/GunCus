package stuuupiiid.guncus.event;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.network.MessageClientValidation;
import stuuupiiid.guncus.network.PacketHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public class ConnectionHandler {
	
	// Server side
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event){
		if (event.entity instanceof EntityPlayer) {
			GunCus.logger.info("onEntityJoinWorld " + event.entity);
			if (!event.world.isRemote) {
				for (int gunIndex = 0; gunIndex < GunCus.instance.guns.size(); gunIndex++) {
					MessageClientValidation clientConnectionMessage = new MessageClientValidation(gunIndex);
					PacketHandler.simpleNetworkManager.sendTo(clientConnectionMessage, (EntityPlayerMP) event.entity);
				}
			}
		}
	}
}
