import java.util.HashMap;
import java.util.Map;
import static org.lwjgl.glfw.GLFW.*;

public class InputValues {

    private InputValues(){

    }

    private static Map<Integer, Boolean> buttonsStates = new HashMap<Integer, Boolean>(){{
        put(GLFW_KEY_W,false);
        put(GLFW_KEY_S,false);
        put(GLFW_KEY_D,false);
        put(GLFW_KEY_A,false);
        put(GLFW_KEY_LEFT_SHIFT, false);
        put(GLFW_KEY_SPACE, false);
    }};

    private static Tuple<Float, Float> mousePos = new Tuple(0f, 0f);

    public static void setMousePos(float x, float y){
        InputValues.mousePos = new Tuple(x, y);
    }

    public static Tuple<Float, Float> getMousePos(){
        return InputValues.mousePos;
    }

    public static void setStateByKey(int key, boolean state){
        InputValues.buttonsStates.replace(key, state);
    }

    public static boolean getStateByKey(int key){
        return InputValues.buttonsStates.get(key);
    }
}
