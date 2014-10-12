 package stuuupiiid.guncus;
 
 import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
 
 public class GunCusEntityAT extends EntityArrow implements IProjectile {
   private int blockX = -1;
   private int blockY = -1;
   private int blockZ = -1;
   private Block collision_block = Blocks.air;
   private boolean field_70254_i = false;
   public EntityPlayer field_70250_c;
   private int field_70252_j;
   private int field_70257_an = 0;
   // private int field_70256_ap;
   private int liveTime = 300;
 
   private int type = 1;
   public List<Integer> effects;
   public HashMap<Integer, Float> effectModifiers;
   public int ticks = 0;
 
   public GunCusEntityAT(World world)
   {
     super(world);
     setSize(0.5F, 0.5F);
   }
 
   public GunCusEntityAT(World par1World, EntityPlayer par2EntityPlayer, int accuracy, int type)
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
     // this.field_70256_ap = 0;
     setThrowableHeading(this.motionX, this.motionY, this.motionZ, type == 1 ? 3.0F : 4.2F, 1.0F);
     this.type = type;
   }
 
   @Override
public void onUpdate()
   {
     super.onEntityUpdate();
 
     this.ticks += 1;
 
     this.liveTime -= 1;
 
     if ((this.posY > 300.0D) || (this.liveTime <= 0))
     {
       setDead();
     }
 
     if ((this.prevRotationPitch == 0.0F) && (this.prevRotationYaw == 0.0F))
     {
       float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
       this.prevRotationYaw = (this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / 3.141592653589793D));
       this.prevRotationPitch = (this.rotationPitch = (float)(Math.atan2(this.motionY, f) * 180.0D / 3.141592653589793D));
     }
 
     Block block = this.worldObj.getBlock(this.blockX, this.blockY, this.blockZ);
 
     if (block != Blocks.air) {
       block.setBlockBoundsBasedOnState(this.worldObj, this.blockX, this.blockY, this.blockZ);
       AxisAlignedBB axisalignedbb = block.getCollisionBoundingBoxFromPool(this.worldObj, this.blockX, this.blockY, this.blockZ);
 
       if ((axisalignedbb != null) && (axisalignedbb.isVecInside(Vec3.createVectorHelper(this.posX, this.posY, this.posZ))))
       {
         this.field_70254_i = true;
       }
     }
     double f3 = 0.25D;
 
     this.worldObj.spawnParticle("largesmoke", this.posX - this.motionX * f3, this.posY - this.motionY * f3, this.posZ - this.motionZ * f3, 0.0D, 0.0D, 0.0D);
     this.worldObj.spawnParticle("largesmoke", this.posX - this.motionX * f3, this.posY - this.motionY * f3, this.posZ - this.motionZ * f3, 0.0D, 0.0D, 0.0D);
     this.worldObj.spawnParticle("largesmoke", this.posX - this.motionX * f3, this.posY - this.motionY * f3, this.posZ - this.motionZ * f3, 0.0D, 0.0D, 0.0D);
     this.worldObj.spawnParticle("largesmoke", this.posX - this.motionX * f3, this.posY - this.motionY * f3, this.posZ - this.motionZ * f3, 0.0D, 0.0D, 0.0D);
 
     if (this.field_70254_i)
     {
       this.motionY -= (this.type == 1 ? 0.007D : 0.15D);
       setPosition(this.posX, this.posY, this.posZ);
       func_145775_I();	// doBlockCollisions();
       this.field_70252_j += 1;
       if (this.field_70252_j > 100)
       {
         explode();
         setDead();
       }
     }
     else
     {
       this.field_70257_an += 1;
       Vec3 vec3 = Vec3.createVectorHelper(posX, posY, posZ);
       Vec3 vec31 = Vec3.createVectorHelper(posX + motionX, posY + motionY, posZ + motionZ);
       MovingObjectPosition movingobjectposition = this.worldObj.func_147447_a(vec3, vec31, false, true, false);
       vec3 = Vec3.createVectorHelper(posX, posY, posZ);
       vec31 = Vec3.createVectorHelper(posX + motionX, posY + motionY, posZ + motionZ);
 
       if (movingobjectposition != null)
       {
         vec31 = Vec3.createVectorHelper(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
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
 
         if ((entityplayer.capabilities.disableDamage) || ((this.field_70250_c != null) && (!this.field_70250_c.canAttackPlayer(entityplayer))))
         {
           movingobjectposition = null;
         }
        }
 
       if (movingobjectposition != null)
       {
         if (movingobjectposition.entityHit != null)
         {
           this.field_70254_i = true;
         }
         else
         {
           blockX = movingobjectposition.blockX;
           blockY = movingobjectposition.blockY;
           blockZ = movingobjectposition.blockZ;
           collision_block = worldObj.getBlock(blockX, blockY, blockZ);
           motionX = ((float)(movingobjectposition.hitVec.xCoord - posX));
           motionY = ((float)(movingobjectposition.hitVec.yCoord - posY));
           motionZ = ((float)(movingobjectposition.hitVec.zCoord - posZ));
           float f2 = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
           posX -= motionX / f2 * 0.0500000007450581D;
           posY -= motionY / f2 * 0.0500000007450581D;
           posZ -= motionZ / f2 * 0.0500000007450581D;
           field_70254_i = true;
           setIsCritical(false);
 
           if (collision_block != Blocks.air)
           {
             collision_block.onEntityCollidedWithBlock(this.worldObj, this.blockX, this.blockY, this.blockZ, this);
           }
         }
         field_70254_i = true;
       }
 
       posX += motionX;
       posY += motionY;
       posZ += motionZ;
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
           this.worldObj.spawnParticle("bubble", this.posX - this.motionX * f3, this.posY - this.motionY * f3, this.posZ - this.motionZ * f3, this.motionX, this.motionY, this.motionZ);
         }
 
         f4 = 0.8F;
       }
 
       this.motionX *= f4;
       this.motionY *= f4;
       this.motionZ *= f4;
       this.motionY -= (this.type == 1 ? 0.0122D : 0.15D);
       setPosition(this.posX, this.posY, this.posZ);
       func_145775_I();	// doBlockCollisions();
 
       if ((this.field_70254_i) && (this.ticks >= 5))
       {
         explode();
         setDead();
       }
     }
   }
 
   public void explode()
   {
     GunCus.createExplosionServer(this.field_70250_c, this.posX, this.posY, this.posZ, 7.0F / this.type);
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
