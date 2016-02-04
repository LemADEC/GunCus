package stuuupiiid.guncus;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

// import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

public class GunCusResourceLoader /* extends AbstractResourcePack /**/ implements IResourcePack {
	/*
	public GunCusResourceLoader(File file) {
		super(file);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected InputStream getInputStreamByName(String p_110591_1_) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected boolean hasResourceName(String p_110593_1_) {
		// TODO Auto-generated method stub
		return false;
	}
	/**/

	private HashMap<String,String> domainToFolderNames = new HashMap<String,String>();
	
	@Override
	public InputStream getInputStream(ResourceLocation resourceLocation) throws IOException {
		File file = new File("GunCus/" + domainToFolderNames.get(resourceLocation.getResourceDomain()), resourceLocation.getResourcePath());
		if (!file.exists()) {
			return null;
		}
		return new FileInputStream(file);
	}
	
	@Override
	public boolean resourceExists(ResourceLocation resourceLocation) {
		File file = new File("GunCus/" + domainToFolderNames.get(resourceLocation.getResourceDomain()), resourceLocation.getResourcePath());
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
			if (fileSubFolder.exists() && fileSubFolder.isDirectory() && !fileSubFolder.getName().equalsIgnoreCase("template") && !fileSubFolder.getName().equalsIgnoreCase("default")) {
				folders.add(fileSubFolder.getName().toLowerCase());
				if (domainToFolderNames.containsKey(fileSubFolder.getName().toLowerCase())) {
					GunCus.logger.warn("Warning: you've overlapping ressource domains with the same name '" + fileSubFolder.getName().toLowerCase() + "', results will be non-deterministic until you fix it!");
				}
				domainToFolderNames.put(fileSubFolder.getName().toLowerCase(), fileSubFolder.getName());
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
