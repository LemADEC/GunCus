package stuuupiiid.guncus.network;

import java.util.List;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.item.ItemMag;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import cr0s.warpdrive.data.Vector3;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class PacketHandler {
	public static final SimpleNetworkWrapper simpleNetworkManager = NetworkRegistry.INSTANCE.newSimpleChannel(GunCus.MODID);
	
	public static void init() {
		// Forge packets
		simpleNetworkManager.registerMessage(MessageReloading.class , MessageReloading.class , 2, Side.CLIENT);	// legacy 2
		simpleNetworkManager.registerMessage(MessageShowHitMarker.class , MessageShowHitMarker.class , 10, Side.CLIENT);	// legacy 10
		simpleNetworkManager.registerMessage(MessageClientValidation.class , MessageClientValidation.class , 14, Side.CLIENT);	// legacy 14
		simpleNetworkManager.registerMessage(MessageWeaponBoxSelection.class , MessageWeaponBoxSelection.class , 15, Side.CLIENT);	// legacy 15
		
		simpleNetworkManager.registerMessage(MessageGunShoot.class , MessageGunShoot.class , 101, Side.SERVER);	// legacy 1
		simpleNetworkManager.registerMessage(MessageGUIaction.class , MessageGUIaction.class , 103, Side.SERVER);	// legacy 3 to 7
		simpleNetworkManager.registerMessage(MessageTubeShoot.class , MessageTubeShoot.class , 108, Side.SERVER);	// legacy 8
		simpleNetworkManager.registerMessage(MessageKnife.class , MessageKnife.class , 113, Side.SERVER);	// legacy 13
		simpleNetworkManager.registerMessage(MessageKickPlayer.class , MessageKickPlayer.class , 114, Side.SERVER);	// legacy 14
	}
	
	public static void sendToClient_showHitMarker(World worldObj, final Vec3 vecHit, EntityPlayer entityPlayer) {
		MessageShowHitMarker bulletSoundMessage = new MessageShowHitMarker();
//			simpleNetworkManager.sendToAllAround(bulletSoundMessage, new TargetPoint(worldObj.provider.dimensionId, vecHit.xCoord, vecHit.yCoord, vecHit.zCoord, 60));
		if (true) {
			List<EntityPlayerMP> playerEntityList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
			int dimensionId = worldObj.provider.dimensionId;
			int radius_square = 3600;
			for (int index = 0; index < playerEntityList.size(); index++) {
				EntityPlayerMP entityplayermp = playerEntityList.get(index);
				
				if (entityPlayer.getUniqueID() == entityplayermp.getUniqueID()) {
					simpleNetworkManager.sendTo(bulletSoundMessage, entityplayermp);
				}
				/*
				if (entityplayermp.dimension == dimensionId) {
					double distance_square = (entityplayermp.posX - vecHit.xCoord) * (entityplayermp.posX - vecHit.xCoord)
							+ (entityplayermp.posY - vecHit.yCoord) * (entityplayermp.posY - vecHit.yCoord)
							+ (entityplayermp.posZ - vecHit.zCoord) * (entityplayermp.posZ - vecHit.zCoord);
					if (distance_square < radius_square) {
						simpleNetworkManager.sendTo(bulletSoundMessage, entityplayermp);
					}
				}
				/**/
			}
		}
		PacketHandler.simpleNetworkManager.sendTo(bulletSoundMessage, (EntityPlayerMP)entityPlayer);
	}
	
	public static void sendToServer_GUIaction(final int guiId, final int buttonId) {
		sendToServer_GUIaction(guiId, buttonId, 0);
	}
	
	public static void sendToServer_GUIaction(final int guiId, final int buttonId, final int currentGun) {
		MessageGUIaction guiActionMessage = new MessageGUIaction(guiId, buttonId, currentGun);
		PacketHandler.simpleNetworkManager.sendToServer(guiActionMessage);
	}
	
	public static void sendToServer_playerAction_shoot(EntityPlayer entityPlayer, ItemMag mag, int[] bullets, int actualBullet) {
		MessageGunShoot gunShootMessage = new MessageGunShoot(MathHelper.floor_double(GunCus.accuracy), (mag == null) ? bullets[actualBullet] : -1);
		PacketHandler.simpleNetworkManager.sendToServer(gunShootMessage);
	}
	
	public static void sendToServer_playerAction_tube() {
		MessageTubeShoot tubeShootMessage = new MessageTubeShoot(MathHelper.floor_double(GunCus.accuracy));
		PacketHandler.simpleNetworkManager.sendToServer(tubeShootMessage);
	}
	
	public static void sendToServer_playerAction_knife() {
		MessageKnife knifeMessage = new MessageKnife();
		PacketHandler.simpleNetworkManager.sendToServer(knifeMessage);
	}
}
