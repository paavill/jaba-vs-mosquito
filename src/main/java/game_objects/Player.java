package game_objects;

import game_objects.blocks.BlockType;
import input.KeyBindings;
import physics.PhysicalObject;
import physics.PhysicsEngine;
import physics.Rigidbody;
import renderer.IRenderable;
import main.Camera;
import main.Tuple;

import org.joml.Vector3f;
import renderer.Model;

import java.util.LinkedList;

public class Player extends Entity implements IRenderable, PhysicalObject {

    private final Camera mainCamera;
    private final KeyBindings bindings;
    private final Rigidbody rigidbody;

    public Player(Camera mainCamera, KeyBindings bindings) {
        this.mainCamera = mainCamera;
        this.bindings = bindings;
        this.setPosition(mainCamera.getCurrentPosition());
        this.rigidbody = new Rigidbody(mainCamera.getCurrentPosition(), new Vector3f(1f, 2f, 1f));
    }

    public void update(PhysicsEngine physics, LinkedList<LinkedList<LinkedList<Tuple<Vector3f, BlockType>>>> blocks) {
        updatePosition(physics, blocks);
        updateRotation();
    }

    @Override
    public Tuple<Model, Tuple<Vector3f, Vector3f>> getModel() {
        return null;
    }

    private void updatePosition(PhysicsEngine physics, LinkedList<LinkedList<LinkedList<Tuple<Vector3f, BlockType>>>> blocks) {
        Vector3f dir = mainCamera.getDirectionByInput(bindings);
        rigidbody.setVelocityWithoutGravity(dir.x, dir.z);
        this.setPosition(physics.tryMoveRigidbody(rigidbody, blocks));
        mainCamera.setCurrentPosition(new Vector3f(rigidbody.getCollider().getPosition()).add(new Vector3f(0f, 0.75f, 0f)));
    }

    private void updateRotation() {
        mainCamera.rotate(bindings);
    }

    public void teleport(Vector3f vec){
        rigidbody.teleport(vec);
        mainCamera.setCurrentPosition(new Vector3f(rigidbody.getCollider().getPosition()).add(new Vector3f(0f, 0.75f, 0f)));
    }

    public Camera getMainCamera() {
        return mainCamera;
    }

    @Override
    public Rigidbody getRidigbody() {
        return this.rigidbody;
    }
}
