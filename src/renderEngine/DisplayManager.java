package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.*;

public class DisplayManager {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final int FPS_CAP = 144;

    private static long lastFrameTime;
    private static float delta;

    private static long startTime = System.nanoTime();
    private static int frames = 0;

    public static void createDisplay(){

        ContextAttribs attribs = new ContextAttribs(3, 3)
        .withForwardCompatible(true)
        .withProfileCore(true);

        try {
            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            Display.create(new PixelFormat(), attribs);
            Display.setTitle("OpenGlJava");
        } catch (LWJGLException e) {
            e.printStackTrace();
        }

        GL11.glViewport(0, 0, WIDTH, HEIGHT);
        lastFrameTime = getCurrentTime();
    }

    public static void updateDisplay(){
        Display.sync(FPS_CAP);
        DisplayManager.calculateFrameRate();
        Display.update();
        long currentFrameTime = getCurrentTime();
        delta = (currentFrameTime - lastFrameTime) / 1000f;
        lastFrameTime = currentFrameTime;
    }

    public static float getDeltaTime(){
        return delta;
    }

    public static void closeDisplay(){
        Display.destroy();
    }

    private static void calculateFrameRate()
    {
        frames++;
        if(System.nanoTime() - startTime >= 1000000000) {
            System.out.println("FPSCounter fps: " + frames);
            frames = 0;
            startTime = System.nanoTime();
        }
    }

    private static long getCurrentTime(){
        return Sys.getTime() * 1000 / Sys.getTimerResolution();
    }

}
