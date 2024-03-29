package water;

import entities.Light;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import shaders.ShaderProgram;
import toolbox.Maths;
import entities.Camera;

public class WaterShader extends ShaderProgram {

	private final static String VERTEX_FILE = "res/shaders/water/waterVertex.glsl";
	private final static String FRAGMENT_FILE = "res/shaders/water/waterFragment.glsl";

	private int location_modelMatrix;
	private int location_viewMatrix;
	private int location_projectionMatrix;
	private int location_refractionTexture;
	private int location_reflectionTexture;
	private int location_moveFactor;
	private int location_dudvTexture;
	private int location_cameraPosition;
	private int location_normalMap;
	private int location_depthMap;
	private int location_lightColour; //TODO support for multiple lights and attenuation
	private int location_lightPosition;
	private int location_skyColour;
	private int location_useFog;


	public WaterShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute(0, "position");
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = getUniformLocation("projectionMatrix");
		location_viewMatrix = getUniformLocation("viewMatrix");
		location_modelMatrix = getUniformLocation("modelMatrix");
		location_reflectionTexture = getUniformLocation("reflectionTexture");
		location_refractionTexture = getUniformLocation("refractionTexture");
		location_dudvTexture = getUniformLocation("dudvMap");
		location_moveFactor = getUniformLocation("moveFactor");
		location_cameraPosition = getUniformLocation("cameraPosition");
		location_normalMap = getUniformLocation("normalMap");
		location_depthMap = getUniformLocation("depthMap");
		location_lightColour = getUniformLocation("lightColour");
		location_lightPosition = getUniformLocation("lightPosition");
		location_useFog = super.getUniformLocation("useFog");
		location_skyColour = super.getUniformLocation("skyColour");

	}

	public void loadProjectionMatrix(Matrix4f projection) {
		loadMatrix(location_projectionMatrix, projection);
	}
	
	public void loadViewMatrix(Camera camera){
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		loadMatrix(location_viewMatrix, viewMatrix);
		super.loadVector3f(location_cameraPosition, camera.getPosition());
	}

	public void loadTextureUnits(){
		super.loadInt(location_reflectionTexture, 0);
		super.loadInt(location_refractionTexture, 1);
		super.loadInt(location_dudvTexture, 2);
		super.loadInt(location_normalMap, 3);
		super.loadInt(location_depthMap, 4);
	}

	public void loadLight(Light light){
		super.loadVector3f(location_lightColour, light.getColour());
		super.loadVector3f(location_lightPosition, light.getPosition());
	}

	public void loadFog(boolean useFog){
		super.loadBoolean(location_useFog, useFog);
	}

	public void loadSkyColour(Vector3f skyColour){
		super.loadVector3f(location_skyColour, skyColour);
	}

	public void changeMoveFactor(float factor){
	    super.loadFloat(location_moveFactor, factor);
    }

	public void loadModelMatrix(Matrix4f modelMatrix){
		loadMatrix(location_modelMatrix, modelMatrix);
	}

}
