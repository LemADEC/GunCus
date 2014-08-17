/*     */ package assets.guncusexplosives;
/*     */ 
/*     */ import assets.guncus.GunCus;
/*     */ import cpw.mods.fml.common.FMLCommonHandler;
/*     */ import cpw.mods.fml.relauncher.Side;
/*     */ import cpw.mods.fml.relauncher.SideOnly;
/*     */ import java.util.Random;
/*     */ import net.minecraft.block.Block;
/*     */ import net.minecraft.block.material.Material;
/*     */ import net.minecraft.client.renderer.texture.IconRegister;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.EntityLivingBase;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.item.Item;
/*     */ import net.minecraft.util.AABBPool;
/*     */ import net.minecraft.util.AxisAlignedBB;
/*     */ import net.minecraft.util.Icon;
/*     */ import net.minecraft.world.Explosion;
/*     */ import net.minecraft.world.IBlockAccess;
/*     */ import net.minecraft.world.World;
/*     */ 
/*     */ public class GunCusExplosivesMine extends Block
/*     */ {
/*     */   private Icon field_94336_cN;
/*     */ 
/*     */   public GunCusExplosivesMine(int par1)
/*     */   {
/*  28 */     super(par1, Material.iron);
/*  29 */     setUnlocalizedName("gcmine");
/*  30 */     setHardness(1.0F);
/*  31 */     setResistance(1.0F);
/*     */   }
/*     */ 
/*     */   @SideOnly(Side.CLIENT)
/*     */   public Icon getIcon(int par1, int par2)
/*     */   {
/*  40 */     return this.field_94336_cN;
/*     */   }
/*     */ 
/*     */   @SideOnly(Side.CLIENT)
/*     */   public void registerIcons(IconRegister par1IconRegister)
/*     */   {
/*  47 */     this.field_94336_cN = par1IconRegister.registerIcon("guncusexplosives:mine");
/*     */   }
/*     */ 
/*     */   public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
/*     */   {
/*  53 */     int l = par1World.getBlockMetadata(par2, par3, par4) & 0x7;
/*  54 */     float f = 0.125F;
/*  55 */     return AxisAlignedBB.getAABBPool().getAABB(par2 + this.minX, par3 + this.minY, par4 + this.minZ, par2 + this.maxX, par3 + l * f, par4 + this.maxZ);
/*     */   }
/*     */ 
/*     */   public boolean isOpaqueCube()
/*     */   {
/*  61 */     return false;
/*     */   }
/*     */ 
/*     */   @SideOnly(Side.CLIENT)
/*     */   public boolean renderAsNormalBlock()
/*     */   {
/*  68 */     return false;
/*     */   }
/*     */ 
/*     */   public void setBlockBoundsForItemRender()
/*     */   {
/*  74 */     setBlockBoundsForSnowDepth(0);
/*     */   }
/*     */ 
/*     */   public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
/*     */   {
/*  80 */     setBlockBoundsForSnowDepth(par1IBlockAccess.getBlockMetadata(par2, par3, par4));
/*     */   }
/*     */ 
/*     */   protected void setBlockBoundsForSnowDepth(int par1)
/*     */   {
/*  85 */     int j = par1 & 0x7;
/*  86 */     float f = 2 * (1 + j) / 16.0F;
/*  87 */     setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);
/*     */   }
/*     */ 
/*     */   public void harvestBlock(World par1World, EntityPlayer par2EntityPlayer, int par3, int par4, int par5, int par6)
/*     */   {
/*  93 */     super.harvestBlock(par1World, par2EntityPlayer, par3, par4, par5, par6);
/*  94 */     par1World.setBlockToAir(par3, par4, par5);
/*     */   }
/*     */ 
/*     */   public int idDropped(int par1, Random par2Random, int par3)
/*     */   {
/* 100 */     return GunCusExplosives.mineItem.itemID;
/*     */   }
/*     */ 
/*     */   @SideOnly(Side.CLIENT)
/*     */   public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
/*     */   {
/* 107 */     return par5 == 1 ? true : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
/*     */   }
/*     */ 
/*     */   public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4)
/*     */   {
/* 112 */     return Block.blocksList[par1World.getBlockId(par2, par3 - 1, par4)].isOpaqueCube();
/*     */   }
/*     */ 
/*     */   public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity)
/*     */   {
/* 117 */     if ((par5Entity instanceof EntityLivingBase))
/*     */     {
/* 119 */       explode(par2, par3, par4, par1World);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void explode(int x, int y, int z, World world)
/*     */   {
/* 125 */     if (FMLCommonHandler.instance().getEffectiveSide().isServer())
/*     */     {
/* 127 */       world.createExplosion(null, x + 0.5D, y + 1, z + 0.5D, 1.3F, GunCus.blockDamage);
/* 128 */       world.createExplosion(null, x + 0.5D, y + 1, z + 0.5D, 3.0F, false);
/* 129 */       world.setBlockToAir(x, y, z);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion)
/*     */   {
/* 136 */     explode(x, y, z, world);
/*     */   }
/*     */ }

/* Location:           C:\Users\Nate\Desktop\Mod\GunCusClass.zip
 * Qualified Name:     assets.guncusexplosives.GunCusExplosivesMine
 * JD-Core Version:    0.6.2
 */