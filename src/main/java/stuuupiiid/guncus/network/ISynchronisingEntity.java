package stuuupiiid.guncus.network;

import net.minecraft.nbt.NBTTagCompound;

public interface ISynchronisingEntity {
	public NBTTagCompound writeSyncDataCompound();
	
	public void readSyncDataCompound(NBTTagCompound syncDataCompound);
}