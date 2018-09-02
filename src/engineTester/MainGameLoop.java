package engineTester;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import terrains.Terrain;
import textures.ModelTexture;

public class MainGameLoop {
	 
    public static void main(String[] args) {
 
        DisplayManager.createDisplay();
        Loader loader = new Loader();
         
        List<Entity> entities = new ArrayList<Entity>();
        List<Terrain> terrains = new ArrayList<Terrain>();
        
        //////////////////////////////////////////////////////////////////////////////////////////
        //Loading Models
        //////////////////////////////////////////////////////////////////////////////////////////
        ModelData ringData = OBJFileLoader.loadOBJ("ring");
        RawModel ringModel = loader.loadToVAO(ringData.getVertices(), ringData.getTextureCoords(), ringData.getNormals(), ringData.getIndices());
        ModelTexture ringTexture = new ModelTexture(loader.loadTexture("ringTexture"));
        Entity ring = new Entity(new TexturedModel(ringModel, ringTexture), new Vector3f(0, 10, -20), 0, 0, 0, 1);
        
        
        
        entities.add(ring);
        //////////////////////////////////////////////////////////////////////////////////////////
        
        Terrain terrain1 = new Terrain(0, -1,loader,new ModelTexture(loader.loadTexture("grass")));
        Terrain terrain2 = new Terrain(-1, -1,loader,new ModelTexture(loader.loadTexture("grass")));
        terrains.add(terrain1); terrains.add(terrain2);
         
         
        Light light = new Light(new Vector3f(20000,20000,2000),new Vector3f(1,1,1));
         
        Camera camera = new Camera();   
        MasterRenderer renderer = new MasterRenderer();
         
        while(!Display.isCloseRequested()){
        	ring.increaseRotation(10, 10, 10);
            camera.move();
             
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
