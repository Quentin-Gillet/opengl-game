package entities.structures;

import entities.Entity;
import entities.Light;
import loaders.Loader;
import loaders.objLoader.OBJFileLoader;
import models.TexturedModel;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;
import textures.ModelTexture;
import toolbox.Colour;

import java.util.List;

public class Lamp extends Entity {

    private Light lampLight;
    private boolean stuck = false;

    public Lamp(Loader loader, List<Light> lights, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(position, rotX, rotY, rotZ, scale);

        TexturedModel lamp = new TexturedModel(OBJFileLoader.loadObjModel("lamp", loader), new ModelTexture(loader.loadTexture("models/lamp")));
        super.setModel(lamp);
        Vector3f lightPosition = new Vector3f(position.x, position.y + 12, position.z);
        lampLight = new Light(lightPosition, new Colour(0, 0, 1, 2.5f), new Vector3f(1, 0.01f, 0.002f));
        lights.add(lampLight);
    }

    public void setPosition(Vector3f position){
        if(stuck) return;
        super.setPosition(position);
        Vector3f lightPosition = new Vector3f(position.x, position.y + 12, position.z);
        lampLight.setPosition(lightPosition);
    }

    public void update(){
        super.update();
        if(Mouse.isButtonDown(1)){
            stuck = true;
        }else stuck = false;
    }


}
