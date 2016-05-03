package stuuupiiid.guncus.data;

public class ScopePart extends ModifierPart {
	public float zoom;
	
	public ScopePart(final int id, final String unlocalizedName, final float zoom) {
		super(id, unlocalizedName);
		this.zoom = zoom;
	}
}
