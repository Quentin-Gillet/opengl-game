package models;

import textures.ModelTexture;

public class TexturedModel {

    private RawModel rawModel;
    private ModelTexture texture;

    public TexturedModel(RawModel model, ModelTexture texture){
        this.texture = texture;
        this.rawModel = model;
    }

    public RawModel getRawModel() {
        return rawModel;
    }

    public ModelTexture getTexture() {
        return texture;
    }
}
