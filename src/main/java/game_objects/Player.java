package game_objects;

import game_objects.blocks.BlockType;
import input.Controls;
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

    private boolean isCreative;

    public Player(Camera mainCamera, KeyBindings bindings) {
        this.mainCamera = mainCamera;
        this.bindings = bindings;
        this.setPosition(mainCamera.getCurrentPosition());
        this.rigidbody = new Rigidbody(mainCamera.getCurrentPosition(), new Vector3f(1f, 2f, 1f));
        this.isCreative = false;
    }

    public void update(PhysicsEngine physics, LinkedList<LinkedList<LinkedList<Tuple<Vector3f, BlockType>>>> blocks) {
        if (bindings.getState(Controls.R)) {
            this.isCreative = !this.isCreative;
            if (isCreative) {
                mainCamera.setCameraMoveSpeed(0.03f);
            } else {
                mainCamera.setCameraMoveSpeed(0.005f);
            }
        }
        updatePosition(physics, blocks);
        updateRotation();
    }

    @Override
    public Tuple<Model, Tuple<Vector3f, Vector3f>> getModel() {
        return null;
    }

    private void updatePosition(PhysicsEngine physics, LinkedList<LinkedList<LinkedList<Tuple<Vector3f, BlockType>>>> blocks) {
        Vector3f dir = mainCamera.getDirectionByInput(bindings);
        if (!isCreative) {
            rigidbody.setVelocityWithoutGravity(dir.x, dir.z);
            if (bindings.getState(Controls.Up) && rigidbody.isOnGround()) {
                rigidbody.addForce(new Vector3f(0f, 0.1f, 0f));
                rigidbody.setOnGround(false);
            }
            this.setPosition(physics.tryMoveRigidbody(rigidbody, blocks));

        } else {
            Vector3f upDown = dir.add(mainCamera.getUpDownDirectionByInput(bindings));
            rigidbody.teleport(new Vector3f(rigidbody.getCollider().getPosition().add(upDown)));
        }
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
