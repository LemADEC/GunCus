package stuuupiiid.guncus.data;


public class ModifierPart {
	public int id;
	public String unlocalizedName;
	
	public ModifierPart(final int id, final String unlocalizedName) {
		this.id = id;
		this.unlocalizedName = unlocalizedName;
	}
}


/*
public enum ModifierPart {
	NONE          (0, "-"),
	SILENCER      (1, "silencer"),
	HEAVY         (2, "heavy"),
	RIFLED        (3, "rifled"),
	POLYGONAL     (4, "polygonal");
	
	public final int    id;
	public final String unlocalizedName;
	
	// cached values
	public static final int length;
	private static final HashMap<Integer, ModifierPart> ID_MAP = new HashMap<Integer, ModifierPart>();
	
	static {
		length = ModifierPart.values().length;
		for (ModifierPart componentType : values()) {
			ID_MAP.put(componentType.ordinal(), componentType);
		}
	}
	
	private ModifierPart(final int id, final String unlocalizedName) {
		this.id = id;
		this.unlocalizedName = unlocalizedName;
	}
	
	public static ModifierPart get(final int damage) {
		return ID_MAP.get(damage);
	}
}
/**/