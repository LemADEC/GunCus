/*     */ package assets.guncus;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Random;
/*     */ import net.minecraft.block.Block;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.IProjectile;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.entity.player.PlayerCapabilities;
/*     */ import net.minecraft.entity.projectile.EntityArrow;
/*     */ import net.minecraft.nbt.NBTTagCompound;
/*     */ import net.minecraft.util.AxisAlignedBB;
/*     */ import net.minecraft.util.MathHelper;
/*     */ import net.minecraft.util.MovingObjectPosition;
/*     */ import net.minecraft.util.Vec3;
/*     */ import net.minecraft.util.Vec3Pool;
/*     */ import net.minecraft.world.World;
/*     */ 
/*     */ public class GunCusEntityAT extends EntityArrow
/*     */   implements IProjectile
/*     */ {
/*  30 */   private int field_70247_d = -1;
/*  31 */   private int field_70248_e = -1;
/*  32 */   private int field_70245_f = -1;
/*  33 */   private int field_70246_g = 0;
/*  34 */   private int field_70253_h = 0;
/*  35 */   private boolean field_70254_i = false;
/*     */   public EntityPlayer field_70250_c;
/*     */   private int field_70252_j;
/*  38 */   private int field_70257_an = 0;
/*     */   private int field_70256_ap;
/*  40 */   private int liveTime = 300;
/*     */ 
/*  42 */   private int type = 1;
/*     */   public List<Integer> effects;
/*     */   public HashMap<Integer, Float> effectModifiers;
/*  47 */   public int ticks = 0;
/*     */ 
/*     */   public GunCusEntityAT(World world)
/*     */   {
/*  51 */     super(world);
/*  52 */     setSize(0.5F, 0.5F);
/*     */   }
/*     */ 
/*     */   public GunCusEntityAT(World par1World, EntityPlayer par2EntityPlayer, int accuracy, int type)
/*     */   {
/*  57 */     super(par1World);
/*  58 */     this.renderDistanceWeight = 10.0D;
/*  59 */     this.field_70250_c = par2EntityPlayer;
/*  60 */     setSize(0.5F, 0.5F);
/*  61 */     setLocationAndAngles(par2EntityPlayer.posX, par2EntityPlayer.posY + par2EntityPlayer.getEyeHeight(), par2EntityPlayer.posZ, par2EntityPlayer.rotationYaw, par2EntityPlayer.rotationPitch);
/*  62 */     this.posX -= MathHelper.cos(this.rotationYaw / 180.0F * 3.141593F) * 0.16F;
/*  63 */     this.posY -= 0.1000000014901161D;
/*  64 */     this.posZ -= MathHelper.sin(this.rotationYaw / 180.0F * 3.141593F) * 0.16F;
/*  65 */     setPosition(par2EntityPlayer.posX, par2EntityPlayer.posY + par2EntityPlayer.getEyeHeight(), par2EntityPlayer.posZ);
/*  66 */     this.yOffset = 0.0F;
/*  67 */     this.motionX = (-MathHelper.sin(this.rotationYaw / 180.0F * 3.141593F) * MathHelper.cos(this.rotationPitch / 180.0F * 3.141593F));
/*  68 */     this.motionZ = (MathHelper.cos(this.rotationYaw / 180.0F * 3.141593F) * MathHelper.cos(this.rotationPitch / 180.0F * 3.141593F));
/*  69 */     this.motionY = (-MathHelper.sin(this.rotationPitch / 180.0F * 3.141593F));
/*  70 */     if (accuracy < 100)
/*     */     {
/*  72 */       int accX1 = this.rand.nextInt(101 - accuracy);
/*  73 */       int accY1 = this.rand.nextInt(101 - accuracy);
/*  74 */       int accZ1 = this.rand.nextInt(101 - accuracy);
/*  75 */       double accX2 = accX1 - this.rand.nextInt(accX1 + 1) * 2;
/*  76 */       double accY2 = accY1 - this.rand.nextInt(accY1 + 1) * 2;
/*  77 */       double accZ2 = accZ1 - this.rand.nextInt(accZ1 + 1) * 2;
/*  78 */       this.motionX += accX2 / 370.0D;
/*  79 */       this.motionY += accY2 / 370.0D;
/*  80 */       this.motionZ += accZ2 / 370.0D;
/*     */     }
/*  82 */     this.field_70256_ap = 0;
/*  83 */     setThrowableHeading(this.motionX, this.motionY, this.motionZ, type == 1 ? 3.0F : 4.2F, 1.0F);
/*  84 */     this.type = type;
/*     */   }
/*     */ 
/*     */   public void onUpdate()
/*     */   {
/*  89 */     super.onEntityUpdate();
/*     */ 
/*  91 */     this.ticks += 1;
/*     */ 
/*  93 */     this.liveTime -= 1;
/*     */ 
/*  95 */     if ((this.posY > 300.0D) || (this.liveTime <= 0))
/*     */     {
/*  97 */       setDead();
/*     */     }
/*     */ 
/* 100 */     if ((this.prevRotationPitch == 0.0F) && (this.prevRotationYaw == 0.0F))
/*     */     {
/* 102 */       float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
/* 103 */       this.prevRotationYaw = (this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / 3.141592653589793D));
/* 104 */       this.prevRotationPitch = (this.rotationPitch = (float)(Math.atan2(this.motionY, f) * 180.0D / 3.141592653589793D));
/*     */     }
/*     */ 
/* 107 */     int i = this.worldObj.getBlockId(this.field_70247_d, this.field_70248_e, this.field_70245_f);
/*     */ 
/* 109 */     if (i > 0)
/*     */     {
/* 111 */       Block.blocksList[i].setBlockBoundsBasedOnState(this.worldObj, this.field_70247_d, this.field_70248_e, this.field_70245_f);
/* 112 */       AxisAlignedBB axisalignedbb = Block.blocksList[i].getCollisionBoundingBoxFromPool(this.worldObj, this.field_70247_d, this.field_70248_e, this.field_70245_f);
/*     */ 
/* 114 */       if ((axisalignedbb != null) && (axisalignedbb.isVecInside(this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ))))
/*     */       {
/* 116 */         this.field_70254_i = true;
/*     */       }
/*     */     }
/* 119 */     double f3 = 0.25D;
/*     */ 
/* 121 */     this.worldObj.spawnParticle("largesmoke", this.posX - this.motionX * f3, this.posY - this.motionY * f3, this.posZ - this.motionZ * f3, 0.0D, 0.0D, 0.0D);
/* 122 */     this.worldObj.spawnParticle("largesmoke", this.posX - this.motionX * f3, this.posY - this.motionY * f3, this.posZ - this.motionZ * f3, 0.0D, 0.0D, 0.0D);
/* 123 */     this.worldObj.spawnParticle("largesmoke", this.posX - this.motionX * f3, this.posY - this.motionY * f3, this.posZ - this.motionZ * f3, 0.0D, 0.0D, 0.0D);
/* 124 */     this.worldObj.spawnParticle("largesmoke", this.posX - this.motionX * f3, this.posY - this.motionY * f3, this.posZ - this.motionZ * f3, 0.0D, 0.0D, 0.0D);
/*     */ 
/* 126 */     if (this.field_70254_i)
/*     */     {
/* 128 */       this.motionY -= (this.type == 1 ? 0.007D : 0.15D);
/* 129 */       setPosition(this.posX, this.posY, this.posZ);
/* 130 */       doBlockCollisions();
/* 131 */       this.field_70252_j += 1;
/* 132 */       if (this.field_70252_j > 100)
/*     */       {
/* 134 */         explode();
/* 135 */         setDead();
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 140 */       this.field_70257_an += 1;
/* 141 */       Vec3 vec3 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ);
/* 142 */       Vec3 vec31 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
/* 143 */       MovingObjectPosition movingobjectposition = this.worldObj.rayTraceBlocks_do_do(vec3, vec31, false, true);
/* 144 */       vec3 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ);
/* 145 */       vec31 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
/*     */ 
/* 147 */       if (movingobjectposition != null)
/*     */       {
/* 149 */         vec31 = this.worldObj.getWorldVec3Pool().getVecFromPool(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
/*     */       }
/*     */ 
/* 152 */       Entity entity = null;
/* 153 */       List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
/* 154 */       double d0 = 0.0D;
/*     */ 
/* 158 */       for (int l = 0; l < list.size(); l++)
/*     */       {
/* 160 */         Entity entity1 = (Entity)list.get(l);
/*     */ 
/* 162 */         if ((entity1.canBeCollidedWith()) && ((entity1 != this.field_70250_c) || (this.field_70257_an >= 5)))
/*     */         {
/* 164 */           float f1 = 0.3F;
/* 165 */           AxisAlignedBB axisalignedbb1 = entity1.boundingBox.expand(f1, f1, f1);
/* 166 */           MovingObjectPosition movingobjectposition1 = axisalignedbb1.calculateIntercept(vec3, vec31);
/*     */ 
/* 168 */           if (movingobjectposition1 != null)
/*     */           {
/* 170 */             double d1 = vec3.distanceTo(movingobjectposition1.hitVec);
/*     */ 
/* 172 */             if ((d1 < d0) || (d0 == 0.0D))
/*     */             {
/* 174 */               entity = entity1;
/* 175 */               d0 = d1;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 181 */       if (entity != null)
/*     */       {
/* 183 */         movingobjectposition = new MovingObjectPosition(entity);
/*     */       }
/*     */ 
/* 186 */       if ((movingobjectposition != null) && (movingobjectposition.entityHit != null) && ((movingobjectposition.entityHit instanceof EntityPlayer)))
/*     */       {
/* 188 */         EntityPlayer entityplayer = (EntityPlayer)movingobjectposition.entityHit;
/*     */ 
/* 190 */         if ((entityplayer.capabilities.disableDamage) || (((this.field_70250_c instanceof EntityPlayer)) && (!this.field_70250_c.canAttackPlayer(entityplayer))))
/*     */         {
/* 192 */           movingobjectposition = null;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 198 */       if (movingobjectposition != null)
/*     */       {
/* 200 */         if (movingobjectposition.entityHit != null)
/*     */         {
/* 202 */           this.field_70254_i = true;
/*     */         }
/*     */         else
/*     */         {
/* 206 */           this.field_70247_d = movingobjectposition.blockX;
/* 207 */           this.field_70248_e = movingobjectposition.blockY;
/* 208 */           this.field_70245_f = movingobjectposition.blockZ;
/* 209 */           this.field_70246_g = this.worldObj.getBlockId(this.field_70247_d, this.field_70248_e, this.field_70245_f);
/* 210 */           this.field_70253_h = this.worldObj.getBlockMetadata(this.field_70247_d, this.field_70248_e, this.field_70245_f);
/* 211 */           this.motionX = ((float)(movingobjectposition.hitVec.xCoord - this.posX));
/* 212 */           this.motionY = ((float)(movingobjectposition.hitVec.yCoord - this.posY));
/* 213 */           this.motionZ = ((float)(movingobjectposition.hitVec.zCoord - this.posZ));
/* 214 */           float f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
/* 215 */           this.posX -= this.motionX / f2 * 0.0500000007450581D;
/* 216 */           this.posY -= this.motionY / f2 * 0.0500000007450581D;
/* 217 */           this.posZ -= this.motionZ / f2 * 0.0500000007450581D;
/* 218 */           this.field_70254_i = true;
/* 219 */           setIsCritical(false);
/*     */ 
/* 221 */           if (this.field_70246_g != 0)
/*     */           {
/* 223 */             Block.blocksList[this.field_70246_g].onEntityCollidedWithBlock(this.worldObj, this.field_70247_d, this.field_70248_e, this.field_70245_f, this);
/*     */           }
/*     */         }
/* 226 */         this.field_70254_i = true;
/*     */       }
/*     */ 
/* 229 */       this.posX += this.motionX;
/* 230 */       this.posY += this.motionY;
/* 231 */       this.posZ += this.motionZ;
/* 232 */       float f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
/* 233 */       this.rotationYaw = ((float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / 3.141592653589793D));
/*     */ 
/* 235 */       for (this.rotationPitch = ((float)(Math.atan2(this.motionY, f2) * 180.0D / 3.141592653589793D)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F);
/* 240 */       while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
/*     */       {
/* 242 */         this.prevRotationPitch += 360.0F;
/*     */       }
/*     */ 
/* 245 */       while (this.rotationYaw - this.prevRotationYaw < -180.0F)
/*     */       {
/* 247 */         this.prevRotationYaw -= 360.0F;
/*     */       }
/*     */ 
/* 250 */       while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
/*     */       {
/* 252 */         this.prevRotationYaw += 360.0F;
/*     */       }
/*     */ 
/* 255 */       this.rotationPitch = (this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F);
/* 256 */       this.rotationYaw = (this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F);
/* 257 */       float f4 = 0.99F;
/* 258 */       float f1 = 0.05F;
/*     */ 
/* 260 */       if (isInWater())
/*     */       {
/* 262 */         for (int j1 = 0; j1 < 4; j1++)
/*     */         {
/* 264 */           this.worldObj.spawnParticle("bubble", this.posX - this.motionX * f3, this.posY - this.motionY * f3, this.posZ - this.motionZ * f3, this.motionX, this.motionY, this.motionZ);
/*     */         }
/*     */ 
/* 267 */         f4 = 0.8F;
/*     */       }
/*     */ 
/* 270 */       this.motionX *= f4;
/* 271 */       this.motionY *= f4;
/* 272 */       this.motionZ *= f4;
/* 273 */       this.motionY -= (this.type == 1 ? 0.0122D : 0.15D);
/* 274 */       setPosition(this.posX, this.posY, this.posZ);
/* 275 */       doBlockCollisions();
/*     */ 
/* 277 */       if ((this.field_70254_i) && (this.ticks >= 5))
/*     */       {
/* 279 */         explode();
/* 280 */         setDead();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void explode()
/*     */   {
/* 287 */     GunCus.createExplosionServer(this.field_70250_c, this.posX, this.posY, this.posZ, 7.0F / this.type);
/*     */   }
/*     */ 
/*     */   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
/*     */   {
/* 292 */     setDead();
/*     */   }
/*     */ 
/*     */   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
/*     */   {
/* 297 */     setDead();
/*     */   }
/*     */ }

/* Location:           C:\Users\Nate\Desktop\Mod\GunCusClass.zip
 * Qualified Name:     assets.guncus.GunCusEntityAT
 * JD-Core Version:    0.6.2
 */