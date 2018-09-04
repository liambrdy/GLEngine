package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
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
import toolbox.MousePicker;

public class MainGameLoop {

	public static void main(String[] args) {
		
		DisplayManager.createDisplay();
		Loader loader = new Loader(); 
		Random random = new Random();
		
		List<Entity> entities = new ArrayList<Entity>();
		List<GuiTexture> guis = new ArrayList<GuiTexture>(); 
		List<Light> lights = new ArrayList<Light>();
		
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
		Player player = new Player(new TexturedModel(playerModel, playerTexture), new Vector3f(100, terrain1.getHeightOfTerrain(100, 100), 100), 0, 20, 0, 1);
		
		ModelData treeData = OBJFileLoader.loadOBJ("lowPolyTree");
		RawModel treeModel = loader.loadToVAO(treeData.getVertices(), treeData.getTextureCoords(), treeData.getNormals(), treeData.getIndices());
		ModelTexture treeTextureAtlas = new ModelTexture(loader.loadTexture("textures/lowPolyTree"));
		treeTextureAtlas.setNumberOfRows(2);
		float x = 200;
		float z = 200;
		Entity tree = new Entity(new TexturedModel(treeModel, treeTextureAtlas), random.nextInt(4), new Vector3f(x, terrain1.getHeightOfTerrain(x, z), z), 0, 0, 0, 2);
		entities.add(tree);
		
		ModelData lampData = OBJFileLoader.loadOBJ("lamp");
		ModelTexture lampTexture = new ModelTexture(loader.loadTexture("textures/lamp"));
		lampTexture.setHasTransparency(true);
		lampTexture.setUseFakeLighting(true);
		TexturedModel lamp = new TexturedModel(loader.loadToVAO(lampData.getVertices(), lampData.getTextureCoords(), lampData.getNormals(), lampData.getIndices()), lampTexture);
		Entity lampEntity = new Entity(lamp, new Vector3f(0, 0, 0), 0, 0, 0, 1);
		entities.add(lampEntity);
		// *****************************************
		Camera camera = new Camera(player);

		Light lampLight = new Light(new Vector3f(0, 0, 0), new Vector3f(0, 1, 0), new Vector3f(1, 0.01f, 0.002f));
		lights.add(lampLight);
		lights.add(new Light(new Vector3f(0, 1000, -7000), new Vector3f(0.4f,0.4f,0.4f)));	
		lights.add(new Light(new Vector3f(203, 32, 176), new Vector3f(0, 1, 0), new Vector3f(1, 0.01f, 0.002f)));
		
		x = 203; z = 176;
		System.out.println(terrain1.getHeightOfTerrain(x, z));
		entities.add(new Entity(lamp, new Vector3f(x, terrain1.getHeightOfTerrain(x, z), z), 0, 0, 0, 1.5f));
		
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		MasterRenderer renderer = new MasterRenderer(loader);
		
		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain1);
		while(!Display.isCloseRequested()) {
			player.move(terrain1);
			camera.move();
			
			picker.update();
			Vector3f terrainPoint = picker.getCurrentTerrainPoint();
			if (terrainPoint != null) {
				lampEntity.setPosition(terrainPoint);
				lampLight.setPosition(new Vector3f(terrainPoint.x, terrainPoint.y + 15, terrainPoint.z));
			}
			
			renderer.processEntity(player);
			renderer.processTerrain(terrain1);
			
			for (Entity entity : entities)
				renderer.processEntity(entity);
			
			renderer.render(lights, camera);
			guiRenderer.render(guis);
			DisplayManager.updateDisplay();
		}

		renderer.cleanUp();
		loader.cleanUp();
		
		DisplayManager.closeDisplay();
	}

}
