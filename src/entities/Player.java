package entities;

import models.TexturedModel;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import particle.Particle;
import particle.ParticleMaster;
import particle.ParticleSystem;
import particle.ParticleTexture;
import renderEngine.DisplayManager;
import terrains.Terrain;
import water.WaterTile;

import java.util.List;

public class Player extends Entity{

    private static final float RUN_SPEED = 60;
    private static final float TURN_SPEED = 160;
    private static final float JUMP_POWER = 30;
    public static final float GRAVITY = -50;

    private static final float TERRAIN_HEIGHT = 0;

    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private float upwardsSpeed = 0;

    private boolean isInAir = false;
    private boolean isInWater = false;

    public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);
    }

    public void move(Terrain terrain, List<WaterTile> waterTiles){
        checkInputs();
        super.increaseRotation(new Vector3f(0f, currentTurnSpeed * DisplayManager.getDeltaTime(), 0f));
        float distance = currentSpeed * DisplayManager.getDeltaTime();
        float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
        float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
        super.increasePosition(new Vector3f(dx, 0, dz));
        upwardsSpeed += GRAVITY * DisplayManager.getDeltaTime();
        super.increasePosition(new Vector3f(0, upwardsSpeed * DisplayManager.getDeltaTime(), 0));
        float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
        if(super.getPosition().y < terrainHeight) {
            upwardsSpeed = 0;
            super.getPosition().y = terrainHeight;
            isInAir = false;
        }

        for(WaterTile water : waterTiles){
            float xMax = water.getX() + WaterTile.TILE_SIZE;
            float xMin = water.getX() - WaterTile.TILE_SIZE;

            float zMax = water.getZ() + WaterTile.TILE_SIZE;
            float zMin = water.getZ() - WaterTile.TILE_SIZE;

            if(super.getPosition().y < water.getHeight() * 1.03 &&
                    super.getPosition().x > xMin && super.getPosition().x < xMax &&
                    super.getPosition().z > zMin && super.getPosition().z < zMax){
                this.isInWater = true;
                break;
            }else {
                this.isInWater = false;
            }
        }

    }

    public boolean isInWater() {
        return isInWater;
    }

    private void jump(){
        if(isInAir) return;
        this.upwardsSpeed = JUMP_POWER;
        isInAir = true;
    }

    private void checkInputs(){
        if(Keyboard.isKeyDown(Keyboard.KEY_Z)){
            this.currentSpeed = RUN_SPEED;
        }else if(Keyboard.isKeyDown(Keyboard.KEY_S)){
            this.currentSpeed = -RUN_SPEED;
        }else {
            this.currentSpeed = 0;
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_Q)){
            this.currentTurnSpeed = TURN_SPEED;
        }else if(Keyboard.isKeyDown(Keyboard.KEY_D)){
            this.currentTurnSpeed = -TURN_SPEED;
        }else {
            this.currentTurnSpeed = 0;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) jump();

        if(Keyboard.isKeyDown(Keyboard.KEY_I))
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        else
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
    }

}
