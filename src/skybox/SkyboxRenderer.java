package skybox;

import entities.Camera;
import loaders.Loader;
import models.RawModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import renderEngine.DisplayManager;
import toolbox.Colour;

public class SkyboxRenderer {

    private static final float SIZE = 500f;

    private static final float[] VERTICES = {
            -SIZE,  SIZE, -SIZE,
            -SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,

            -SIZE, -SIZE,  SIZE,
            -SIZE, -SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE,  SIZE,
            -SIZE, -SIZE,  SIZE,

            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,

            -SIZE, -SIZE,  SIZE,
            -SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE, -SIZE,  SIZE,
            -SIZE, -SIZE,  SIZE,

            -SIZE,  SIZE, -SIZE,
            SIZE,  SIZE, -SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            -SIZE,  SIZE,  SIZE,
            -SIZE,  SIZE, -SIZE,

            -SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE,  SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE,  SIZE,
            SIZE, -SIZE,  SIZE
    };

    private static final String[] TEXTURES_FILES = {"right", "left", "top", "bottom", "back", "front"};
    private static final String[] NIGHT_TEXTURES_FILES = {"nightRight", "nightLeft", "nightTop", "nightBottom", "nightBack", "nightFront"};

    private RawModel cube;
    private int textureId;
    private int nightTextureId;
    private SkyboxShader shader;
    private float time = 0;

    public SkyboxRenderer(Loader loader, Matrix4f projectionMatrix){
        cube = loader.loadToVao(VERTICES, 3);
        textureId = loader.loadCubeMap(TEXTURES_FILES);
        nightTextureId = loader.loadCubeMap(NIGHT_TEXTURES_FILES);
        shader = new SkyboxShader();
        shader.start();
        shader.connectedTextureUnits();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void render(Camera camera, Colour fogColour){
        shader.start();
        shader.loadViewMatrix(camera);
        shader.loadFogColour(fogColour);
        GL30.glBindVertexArray(cube.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        bindTextures();
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.getVertexCount());
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        shader.stop();
    }

    private void bindTextures(){
        time += DisplayManager.getDeltaTime() * 1000;
        time %= 24000;
        int texture1;
        int texture2;
        float blendFactor;
        if(time >= 0 && time < 5000){
            texture1 = nightTextureId;
            texture2 = nightTextureId;
            blendFactor = (time - 0)/(5000);
        }else if(time >= 5000 && time < 8000){
            texture1 = nightTextureId;
            texture2 = textureId;
            blendFactor = (time - 5000)/(8000 - 5000);
        }else if(time >= 8000 && time < 21000){
            texture1 = textureId;
            texture2 = textureId;
            blendFactor = (time - 8000)/(21000 - 8000);
        }else{
            texture1 = textureId;
            texture2 = nightTextureId;
            blendFactor = (time - 21000)/(24000 - 21000);
        }

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, textureId);
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, textureId);
        shader.loadBlendFactor(blendFactor);
    }

}
