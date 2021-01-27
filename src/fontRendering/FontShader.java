package fontRendering;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import shaders.ShaderProgram;
import shaders.StaticShader;

public class FontShader extends ShaderProgram {

    private static final String VERTEX_FILE = "res/shaders/font/fontVertexShader.glsl";
    private static final String FRAGMENT_FILE = "res/shaders/font/fontFragmentShader.glsl";

    private int location_translation;
    private int location_colour;
    private int location_fontAtlas;

    public FontShader(){
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        location_translation = super.getUniformLocation("translation");
        location_colour = super.getUniformLocation("colour");
        location_fontAtlas = super.getUniformLocation("fontAtlas");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
    }

    protected void loadColour(Vector3f colour){
        super.loadVector3f(location_colour, colour);
    }

    protected void loadTranslation(Vector2f translation){
        super.loadVector2f(location_translation, translation);
    }
}
