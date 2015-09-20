package stuuupiiid.guncus;

import cpw.mods.fml.client.registry.RenderingRegistry;
import stuuupiiid.guncus.entity.EntityGrenade;
import stuuupiiid.guncus.entity.EntityBullet;
import stuuupiiid.guncus.render.RenderGrenade;
import stuuupiiid.guncus.render.RenderBullet;

public class ClientProxy extends CommonProxy {
	@Override
	public void initRenderingRegistry() {
		RenderingRegistry.registerEntityRenderingHandler(EntityBullet.class, new RenderBullet());
		RenderingRegistry.registerEntityRenderingHandler(EntityGrenade.class, new RenderGrenade());
	}
}
