package stuuupiiid.guncus.network;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.entity.EntityGrenade;
import stuuupiiid.guncus.item.ItemGun;
import stuuupiiid.guncus.item.ItemRPG;
import net.minecraft.entity.player.EntityPlayerMP;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageTubeShoot implements IMessage, IMessageHandler<MessageTubeShoot, IMessage> {
	private int accuracy;
	
	public MessageTubeShoot() {
		// required on receiving side
	}
	
	public MessageTubeShoot(final int accuracy) {
		this.accuracy = accuracy;
	}
	
	@Override
	public void fromBytes(ByteBuf buffer) {
		accuracy = buffer.readInt();
	}
	
	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(accuracy);
	}
	
	private void handle(EntityPlayerMP entityPlayer) {
		if ((entityPlayer != null) && (entityPlayer.inventory.getCurrentItem() != null)) {
			if (entityPlayer.inventory.getCurrentItem().getItem() instanceof ItemGun) {
				ItemGun gun = (ItemGun) entityPlayer.inventory.getCurrentItem().getItem();
				int metadata = entityPlayer.inventory.getCurrentItem().getItemDamage();
				
				if ( gun.hasM320(metadata)
				  && (entityPlayer.capabilities.isCreativeMode || entityPlayer.inventory.consumeInventoryItem(GunCus.ammoM320)) ) {
					EntityGrenade rocket = new EntityGrenade(entityPlayer.worldObj, entityPlayer, accuracy, false);
					entityPlayer.worldObj.playSoundAtEntity(entityPlayer, "random.explode", 4.0F, 1.0F);
					entityPlayer.worldObj.spawnEntityInWorld(rocket);
				}
			} else if ((entityPlayer.inventory.getCurrentItem().getItem() == GunCus.attachment)
					&& (entityPlayer.inventory.getCurrentItem().getItemDamage() == 4)) {
				if (entityPlayer.capabilities.isCreativeMode || entityPlayer.inventory.consumeInventoryItem(GunCus.ammoM320)) {
					EntityGrenade rocket = new EntityGrenade(entityPlayer.worldObj, entityPlayer, accuracy, false);
					entityPlayer.worldObj.playSoundAtEntity(entityPlayer, "random.explode", 4.0F, 1.0F);
					entityPlayer.worldObj.spawnEntityInWorld(rocket);
				}
			} else if (entityPlayer.inventory.getCurrentItem().getItem() instanceof ItemRPG) {
				ItemRPG rpg = (ItemRPG) entityPlayer.inventory.getCurrentItem().getItem();
				if (entityPlayer.capabilities.isCreativeMode || entityPlayer.inventory.consumeInventoryItem(rpg.ammo)) {
					EntityGrenade rocket = new EntityGrenade(entityPlayer.worldObj, entityPlayer, accuracy, true);
					entityPlayer.worldObj.playSoundAtEntity(entityPlayer, "random.explode", 4.0F, 1.0F);
					entityPlayer.worldObj.spawnEntityInWorld(rocket);
				}
			}
		}
	}
	
	@Override
	public IMessage onMessage(MessageTubeShoot tubeShootMessage, MessageContext context) {
		if (GunCus.logging_enableNetwork) {
			GunCus.logger.info("Received tubeShoot packet: (accuracy " + tubeShootMessage.accuracy + ")");
		}
		
		tubeShootMessage.handle(context.getServerHandler().playerEntity);
		
		return null;	// no response
	}
}
