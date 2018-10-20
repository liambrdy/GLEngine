package particles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;

import entities.Camera;
import renderEngine.Loader;

public class ParticleMaster {

	private static List<Particle> particles = new ArrayList<Particle>();
	private static ParticleRenderer renderer;
	
	public static void init(Loader loader, Matrix4f projectionMatrix) {
		renderer = new ParticleRenderer(loader, projectionMatrix);
	}
	
	public static void update() {
		Iterator<Particle> itorator = particles.iterator();
		while (itorator.hasNext()) {
			Particle p = itorator.next();
			boolean stillAlive = p.update();
			if (!stillAlive) {
				itorator.remove();
			}
		}
	}
	
	public static void renderParticle(Camera camera) {
		renderer.render(particles, camera);
	}
	
	public static void cleanUp() {
		renderer.cleanUp();
	}
	
	public static void addParticle(Particle p) {
		particles.add(p);
	}
	
}
