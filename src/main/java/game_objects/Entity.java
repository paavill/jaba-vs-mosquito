package game_objects;

import org.joml.Vector3f;

public class Entity {

    private Vector3f position;
    private Vector3f rotation;

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }
}
