/*     */ package assets.guncus;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.item.ItemStack;
/*     */ 
/*     */ public class GunCusItemBullet extends GunCusItem
/*     */ {
/*     */   public int bulletType;
/*     */   public int sulphur;
/*     */   public int iron;
/*     */   public int stackOnCreate;
/*     */   public String name;
/*     */   public String pack;
/*     */   public float damage;
/*  21 */   public int spray = 100;
/*  22 */   public int split = 1;
/*  23 */   public List<Integer> effects = new ArrayList();
/*  24 */   public HashMap<Integer, Float> effectModifiers = new HashMap();
/*  25 */   public double gravity = 1.0D;
/*     */ 
/*  27 */   public static HashMap<String, List<GunCusItemBullet>> bulletsList = new HashMap();
/*     */ 
/*     */   public GunCusItemBullet(int par1, String name, int bulletType, int sulphur, int iron, int stackOnCreate, String pack, String icon, float damage)
/*     */   {
/*  31 */     super(par1, icon, name, "bullet" + pack + bulletType);
/*     */ 
/*  33 */     if (!bulletsList.containsKey(pack))
/*     */     {
/*  35 */       bulletsList.put(pack, new ArrayList());
/*     */     }
/*     */ 
/*  38 */     this.damage = damage;
/*  39 */     this.bulletType = bulletType;
/*  40 */     this.sulphur = sulphur;
/*  41 */     this.iron = iron;
/*  42 */     this.stackOnCreate = stackOnCreate;
/*  43 */     this.name = name;
/*  44 */     this.pack = pack;
/*  45 */     if (((List)bulletsList.get(pack)).size() <= bulletType)
/*     */     {
/*  47 */       for (int v1 = ((List)bulletsList.get(pack)).size(); v1 <= bulletType; v1++)
/*     */       {
/*  49 */         ((List)bulletsList.get(pack)).add(null);
/*     */       }
/*     */     }
/*  52 */     ((List)bulletsList.get(pack)).set(bulletType, this);
/*     */   }
/*     */ 
/*     */   public GunCusItemBullet setGravityModifier(double modify)
/*     */   {
/*  57 */     this.gravity = modify;
/*  58 */     return this;
/*     */   }
/*     */ 
/*     */   public boolean hasEffects()
/*     */   {
/*  63 */     return this.effects.size() > 0;
/*     */   }
/*     */ 
/*     */   public GunCusItemBullet setSplit(int split)
/*     */   {
/*  68 */     this.split = split;
/*  69 */     return this;
/*     */   }
/*     */ 
/*     */   public GunCusItemBullet setSpray(int modify)
/*     */   {
/*  74 */     this.spray = modify;
/*  75 */     return this;
/*     */   }
/*     */ 
/*     */   public GunCusItemBullet addEffect(int eff, float modify)
/*     */   {
/*  80 */     addEffect(eff);
/*  81 */     setEffectModifier(eff, modify);
/*  82 */     return this;
/*     */   }
/*     */ 
/*     */   public GunCusItemBullet addEffect(int eff)
/*     */   {
/*  87 */     this.effects.add(Integer.valueOf(eff));
/*  88 */     return this;
/*     */   }
/*     */ 
/*     */   public GunCusItemBullet setEffectModifier(int eff, float modify)
/*     */   {
/*  93 */     this.effectModifiers.put(Integer.valueOf(eff), Float.valueOf(modify));
/*  94 */     return this;
/*     */   }
/*     */ 
/*     */   public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par2List, boolean par4)
/*     */   {
/* 100 */     par2List.add(this.pack);
/*     */   }
/*     */ }

/* Location:           C:\Users\Nate\Desktop\Mod\GunCusClass.zip
 * Qualified Name:     assets.guncus.GunCusItemBullet
 * JD-Core Version:    0.6.2
 */