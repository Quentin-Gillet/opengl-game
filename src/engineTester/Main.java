package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import entities.structures.Lamp;
import entities.structures.TreeGenerator;
import guis.GuiRenderer;
import guis.GuiTexture;
import inputs.MousePicker;
import loaders.Loader;
import loaders.objLoader.OBJFileLoader;
import models.TexturedModel;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import renderEngine.*;
import terrains.Terrains;
import textures.ModelTexture;
import toolbox.Colour;
import water.WaterFrameBuffers;
import water.WaterTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        DisplayManager.createDisplay();

        Loader loader = new Loader();

        ModelTexture fernTexture = new ModelTexture(loader.loadTexture("models/fernAtlas"));
        fernTexture.setTransparency(true);
        fernTexture.setUseFakeLightning(true);
        fernTexture.setNumberOfRows(2);
        TexturedModel fern = new TexturedModel(OBJFileLoader.loadObjModel("fern", loader), fernTexture);
        ModelTexture grassTexture = new ModelTexture(loader.loadTexture("models/grassTexture"));
        grassTexture.setTransparency(true);
        grassTexture.setUseFakeLightning(true);
        TexturedModel grass = new TexturedModel(OBJFileLoader.loadObjModel("grassModel", loader), grassTexture);

        List<Entity> allEntities = new ArrayList<Entity>();
        Random r = new Random();

        List<Light> lights = new ArrayList<Light>();
        lights.add(new Light(new Vector3f(0, 250, -400), Colour.WHITE));

        WaterFrameBuffers fbos = new WaterFrameBuffers();

        MasterRenderer renderer = new MasterRenderer(loader, fbos);

        Terrains terrains = new Terrains(loader, renderer);

        TexturedModel playerModel = new TexturedModel(OBJFileLoader.loadObjModel("person", loader), new ModelTexture(loader.loadTexture("models/playerTexture")));
        Player player = new Player(playerModel, new Vector3f(-200, 0, -480), 0, 180, 0, 0.5f);
        Camera camera = new Camera(player);

        TreeGenerator treeGenerator = new TreeGenerator(loader);

        MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrains);

        Lamp lamp = null;

        for (int i = 0; i < 1000; i++){
            float x1 = r.nextFloat() * -800 + 800;
            float x2 = r.nextFloat() * -800;
            float x = r.nextBoolean() ? x1 : x2;
            float z = r.nextFloat() * -800;
            int random = r.nextInt(4);
            float y = terrains.getCurrentTerrain(new Vector3f(x, 0, z)).getHeightOfTerrain(x, z);
            if(random == 2){
                allEntities.add(new Entity(fern, r.nextInt(4), new Vector3f(x, y, z), 0, 0, 0, 0.75f));
            }else if(random == 0){
                allEntities.add(new Entity(grass, new Vector3f(x, y, z), 0, 0, 0, 1.5f));
            }else if(random == 3){
                allEntities.add(treeGenerator.generateTree(new Vector3f(x, y, z), 0, 0, 0));
            }else if(r.nextInt(10) == 1){
                if(lights.size() > 3) continue;
                lamp = new Lamp(loader, lights, new Vector3f(x, y, z), 0, 0, 0, 1);
                allEntities.add(lamp);
            }
        }

        allEntities.add(player);

        List<GuiTexture> guis = new ArrayList<GuiTexture>();
        guis.add(new GuiTexture(loader.loadTexture("guis/health"), new Vector2f(0.75f, 0.75f), new Vector2f(0.25f, .25f)));

        GuiRenderer guiRenderer = new GuiRenderer(loader);

        List<WaterTile> waterTiles = new ArrayList<WaterTile>();
        List<WaterTile> waterTilesEmpty = new ArrayList<WaterTile>();
        WaterTile waterTile = new WaterTile(-210, -485, -8f);
        waterTiles.add(waterTile);


        while(!Display.isCloseRequested()){
            //Game logic
            player.move(terrains.getCurrentTerrain(player.getPosition()));
            camera.move();
            //picker.update();
            //camera.cameraOwnMovement();

            Vector3f terrainPoint = picker.getCurrentTerrainPoint();
            if(terrainPoint != null && lamp != null) lamp.setPosition(terrainPoint);

            //Render
            fbos.bindReflectionFrameBuffer();
            float distance = 2 * (camera.getPosition().y - waterTile.getHeight());
            camera.getPosition().y -= distance;
            camera.invertPitch();
            renderer.renderScene(allEntities, lights, waterTilesEmpty, terrains, camera, new Vector4f(0, 1, 0, -waterTile.getHeight() + 1f));
            camera.getPosition().y += distance;
            camera.invertPitch();

            fbos.bindRefractionFrameBuffer();
            renderer.renderScene(allEntities, lights, waterTilesEmpty, terrains, camera, new Vector4f(0, -1, 0, waterTile.getHeight() + 1f));

            GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
            fbos.unbindCurrentFrameBuffer();
            renderer.renderScene(allEntities, lights, waterTiles, terrains, camera,  new Vector4f(0, -1, 0, 150000));
            guiRenderer.render(guis);

            //Update display
            DisplayManager.updateDisplay();
        }

        fbos.cleanUp();
        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }

}
