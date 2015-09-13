package stuuupiiid.guncus.network;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.item.ItemMag;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
		simpleNetworkManager.registerMessage(MessageSyncEntity.class , MessageSyncEntity.class , 20, Side.CLIENT);
		
		simpleNetworkManager.registerMessage(MessageGunShoot.class , MessageGunShoot.class , 101, Side.SERVER);	// legacy 1
		simpleNetworkManager.registerMessage(MessageGUIaction.class , MessageGUIaction.class , 103, Side.SERVER);	// legacy 3 to 7
		simpleNetworkManager.registerMessage(MessageTubeShoot.class , MessageTubeShoot.class , 108, Side.SERVER);	// legacy 8
		simpleNetworkManager.registerMessage(MessageKnife.class , MessageKnife.class , 113, Side.SERVER);	// legacy 13
		simpleNetworkManager.registerMessage(MessageKickPlayer.class , MessageKickPlayer.class , 114, Side.SERVER);	// legacy 14
	}
	
	public static void sendToClient_showHitMarker(World worldObj, final Vec3 vecHit, EntityPlayer entityPlayer) {
		MessageShowHitMarker bulletSoundMessage = new MessageShowHitMarker();
		simpleNetworkManager.sendTo(bulletSoundMessage, (EntityPlayerMP)entityPlayer);
	}
	
	public static void sendToServer_GUIaction(final int guiId, final int buttonId) {
		sendToServer_GUIaction(guiId, buttonId, 0);
	}
	
	public static void sendToServer_GUIaction(final int guiId, final int buttonId, final int currentGun) {
		MessageGUIaction guiActionMessage = new MessageGUIaction(guiId, buttonId, currentGun);
		simpleNetworkManager.sendToServer(guiActionMessage);
	}
	
	public static void sendToServer_playerAction_shoot(EntityPlayer entityPlayer, ItemMag mag, int[] bullets, int actualBullet) {
		MessageGunShoot gunShootMessage = new MessageGunShoot(MathHelper.floor_double(GunCus.accuracy), (mag == null) ? bullets[actualBullet] : -1);
		simpleNetworkManager.sendToServer(gunShootMessage);
	}
	
	public static void sendToServer_playerAction_tube() {
		MessageTubeShoot tubeShootMessage = new MessageTubeShoot(MathHelper.floor_double(GunCus.accuracy));
		simpleNetworkManager.sendToServer(tubeShootMessage);
	}
	
	public static void sendToServer_playerAction_knife() {
		MessageKnife knifeMessage = new MessageKnife();
		simpleNetworkManager.sendToServer(knifeMessage);
	}
	
	public static void sendToClient_syncEntity(Entity entity) {
		MessageSyncEntity syncEntityMessage = new MessageSyncEntity((ISynchronisingEntity)entity);
		simpleNetworkManager.sendToAllAround(syncEntityMessage, new TargetPoint(entity.worldObj.provider.dimensionId, entity.posX, entity.posY, entity.posZ, 60));
	}
}
