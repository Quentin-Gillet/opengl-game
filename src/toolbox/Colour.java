package toolbox;

import org.lwjgl.util.vector.Vector3f;

public class Colour {

    public float r, g, b, a;

    public static Colour BLUE = new Colour(0, 0, 1, 1);
    public static Colour GREEN = new Colour(0, 1, 0, 1);
    public static Colour RED = new Colour(1, 0, 0, 1);
    public static Colour WHITE = new Colour(1, 1, 1, 1);
    public static Colour CYAN = new Colour(0, 0.5f, 0.5f, 1);
    public static Colour GREY = new Colour(0.4f, 0.4f, 0.45f, 1);
    public static Colour BLACK = new Colour(0, 0, 0, 1);

    public Colour(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Vector3f toVector3f(){
        return new Vector3f(this.r * this.a, this.g * this.a, this.b * this.a);
    }

}
