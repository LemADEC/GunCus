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
	
	public static HashMap<String, List<ItemBullet>> bulletsList = new HashMap();
	
	public ItemBullet(String pack, String name, int bulletId, String iconName, int texture, int gunpowder, int ironIngot, int stackOnCreate, float damageModifier) {
		super(iconName, pack + ".bullet." + name);
		setCreativeTab(GunCus.creativeTabBullets);
		
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
	
	public ItemBullet setGravityModifier(double gravityModifier) {
		this.gravityModifier = gravityModifier;
		return this;
	}
	
	public boolean hasEffects() {
		return this.effectModifiers.size() > 0;
	}
	
	public ItemBullet setSplit(int split) {
		this.split = split;
		return this;
	}
	
	public ItemBullet setSpray(int modify) {
		this.spray = modify;
		return this;
	}
	
	public ItemBullet addEffect(int effect, float modifier, int amplifier) {
		effectModifiers.put(effect, modifier);
		effectAmplifiers.put(effect, amplifier);
		return this;
	}
	
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par2List, boolean par4) {
		par2List.add("Pack: " + pack);
	}
}
