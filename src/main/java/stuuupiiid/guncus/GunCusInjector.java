package stuuupiiid.guncus;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;

public class GunCusInjector {
	public ClassLoader classloader;
	public Method method;

	public GunCusInjector(ClassLoader classloader, Method method) {
		this.classloader = classloader;
		this.method = method;
	}

	public void addToClassPath(File file) {
		try {
			this.method.invoke(this.classloader, new Object[] { file.toURI().toURL() });
		} catch (NullPointerException e) {
		} catch (Exception e) {
			GunCus.log("Failed to add some textures to class path");
			e.printStackTrace();
		}
	}
}
