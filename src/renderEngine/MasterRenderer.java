package renderEngine;

import entities.Camera;
import entities.Entity;
import entities.Light;
import loaders.Loader;
import models.TexturedModel;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;
import renderEngine.normalMappingRenderer.NormalMappingRenderer;
import shaders.StaticShader;
import skybox.SkyboxRenderer;
import terrains.Terrain;
import terrains.TerrainShader;
import terrains.Terrains;
import toolbox.Colour;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterRenderer {

    private static final float FOV = 90;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000f;

    public static final Colour SKY_COLOUR = Colour.GREY;

    private Matrix4f projectionMatrix;

    private StaticShader staticShader = new StaticShader();
    private EntityRenderer entityRenderer;

    private TerrainShader terrainShader = new TerrainShader();
    private TerrainRenderer terrainRenderer;

    private SkyboxRenderer skyboxRenderer;

    private WaterShader waterShader = new WaterShader();
    private WaterRenderer waterRenderer;

    private NormalMappingRenderer normalMappingRenderer;

    private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
    private Map<TexturedModel, List<Entity>> normalMapEntities = new HashMap<TexturedModel, List<Entity>>();
    private List<Terrain> terrains = new ArrayList<Terrain>();

    public MasterRenderer(Loader loader, WaterFrameBuffers fbos){
        enableFaceCulling();
        createProjectionMatrix();
        entityRenderer = new EntityRenderer(staticShader, projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
        skyboxRenderer =  new SkyboxRenderer(loader, projectionMatrix);
        waterRenderer = new WaterRenderer(loader, waterShader, projectionMatrix, fbos);
        normalMappingRenderer = new NormalMappingRenderer(projectionMatrix);
    }

    public static void enableFaceCulling(){
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    public static void disableFaceCulling(){
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    public void render(List<Light> lights, List<WaterTile> waterTiles, Camera camera, Vector4f clipPlane){
        prepare();

        staticShader.start();
        staticShader.loadPlane(clipPlane);
        staticShader.loadSkyColour(SKY_COLOUR.toVector3f());
        staticShader.loadLights(lights);
        staticShader.loadViewMatrix(camera);
        entityRenderer.render(entities);
        staticShader.stop();

        normalMappingRenderer.render(normalMapEntities, clipPlane, lights, camera);

        terrainShader.start();
        terrainShader.loadPlane(clipPlane);
        terrainShader.loadSkyColour(SKY_COLOUR.toVector3f());
        terrainShader.loadLights(lights);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains);
        terrainShader.stop();

        skyboxRenderer.render(camera, SKY_COLOUR);

        waterRenderer.render(waterTiles, camera, lights.get(0));

        terrains.clear();
        entities.clear();
        normalMapEntities.clear();
    }

    public void renderScene(List<Entity> entities, List<Entity> normalMapEntities, List<Light> lights, List<WaterTile> waterTiles,Terrains terrains, Camera camera,
                            Vector4f clipPlane){
        terrains.render();
        for(Entity entity : entities){
            processEntity(entity);
            entity.update();
        }
        for (Entity entity : normalMapEntities){
            processNormalEntity(entity);
            entity.update();
        }
        render(lights, waterTiles, camera, clipPlane);
    }

    public void processTerrain(Terrain terrain){
        terrains.add(terrain);
    }

    public void processEntity(Entity entity){
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);
        if(batch != null){
            batch.add(entity);
        }else{
            List<Entity> newBatch = new ArrayList<Entity>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }

    public void processNormalEntity(Entity entity){
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = normalMapEntities.get(entityModel);
        if(batch != null){
            batch.add(entity);
        }else{
            List<Entity> newBatch = new ArrayList<Entity>();
            newBatch.add(entity);
            normalMapEntities.put(entityModel, newBatch);
        }
    }

    public void prepare(){
        GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(SKY_COLOUR.r, SKY_COLOUR.g, SKY_COLOUR.b, SKY_COLOUR.a);
    }

    private void createProjectionMatrix(){
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public void cleanUp(){
        waterShader.cleanUp();
        staticShader.cleanUp();
        terrainShader.cleanUp();
        normalMappingRenderer.cleanUp();
    }
}
