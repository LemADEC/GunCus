package stuuupiiid.guncus;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

public class GunCusResourceLoader implements IResourcePack {
	
	@Override
	public InputStream getInputStream(ResourceLocation resourceLocation) throws IOException {
		File file = new File("GunCus/" + resourceLocation.getResourceDomain(), resourceLocation.getResourcePath());
		if (!file.exists()) {
			return null;
		}
		return new FileInputStream(file);
	}
	
	@Override
	public boolean resourceExists(ResourceLocation resourceLocation) {
		File file = new File("GunCus/" + resourceLocation.getResourceDomain(), resourceLocation.getResourcePath());
		if (file.exists()) {
			return true;
		}
		return false;
	}
	
	@Override
	public Set getResourceDomains() {
		File fileBaseFolder = new File("GunCus");
		if (!fileBaseFolder.exists()) {
			fileBaseFolder.mkdir();
		}
		
		HashSet<String> folders = new HashSet();
		for (String stringSubFolder : fileBaseFolder.list()) {
			File fileSubFolder = new File(fileBaseFolder, stringSubFolder);
			if (fileSubFolder.exists() && fileSubFolder.isDirectory()) {
				folders.add(fileSubFolder.getName().toLowerCase());
			}
		}
		return folders;
	}
	
	@Override
	public IMetadataSection getPackMetadata(IMetadataSerializer p_135058_1_, String p_135058_2_) throws IOException {
		return null;
	}
	
	@Override
	public BufferedImage getPackImage() throws IOException {
		return null;
	}
	
	@Override
	public String getPackName() {
		return "GunCus resources";
	}
}
