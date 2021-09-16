
import static org.lwjgl.opengl.GL11.glViewport;

public class InputHandler {
    public static void resizeWindow(long window, int width, int height){
        glViewport(0,0, width, height);
    };
}
