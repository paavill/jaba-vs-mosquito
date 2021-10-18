
import org.joml.Vector3f;

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
        if(action==GLFW_PRESS){
            InputValues.setStateByKey(key, true);
        }
        if(action==GLFW_RELEASE){
            InputValues.setStateByKey(key, false);
        }
    }

    public static void mouseCallBack(long window, double xpos, double ypos){
        InputValues.setMousePos((float) xpos, (float) ypos);
    }
}
