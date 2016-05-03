package stuuupiiid.guncus.item;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;

import stuuupiiid.guncus.data.ModifierPart;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemModifierPart extends ItemBase {
	private HashMap<Integer, ModifierPart> modifierParts;
	public int idMax;
	
	public ItemModifierPart(String unlocalizedName, ModifierPart[] modifierParts) {
		super(unlocalizedName);
		
		this.modifierParts = new HashMap(modifierParts.length);
		idMax = 0;
		for (ModifierPart modifierPart : modifierParts) {
			if (modifierPart.id > 32767) {
				throw new InvalidParameterException(
					String.format("Metadata %d (%s) for %s is out of range.", modifierPart.id, modifierPart.unlocalizedName, unlocalizedName));
			}
			if (this.modifierParts.containsKey(modifierPart.id)) {
				throw new InvalidParameterException(
					String.format("Metadata %d (%s) for %s is already defined.", modifierPart.id, modifierPart.unlocalizedName, unlocalizedName));
			}
			this.modifierParts.put(modifierPart.id, modifierPart);
			if (idMax < modifierPart.id) {
				idMax = modifierPart.id;
			}
		}
		setHasSubtypes(true);
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs creativeTab, List list) {
		for (ModifierPart customizationPart : modifierParts.values()) {
			ItemStack itemStack = new ItemStack(item, 1, customizationPart.id);
			list.add(itemStack);
		}
	}
	
	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		ModifierPart modifierPart = ((ItemModifierPart) itemStack.getItem()).getModifierPart(itemStack.getItemDamage());
		if (modifierPart != null) {
			return super.getUnlocalizedName() + "." + modifierPart.unlocalizedName;
		} else {
			return super.getUnlocalizedName();
		}
	}
	
	@Override
	public String getRegistryName(ItemStack itemStack) {
		ModifierPart modifierPart = ((ItemModifierPart) itemStack.getItem()).getModifierPart(itemStack.getItemDamage());
		if (modifierPart != null) {
			return getRegistryName() + "." + modifierPart.unlocalizedName;
		} else {
			return getRegistryName();
		}
	}
	
	@Override
	public int getMetadata(int damage) {
		int metadata = super.getMetadata(damage);
		return modifierParts.containsKey(metadata) ? metadata : 0;
	}
	
	public ModifierPart getModifierPart(final int partId) {
		return modifierParts.get(partId);
	}
}
