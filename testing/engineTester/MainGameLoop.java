package engineTester;

import org.lwjgl.util.Display;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import scenes.IslandScene;

public class MainGameLoop {

	public static void main(String[] args) {
				
		// ************** MAIN LOOP ****************
		DisplayManager.createDisplay();
		
		Loader loader = new Loader();
		MasterRenderer renderer = new MasterRenderer(loader);
		IslandScene scene = new IslandScene();
		
		scene.init(loader, renderer);
		while(!Display.isCloseRequested()) {
			scene.update();
			scene.render();
			
		}
		scene.cleanup();
		
		DisplayManager.closeDisplay();
	}

}
