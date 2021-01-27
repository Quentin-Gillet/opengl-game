package textures;

public class ModelTexture {

    private int textureID;
    private int normalMapID;

    private float shineDamper = 1;
    private float reflectivity = 0;

    private boolean hasTransparency = false;
    private boolean useFakeLightning = false;

    private int numberOfRows = 1;

    public ModelTexture(int id){
        this.textureID = id;
    }

    public int getTextureID(){
        return this.textureID;
    }

    public float getShineDamper() {
        return shineDamper;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public void setNumberOfRows(int numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    public void setShineDamper(float shineDamper) {
        this.shineDamper = shineDamper;
    }

    public float getReflectivity() {
        return reflectivity;
    }

    public void setReflectivity(float reflectivity) {
        this.reflectivity = reflectivity;
    }

    public void setTextureID(int textureID) {
        this.textureID = textureID;
    }

    public boolean isTransparent() {
        return hasTransparency;
    }

    public void setTransparency(boolean hasTransparency) {
        this.hasTransparency = hasTransparency;
    }

    public boolean isUseFakeLightning() {
        return useFakeLightning;
    }

    public void setUseFakeLightning(boolean useFakeLightning) {
        this.useFakeLightning = useFakeLightning;
    }

    public int getNormalMapID() {
        return normalMapID;
    }

    public void setNormalMapID(int normalMapID) {
        this.normalMapID = normalMapID;
    }
}
