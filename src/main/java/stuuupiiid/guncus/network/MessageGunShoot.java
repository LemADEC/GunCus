package stuuupiiid.guncus.network;

import stuuupiiid.guncus.GunCus;
import stuuupiiid.guncus.entity.EntityBullet;
import stuuupiiid.guncus.item.ItemBullet;
import stuuupiiid.guncus.item.ItemGun;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageGunShoot implements IMessage, IMessageHandler<MessageGunShoot, IMessage> {
	private int accuracy;
	private int bulletType;
	
	public MessageGunShoot() {
		// required on receiving side
	}
	
	public MessageGunShoot(final int accuracy, final int bulletType) {
		this.accuracy = accuracy;
		this.bulletType = bulletType;
	}
	
	@Override
	public void fromBytes(ByteBuf buffer) {
		accuracy = buffer.readInt();
		bulletType = buffer.readInt();
	}
	
	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(accuracy);
		buffer.writeInt(bulletType);
	}
	
	private void handle(EntityPlayerMP playerEntity) {
		
		if ((playerEntity != null) && (playerEntity.inventory.getCurrentItem() != null)
				&& ((playerEntity.inventory.getCurrentItem().getItem() instanceof ItemGun))) {
			ItemGun gun = (ItemGun) playerEntity.inventory.getCurrentItem().getItem();
			int metadata = playerEntity.inventory.getCurrentItem().getItemDamage();
			ItemStack mag = null;

			if (gun.mag != null) {
				for (int v1 = 0; v1 < playerEntity.inventory.getSizeInventory(); v1++) {
					if ((playerEntity.inventory.getStackInSlot(v1) != null)
							&& (playerEntity.inventory.getStackInSlot(v1).getItem() == gun.mag)
							&& (playerEntity.inventory.getStackInSlot(v1).isItemDamaged())
							&& (playerEntity.inventory.getStackInSlot(v1).getItemDamage() < playerEntity.inventory
									.getStackInSlot(v1).getMaxDamage())) {
						mag = playerEntity.inventory.getStackInSlot(v1);
						break;
					}
				}

				if (mag == null) {
					for (int v1 = 0; v1 < playerEntity.inventory.getSizeInventory(); v1++) {
						if ((playerEntity.inventory.getStackInSlot(v1) != null)
								&& (playerEntity.inventory.getStackInSlot(v1).getItem() == gun.mag)
								&& (!playerEntity.inventory.getStackInSlot(v1).isItemDamaged())) {
							mag = playerEntity.inventory.getStackInSlot(v1);
							break;
						}
					}
				}
			}
			
			if (gun.mag != null) {
				assert bulletType == -1;
			}
			
			if ( ((mag != null) && (gun.mag != null))
					|| ((gun.mag == null) && (playerEntity.inventory.hasItem(ItemBullet.bulletsList.get(gun.pack).get(bulletType))))
					|| (playerEntity.capabilities.isCreativeMode)) {
				if ((!playerEntity.capabilities.isCreativeMode) && (gun.mag != null) && (mag != null)) {
					mag.damageItem(1, playerEntity);
				} else if ((!playerEntity.capabilities.isCreativeMode) && (gun.mag == null)) {
					playerEntity.inventory.consumeInventoryItem(ItemBullet.bulletsList.get(gun.pack).get(bulletType));
				}

				if (gun.hasSilencer(metadata)) {
					playerEntity.worldObj.playSoundAtEntity(playerEntity, gun.soundSilenced, 1.0F, 1.0F / (playerEntity.worldObj.rand.nextFloat() * 0.4F + 0.8F));
				} else {
					playerEntity.worldObj.playSoundAtEntity(playerEntity, gun.soundNormal, 5.0F * (float) gun.soundModify, 1.0F / (playerEntity.worldObj.rand.nextFloat() * 0.4F + 0.8F));
				}
				ItemBullet bulletItem;
				if (gun.mag != null) {
					bulletItem = ItemBullet.bulletsList.get(gun.pack).get(gun.mag.bulletType);
				} else {
					bulletItem = ItemBullet.bulletsList.get(gun.pack).get(bulletType);
				}
				
				if (accuracy > bulletItem.spray) {
					accuracy = bulletItem.spray;
				}
				
				float damage = gun.damage * bulletItem.damage;
				
				if (gun.hasHeavyBarrel(metadata)) {
					damage += 2.0F;
				}
				if (gun.hasStrongSpiralSpring(metadata)) {
					damage += 1.0F;
				}
				
				for (int v1 = 0; v1 < bulletItem.split; v1++) {
					EntityBullet bulletEntity = new EntityBullet(playerEntity.worldObj, playerEntity, 10.0F, damage, accuracy)
							.setLowerGravity(gun.hasPolygonalBarrel(metadata))
							.setBullet(bulletItem);
					playerEntity.worldObj.spawnEntityInWorld(bulletEntity);
				}
				
				if ((!playerEntity.capabilities.isCreativeMode) && (mag != null) && (mag.getItemDamage() >= mag.getMaxDamage())) {
					// reloading...
					MessageReloading reloadingMessage = new MessageReloading();
					
					PacketHandler.simpleNetworkManager.sendTo(reloadingMessage, playerEntity);
				}
			}
		}
	}
	
	@Override
	public IMessage onMessage(MessageGunShoot gunShootMessage, MessageContext context) {
		if (GunCus.logging_enableNetwork) {
			GunCus.logger.info("Received gunShoot packet: (accuracy " + gunShootMessage.accuracy + " bulletType " + gunShootMessage.bulletType);
		}
		
		gunShootMessage.handle(context.getServerHandler().playerEntity);
		
		return null;	// no response
	}
}
