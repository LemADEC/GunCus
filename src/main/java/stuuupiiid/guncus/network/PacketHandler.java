package stuuupiiid.guncus.network;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.item.ItemMagazine;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
	public static final SimpleNetworkWrapper simpleNetworkManager = NetworkRegistry.INSTANCE.newSimpleChannel(GunCus.MODID);
	
	public static void init() {
		// Forge packets
		simpleNetworkManager.registerMessage(MessageReloading.class , MessageReloading.class , 2, Side.CLIENT);
		simpleNetworkManager.registerMessage(MessageShowHitMarker.class , MessageShowHitMarker.class , 10, Side.CLIENT);
		simpleNetworkManager.registerMessage(MessageClientValidation.class , MessageClientValidation.class , 14, Side.CLIENT);
		simpleNetworkManager.registerMessage(MessageWeaponBoxSelection.class , MessageWeaponBoxSelection.class , 15, Side.CLIENT);
		simpleNetworkManager.registerMessage(MessageSyncEntity.class , MessageSyncEntity.class , 20, Side.CLIENT);
		
		simpleNetworkManager.registerMessage(MessageGunShoot.class , MessageGunShoot.class , 101, Side.SERVER);
		simpleNetworkManager.registerMessage(MessageGUIaction.class , MessageGUIaction.class , 103, Side.SERVER);
		simpleNetworkManager.registerMessage(MessageTubeShoot.class , MessageTubeShoot.class , 108, Side.SERVER);
		simpleNetworkManager.registerMessage(MessageKnife.class , MessageKnife.class , 113, Side.SERVER);
		simpleNetworkManager.registerMessage(MessageKickPlayer.class , MessageKickPlayer.class , 114, Side.SERVER);
	}
	
	public static void sendToClient_showHitMarker(World worldObj, final Vec3 vecHit, EntityPlayer entityPlayer) {
		MessageShowHitMarker bulletSoundMessage = new MessageShowHitMarker();
		simpleNetworkManager.sendTo(bulletSoundMessage, (EntityPlayerMP)entityPlayer);
	}
	
	public static void sendToServer_GUIaction(final int guiId, final int buttonId) {
		sendToServer_GUIaction(guiId, buttonId, "");
	}
	
	public static void sendToServer_GUIaction(final int guiId, final int buttonId, final String actualGunName) {
		MessageGUIaction guiActionMessage = new MessageGUIaction(guiId, buttonId, actualGunName);
		simpleNetworkManager.sendToServer(guiActionMessage);
	}
	
	public static void sendToServer_playerAction_shoot(EntityPlayer entityPlayer, ItemMagazine mag, int[] bullets, int actualBullet) {
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
		simpleNetworkManager.sendToAllAround(syncEntityMessage, new TargetPoint(entity.worldObj.provider.getDimensionId(), entity.posX, entity.posY, entity.posZ, 60));
	}
}
