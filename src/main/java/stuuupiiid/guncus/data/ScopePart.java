package stuuupiiid.guncus.data;

public class ScopePart extends CustomizationPart {
	public String sight;
	public float zoom;

	public ScopePart(String sight, float zoom, int id) {
		super("scp", id);
		this.sight = sight;
		this.zoom = zoom;
	}
}
