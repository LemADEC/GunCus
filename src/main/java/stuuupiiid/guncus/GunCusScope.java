package stuuupiiid.guncus;

public class GunCusScope extends GunCusCustomizationPart {
	public String sight;
	public float zoom;

	public GunCusScope(String localized, String sight, float zoom, int id) {
		super(localized, "-scp", id);
		this.sight = sight;
		this.zoom = zoom;
	}
}
