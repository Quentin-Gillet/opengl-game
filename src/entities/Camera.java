package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;
import water.WaterTile;

import java.util.List;

public class Camera {

    private float distanceFromPlayer = 50;
    private float angleAroundPlayer = 0;

    private Vector3f position = new Vector3f(0, 0, 0);
    private float pitch = 20;
    private float yaw = 0;
    private float roll;

    private Player player;
    
    public Camera(Player player){
        this.player = player;
    }

    public void move(){
        this.calculateAngleAroundPlayer();
        this.calculatePitch();
        this.calculateZoom();
        float horizontalDistance = this.calculateHorizontalDistance();
        float verticalDistance = this.calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance, verticalDistance);
        this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
    }

    public void cameraOwnMovement(){
        if(Keyboard.isKeyDown(Keyboard.KEY_Z)){
            position.z -= 1f;
        }else if(Keyboard.isKeyDown(Keyboard.KEY_S)){
            position.z += 1f;
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_Q)){
            position.x -= 1f;
        }else if(Keyboard.isKeyDown(Keyboard.KEY_D)){
            position.x += 1f;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) position.y += 1f;
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) position.y -= 1f;
        this.calculatePitch();
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }

    private void calculateCameraPosition(float horizontalDistance, float verticalDistance){
        float theta = player.getRotY() + angleAroundPlayer;
        float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
        position.x = player.getPosition().x - offsetX;
        position.z = player.getPosition().z - offsetZ;
        position.y = player.getPosition().y + verticalDistance;
    }

    private void calculateZoom(){
        float zoomLevel = Mouse.getDWheel() * 0.1f;
        distanceFromPlayer -= zoomLevel;
    }

    private void calculatePitch(){
        if(Mouse.isButtonDown(1)){
            float pitchChange = Mouse.getDY() * 0.1f;
            pitch -= pitchChange;
        }
    }

    private void calculateAngleAroundPlayer(){
        if(Mouse.isButtonDown(0)){
            float angleChange = Mouse.getDX() * 0.3f;
            angleAroundPlayer -= angleChange;
        }
    }

    private float calculateHorizontalDistance(){
        return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
    }

    private float calculateVerticalDistance(){
        return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
    }

    public void invertPitch() {
        this.pitch = -this.pitch;
    }
}

