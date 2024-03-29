package terrains;

import entities.Camera;
import entities.Light;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import shaders.ShaderProgram;
import toolbox.Maths;

import java.util.List;

public class TerrainShader extends ShaderProgram {

    private static final int MAX_LIGHTS = 4;

    private static final String VERTEX_FILE = "res/shaders/terrain/terrainVertexShader.glsl";
    private static final String FRAGMENT_FILE = "res/shaders/terrain/terrainFragmentShader.glsl";

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int[] location_lightPosition;
    private int[] location_lightColour;
    private int[] location_lightAttenuation;
    private int location_shineDamper;
    private int location_reflectivity;
    private int location_skyColour;
    private int location_backgroundTexture;
    private int location_rTexture;
    private int location_gTexture;
    private int location_bTexture;
    private int location_blendMap;
    private int location_useFog;
    private int location_plane;

    public TerrainShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        location_lightPosition = new int[MAX_LIGHTS];
        location_lightColour = new int[MAX_LIGHTS];
        location_lightAttenuation = new int[MAX_LIGHTS];

        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        for (int i = 0; i < MAX_LIGHTS; i++){
            location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
            location_lightColour[i] = super.getUniformLocation("lightColour[" + i + "]");
            location_lightAttenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
        }
        location_shineDamper = super.getUniformLocation("shineDamper");
        location_reflectivity = super.getUniformLocation("reflectivity");
        location_skyColour = super.getUniformLocation("skyColour");
        location_backgroundTexture = super.getUniformLocation("backgroundTexture");
        location_rTexture = super.getUniformLocation("rTexture");
        location_gTexture = super.getUniformLocation("gTexture");
        location_bTexture = super.getUniformLocation("bTexture");
        location_blendMap = super.getUniformLocation("blendMap");
        location_useFog = super.getUniformLocation("useFog");
        location_plane = super.getUniformLocation("plane");

    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "normal");
    }

    public void loadTerrainTextures(){
        super.loadInt(location_backgroundTexture, 0);
        super.loadInt(location_rTexture, 1);
        super.loadInt(location_gTexture, 2);
        super.loadInt(location_bTexture, 3);
        super.loadInt(location_blendMap, 4);
    }

    public void loadShineVariables(float shineDamper, float reflectivity){
        super.loadFloat(location_shineDamper, shineDamper);
        super.loadFloat(location_reflectivity, reflectivity);
    }

    public void loadPlane(Vector4f plane){
        super.loadVector4f(location_plane, plane);
    }

    public void loadFog(boolean useFog){
        super.loadBoolean(location_useFog, useFog);
    }

    public void loadSkyColour(Vector3f colour){
        super.loadVector3f(location_skyColour, colour);
    }

    public void loadTransformationMatrix(Matrix4f matrix){
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    public void loadProjectionMatrix(Matrix4f matrix){
        super.loadMatrix(location_projectionMatrix, matrix);
    }

    public void loadViewMatrix(Camera camera){
        Matrix4f matrix = Maths.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix, matrix);
    }

    public void loadLights(List<Light> lights){
        for (int i = 0; i < MAX_LIGHTS; i++){
            if(i < lights.size()){
                super.loadVector3f(location_lightPosition[i], lights.get(i).getPosition());
                super.loadVector3f(location_lightColour[i], lights.get(i).getColour());
                super.loadVector3f(location_lightAttenuation[i], lights.get(i).getAttenuation());
            }else{
                super.loadVector3f(location_lightPosition[i], new Vector3f(0, 0, 0));
                super.loadVector3f(location_lightColour[i], new Vector3f(0, 0, 0));
                super.loadVector3f(location_lightAttenuation[i], new Vector3f(1, 0, 0));
            }
        }
    }

}
