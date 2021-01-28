package particle;

public class ParticleTexture {

    private int textureId;
    private int numberOfRows;
    private boolean additive;

    public ParticleTexture(int textureId, int numberOfRows, boolean additive) {
        this.textureId = textureId;
        this.numberOfRows = numberOfRows;
        this.additive = additive;
    }

    public boolean isAdditive() {
        return additive;
    }

    public int getTextureId() {
        return textureId;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }
}
