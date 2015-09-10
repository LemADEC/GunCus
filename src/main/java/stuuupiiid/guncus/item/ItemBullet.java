package stuuupiiid.guncus.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemBullet extends GunCusItem {
	public int bulletType;
	public int sulphur;
	public int iron;
	public int stackOnCreate;
	public String name;
	public String pack;
	public float damage;
	public int spray = 100;
	public int split = 1;
	public HashMap<Integer, Float> effectModifiers = new HashMap();
	public HashMap<Integer, Integer> effectAmplifiers = new HashMap();
	public double gravity = 1.0D;
	
	public static HashMap<String, List<ItemBullet>> bulletsList = new HashMap();
	
	public ItemBullet(String name, int bulletType, int sulphur, int iron, int stackOnCreate, String pack, String icon, float damage) {
		super(icon, name, pack + ".bullet" + bulletType);
		
		if (!bulletsList.containsKey(pack)) {
			bulletsList.put(pack, new ArrayList());
		}
		
		this.damage = damage;
		this.bulletType = bulletType;
		this.sulphur = sulphur;
		this.iron = iron;
		this.stackOnCreate = stackOnCreate;
		this.name = name;
		this.pack = pack;
		if (bulletsList.get(pack).size() <= bulletType) {
			for (int bulletIndex = bulletsList.get(pack).size(); bulletIndex <= bulletType; bulletIndex++) {
				bulletsList.get(pack).add(null);
			}
		}
		bulletsList.get(pack).set(bulletType, this);
	}
	
	public ItemBullet setGravityModifier(double modify) {
		this.gravity = modify;
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
