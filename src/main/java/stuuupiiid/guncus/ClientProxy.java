package stuuupiiid.guncus;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import stuuupiiid.guncus.entity.EntityGrenade;
import stuuupiiid.guncus.entity.EntityBullet;
import stuuupiiid.guncus.entity.EntityRocket;
import stuuupiiid.guncus.render.RenderGameOverlay;
import stuuupiiid.guncus.render.RenderGrenade;
import stuuupiiid.guncus.render.RenderBullet;
import stuuupiiid.guncus.render.RenderRocket;

public class ClientProxy extends CommonProxy {
	@Override
	public void onForgePreInit() {
		
		//blocks
		GunCus.blockAmmoBox.onForgePreinit();
		GunCus.blockBulletBox.onForgePreinit();
		GunCus.blockGunBox.onForgePreinit();
		GunCus.blockMagazineBox.onForgePreinit();
		GunCus.blockWeaponBox.onForgePreinit();
		if (GunCus.blockMine != null) GunCus.blockMine.onForgePreinit();
		
		// items
		if (GunCus.itemBoxpart != null) GunCus.itemBoxpart.onForgePreinit();
		if (GunCus.itemQuickKnife != null) GunCus.itemQuickKnife.onForgePreinit();
		if (GunCus.itemMagazineFiller != null) GunCus.itemMagazineFiller.onForgePreinit();
		if (GunCus.itemAmmoM320 != null) GunCus.itemAmmoM320.onForgePreinit();
		if (GunCus.itemAttachment != null) GunCus.itemAttachment.onForgePreinit();
		if (GunCus.itemBarrel != null) GunCus.itemBarrel.onForgePreinit();
		if (GunCus.itemScope != null) GunCus.itemScope.onForgePreinit();
		
		if (GunCus.itemRPGmagazine != null) GunCus.itemRPGmagazine.onForgePreinit();
		if (GunCus.itemRPG != null) GunCus.itemRPG.onForgePreinit();
		
		if (GunCus.itemSMAWmagazine != null) GunCus.itemSMAWmagazine.onForgePreinit();
		if (GunCus.itemSMAW != null) GunCus.itemSMAW.onForgePreinit();
		
		// entities
		RenderingRegistry.registerEntityRenderingHandler(EntityBullet.class, RenderBullet::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityGrenade.class, RenderGrenade::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityRocket.class, RenderRocket::new);
		
		RenderGameOverlay renderGameOverlay = new RenderGameOverlay();
		MinecraftForge.EVENT_BUS.register(new GunCusKeyBindings());
		MinecraftForge.EVENT_BUS.register(renderGameOverlay);
	}
}
