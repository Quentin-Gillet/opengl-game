package terrains;

import loaders.Loader;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.MasterRenderer;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Terrains {

    private Loader loader;
    private MasterRenderer renderer;

    List<Terrain> terrains = new ArrayList<Terrain>();
    private Random random = new Random();
    private int seed;

    public Terrains(Loader loader, MasterRenderer renderer) {
        this.loader = loader;
        this.renderer = renderer;
        this.seed = random.nextInt(1000000000);

        addTerrains();
    }


    public void render(){
        for (Terrain terrain : terrains){
            renderer.processTerrain(terrain);
        }
    }

    public Terrain getCurrentTerrain(Vector3f position){
        for (Terrain terrain : terrains){
            if (position.x < terrain.getX() + terrain.getSize() && position.x >= terrain.getX() && position.z >= terrain.getZ() && position.z < terrain.getZ() + terrain.getSize()) {
                return terrain;
            }
        }
        return terrains.get(0);
    }

    private void addTerrains(){
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("terrain/grassy2"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("terrain/mud"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("terrain/grassFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("terrain/path"));
        TerrainTexture blendMap1 = new TerrainTexture(loader.loadTexture("maps/blendMap"));
        TerrainTexture blendMap2 = new TerrainTexture(loader.loadTexture("maps/blendMap2"));

        TerrainTexturePack terrainTexturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);

        Terrain terrain1 = new Terrain(0, 0, loader, terrainTexturePack, blendMap2, seed);
        Terrain terrain2 = new Terrain(0, 1, loader, terrainTexturePack, blendMap1, seed);
        terrains.add(terrain1);
        terrains.add(terrain2);
    }
}
