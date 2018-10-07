package engineTester;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import scenes.IslandScene;
import terrains.Terrain;
import water.WaterTile;

public class MainGameLoop {

	public static void main(String[] args) {
		
		DisplayManager.createDisplay();
		Loader loader = new Loader(); 
		TextMaster.init(loader);
        
        FontType font = new FontType(loader.loadTexture("fonts/distance-candara", -0.4f), new File("res/fonts/distance-candara.fnt"));
        GUIText text = new GUIText("This is some text!", 3f, font, new Vector2f(0, 0), 1f, true);
        text.setColor(1, 0, 0);
		
		MasterRenderer renderer = new MasterRenderer(loader);
		IslandScene scene = new IslandScene(loader, renderer);
		GuiRenderer guiRenderer = scene.getGuiRenderer();
		
		List<Entity> entities = new ArrayList<Entity>();
		List<Terrain> terrains = new ArrayList<Terrain>();
		List<GuiTexture> guis = new ArrayList<GuiTexture>(); 
		List<Light> lights = new ArrayList<Light>();
		List<WaterTile> waters = new ArrayList<WaterTile>();
		
		terrains = scene.getTerrains();
		entities = scene.getEntities();
		lights = scene.getLights();
		waters = scene.getWaters();
		
		Terrain terrain = terrains.get(0);
		Player player = scene.getPlayer();
		
		Camera camera = scene.getCamera();
		Light sun = lights.get(0);
				
		// ************** MAIN LOOP ****************
		while(!Display.isCloseRequested()) {
			camera.move();	
			player.move(terrain);
			
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			scene.renderFbos();
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			
			renderer.renderScene(entities, scene.getNormalMapEntities(), terrains, lights, camera, new Vector4f(0, -1, 0, 10000000));
			scene.getWaterRenderer().render(waters, camera, sun);
			guiRenderer.render(guis);
			TextMaster.render();
			DisplayManager.updateDisplay();
		}
		TextMaster.cleanUp();
		scene.cleanUp();
		
		DisplayManager.closeDisplay();
	}

}
