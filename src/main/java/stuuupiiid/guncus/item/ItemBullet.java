package stuuupiiid.guncus.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import stuuupiiid.guncus.GunCus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemBullet extends ItemBase {
	public int bulletId;
	public int texture;
	public int gunpowder;
	public int ironIngots;
	public int stackOnCreate;
	public String packName;
	public String blockHit = "guncus:inground";
	public String entityHit = "guncus:inground";
	public float damageModifier;
	public int spray = 100;
	public int split = 1;
	public HashMap<Integer, Float> effectModifiers = new HashMap();
	public HashMap<Integer, Integer> effectAmplifiers = new HashMap();
	public double gravityModifier = 1.0D;
	public double initialSpeed = 10.0D;
	public double frictionInAir = 0.01D;
	public double frictionInLiquid = 0.5D;
	public float playerInaccuracyMultiplier = 1.0F;
	
	public static HashMap<String, List<ItemBullet>> bullets = new HashMap();
	
	public ItemBullet(String packName, String bulletName, int bulletId, int texture, int gunpowder, int ironIngot, int stackOnCreate, float damageModifier) {
		super(packName + ".bullet." + bulletName);
		setCreativeTab(GunCus.creativeTabBullets);
		setMaxStackSize(64);
		
		if (!bullets.containsKey(packName)) {
			bullets.put(packName, new ArrayList());
		}
		
		this.damageModifier = damageModifier;
		this.bulletId = bulletId;
		this.texture = texture;
		this.gunpowder = gunpowder;
		this.ironIngots = ironIngot;
		this.stackOnCreate = stackOnCreate;
		this.packName = packName;
		if (bullets.get(packName).size() <= bulletId) {
			for (int bulletIndex = bullets.get(packName).size(); bulletIndex <= bulletId; bulletIndex++) {
				bullets.get(packName).add(null);
			}
		}
		bullets.get(packName).set(bulletId, this);
		GunCus.logger.info("Added bullet #" + bulletId + ": " + bulletName);
	}
	
	public ItemBullet setGravityModifier(final double gravityModifier) {
		this.gravityModifier = gravityModifier;
		return this;
	}
	
	public boolean hasEffects() {
		return this.effectModifiers.size() > 0;
	}
	
	public ItemBullet setBlockHit(String blockHit) {
		this.blockHit = blockHit;
		return this;
	}
	
	public ItemBullet setEntityHit(String entityHit) {
		this.entityHit = entityHit;
		return this;
	}
	
	public ItemBullet setSplit(final int split) {
		this.split = split;
		return this;
	}
	
	public ItemBullet setAccuracyModifiers(final int spray, final float playerInaccuracyMultiplier) {
		this.spray = spray;
		this.playerInaccuracyMultiplier = playerInaccuracyMultiplier;
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
		list.add(packName + " pack");
	}
}
