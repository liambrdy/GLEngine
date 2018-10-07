package scenes;

import java.util.ArrayList;
import java.util.List;

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
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class LakeScene {
		
	List<Entity> entities = new ArrayList<Entity>();
	List<Terrain> terrains = new ArrayList<Terrain>();
	List<GuiTexture> guis = new ArrayList<GuiTexture>(); 
	List<Light> lights = new ArrayList<Light>();
	List<WaterTile> waters = new ArrayList<WaterTile>();
	List<Entity> normalMapEntities = new ArrayList<Entity>();
	
	private Player player;
	private Camera camera;
	
	private Loader loader;
	
	private MasterRenderer renderer;
	
	private GuiRenderer guiRenderer;
	
	private WaterRenderer waterRenderer;
	private WaterFrameBuffers fbos;
	private WaterShader waterShader;
	private WaterTile water;
	
	public LakeScene(Loader loader, MasterRenderer renderer) {
		// *********TERRAIN TEXTURE STUFF***********
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("textures/grassy2", -0.4f));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("textures/mud", -0.4f));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("textures/grass", -0.4f));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("textures/grassy2", -0.4f));
		
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMaps/lakeBlendMap", -0.4f));
		Terrain terrain = new Terrain(0,0,loader, texturePack, blendMap, "heightMaps/lakeHeightMap.jpg");
		terrains.add(terrain);
		// *****************************************
		
		// *********MODEL LOADING STUFF ************
		RawModel playerModel = OBJFileLoader.loadOBJ("player", loader);
		ModelTexture playerTexture = new ModelTexture(loader.loadTexture("textures/playerTexture", -0.4f));
		player = new Player(new TexturedModel(playerModel, playerTexture), new Vector3f(390.8f, terrain.getHeightOfTerrain(390, 370), 379.2f), 0, 180, 0, 1);
		entities.add(player);
		entities.add(new Entity(new TexturedModel(playerModel, playerTexture), new Vector3f(200, 50, 200), 0, 0, 0, 2));
		// *****************************************
		
		// ************ ENTITIES *******************
		camera = new Camera(player);
		Light sun = new Light(new Vector3f(400, 400, 400), new Vector3f(1f,1f,1f));
		lights.add(sun);
		// *****************************************
		
		// ************* WATER STUFF ***************
		waterShader = new WaterShader();
		fbos = new WaterFrameBuffers();
		waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), fbos);
		water = new WaterTile(350, 400, 400, -5);
		waters.add(water);
		// *****************************************
		
		this.loader = loader;
		
		this.renderer = renderer;
		guiRenderer = new GuiRenderer(loader);
	}
	
	public void renderFbos() {
		fbos.bindReflectionFrameBuffer();
		float distance = 2 * (camera.getPosition().y - water.getHeight());
		camera.getPosition().y -= distance;
		camera.invertPitch();
		renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, 1, 0, -water.getHeight()+1f));
		camera.getPosition().y += distance;
		camera.invertPitch();

		fbos.bindRefractionFrameBuffer();
		renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, -1, 0, water.getHeight()));
	}
	
	public void cleanUp() {
		fbos.cleanUp();
		waterShader.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public WaterRenderer getWaterRenderer() {
		return waterRenderer;
	}
	
	public GuiRenderer getGuiRenderer() {
		return guiRenderer;
	}
	
	public WaterFrameBuffers getFbos() {
		return fbos;
	}
	
	public WaterShader getWaterShader() {
		return waterShader;
	}
	
	public Camera getCamera() {
		return camera;
	}
	
	public List<Light> getLights() {
		return lights;
	}
	
	public List<Terrain> getTerrains() {
		return terrains;
	}
	
	public List<Entity> getEntities() {
		return entities;
	}
	
	public List<GuiTexture> getGuis() {
		return guis;
	}
	
	public List<WaterTile> getWaters() {
		return waters;
	}
}
