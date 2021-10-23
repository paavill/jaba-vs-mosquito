package input;

import main.Tuple;
import main.Window;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class InputManager {

    private Map<Integer, Boolean> keys;
    private Tuple<Float, Float> mousePosition;

    public InputManager(Window window) {
        this.keys = new HashMap<>();
        this.mousePosition = Tuple.from(0.0f, 0.0f);

        glfwSetKeyCallback(window.getWindowDescriptor(), this::keyCallback);
        glfwSetCursorPosCallback(window.getWindowDescriptor(), this::mouseCallback);
    }

    public void handleEvents() {
        glfwPollEvents();
    }

    public void keyCallback(long window, int key, int scancode, int action, int mods) {
        this.keys.replace(key, action != GLFW_RELEASE);
    }

    public void mouseCallback(long window, double xpos, double ypos){
        this.mousePosition = Tuple.from((float) xpos, (float) ypos);
    }

    public Boolean getKeyState(Integer key) {
        if (!keys.containsKey(key)) {
            return false;
        }
        return keys.get(key);
    }

    public Tuple<Float, Float> getMousePosition() {
        return mousePosition;
    }
}
