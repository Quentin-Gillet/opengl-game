package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import entities.structures.Lamp;
import entities.structures.TreeGenerator;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;
import inputs.MousePicker;
import loaders.Loader;
import loaders.normalMappingObjLoader.NormalMappedObjLoader;
import loaders.objLoader.OBJFileLoader;
import models.TexturedModel;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import particle.ParticleMaster;
import particle.ParticleSystem;
import particle.ParticleTexture;
import renderEngine.*;
import terrains.Terrains;
import textures.ModelTexture;
import toolbox.Colour;
import water.WaterFrameBuffers;
import water.WaterTile;

import java.io.File;
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

        List<Entity> entities = new ArrayList<Entity>();
        List<Entity> normalMapEntities = new ArrayList<Entity>();

        Random r = new Random();

        List<Light> lights = new ArrayList<Light>();
        lights.add(new Light(new Vector3f(0, 250, -400), Colour.WHITE));

        WaterFrameBuffers fbos = new WaterFrameBuffers();

        MasterRenderer renderer = new MasterRenderer(loader, fbos);

        Terrains terrains = new Terrains(loader, renderer);

        TexturedModel playerModel = new TexturedModel(OBJFileLoader.loadObjModel("person", loader), new ModelTexture(loader.loadTexture("models/playerTexture")));
        Player player = new Player(playerModel, new Vector3f(400, 0, 400), 0, 180, 0, 0.5f);
        Camera camera = new Camera(player);

        TreeGenerator treeGenerator = new TreeGenerator(loader);

        MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrains);

        TexturedModel barrel = new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel", loader), new ModelTexture(loader.loadTexture("models/barrel")));
        barrel.getTexture().setShineDamper(10);
        barrel.getTexture().setReflectivity(0.5f);
        barrel.getTexture().setNormalMapID(loader.loadTexture("models/normals/barrelNormal"));
        normalMapEntities.add(new Entity(barrel, new Vector3f(75, 10, -75), 0, 0, 0, 1f));

        List<WaterTile> waterTiles = new ArrayList<>();
        List<WaterTile> waterTilesEmpty = new ArrayList<>();
        WaterTile waterTile = new WaterTile(400, 400, -2f);
        WaterTile waterTile2 = new WaterTile(400, 1200, -2f);
        waterTiles.add(waterTile);
        waterTiles.add(waterTile2);

        Lamp lamp = null;

        for (int i = 0; i < 1000; i++){
            float x = r.nextFloat() * 800;
            float z = r.nextFloat() * 1600;
            int random = r.nextInt(4);
            float y = terrains.getCurrentTerrain(new Vector3f(x, 0, z)).getHeightOfTerrain(x, z);
            if(WaterTile.isInWater(new Vector3f(x, y, z), waterTiles)){
                i--;
                continue;
            }
            if(random == 2){
                entities.add(new Entity(fern, r.nextInt(4), new Vector3f(x, y, z), 0, 0, 0, 0.75f));
            }else if(random == 0){
                entities.add(new Entity(grass, new Vector3f(x, y, z), 0, 0, 0, 1.5f));
            }else if(random == 3){
                entities.add(treeGenerator.generateTree(new Vector3f(x, y, z), 0, 0, 0));
            }else if(r.nextInt(10) == 1){
                if(lights.size() > 3) continue;
                lamp = new Lamp(loader, lights, new Vector3f(x, y, z), 0, 0, 0, 1);
                entities.add(lamp);
            }
        }

        entities.add(player);

        List<GuiTexture> guis = new ArrayList<GuiTexture>();
        guis.add(new GuiTexture(loader.loadTexture("guis/health"), new Vector2f(0.75f, 0.75f), new Vector2f(0.25f, .25f)));

        GuiRenderer guiRenderer = new GuiRenderer(loader);

        TextMaster.init(loader);

        FontType font = new FontType(loader.loadFontTextureAtlas("fonts/harrington"), new File("res/textures/fonts/harrington.fnt"));
        //GUIText text = new GUIText("My first text!%-", 1, font, new Vector2f(.5f, .5f), .5f, false);

        ParticleMaster.init(loader, renderer.getProjectionMatrix());
        ParticleSystem particleSystem = new ParticleSystem(new ParticleTexture(loader.loadTexture("particles/particleStar"), 1, true), 100, 80, 1, 1f, 1);
        particleSystem.setDirection(new Vector3f(0, 1, 0), 0.4f);
        particleSystem.randomizeRotation();
        particleSystem.setSpeedError(0.6f);
        particleSystem.setLifeError(0.2f);
        particleSystem.setScaleError(0.4f);

        while(!Display.isCloseRequested()){
            //Game logic
            //player.move(terrains.getCurrentTerrain(player.getPosition()), waterTiles);
            player.playerCameraMove();
            camera.move();
            //picker.update();
            ParticleMaster.update(camera);
            particleSystem.generateParticles(new Vector3f(-210, 10, -485));

            System.out.println(player.getPosition());

            Vector3f terrainPoint = picker.getCurrentTerrainPoint();
            if(terrainPoint != null && lamp != null) lamp.setPosition(terrainPoint);

            //Render
            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
            fbos.bindReflectionFrameBuffer();
            float distance = 2 * (camera.getPosition().y - waterTile.getHeight());
            camera.getPosition().y -= distance;
            camera.invertPitch();
            renderer.renderScene(entities, normalMapEntities, lights, waterTilesEmpty, terrains, camera, new Vector4f(0, 1, 0, -waterTile.getHeight() + 1f));
            camera.getPosition().y += distance;
            camera.invertPitch();

            fbos.bindRefractionFrameBuffer();
            renderer.renderScene(entities, normalMapEntities, lights, waterTilesEmpty, terrains, camera, new Vector4f(0, -1, 0, waterTile.getHeight() + 1f));

            fbos.unbindCurrentFrameBuffer();
            renderer.renderScene(entities, normalMapEntities, lights, waterTiles, terrains, camera,  new Vector4f(0, -1, 0, 150000));
            ParticleMaster.renderParticles(camera);

            guiRenderer.render(guis);
            TextMaster.render();

            //Update display
            DisplayManager.updateDisplay();
        }

        ParticleMaster.cleanUp();
        TextMaster.cleanUp();
        fbos.cleanUp();
        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }

}
