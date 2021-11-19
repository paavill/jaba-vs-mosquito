package game_objects;

import input.KeyBindings;
import renderer.IRenderable;
import main.Camera;
import main.Tuple;

import org.joml.Vector3f;
import renderer.Model;

public class Player extends Entity implements IRenderable {

    private final Camera mainCamera;
    private final KeyBindings bindings;

    public Player(Camera mainCamera, KeyBindings bindings) {
        this.mainCamera = mainCamera;
        this.bindings = bindings;
        this.setPosition(mainCamera.getCurrentPosition());
    }

    public void update() {
        updatePosition();
        this.setPosition(mainCamera.getCurrentPosition());
        updateRotation();
    }

    @Override
    public Tuple<Model, Tuple<Vector3f, Vector3f>> getModel() {
        return null;
    }

    private void updatePosition() {
        mainCamera.move(bindings);
    }

    private void updateRotation() {
        mainCamera.rotate(bindings);
    }

    public void move(Vector3f vec){
        mainCamera.moveByVector(vec);
    }
}
