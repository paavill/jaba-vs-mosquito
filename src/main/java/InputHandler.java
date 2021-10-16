
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

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
    }

    public static void mouseCallBack(long window, double xpos, double ypos){
        glfwSetCursorPos(window, Camera.lastX,Camera.lastY);
        float xoffset = (float) xpos - Camera.lastX;
        float yoffset = Camera.lastY - (float)ypos; // уменьшаемое и вычитаемое поменяны местами, так как диапазон y-координаты определяется снизу вверх

        float sensitivity = 0.2f;
        xoffset *= sensitivity;
        yoffset *= sensitivity;

        Camera.yaw   += xoffset;
        Camera.pitch += yoffset;

        if(Camera.pitch > 89.0f)
            Camera.pitch = 89.0f;
        if(Camera.pitch < -89.0f)
            Camera.pitch = -89.0f;

        Vector3f direction = new Vector3f();
        direction.x = (float)(Math.cos(Math.toRadians(Camera.yaw)) * Math.cos(Math.toRadians(Camera.pitch)));
        direction.y = (float)Math.sin(Math.toRadians(Camera.pitch));
        direction.z = (float)(Math.sin(Math.toRadians(Camera.yaw)) * Math.cos(Math.toRadians(Camera.pitch)));
        Camera.setCurrentFront(direction.normalize());
    }

    public static void moveCamera(long window){
        int key = GLFW_KEY_W;
        int action = glfwGetKey(window, key);
        if ((action == GLFW_PRESS || action == GLFW_REPEAT)) {
            Camera.moveFront();
        }

        key = GLFW_KEY_S;
        action = glfwGetKey(window, key);
        if ((action == GLFW_PRESS || action == GLFW_REPEAT)) {
            Camera.moveBack();
        }

        key = GLFW_KEY_D;
        action = glfwGetKey(window, key);
        if ((action == GLFW_PRESS || action == GLFW_REPEAT)) {
            Camera.moveRight();
        }

        key = GLFW_KEY_A;
        action = glfwGetKey(window, key);
        if ((action == GLFW_PRESS || action == GLFW_REPEAT)) {
            Camera.moveLeft();
        }
    }
}
