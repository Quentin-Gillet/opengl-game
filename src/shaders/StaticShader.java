package shaders;

import entities.Camera;
import entities.Light;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import toolbox.Maths;

import java.util.List;

public class StaticShader extends ShaderProgram{

    private static final int MAX_LIGHTS = 4;

    private static final String VERTEX_FILE = "res/shaders/vertexShader.glsl";
    private static final String FRAGMENT_FILE = "res/shaders/fragmentShader.glsl";

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int[] location_lightPosition;
    private int[] location_lightColour;
    private int[] location_lightAttenuation;
    private int location_shineDamper;
    private int location_reflectivity;
    private int location_useFakeLightning;
    private int location_skyColour;
    private int location_numberOfRows;
    private int location_offset;
    private int location_useFog;
    private int location_plane;

    public StaticShader() {
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
        location_useFakeLightning = super.getUniformLocation("useFakeLightning");
        location_shineDamper = super.getUniformLocation("shineDamper");
        location_reflectivity = super.getUniformLocation("reflectivity");
        location_skyColour = super.getUniformLocation("skyColour");
        location_numberOfRows = super.getUniformLocation("numberOfRows");
        location_offset = super.getUniformLocation("offset");
        location_useFog = super.getUniformLocation("useFog");
        location_plane = super.getUniformLocation("plane");


        for (int i = 0; i < MAX_LIGHTS; i++){
            location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
            location_lightColour[i] = super.getUniformLocation("lightColour[" + i + "]");
            location_lightAttenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
        }
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "normal");
    }

    public void loadShineVariables(float shineDamper, float reflectivity){
        super.loadFloat(location_shineDamper, shineDamper);
        super.loadFloat(location_reflectivity, reflectivity);
    }

    public void loadFog(boolean useFog){
        super.loadBoolean(location_useFog, useFog);
    }

    public void loadSkyColour(Vector3f skyColour){
        super.loadVector3f(location_skyColour, skyColour);
    }

    public void loadOffset(Vector2f offset){
        super.loadVector2f(location_offset, offset);
    }

    public void loadNumberOfRows(float numberOfRows){
        super.loadFloat(location_numberOfRows, numberOfRows);
    }

    public void loadFakeLightning(boolean useFakeLightning){ super.loadBoolean(location_useFakeLightning, useFakeLightning); }

    public void loadTransformationMatrix(Matrix4f matrix){
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    public void loadProjectionMatrix(Matrix4f matrix){
        super.loadMatrix(location_projectionMatrix, matrix);
    }

    public void loadPlane(Vector4f plane){
        super.loadVector4f(location_plane, plane);
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
