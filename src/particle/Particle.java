package particle;


import entities.Camera;
import entities.Player;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;

public class Particle {

    private Vector3f position;
    private Vector3f velocity;
    private float gravityEffect;
    private float lifeLenght;
    private float rotation;
    private float scale;

    private ParticleTexture texture;

    private Vector2f texOffest1 = new Vector2f();
    private Vector2f texOffest2 = new Vector2f();
    private float blendFactor;

    private float elapsedTime = 0;
    private float distance;

    public Particle(ParticleTexture texture, Vector3f position, Vector3f velocity, float gravityEffect, float lifeLenght, float rotation, float scale) {
        this.position = position;
        this.velocity = velocity;
        this.gravityEffect = gravityEffect;
        this.lifeLenght = lifeLenght;
        this.rotation = rotation;
        this.scale = scale;
        this.texture = texture;
        ParticleMaster.addParticle(this);
    }

    protected boolean update(Camera camera){
        velocity.y += Player.GRAVITY * gravityEffect * DisplayManager.getDeltaTime();
        Vector3f change = new Vector3f(velocity);
        change.scale(DisplayManager.getDeltaTime());
        Vector3f.add(change, position, position);
        distance = Vector3f.sub(camera.getPosition(), position, null).lengthSquared();
        updateTextureCoordInfo();
        elapsedTime += DisplayManager.getDeltaTime();
        return elapsedTime < lifeLenght;
    }

    public float getDistance() {
        return distance;
    }

    public Vector2f getTexOffest1() {
        return texOffest1;
    }

    public Vector2f getTexOffest2() {
        return texOffest2;
    }

    public float getBlendFactor() {
        return blendFactor;
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getRotation() {
        return rotation;
    }

    public float getScale() {
        return scale;
    }

    public ParticleTexture getTexture() {
        return texture;
    }

    private void updateTextureCoordInfo(){
        float lifeFactor = elapsedTime / lifeLenght;
        int stageCount = texture.getNumberOfRows() * texture.getNumberOfRows();
        float atlasProgression = lifeFactor * stageCount;
        int index1 = (int) Math.floor(atlasProgression);
        int index2 = index1 < stageCount -1 ? index1 + 1 : index1;
        this.blendFactor = atlasProgression % 1;
        setTextureOffset(texOffest1, index1);
        setTextureOffset(texOffest2, index2);
    }

    private void setTextureOffset(Vector2f offset, int index){
        int column = index % texture.getNumberOfRows();
        int row = index / texture.getNumberOfRows();
        offset.x = (float)column / texture.getNumberOfRows();
        offset.y = (float)row / texture.getNumberOfRows();
    }
}
