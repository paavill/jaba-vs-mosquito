package input;

import main.Tuple;
import main.Window;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class InputManager {

    private Window window;
    private Map<Integer, Boolean> keys;
    private Tuple<Float, Float> mousePosition;

    public InputManager(Window window) {
        this.window = window;
        this.keys = new HashMap<>();
        this.mousePosition = new Tuple(0.0f, 0.0f);

        glfwSetKeyCallback(window.getWindowDescriptor(), this::keyCallback);
        glfwSetCursorPosCallback(window.getWindowDescriptor(), this::mouseCallback);
    }

    public void handleEvents() {
        glfwPollEvents();
    }

    public void keyCallback(long window, int key, int scancode, int action, int mods) {
        if(keys.containsKey(key)) {
            this.keys.replace(key, action != GLFW_RELEASE);
        } else {
            this.keys.put(key, true);
        }
    }

    public void mouseCallback(long window, double xpos, double ypos){
        this.mousePosition = new Tuple<Float, Float>(Float.valueOf((float) xpos), Float.valueOf((float) ypos));
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

    public void setMousePosition(Tuple<Float, Float> position){
        this.mousePosition = position;
    }

    public void setCursorPosition(Tuple<Float, Float> position){
        glfwSetCursorPos(window.getWindowDescriptor(), position.first, position.second);
    }

    public void setKeys(Map<Integer, Boolean> keys){
        this.keys = keys;
    }
}
