package textures;

public class ModelTexture {
	
	private int textureID;
	
	private float shineDamper = 1;
	private float reflectivity = 0;
	
	private boolean hasTransparancy = false;
	private boolean useFakeLighting = false;
	
	public boolean isUseFakeLighting() {
		return useFakeLighting;
	}

	public void setUseFakeLighting(boolean useFakeLighting) {
		this.useFakeLighting = useFakeLighting;
	}

	public boolean isHasTransparancy() {
		return hasTransparancy;
	}

	public void setHasTransparancy(boolean hasTransparancy) {
		this.hasTransparancy = hasTransparancy;
	}

	public ModelTexture(int textureID) {
		this.textureID = textureID;
	}
	
	public float getShineDamper() {
		return shineDamper;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}

	public int getTextureID() {
		return this.textureID;
	}
	
}
