/*     */ package assets.guncus;
/*     */ 
/*     */ import com.google.common.io.ByteArrayDataOutput;
/*     */ import com.google.common.io.ByteStreams;
/*     */ import cpw.mods.fml.common.FMLCommonHandler;
/*     */ import cpw.mods.fml.common.network.PacketDispatcher;
/*     */ import cpw.mods.fml.common.network.Player;
/*     */ import cpw.mods.fml.relauncher.Side;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Random;
/*     */ import net.minecraft.block.Block;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.EntityLiving;
/*     */ import net.minecraft.entity.EntityLivingBase;
/*     */ import net.minecraft.entity.IProjectile;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.entity.player.PlayerCapabilities;
/*     */ import net.minecraft.entity.projectile.EntityArrow;
/*     */ import net.minecraft.nbt.NBTTagCompound;
/*     */ import net.minecraft.network.packet.Packet250CustomPayload;
/*     */ import net.minecraft.potion.Potion;
/*     */ import net.minecraft.potion.PotionEffect;
/*     */ import net.minecraft.util.AxisAlignedBB;
/*     */ import net.minecraft.util.DamageSource;
/*     */ import net.minecraft.util.MathHelper;
/*     */ import net.minecraft.util.MovingObjectPosition;
/*     */ import net.minecraft.util.Vec3;
/*     */ import net.minecraft.util.Vec3Pool;
/*     */ import net.minecraft.world.World;
/*     */ 
/*     */ public class GunCusEntityBullet extends EntityArrow
/*     */   implements IProjectile
/*     */ {
/*  42 */   private int field_70247_d = -1;
/*  43 */   private int field_70248_e = -1;
/*  44 */   private int field_70245_f = -1;
/*  45 */   private int field_70246_g = 0;
/*  46 */   private int field_70253_h = 0;
/*  47 */   private boolean field_70254_i = false;
/*     */   public EntityPlayer field_70250_c;
/*     */   private int field_70252_j;
/*  50 */   private int field_70257_an = 0;
/*     */   private float field_70255_ao;
/*     */   private int field_70256_ap;
/*  53 */   private int liveTime = 300;
/*  54 */   public int ticks = 0;
/*  55 */   public boolean gravity = false;
/*  56 */   public double gravityMod = 1.0D;
/*     */ 
/*  58 */   public List<Integer> effects = new ArrayList();
/*  59 */   public HashMap<Integer, Float> effectModifiers = new HashMap();
/*     */ 
/*     */   public GunCusEntityBullet(World world)
/*     */   {
/*  63 */     super(world);
/*  64 */     setSize(0.5F, 0.5F);
/*     */   }
/*     */ 
/*     */   public GunCusEntityBullet(World par1World, EntityPlayer par2EntityPlayer, float speed, float damage, int accuracy)
/*     */   {
/*  69 */     super(par1World);
/*  70 */     this.renderDistanceWeight = 10.0D;
/*  71 */     this.field_70250_c = par2EntityPlayer;
/*  72 */     setSize(0.5F, 0.5F);
/*  73 */     setLocationAndAngles(par2EntityPlayer.posX, par2EntityPlayer.posY + par2EntityPlayer.getEyeHeight(), par2EntityPlayer.posZ, par2EntityPlayer.rotationYaw, par2EntityPlayer.rotationPitch);
/*  74 */     this.posX -= MathHelper.cos(this.rotationYaw / 180.0F * 3.141593F) * 0.16F;
/*  75 */     this.posY -= 0.1000000014901161D;
/*  76 */     this.posZ -= MathHelper.sin(this.rotationYaw / 180.0F * 3.141593F) * 0.16F;
/*  77 */     setPosition(par2EntityPlayer.posX, par2EntityPlayer.posY + par2EntityPlayer.getEyeHeight(), par2EntityPlayer.posZ);
/*  78 */     this.yOffset = 0.0F;
/*  79 */     this.motionX = (-MathHelper.sin(this.rotationYaw / 180.0F * 3.141593F) * MathHelper.cos(this.rotationPitch / 180.0F * 3.141593F));
/*  80 */     this.motionZ = (MathHelper.cos(this.rotationYaw / 180.0F * 3.141593F) * MathHelper.cos(this.rotationPitch / 180.0F * 3.141593F));
/*  81 */     this.motionY = (-MathHelper.sin(this.rotationPitch / 180.0F * 3.141593F));
/*  82 */     if (accuracy < 100)
/*     */     {
/*  84 */       int accX1 = this.rand.nextInt(101 - accuracy);
/*  85 */       int accY1 = this.rand.nextInt(101 - accuracy);
/*  86 */       int accZ1 = this.rand.nextInt(101 - accuracy);
/*  87 */       double accX2 = accX1 - this.rand.nextInt(accX1 + 1) * 2;
/*  88 */       double accY2 = accY1 - this.rand.nextInt(accY1 + 1) * 2;
/*  89 */       double accZ2 = accZ1 - this.rand.nextInt(accZ1 + 1) * 2;
/*  90 */       this.motionX += accX2 / 370.0D;
/*  91 */       this.motionY += accY2 / 370.0D;
/*  92 */       this.motionZ += accZ2 / 370.0D;
/*     */     }
/*  94 */     this.field_70256_ap = 0;
/*  95 */     this.field_70255_ao = damage;
/*  96 */     setThrowableHeading(this.motionX, this.motionY, this.motionZ, speed, 1.0F);
/*     */   }
/*     */ 
/*     */   public GunCusEntityBullet setEffects(List effects, HashMap<Integer, Float> modifies)
/*     */   {
/* 101 */     this.effects = effects;
/* 102 */     this.effectModifiers = modifies;
/* 103 */     return this;
/*     */   }
/*     */ 
/*     */   public GunCusEntityBullet setGravityMod(double modifier)
/*     */   {
/* 108 */     this.gravityMod = modifier;
/* 109 */     return this;
/*     */   }
/*     */ 
/*     */   public GunCusEntityBullet setLowerGravity(boolean flag)
/*     */   {
/* 114 */     this.gravity = flag;
/* 115 */     return this;
/*     */   }
/*     */ 
/*     */   public void onUpdate()
/*     */   {
/* 120 */     super.onEntityUpdate();
/*     */ 
/* 122 */     this.ticks += 1;
/*     */ 
/* 124 */     this.liveTime -= 1;
/*     */ 
/* 126 */     if (this.liveTime <= 0)
/*     */     {
/* 128 */       setDead();
/*     */     }
/*     */ 
/* 131 */     if ((this.prevRotationPitch == 0.0F) && (this.prevRotationYaw == 0.0F))
/*     */     {
/* 133 */       float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
/* 134 */       this.prevRotationYaw = (this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / 3.141592653589793D));
/* 135 */       this.prevRotationPitch = (this.rotationPitch = (float)(Math.atan2(this.motionY, f) * 180.0D / 3.141592653589793D));
/*     */     }
/*     */ 
/* 138 */     int i = this.worldObj.getBlockId(this.field_70247_d, this.field_70248_e, this.field_70245_f);
/*     */ 
/* 140 */     if ((i > 0) && (i != 20) && (i != 30) && (i != 89) && (i != 102))
/*     */     {
/* 142 */       Block.blocksList[i].setBlockBoundsBasedOnState(this.worldObj, this.field_70247_d, this.field_70248_e, this.field_70245_f);
/* 143 */       AxisAlignedBB axisalignedbb = Block.blocksList[i].getCollisionBoundingBoxFromPool(this.worldObj, this.field_70247_d, this.field_70248_e, this.field_70245_f);
/*     */ 
/* 145 */       if ((axisalignedbb != null) && (axisalignedbb.isVecInside(this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ))))
/*     */       {
/* 147 */         this.field_70254_i = true;
/*     */       }
/*     */     }
/*     */ 
/* 151 */     if (this.field_70254_i)
/*     */     {
/* 153 */       onInGround();
/*     */     }
/*     */     else
/*     */     {
/* 157 */       this.field_70257_an += 1;
/* 158 */       Vec3 vec3 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ);
/* 159 */       Vec3 vec31 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
/* 160 */       MovingObjectPosition movingobjectposition = this.worldObj.rayTraceBlocks_do_do(vec3, vec31, false, true);
/* 161 */       vec3 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ);
/* 162 */       vec31 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
/*     */ 
/* 164 */       if (movingobjectposition != null)
/*     */       {
/* 166 */         vec31 = this.worldObj.getWorldVec3Pool().getVecFromPool(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
/*     */       }
/*     */ 
/* 169 */       Entity entity = null;
/* 170 */       List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
/* 171 */       double d0 = 0.0D;
/*     */ 
/* 175 */       for (int l = 0; l < list.size(); l++)
/*     */       {
/* 177 */         Entity entity1 = (Entity)list.get(l);
/*     */ 
/* 179 */         if ((entity1.canBeCollidedWith()) && ((entity1 != this.field_70250_c) || (this.field_70257_an >= 5)))
/*     */         {
/* 181 */           float f1 = 0.3F;
/* 182 */           AxisAlignedBB axisalignedbb1 = entity1.boundingBox.expand(f1, f1, f1);
/* 183 */           MovingObjectPosition movingobjectposition1 = axisalignedbb1.calculateIntercept(vec3, vec31);
/*     */ 
/* 185 */           if (movingobjectposition1 != null)
/*     */           {
/* 187 */             double d1 = vec3.distanceTo(movingobjectposition1.hitVec);
/*     */ 
/* 189 */             if ((d1 < d0) || (d0 == 0.0D))
/*     */             {
/* 191 */               entity = entity1;
/* 192 */               d0 = d1;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 198 */       if (entity != null)
/*     */       {
/* 200 */         movingobjectposition = new MovingObjectPosition(entity);
/*     */       }
/*     */ 
/* 203 */       if ((movingobjectposition != null) && (movingobjectposition.entityHit != null) && ((movingobjectposition.entityHit instanceof EntityPlayer)))
/*     */       {
/* 205 */         EntityPlayer entityplayer = (EntityPlayer)movingobjectposition.entityHit;
/*     */ 
/* 207 */         if ((entityplayer.capabilities.disableDamage) || (((this.field_70250_c instanceof EntityPlayer)) && (!this.field_70250_c.canAttackPlayer(entityplayer))))
/*     */         {
/* 209 */           movingobjectposition = null;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 216 */       if (movingobjectposition != null)
/*     */       {
/* 218 */         if ((movingobjectposition.entityHit != null) && (this.ticks >= 1))
/*     */         {
/* 220 */           this.field_70254_i = true;
/* 221 */           DamageSource damagesource = null;
/*     */ 
/* 223 */           if (this.field_70250_c != null)
/*     */           {
/* 225 */             damagesource = DamageSource.causePlayerDamage(this.field_70250_c);
/*     */           }
/*     */           else
/*     */           {
/* 229 */             damagesource = DamageSource.causeArrowDamage(this, this);
/*     */           }
/*     */ 
/* 232 */           if ((this.field_70255_ao > 0.0F) && (movingobjectposition.entityHit.attackEntityFrom(damagesource, this.field_70255_ao)))
/*     */           {
/* 234 */             if (((movingobjectposition.entityHit instanceof EntityLiving)) && (!movingobjectposition.entityHit.isDead))
/*     */             {
/* 236 */               EntityLiving entityliving = (EntityLiving)movingobjectposition.entityHit;
/* 237 */               entityliving.hurtResistantTime = 0;
/*     */             }
/*     */           }
/* 240 */           if (!movingobjectposition.entityHit.isDead)
/*     */           {
/* 242 */             ByteArrayDataOutput bytes = ByteStreams.newDataOutput();
/* 243 */             bytes.writeInt(10);
/* 244 */             bytes.writeInt(0);
/* 245 */             PacketDispatcher.sendPacketToPlayer(new Packet250CustomPayload("guncus", bytes.toByteArray()), (Player)this.field_70250_c);
/*     */           }
/*     */ 
/* 248 */           hitEntity(movingobjectposition.entityHit);
/*     */         }
/*     */         else
/*     */         {
/* 252 */           this.field_70247_d = movingobjectposition.blockX;
/* 253 */           this.field_70248_e = movingobjectposition.blockY;
/* 254 */           this.field_70245_f = movingobjectposition.blockZ;
/* 255 */           this.field_70246_g = this.worldObj.getBlockId(this.field_70247_d, this.field_70248_e, this.field_70245_f);
/* 256 */           if ((this.field_70246_g == 20) || (this.field_70246_g == 30) || (this.field_70246_g == 89) || (this.field_70246_g == 102))
/*     */           {
/* 258 */             GunCus.removeBlockServer(this.field_70250_c, MathHelper.floor_double(this.field_70247_d), MathHelper.floor_double(this.field_70248_e), MathHelper.floor_double(this.field_70245_f));
/*     */           }
/* 260 */           else if (this.field_70246_g != 0)
/*     */           {
/* 262 */             this.field_70253_h = this.worldObj.getBlockMetadata(this.field_70247_d, this.field_70248_e, this.field_70245_f);
/* 263 */             this.motionX = ((float)(movingobjectposition.hitVec.xCoord - this.posX));
/* 264 */             this.motionY = ((float)(movingobjectposition.hitVec.yCoord - this.posY));
/* 265 */             this.motionZ = ((float)(movingobjectposition.hitVec.zCoord - this.posZ));
/* 266 */             float f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
/* 267 */             this.posX -= this.motionX / f2 * 0.0500000007450581D;
/* 268 */             this.posY -= this.motionY / f2 * 0.0500000007450581D;
/* 269 */             this.posZ -= this.motionZ / f2 * 0.0500000007450581D;
/* 270 */             setIsCritical(false);
/* 271 */             this.field_70254_i = true;
/* 272 */             Block.blocksList[this.field_70246_g].onEntityCollidedWithBlock(this.worldObj, this.field_70247_d, this.field_70248_e, this.field_70245_f, this);
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 277 */       this.posX += this.motionX;
/* 278 */       this.posY += this.motionY;
/* 279 */       this.posZ += this.motionZ;
/* 280 */       float f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
/* 281 */       this.rotationYaw = ((float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / 3.141592653589793D));
/*     */ 
/* 283 */       for (this.rotationPitch = ((float)(Math.atan2(this.motionY, f2) * 180.0D / 3.141592653589793D)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F);
/* 288 */       while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
/*     */       {
/* 290 */         this.prevRotationPitch += 360.0F;
/*     */       }
/*     */ 
/* 293 */       while (this.rotationYaw - this.prevRotationYaw < -180.0F)
/*     */       {
/* 295 */         this.prevRotationYaw -= 360.0F;
/*     */       }
/*     */ 
/* 298 */       while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
/*     */       {
/* 300 */         this.prevRotationYaw += 360.0F;
/*     */       }
/*     */ 
/* 303 */       this.rotationPitch = (this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F);
/* 304 */       this.rotationYaw = (this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F);
/* 305 */       float f4 = 0.99F;
/* 306 */       float f1 = 0.05F;
/*     */ 
/* 308 */       if (isInWater())
/*     */       {
/* 310 */         for (int j1 = 0; j1 < 4; j1++)
/*     */         {
/* 312 */           float f3 = 0.25F;
/* 313 */           this.worldObj.spawnParticle("bubble", this.posX - this.motionX * f3, this.posY - this.motionY * f3, this.posZ - this.motionZ * f3, this.motionX, this.motionY, this.motionZ);
/*     */         }
/*     */ 
/* 316 */         f4 = 0.8F;
/*     */       }
/*     */ 
/* 319 */       this.motionX *= f4;
/* 320 */       this.motionY *= f4;
/* 321 */       this.motionZ *= f4;
/* 322 */       this.motionY -= (this.gravity ? 0.014D : 0.02D) * this.gravityMod;
/* 323 */       setPosition(this.posX, this.posY, this.posZ);
/* 324 */       doBlockCollisions();
/*     */ 
/* 326 */       if (this.field_70254_i)
/*     */       {
/* 328 */         onInGround();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void doBlockCollisions()
/*     */   {
/* 336 */     int i = MathHelper.floor_double(this.boundingBox.minX + 0.001D);
/* 337 */     int j = MathHelper.floor_double(this.boundingBox.minY + 0.001D);
/* 338 */     int k = MathHelper.floor_double(this.boundingBox.minZ + 0.001D);
/* 339 */     int l = MathHelper.floor_double(this.boundingBox.maxX - 0.001D);
/* 340 */     int i1 = MathHelper.floor_double(this.boundingBox.maxY - 0.001D);
/* 341 */     int j1 = MathHelper.floor_double(this.boundingBox.maxZ - 0.001D);
/*     */ 
/* 343 */     if (this.worldObj.checkChunksExist(i, j, k, l, i1, j1))
/*     */     {
/* 345 */       for (int k1 = i; k1 <= l; k1++)
/*     */       {
/* 347 */         for (int l1 = j; l1 <= i1; l1++)
/*     */         {
/* 349 */           for (int i2 = k; i2 <= j1; i2++)
/*     */           {
/* 351 */             int j2 = this.worldObj.getBlockId(k1, l1, i2);
/*     */ 
/* 353 */             if ((j2 > 0) && (j2 != 20) && (j2 != 30) && (j2 != 89) && (j2 != 102))
/*     */             {
/* 355 */               Block.blocksList[j2].onEntityCollidedWithBlock(this.worldObj, k1, l1, i2, this);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void hitEntity(Entity entity2)
/*     */   {
/* 365 */     if ((FMLCommonHandler.instance().getEffectiveSide().isServer()) && ((entity2 instanceof EntityLivingBase)))
/*     */     {
/* 367 */       EntityLivingBase entity = (EntityLivingBase)entity2;
/*     */ 
/* 369 */       if (this.effects.contains(Integer.valueOf(1)))
/*     */       {
/* 371 */         entity.addPotionEffect(new PotionEffect(Potion.poison.id, MathHelper.floor_double(((Float)this.effectModifiers.get(Integer.valueOf(1))).floatValue()) * 20, 0));
/*     */       }
/*     */ 
/* 374 */       if (this.effects.contains(Integer.valueOf(2)))
/*     */       {
/* 376 */         entity.addPotionEffect(new PotionEffect(Potion.confusion.id, MathHelper.floor_double(((Float)this.effectModifiers.get(Integer.valueOf(2))).floatValue()) * 20, 0));
/*     */       }
/*     */ 
/* 379 */       if (this.effects.contains(Integer.valueOf(3)))
/*     */       {
/* 381 */         entity.setFire(MathHelper.floor_double(((Float)this.effectModifiers.get(Integer.valueOf(3))).floatValue()));
/*     */       }
/*     */ 
/* 384 */       if (this.effects.contains(Integer.valueOf(7)))
/*     */       {
/* 386 */         entity.addPotionEffect(new PotionEffect(Potion.blindness.id, MathHelper.floor_double(((Float)this.effectModifiers.get(Integer.valueOf(7))).floatValue()) * 20, 0));
/*     */       }
/*     */ 
/* 389 */       if (this.effects.contains(Integer.valueOf(6)))
/*     */       {
/* 391 */         entity.heal(((Float)this.effectModifiers.get(Integer.valueOf(6))).floatValue());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onInGround()
/*     */   {
/* 398 */     if (FMLCommonHandler.instance().getEffectiveSide().isServer())
/*     */     {
/* 400 */       if (this.effects.contains(Integer.valueOf(4)))
/*     */       {
/* 402 */         this.worldObj.createExplosion(this.field_70250_c, this.posX, this.posY, this.posZ, ((Float)this.effectModifiers.get(Integer.valueOf(4))).floatValue(), GunCus.blockDamage);
/*     */       }
/*     */ 
/* 405 */       if (this.effects.contains(Integer.valueOf(5)))
/*     */       {
/* 407 */         this.worldObj.createExplosion(this.field_70250_c, this.posX, this.posY, this.posZ, ((Float)this.effectModifiers.get(Integer.valueOf(5))).floatValue(), false);
/*     */       }
/*     */ 
/* 410 */       if ((this.effects.contains(Integer.valueOf(3))) && (this.field_70248_e > 0))
/*     */       {
/* 412 */         if (this.worldObj.isAirBlock(this.field_70247_d, this.field_70248_e + 1, this.field_70245_f))
/*     */         {
/* 414 */           this.worldObj.setBlock(this.field_70247_d, this.field_70248_e + 1, this.field_70245_f, Block.fire.blockID);
/*     */         }
/*     */ 
/* 417 */         if (this.worldObj.isAirBlock(this.field_70247_d, this.field_70248_e, this.field_70245_f))
/*     */         {
/* 419 */           this.worldObj.setBlock(this.field_70247_d, this.field_70248_e, this.field_70245_f, Block.fire.blockID);
/*     */         }
/*     */       }
/*     */ 
/* 423 */       setDead();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
/*     */   {
/* 429 */     setDead();
/*     */   }
/*     */ 
/*     */   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
/*     */   {
/* 434 */     setDead();
/*     */   }
/*     */ }

/* Location:           C:\Users\Nate\Desktop\Mod\GunCusClass.zip
 * Qualified Name:     assets.guncus.GunCusEntityBullet
 * JD-Core Version:    0.6.2
 */