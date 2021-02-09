package water;

import org.lwjgl.util.vector.Vector3f;

import java.util.List;

public class WaterTile {
	
	public static final float TILE_SIZE = 400;
	
	private float height;
	private float x,z;
	
	public WaterTile(float centerX, float centerZ, float height){
		this.x = centerX;
		this.z = centerZ;
		this.height = height;
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

	public static boolean isInWater(Vector3f position, List<WaterTile> waterTiles){
		for(WaterTile water : waterTiles){
			float xMax = water.getX() + WaterTile.TILE_SIZE;
			float xMin = water.getX() - WaterTile.TILE_SIZE;

			float zMax = water.getZ() + WaterTile.TILE_SIZE;
			float zMin = water.getZ() - WaterTile.TILE_SIZE;

			if(position.y < water.getHeight() * 1.03 &&
					position.x > xMin && position.x < xMax &&
					position.z > zMin && position.z < zMax){
				return true;
			}else {
				return false;
			}
		}
		return false;
	}

}
