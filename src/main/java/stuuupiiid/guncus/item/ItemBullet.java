package stuuupiiid.guncus.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import stuuupiiid.guncus.GunCus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemBullet extends GunCusItem {
	public int bulletId;
	public int texture;
	public int gunpowder;
	public int ironIngots;
	public int stackOnCreate;
	public String pack;
	public float damageModifier;
	public int spray = 100;
	public int split = 1;
	public HashMap<Integer, Float> effectModifiers = new HashMap();
	public HashMap<Integer, Integer> effectAmplifiers = new HashMap();
	public double gravityModifier = 1.0D;
	public double initialSpeed = 10.0D;
	public double frictionInAir = 0.01D;
	public double frictionInLiquid = 0.5D;
	
	public static HashMap<String, List<ItemBullet>> bulletsList = new HashMap();
	
	public ItemBullet(String pack, String name, int bulletId, String iconName, int texture, int gunpowder, int ironIngot, int stackOnCreate, float damageModifier) {
		super(iconName, pack + ".bullet." + name);
		setCreativeTab(GunCus.creativeTabBullets);
		setMaxStackSize(64);
		
		if (!bulletsList.containsKey(pack)) {
			bulletsList.put(pack, new ArrayList());
		}
		
		this.damageModifier = damageModifier;
		this.bulletId = bulletId;
		this.texture = texture;
		this.gunpowder = gunpowder;
		this.ironIngots = ironIngot;
		this.stackOnCreate = stackOnCreate;
		this.pack = pack;
		if (bulletsList.get(pack).size() <= bulletId) {
			for (int bulletIndex = bulletsList.get(pack).size(); bulletIndex <= bulletId; bulletIndex++) {
				bulletsList.get(pack).add(null);
			}
		}
		bulletsList.get(pack).set(bulletId, this);
	}
	
	public ItemBullet setGravityModifier(final double gravityModifier) {
		this.gravityModifier = gravityModifier;
		return this;
	}
	
	public boolean hasEffects() {
		return this.effectModifiers.size() > 0;
	}
	
	public ItemBullet setSplit(final int split) {
		this.split = split;
		return this;
	}
	
	public ItemBullet setSpray(final int spray) {
		this.spray = spray;
		return this;
	}

	public ItemBullet setSpeedStats(final double initialSpeed, final double frictionInAir, final double frictionInLiquid) {
		this.initialSpeed = initialSpeed;
		this.frictionInAir = frictionInAir;
		this.frictionInLiquid = frictionInLiquid;
		return this;
	}
	
	public ItemBullet addEffect(int effect, float modifier, int amplifier) {
		effectModifiers.put(effect, modifier);
		effectAmplifiers.put(effect, amplifier);
		return this;
	}
	
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
		list.add("");
		list.add(pack + " pack");
	}
}
