package stuuupiiid.guncus.network;

import net.minecraft.nbt.NBTTagCompound;

public interface ISynchronisingEntity {
	public int getEntityId();
	
	public NBTTagCompound writeSyncDataCompound();
	
	public void readSyncDataCompound(NBTTagCompound syncDataCompound);
}