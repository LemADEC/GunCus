package stuuupiiid.guncus.network;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.entity.EntityGrenade;
import stuuupiiid.guncus.entity.EntityRocket;
import stuuupiiid.guncus.item.ItemGun;
import stuuupiiid.guncus.item.ItemRPG;
import net.minecraft.entity.player.EntityPlayerMP;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageTubeShoot implements IMessage, IMessageHandler<MessageTubeShoot, IMessage> {
	private float accuracy;
	
	public MessageTubeShoot() {
		// required on receiving side
	}
	
	public MessageTubeShoot(final float accuracy) {
		this.accuracy = accuracy;
	}
	
	@Override
	public void fromBytes(ByteBuf buffer) {
		accuracy = buffer.readInt();
	}
	
	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeFloat(accuracy);
	}
	
	private void handle(EntityPlayerMP entityPlayer) {
		if ((entityPlayer != null) && (entityPlayer.inventory.getCurrentItem() != null)) {
			if (entityPlayer.inventory.getCurrentItem().getItem() instanceof ItemGun) {
				ItemGun gun = (ItemGun) entityPlayer.inventory.getCurrentItem().getItem();
				int metadata = entityPlayer.inventory.getCurrentItem().getItemDamage();
				
				if ( gun.hasM320(metadata)
				  && (entityPlayer.capabilities.isCreativeMode || entityPlayer.inventory.consumeInventoryItem(GunCus.ammoM320)) ) {
					// m320 - 75m/s  => 3.75 blocks per tick
					EntityGrenade grenade = new EntityGrenade(entityPlayer.worldObj, entityPlayer, 3.75F, accuracy);
					entityPlayer.worldObj.playSoundAtEntity(entityPlayer, "random.explode", 4.0F, 1.0F);
					entityPlayer.worldObj.spawnEntityInWorld(grenade);
				}
			} else if ((entityPlayer.inventory.getCurrentItem().getItem() == GunCus.attachment)
					&& (entityPlayer.inventory.getCurrentItem().getItemDamage() == 4)) {
				if (entityPlayer.capabilities.isCreativeMode || entityPlayer.inventory.consumeInventoryItem(GunCus.ammoM320)) {
					// m320 - 75m/s  => 3.75 blocks per tick
					EntityGrenade grenade = new EntityGrenade(entityPlayer.worldObj, entityPlayer, 3.75F, accuracy);
					entityPlayer.worldObj.playSoundAtEntity(entityPlayer, "random.explode", 4.0F, 1.0F);
					entityPlayer.worldObj.spawnEntityInWorld(grenade);
				}
			} else if (entityPlayer.inventory.getCurrentItem().getItem() instanceof ItemRPG) {
				ItemRPG rpg = (ItemRPG) entityPlayer.inventory.getCurrentItem().getItem();
				if (entityPlayer.capabilities.isCreativeMode || entityPlayer.inventory.consumeInventoryItem(rpg.ammo)) {
					// rpg  - 115m/s => 5.75 blocks per tick
					// smaw - 220m/s => 11 blocks per tick
					float speed = (rpg == GunCus.rpg) ? 5.75F : 11.0F;
					EntityGrenade rocket = new EntityRocket(entityPlayer.worldObj, entityPlayer, speed, accuracy);
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
