package engineTester;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
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
         
        List<Entity> entities = new ArrayList<Entity>();
        List<Terrain> terrains = new ArrayList<Terrain>();
        
        //////////////////////////////////////////////////////////////////////////////////////////
        //Loading Models
        //////////////////////////////////////////////////////////////////////////////////////////
        ModelData personData = OBJFileLoader.loadOBJ("person");
        RawModel personModel = loader.loadToVAO(personData.getVertices(), personData.getTextureCoords(), personData.getNormals(), personData.getIndices());
        ModelTexture personTexture = new ModelTexture(loader.loadTexture("textures/playerTexture"));
        Player person = new Player(new TexturedModel(personModel, personTexture), new Vector3f(0, 10, -20), 0, 0, 0, 1);
        
        
        
        entities.add(person);
        //////////////////////////////////////////////////////////////////////////////////////////
        //Terrain Texture
        //////////////////////////////////////////////////////////////////////////////////////////
        
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("textures/grassy"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("textures/dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("textures/pinkFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("textures/path"));
        
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMaps/blendMap"));
        
        //////////////////////////////////////////////////////////////////////////////////////////
        
        Terrain terrain1 = new Terrain(0, -1,loader, texturePack, blendMap);
        Terrain terrain2 = new Terrain(-1, -1,loader, texturePack, blendMap);
        terrains.add(terrain1); terrains.add(terrain2);
         
         
        Light light = new Light(new Vector3f(20000,20000,2000),new Vector3f(1,1,1));
         
        Camera camera = new Camera(person);   
        MasterRenderer renderer = new MasterRenderer();
         
        while(!Display.isCloseRequested()){
            camera.move();
            person.move();
            for (Terrain terrain : terrains)
            	renderer.processTerrain(terrain);
            for (Entity entity : entities)
                renderer.processEntity(entity);
            
            renderer.render(light, camera);
            DisplayManager.updateDisplay();
        }
 
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
 
    }
 
}
