package renderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Light {

    private Vector3f position;

    public Light(Vector3f position) {
        this.position = position;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }
}
