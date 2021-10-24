package input;

import main.Tuple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class KeyBindings {

    private final Map<Controls, Integer> controls;
    private final InputManager inputManager;

    public KeyBindings(InputManager manager) {
        this.inputManager = manager;
        this.controls = new HashMap<>();
        controls.put(Controls.Forward,      GLFW_KEY_W);
        controls.put(Controls.Back,         GLFW_KEY_S);
        controls.put(Controls.Right,        GLFW_KEY_D);
        controls.put(Controls.Left,         GLFW_KEY_A);
        controls.put(Controls.Up,           GLFW_KEY_SPACE);
        controls.put(Controls.Down,         GLFW_KEY_LEFT_SHIFT);
        controls.put(Controls.SwitchCursor, GLFW_KEY_LEFT_CONTROL);
    }

    public Boolean getState(Controls control) {
        return inputManager.getKeyState(controls.get(control));
    }

    public Tuple getMousePosition() {
        return inputManager.getMousePosition();
    }

    public  void setMousePosition(Tuple<Float, Float> position){
        this.inputManager.setMousePosition(position);
    }

    public void setCursorPosition(Tuple<Float, Float> position){
        this.inputManager.setCursorPosition(position);
    }
}
