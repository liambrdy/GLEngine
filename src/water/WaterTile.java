package water;

public class WaterTile {
	
	private int size;
	private float height;
	private float x,z;
	
	public WaterTile(int size, float centerX, float centerZ, float height){
		this.size = size;
		this.x = centerX;
		this.z = centerZ;
		this.height = height;
	}

	public int getSize() {
		return this.size;
	}
	
	public float getHeight() {
		return height;
	}

	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}



}