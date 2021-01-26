package skybox;

import entities.Camera;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import shaders.ShaderProgram;
import toolbox.Colour;
import toolbox.Maths;

public class SkyboxShader extends ShaderProgram {

    private static final String VERTEX_FILE = "res/shaders/skybox/skyboxVertexShader.glsl";
    private static final String FRAGMENT_FILE = "res/shaders/skybox/skyboxFragmentShader.glsl";

    private static final float ROTATE_SPEED = 1f;

    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_fogColour;
    private int location_cubeMap;
    private int location_cubeMapNight;
    private int location_blendFactor;

    private float rotation;

    public SkyboxShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_fogColour = super.getUniformLocation("fogColour");
        location_cubeMap = super.getUniformLocation("cubeMap");
        location_cubeMapNight = super.getUniformLocation("cubeMapNight");
        location_blendFactor = super.getUniformLocation("blendFactor");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

    public void connectedTextureUnits(){
        super.loadInt(location_cubeMap, 0);
        super.loadInt(location_cubeMapNight, 1);
    }

    public void loadBlendFactor(float blend){
        super.loadFloat(location_blendFactor, blend);
    }

    public void loadFogColour(Colour fogColour){
        super.loadVector3f(location_fogColour, fogColour.toVector3f());
    }

    public void loadProjectionMatrix(Matrix4f matrix){
        super.loadMatrix(location_projectionMatrix, matrix);
    }

    public void loadViewMatrix(Camera camera){
        Matrix4f matrix = Maths.createViewMatrix(camera);
        matrix.m30 = 0;
        matrix.m31 = 0;
        matrix.m32 = 0;
        rotation += ROTATE_SPEED * DisplayManager.getDeltaTime();
        matrix.rotate((float) Math.toRadians(rotation), new Vector3f(0, 1, 0));
        super.loadMatrix(location_viewMatrix, matrix);
    }

}
