package scenes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
import normalMappingObjConverter.NormalMappedObjLoader;
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

public class IslandScene {
		
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
	
	public IslandScene(Loader loader, MasterRenderer renderer) {
				
		// *********TERRAIN TEXTURE STUFF**********
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("textures/grassy2"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("textures/mud"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("textures/grassFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("textures/path"));
 
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture,
                gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMaps/blendMap"));
        
        // *****************************************
 
        TexturedModel rocks = new TexturedModel(OBJFileLoader.loadOBJ("rocks", loader),
                new ModelTexture(loader.loadTexture("textures/rocks")));
 
        ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("textures/fern"));
        fernTextureAtlas.setNumberOfRows(2);
 
        TexturedModel fern = new TexturedModel(OBJFileLoader.loadOBJ("fern", loader),
                fernTextureAtlas);
 
        TexturedModel bobble = new TexturedModel(OBJFileLoader.loadOBJ("pine", loader),
                new ModelTexture(loader.loadTexture("textures/pine")));
        bobble.getTexture().setHasTransparency(true);
 
        fern.getTexture().setHasTransparency(true);
 
        Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "heightmap.png");
        terrains.add(terrain);
 
        TexturedModel lamp = new TexturedModel(OBJFileLoader.loadOBJ("lamp", loader),
                new ModelTexture(loader.loadTexture("textures/lamp")));
        lamp.getTexture().setUseFakeLighting(true);         
        //******************NORMAL MAP MODELS************************
         
        TexturedModel barrelModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel", loader),
                new ModelTexture(loader.loadTexture("textures/barrel")));
        barrelModel.getTexture().setNormalMap(loader.loadTexture("normal/barrelNormal"));
        barrelModel.getTexture().setShineDamper(10);
        barrelModel.getTexture().setReflectivity(0.5f);
         
        TexturedModel crateModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("crate", loader),
                new ModelTexture(loader.loadTexture("textures/crate")));
        crateModel.getTexture().setNormalMap(loader.loadTexture("normal/crateNormal"));
        crateModel.getTexture().setShineDamper(10);
        crateModel.getTexture().setReflectivity(0.5f);
         
        TexturedModel boulderModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("boulder", loader),
                new ModelTexture(loader.loadTexture("textures/boulder")));
        boulderModel.getTexture().setNormalMap(loader.loadTexture("normal/boulderNormal"));
        boulderModel.getTexture().setShineDamper(10);
        boulderModel.getTexture().setReflectivity(0.5f);
         
         
        //************ENTITIES*******************
         
        Entity entity = new Entity(barrelModel, new Vector3f(75, 10, -75), 0, 0, 0, 1f);
        Entity entity2 = new Entity(boulderModel, new Vector3f(85, 10, -75), 0, 0, 0, 1f);
        Entity entity3 = new Entity(crateModel, new Vector3f(65, 10, -75), 0, 0, 0, 0.04f);
        normalMapEntities.add(entity);
        normalMapEntities.add(entity2);
        normalMapEntities.add(entity3);
         
        Random random = new Random(5666778);
        for (int i = 0; i < 60; i++) {
            if (i % 3 == 0) {
                float x = random.nextFloat() * 150;
                float z = random.nextFloat() * -150;
                if ((x > 50 && x < 100) || (z < -50 && z > -100)) {
                } else {
                    float y = terrain.getHeightOfTerrain(x, z);
 
                    entities.add(new Entity(fern, 3, new Vector3f(x, y, z), 0,
                            random.nextFloat() * 360, 0, 0.9f));
                }
            }
            if (i % 2 == 0) {
 
                float x = random.nextFloat() * 150;
                float z = random.nextFloat() * -150;
                if ((x > 50 && x < 100) || (z < -50 && z > -100)) {
 
                } else {
                    float y = terrain.getHeightOfTerrain(x, z);
                    entities.add(new Entity(bobble, 1, new Vector3f(x, y, z), 0,
                            random.nextFloat() * 360, 0, random.nextFloat() * 0.6f + 0.8f));
                }
            }
        }
        entities.add(new Entity(rocks, new Vector3f(75, 4.6f, -75), 0, 0, 0, 75));
         
        //*******************OTHER SETUP***************
 
        Light sun = new Light(new Vector3f(10000, 10000, -10000), new Vector3f(1.3f, 1.3f, 1.3f));
        lights.add(sun);
  
        RawModel bunnyModel = OBJFileLoader.loadOBJ("person", loader);
        TexturedModel stanfordBunny = new TexturedModel(bunnyModel, new ModelTexture(
                loader.loadTexture("textures/playerTexture")));
 
        player = new Player(stanfordBunny, new Vector3f(75, 5, -75), 0, 100, 0, 0.6f);
        entities.add(player);
        camera = new Camera(player);
        guiRenderer = new GuiRenderer(loader);     
        //**********Water Renderer Set-up************************
         
        fbos = new WaterFrameBuffers();
        waterShader = new WaterShader();
        waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), fbos);
        water = new WaterTile(60, 75, -75, 0);
        waters.add(water);
        
        this.renderer = renderer;
        this.loader = loader;
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
		
		fbos.unbindCurrentFrameBuffer();
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
	
	public List<Entity> getNormalMapEntities() {
		return normalMapEntities;
	}
}