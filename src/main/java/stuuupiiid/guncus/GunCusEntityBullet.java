 package stuuupiiid.guncus;
 
 import com.google.common.io.ByteArrayDataOutput;
 import com.google.common.io.ByteStreams;
 import cpw.mods.fml.common.FMLCommonHandler;
 import cpw.mods.fml.common.network.PacketDispatcher;
 import cpw.mods.fml.common.network.Player;
 import cpw.mods.fml.relauncher.Side;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Random;
 import net.minecraft.block.Block;
 import net.minecraft.entity.Entity;
 import net.minecraft.entity.EntityLiving;
 import net.minecraft.entity.EntityLivingBase;
 import net.minecraft.entity.IProjectile;
 import net.minecraft.entity.player.EntityPlayer;
 import net.minecraft.entity.player.PlayerCapabilities;
 import net.minecraft.entity.projectile.EntityArrow;
 import net.minecraft.nbt.NBTTagCompound;
 import net.minecraft.network.packet.Packet250CustomPayload;
 import net.minecraft.potion.Potion;
 import net.minecraft.potion.PotionEffect;
 import net.minecraft.util.AxisAlignedBB;
 import net.minecraft.util.DamageSource;
 import net.minecraft.util.MathHelper;
 import net.minecraft.util.MovingObjectPosition;
 import net.minecraft.util.Vec3;
 import net.minecraft.util.Vec3Pool;
 import net.minecraft.world.World;
 
 public class GunCusEntityBullet extends EntityArrow
   implements IProjectile
 {
   private int field_70247_d = -1;
   private int field_70248_e = -1;
   private int field_70245_f = -1;
   private int field_70246_g = 0;
   private int field_70253_h = 0;
   private boolean field_70254_i = false;
   public EntityPlayer field_70250_c;
   private int field_70252_j;
   private int field_70257_an = 0;
   private float field_70255_ao;
   private int field_70256_ap;
   private int liveTime = 300;
   public int ticks = 0;
   public boolean gravity = false;
   public double gravityMod = 1.0D;
 
   public List<Integer> effects = new ArrayList();
   public HashMap<Integer, Float> effectModifiers = new HashMap();
 
   public GunCusEntityBullet(World world)
   {
     super(world);
     setSize(0.5F, 0.5F);
   }
 
   public GunCusEntityBullet(World par1World, EntityPlayer par2EntityPlayer, float speed, float damage, int accuracy)
   {
     super(par1World);
     this.renderDistanceWeight = 10.0D;
     this.field_70250_c = par2EntityPlayer;
     setSize(0.5F, 0.5F);
     setLocationAndAngles(par2EntityPlayer.posX, par2EntityPlayer.posY + par2EntityPlayer.getEyeHeight(), par2EntityPlayer.posZ, par2EntityPlayer.rotationYaw, par2EntityPlayer.rotationPitch);
     this.posX -= MathHelper.cos(this.rotationYaw / 180.0F * 3.141593F) * 0.16F;
     this.posY -= 0.1000000014901161D;
     this.posZ -= MathHelper.sin(this.rotationYaw / 180.0F * 3.141593F) * 0.16F;
     setPosition(par2EntityPlayer.posX, par2EntityPlayer.posY + par2EntityPlayer.getEyeHeight(), par2EntityPlayer.posZ);
     this.yOffset = 0.0F;
     this.motionX = (-MathHelper.sin(this.rotationYaw / 180.0F * 3.141593F) * MathHelper.cos(this.rotationPitch / 180.0F * 3.141593F));
     this.motionZ = (MathHelper.cos(this.rotationYaw / 180.0F * 3.141593F) * MathHelper.cos(this.rotationPitch / 180.0F * 3.141593F));
     this.motionY = (-MathHelper.sin(this.rotationPitch / 180.0F * 3.141593F));
     if (accuracy < 100)
     {
       int accX1 = this.rand.nextInt(101 - accuracy);
       int accY1 = this.rand.nextInt(101 - accuracy);
       int accZ1 = this.rand.nextInt(101 - accuracy);
       double accX2 = accX1 - this.rand.nextInt(accX1 + 1) * 2;
       double accY2 = accY1 - this.rand.nextInt(accY1 + 1) * 2;
       double accZ2 = accZ1 - this.rand.nextInt(accZ1 + 1) * 2;
       this.motionX += accX2 / 370.0D;
       this.motionY += accY2 / 370.0D;
       this.motionZ += accZ2 / 370.0D;
     }
     this.field_70256_ap = 0;
     this.field_70255_ao = damage;
     setThrowableHeading(this.motionX, this.motionY, this.motionZ, speed, 1.0F);
   }
 
   public GunCusEntityBullet setEffects(List effects, HashMap<Integer, Float> modifies)
   {
     this.effects = effects;
     this.effectModifiers = modifies;
     return this;
   }
 
   public GunCusEntityBullet setGravityMod(double modifier)
   {
     this.gravityMod = modifier;
     return this;
   }
 
   public GunCusEntityBullet setLowerGravity(boolean flag)
   {
     this.gravity = flag;
     return this;
   }
 
   @Override
public void onUpdate()
   {
     super.onEntityUpdate();
 
     this.ticks += 1;
 
     this.liveTime -= 1;
 
     if (this.liveTime <= 0)
     {
       setDead();
     }
 
     if ((this.prevRotationPitch == 0.0F) && (this.prevRotationYaw == 0.0F))
     {
       float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
       this.prevRotationYaw = (this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / 3.141592653589793D));
       this.prevRotationPitch = (this.rotationPitch = (float)(Math.atan2(this.motionY, f) * 180.0D / 3.141592653589793D));
     }
 
     int i = this.worldObj.getBlockId(this.field_70247_d, this.field_70248_e, this.field_70245_f);
 
     if ((i > 0) && (i != 20) && (i != 30) && (i != 89) && (i != 102))
     {
       Block.blocksList[i].setBlockBoundsBasedOnState(this.worldObj, this.field_70247_d, this.field_70248_e, this.field_70245_f);
       AxisAlignedBB axisalignedbb = Block.blocksList[i].getCollisionBoundingBoxFromPool(this.worldObj, this.field_70247_d, this.field_70248_e, this.field_70245_f);
 
       if ((axisalignedbb != null) && (axisalignedbb.isVecInside(this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ))))
       {
         this.field_70254_i = true;
       }
     }
 
     if (this.field_70254_i)
     {
       onInGround();
     }
     else
     {
       this.field_70257_an += 1;
       Vec3 vec3 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ);
       Vec3 vec31 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
       MovingObjectPosition movingobjectposition = this.worldObj.rayTraceBlocks_do_do(vec3, vec31, false, true);
       vec3 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ);
       vec31 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
 
       if (movingobjectposition != null)
       {
         vec31 = this.worldObj.getWorldVec3Pool().getVecFromPool(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
       }
 
       Entity entity = null;
       List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
       double d0 = 0.0D;
 
       for (int l = 0; l < list.size(); l++)
       {
         Entity entity1 = (Entity)list.get(l);
 
         if ((entity1.canBeCollidedWith()) && ((entity1 != this.field_70250_c) || (this.field_70257_an >= 5)))
         {
           float f1 = 0.3F;
           AxisAlignedBB axisalignedbb1 = entity1.boundingBox.expand(f1, f1, f1);
           MovingObjectPosition movingobjectposition1 = axisalignedbb1.calculateIntercept(vec3, vec31);
 
           if (movingobjectposition1 != null)
           {
             double d1 = vec3.distanceTo(movingobjectposition1.hitVec);
 
             if ((d1 < d0) || (d0 == 0.0D))
             {
               entity = entity1;
               d0 = d1;
             }
           }
         }
       }
 
       if (entity != null)
       {
         movingobjectposition = new MovingObjectPosition(entity);
       }
 
       if ((movingobjectposition != null) && (movingobjectposition.entityHit != null) && ((movingobjectposition.entityHit instanceof EntityPlayer)))
       {
         EntityPlayer entityplayer = (EntityPlayer)movingobjectposition.entityHit;
 
         if ((entityplayer.capabilities.disableDamage) || (((this.field_70250_c instanceof EntityPlayer)) && (!this.field_70250_c.canAttackPlayer(entityplayer))))
         {
           movingobjectposition = null;
         }
 
       }
 
       if (movingobjectposition != null)
       {
         if ((movingobjectposition.entityHit != null) && (this.ticks >= 1))
         {
           this.field_70254_i = true;
           DamageSource damagesource = null;
 
           if (this.field_70250_c != null)
           {
             damagesource = DamageSource.causePlayerDamage(this.field_70250_c);
           }
           else
           {
             damagesource = DamageSource.causeArrowDamage(this, this);
           }
 
           if ((this.field_70255_ao > 0.0F) && (movingobjectposition.entityHit.attackEntityFrom(damagesource, this.field_70255_ao)))
           {
             if (((movingobjectposition.entityHit instanceof EntityLiving)) && (!movingobjectposition.entityHit.isDead))
             {
               EntityLiving entityliving = (EntityLiving)movingobjectposition.entityHit;
               entityliving.hurtResistantTime = 0;
             }
           }
           if (!movingobjectposition.entityHit.isDead)
           {
             ByteArrayDataOutput bytes = ByteStreams.newDataOutput();
             bytes.writeInt(10);
             bytes.writeInt(0);
             PacketDispatcher.sendPacketToPlayer(new Packet250CustomPayload("guncus", bytes.toByteArray()), (Player)this.field_70250_c);
           }
 
           hitEntity(movingobjectposition.entityHit);
         }
         else
         {
           this.field_70247_d = movingobjectposition.blockX;
           this.field_70248_e = movingobjectposition.blockY;
           this.field_70245_f = movingobjectposition.blockZ;
           this.field_70246_g = this.worldObj.getBlockId(this.field_70247_d, this.field_70248_e, this.field_70245_f);
           if ((this.field_70246_g == 20) || (this.field_70246_g == 30) || (this.field_70246_g == 89) || (this.field_70246_g == 102))
           {
             GunCus.removeBlockServer(this.field_70250_c, MathHelper.floor_double(this.field_70247_d), MathHelper.floor_double(this.field_70248_e), MathHelper.floor_double(this.field_70245_f));
           }
           else if (this.field_70246_g != 0)
           {
             this.field_70253_h = this.worldObj.getBlockMetadata(this.field_70247_d, this.field_70248_e, this.field_70245_f);
             this.motionX = ((float)(movingobjectposition.hitVec.xCoord - this.posX));
             this.motionY = ((float)(movingobjectposition.hitVec.yCoord - this.posY));
             this.motionZ = ((float)(movingobjectposition.hitVec.zCoord - this.posZ));
             float f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
             this.posX -= this.motionX / f2 * 0.0500000007450581D;
             this.posY -= this.motionY / f2 * 0.0500000007450581D;
             this.posZ -= this.motionZ / f2 * 0.0500000007450581D;
             setIsCritical(false);
             this.field_70254_i = true;
             Block.blocksList[this.field_70246_g].onEntityCollidedWithBlock(this.worldObj, this.field_70247_d, this.field_70248_e, this.field_70245_f, this);
           }
         }
       }
 
       this.posX += this.motionX;
       this.posY += this.motionY;
       this.posZ += this.motionZ;
       float f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
       this.rotationYaw = ((float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / 3.141592653589793D));
 
       for (this.rotationPitch = ((float)(Math.atan2(this.motionY, f2) * 180.0D / 3.141592653589793D)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F);
       while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
       {
         this.prevRotationPitch += 360.0F;
       }
 
       while (this.rotationYaw - this.prevRotationYaw < -180.0F)
       {
         this.prevRotationYaw -= 360.0F;
       }
 
       while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
       {
         this.prevRotationYaw += 360.0F;
       }
 
       this.rotationPitch = (this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F);
       this.rotationYaw = (this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F);
       float f4 = 0.99F;
       // float f1 = 0.05F;
 
       if (isInWater())
       {
         for (int j1 = 0; j1 < 4; j1++)
         {
           float f3 = 0.25F;
           this.worldObj.spawnParticle("bubble", this.posX - this.motionX * f3, this.posY - this.motionY * f3, this.posZ - this.motionZ * f3, this.motionX, this.motionY, this.motionZ);
         }
 
         f4 = 0.8F;
       }
 
       this.motionX *= f4;
       this.motionY *= f4;
       this.motionZ *= f4;
       this.motionY -= (this.gravity ? 0.014D : 0.02D) * this.gravityMod;
       setPosition(this.posX, this.posY, this.posZ);
       doBlockCollisions();
 
       if (this.field_70254_i)
       {
         onInGround();
       }
     }
   }
 
   @Override
protected void doBlockCollisions()
   {
     int i = MathHelper.floor_double(this.boundingBox.minX + 0.001D);
     int j = MathHelper.floor_double(this.boundingBox.minY + 0.001D);
     int k = MathHelper.floor_double(this.boundingBox.minZ + 0.001D);
     int l = MathHelper.floor_double(this.boundingBox.maxX - 0.001D);
     int i1 = MathHelper.floor_double(this.boundingBox.maxY - 0.001D);
     int j1 = MathHelper.floor_double(this.boundingBox.maxZ - 0.001D);
 
     if (this.worldObj.checkChunksExist(i, j, k, l, i1, j1))
     {
       for (int k1 = i; k1 <= l; k1++)
       {
         for (int l1 = j; l1 <= i1; l1++)
         {
           for (int i2 = k; i2 <= j1; i2++)
           {
             int j2 = this.worldObj.getBlockId(k1, l1, i2);
 
             if ((j2 > 0) && (j2 != 20) && (j2 != 30) && (j2 != 89) && (j2 != 102))
             {
               Block.blocksList[j2].onEntityCollidedWithBlock(this.worldObj, k1, l1, i2, this);
             }
           }
         }
       }
     }
   }
 
   public void hitEntity(Entity entity2)
   {
     if ((FMLCommonHandler.instance().getEffectiveSide().isServer()) && ((entity2 instanceof EntityLivingBase)))
     {
       EntityLivingBase entity = (EntityLivingBase)entity2;
 
       if (this.effects.contains(Integer.valueOf(1)))
       {
         entity.addPotionEffect(new PotionEffect(Potion.poison.id, MathHelper.floor_double(this.effectModifiers.get(Integer.valueOf(1)).floatValue()) * 20, 0));
       }
 
       if (this.effects.contains(Integer.valueOf(2)))
       {
         entity.addPotionEffect(new PotionEffect(Potion.confusion.id, MathHelper.floor_double(this.effectModifiers.get(Integer.valueOf(2)).floatValue()) * 20, 0));
       }
 
       if (this.effects.contains(Integer.valueOf(3)))
       {
         entity.setFire(MathHelper.floor_double(this.effectModifiers.get(Integer.valueOf(3)).floatValue()));
       }
 
       if (this.effects.contains(Integer.valueOf(7)))
       {
         entity.addPotionEffect(new PotionEffect(Potion.blindness.id, MathHelper.floor_double(this.effectModifiers.get(Integer.valueOf(7)).floatValue()) * 20, 0));
       }
 
       if (this.effects.contains(Integer.valueOf(6)))
       {
         entity.heal(this.effectModifiers.get(Integer.valueOf(6)).floatValue());
       }
     }
   }
 
   public void onInGround()
   {
     if (FMLCommonHandler.instance().getEffectiveSide().isServer())
     {
       if (this.effects.contains(Integer.valueOf(4)))
       {
         this.worldObj.createExplosion(this.field_70250_c, this.posX, this.posY, this.posZ, this.effectModifiers.get(Integer.valueOf(4)).floatValue(), GunCus.blockDamage);
       }
 
       if (this.effects.contains(Integer.valueOf(5)))
       {
         this.worldObj.createExplosion(this.field_70250_c, this.posX, this.posY, this.posZ, this.effectModifiers.get(Integer.valueOf(5)).floatValue(), false);
       }
 
       if ((this.effects.contains(Integer.valueOf(3))) && (this.field_70248_e > 0))
       {
         if (this.worldObj.isAirBlock(this.field_70247_d, this.field_70248_e + 1, this.field_70245_f))
         {
           this.worldObj.setBlock(this.field_70247_d, this.field_70248_e + 1, this.field_70245_f, Block.fire.blockID);
         }
 
         if (this.worldObj.isAirBlock(this.field_70247_d, this.field_70248_e, this.field_70245_f))
         {
           this.worldObj.setBlock(this.field_70247_d, this.field_70248_e, this.field_70245_f, Block.fire.blockID);
         }
       }
 
       setDead();
     }
   }
 
   @Override
public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
   {
     setDead();
   }
 
   @Override
public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
   {
     setDead();
   }
 }




