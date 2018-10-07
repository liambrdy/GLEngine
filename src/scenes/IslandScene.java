package scenes;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
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
import models.RawModel;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import objConverter.OBJFileLoader;
import renderEngine.DisplayManager;
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

public class IslandScene implements Scene {
	
	
	List<Entity> entities;
	List<Terrain> terrains;
	List<GuiTexture> guis;
	List<Light> lights;
	List<WaterTile> waters;
	List<Entity> normalMapEntities;
	
	private Player player;
	private Camera camera;
	
	private Loader loader;
	
	private MasterRenderer renderer;
	
	private GuiRenderer guiRenderer;
	
	private WaterRenderer waterRenderer;
	private WaterFrameBuffers fbos;
	private WaterShader waterShader;
	private WaterTile water;
	
	@Override
	public void init(Loader loader, MasterRenderer renderer) {	
		entities = new ArrayList<Entity>();
		terrains = new ArrayList<Terrain>();
		guis = new ArrayList<GuiTexture>(); 
		lights = new ArrayList<Light>();
		waters = new ArrayList<WaterTile>();
		normalMapEntities = new ArrayList<Entity>();
				
		TextMaster.init(loader);
		FontType font = new FontType(loader.loadTexture("fonts/distance-candara", -0.4f), new File("res/fonts/distance-candara.fnt"));
        GUIText text = new GUIText("This is text!", 3f, font, new Vector2f(0, 0), 1f, true);
        text.setColor(1, 0, 0);
		
		// *********TERRAIN TEXTURE STUFF**********
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("textures/grassy2", -0.4f));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("textures/mud", -0.4f));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("textures/grassFlowers", -0.4f));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("textures/path", -0.4f));
 
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture,
                gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMaps/blendMap", -0.4f));
        
        // *****************************************
        
        // ***************** LOADING MODELS **********
        TexturedModel rocks = new TexturedModel(OBJFileLoader.loadOBJ("rocks", loader),
                new ModelTexture(loader.loadTexture("textures/rocks", -0.4f)));
 
        ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("textures/fern", -0.4f));
        fernTextureAtlas.setNumberOfRows(2);
 
        TexturedModel fern = new TexturedModel(OBJFileLoader.loadOBJ("fern", loader),
                fernTextureAtlas);
 
        TexturedModel bobble = new TexturedModel(OBJFileLoader.loadOBJ("pine", loader),
                new ModelTexture(loader.loadTexture("textures/pine", -0.4f)));
        bobble.getTexture().setHasTransparency(true);
 
        fern.getTexture().setHasTransparency(true);
 
        Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "heightmap");
        terrains.add(terrain);
 
        TexturedModel lamp = new TexturedModel(OBJFileLoader.loadOBJ("lamp", loader),
                new ModelTexture(loader.loadTexture("textures/lamp", -0.4f)));
        lamp.getTexture().setUseFakeLighting(true);   
        // **************************************************
        
      //******************NORMAL MAP MODELS************************
        
        TexturedModel barrelModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel", loader),
                new ModelTexture(loader.loadTexture("textures/barrel", -0.4f)));
        barrelModel.getTexture().setNormalMap(loader.loadTexture("normal/barrelNormal", -0.4f));
        barrelModel.getTexture().setShineDamper(10);
        barrelModel.getTexture().setReflectivity(0.5f);
         
        TexturedModel crateModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("crate", loader),
                new ModelTexture(loader.loadTexture("textures/crate", -0.4f)));
        crateModel.getTexture().setNormalMap(loader.loadTexture("normal/crateNormal", -0.4f));
        crateModel.getTexture().setShineDamper(10);
        crateModel.getTexture().setReflectivity(0.5f);
         
        TexturedModel boulderModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("boulder", loader),
                new ModelTexture(loader.loadTexture("textures/boulder", -0.4f)));
        boulderModel.getTexture().setNormalMap(loader.loadTexture("normal/boulderNormal", -0.4f));
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
                loader.loadTexture("textures/playerTexture", -0.4f)));
 
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

	@Override
	public void update() {
		camera.move();	
		player.move(terrains.get(0));
		
	}

	@Override
	public void render() {
		GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
		renderFbos();
		GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
		
		renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, -1, 0, 10000000));
		waterRenderer.render(waters, camera, lights.get(0));
		guiRenderer.render(guis);
		TextMaster.render();
		DisplayManager.updateDisplay();
		
	}

	@Override
	public void cleanup() {
		TextMaster.cleanUp();
		fbos.cleanUp();
		waterShader.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		
	}
	
	private void renderFbos() {
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
	
	public IslandScene() {  
		
	}
}