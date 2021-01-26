package models;

public class RawModel {

    private int vaoID;
    private int vertexCount;
    private boolean useFog = true;

    public RawModel(int vaoID, int vertexCount){
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
    }

    public boolean isUseFog() {
        return useFog;
    }

    public void setUseFog(boolean useFog) {
        this.useFog = useFog;
    }

    public int getVaoID() {
        return vaoID;
    }

    public int getVertexCount() {
        return vertexCount;
    }
}
