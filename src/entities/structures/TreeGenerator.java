package entities.structures;

import entities.Entity;
import loaders.Loader;
import loaders.objLoader.OBJFileLoader;
import models.TexturedModel;
import org.lwjgl.util.vector.Vector3f;
import textures.ModelTexture;

import java.util.List;
import java.util.Random;

public class TreeGenerator{

    private Loader loader;

    TexturedModel tree;
    TexturedModel bobbleTree;
    TexturedModel pineTree;

    private TexturedModel[] treesModels;
    private float[] treesScales = {7.5f, .70f, 1.70f};

    public TreeGenerator(Loader loader) {
        this.loader = loader;

        tree = new TexturedModel(OBJFileLoader.loadObjModel("tree", loader), new ModelTexture(loader.loadTexture("models/tree")));
        bobbleTree = new TexturedModel(OBJFileLoader.loadObjModel("bobbleTree", loader), new ModelTexture(loader.loadTexture("models/bobbleTree")));
        pineTree = new TexturedModel(OBJFileLoader.loadObjModel("pine", loader), new ModelTexture(loader.loadTexture("models/pine")));
        treesModels = new TexturedModel[] {tree, bobbleTree, pineTree};
    }

    public Entity generateTree(Vector3f position, float rotX, float rotY, float rotZ){
        Random r = new Random();
        int randomTree = r.nextInt(3);
        TexturedModel treeModel = treesModels[randomTree];
        float scale = treesScales[randomTree];
        return new Entity(treeModel, position, rotX, rotY, rotZ, scale);
    }

}
