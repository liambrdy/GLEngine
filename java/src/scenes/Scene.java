package scenes;

import renderEngine.Loader;
import renderEngine.MasterRenderer;

public interface Scene {

	public void init(Loader loader, MasterRenderer renderer);
	public void update();
	public void render();
	public void cleanup();
	
}
