
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;

public class InputHandler {
    public static void resizeWindow(long window, int width, int height) {
        glViewport(0, 0, width, height);
    }

    ;

    public static void keyCallBack(long window, int key, int scancode, int action, int mods) {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
            glfwSetWindowShouldClose(window, true);
        }

        if (key == GLFW_KEY_W && action == GLFW_PRESS){
            Camera.move(1);
        }

        if (key == GLFW_KEY_S && action == GLFW_PRESS){
            Camera.move(-1);
        }
    }
}
