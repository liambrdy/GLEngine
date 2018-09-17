package engineTester;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import scenes.WaterScene;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class MainGameLoop {

	public static void main(String[] args) {
		
		DisplayManager.createDisplay();
		Loader loader = new Loader(); 
		
		MasterRenderer renderer = new MasterRenderer(loader);
		WaterScene scene = new WaterScene(loader, renderer);
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
		
		WaterTile water = waters.get(0);
		
		// ************** MAIN LOOP ****************
		while(!Display.isCloseRequested()) {
			camera.move();	
			player.move(terrain);
			
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			scene.renderFbos();
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			
			scene.getFbos().unbindCurrentFrameBuffer();
			renderer.renderScene(entities, terrains, lights, camera, new Vector4f(0, -1, 0, 10000000));
			scene.getWaterRenderer().render(waters, camera, sun);
			guiRenderer.render(guis);
			DisplayManager.updateDisplay();
		}
		scene.cleanUp();
		
		DisplayManager.closeDisplay();
	}

}
