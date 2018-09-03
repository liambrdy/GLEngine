package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

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
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

public class MainGameLoop {

	public static void main(String[] args) {
		
		DisplayManager.createDisplay();
		Loader loader = new Loader();
		Random random = new Random();
		
		List<Entity> entities = new ArrayList<Entity>();
		
		// *********TERRAIN TEXTURE STUFF***********
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("textures/grassy3"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("textures/dirt"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("textures/pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("textures/mossPath256"));

		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMaps/blendMap"));
		Terrain terrain1 = new Terrain(0,0,loader, texturePack, blendMap, "heightMaps/heightmap");
		// *****************************************
		
		// *********MODEL LOADING STUFF ************
		ModelData playerData = OBJFileLoader.loadOBJ("player");
		RawModel playerModel = loader.loadToVAO(playerData.getVertices(), playerData.getTextureCoords(), playerData.getNormals(), playerData.getIndices());
		ModelTexture playerTexture = new ModelTexture(loader.loadTexture("textures/playerTexture"));
		Player player = new Player(new TexturedModel(playerModel, playerTexture), new Vector3f(0, 0, 0), 0, 20, 0, 1);
		
		ModelData treeData = OBJFileLoader.loadOBJ("lowPolyTree");
		RawModel treeModel = loader.loadToVAO(treeData.getVertices(), treeData.getTextureCoords(), treeData.getNormals(), treeData.getIndices());
		ModelTexture treeTextureAtlas = new ModelTexture(loader.loadTexture("textures/lowPolyTree"));
		treeTextureAtlas.setNumberOfRows(2);
		float x = 200;
		float z = 200;
		Entity tree = new Entity(new TexturedModel(treeModel, treeTextureAtlas), random.nextInt(4), new Vector3f(x, terrain1.getHeightOfTerrain(x, z), z), 0, 0, 0, 2);
		entities.add(tree);
		// *****************************************

		Light light = new Light(new Vector3f(20000, 20000, 2000), new Vector3f(1,1,1));		
		Camera camera = new Camera(player);
		
		List<GuiTexture> guis = new ArrayList<GuiTexture>(); 
		GuiTexture gui = new GuiTexture(loader.loadTexture("guis/health"), new Vector2f(-0.75f, 0.9f), new Vector2f(0.25f, 0.25f));
		guis.add(gui);
		
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		
		MasterRenderer renderer = new MasterRenderer();
		while(!Display.isCloseRequested()) {
			player.move(terrain1);
			camera.move();
			renderer.processEntity(player);
			renderer.processTerrain(terrain1);
			
			for (Entity entity : entities)
				renderer.processEntity(entity);
			
			renderer.render(light, camera);
			guiRenderer.render(guis);
			DisplayManager.updateDisplay();
		}

		renderer.cleanUp();
		loader.cleanUp();
		
		DisplayManager.closeDisplay();
	}

}
