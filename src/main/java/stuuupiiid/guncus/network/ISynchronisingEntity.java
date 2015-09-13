package stuuupiiid.guncus.network;

import net.minecraft.nbt.NBTTagCompound;

public interface ISynchronisingEntity {
	public int getEntityId();
	
	public NBTTagCompound getSyncDataCompound();
	
	public void setSyncDataCompound(NBTTagCompound syncDataCompound);
}