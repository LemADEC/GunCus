package stuuupiiid.guncus;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import stuuupiiid.guncus.entity.EntityGrenade;
import stuuupiiid.guncus.entity.EntityBullet;
import stuuupiiid.guncus.render.RenderGameOverlay;
import stuuupiiid.guncus.render.RenderGrenade;
import stuuupiiid.guncus.render.RenderBullet;

public class ClientProxy extends CommonProxy {
	@Override
	public void initRenderingRegistry() {
		RenderingRegistry.registerEntityRenderingHandler(EntityBullet.class, new RenderBullet());
		RenderingRegistry.registerEntityRenderingHandler(EntityGrenade.class, new RenderGrenade());
		
		RenderGameOverlay renderGameOverlay = new RenderGameOverlay();
		FMLCommonHandler.instance().bus().register(renderGameOverlay);
		MinecraftForge.EVENT_BUS.register(renderGameOverlay);
	}
}
