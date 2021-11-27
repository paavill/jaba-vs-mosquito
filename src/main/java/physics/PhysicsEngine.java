package physics;

import game_objects.blocks.BlockType;
import main.Tuple;
import org.joml.Vector3f;
import org.lwjgl.system.CallbackI;

import java.util.LinkedList;

public class PhysicsEngine {

    private final Vector3f gravity;

    public PhysicsEngine() {
        gravity = new Vector3f(0.0f, -0.005f, 0.0f);
    }

    public Vector3f tryMoveRigidbody(Rigidbody object, LinkedList<LinkedList<LinkedList<Tuple<Vector3f, BlockType>>>> blocks) {
        useGravity(object, blocks);
        Tuple<Collision, Boolean> res = checkCollision(blocks, object, false);
        if (res.second) {
            //object.getCollider().clipToCollider(res.first, new Vector3f(object.getVelocity().x, 0f, 0f).normalize());
            //object.getCollider().clipToCollider(res.first, new Vector3f(0f, 0f, object.getVelocity().z).normalize());
            object.setVelocity(new Vector3f(0f, object.getVelocity().y, 0f));
        }

        object.update();
        return object.getCollider().getPosition();
    }


    private void useGravity(Rigidbody object,  LinkedList<LinkedList<LinkedList<Tuple<Vector3f, BlockType>>>> blocks) {
        Tuple<Collision, Boolean> res = checkCollision(blocks, object, true);
        if (res.second) {
            //object.getCollider().clipToCollider(res.first, new Vector3f(0f, object.getVelocity().y, 0f).normalize());
            object.setVelocity(new Vector3f(object.getVelocity().x, 0, object.getVelocity().z));
        } else {
            object.getVelocity().add(gravity);
        }
    }

    private Tuple<Collision, Boolean> checkCollision(LinkedList<LinkedList<LinkedList<Tuple<Vector3f, BlockType>>>> blocks, Rigidbody rigidbody, boolean isGravity) {
        Tuple<Collision, Boolean> res = new Tuple<>(null, false);

        for (LinkedList<LinkedList<Tuple<Vector3f, BlockType>>> i : blocks) {
            for (LinkedList<Tuple<Vector3f, BlockType>> j : i) {
                for (Tuple<Vector3f, BlockType> block : j) {
                    if (block.second != BlockType.AIR) {
                        Collision col = new Collision(block.first, new Vector3f(1.f, 1.f, 1.f));
                        Collision body =  new Collision(new Vector3f(rigidbody.getCollider().getPosition()), rigidbody.getCollider().getExtend());
                        if (isGravity) {
                            body.move(new Vector3f(0.f, rigidbody.getVelocity().y, 0.f));
                            if (body.isCollideWith(col)) {
                                return new Tuple<>(col, true);
                            }
                        } else {
                            body.move(new Vector3f(rigidbody.getVelocity().x, 0.f, rigidbody.getVelocity().z));
                            if (body.isCollideWith(col)) {
                                return new Tuple<>(col, true);
                            }
                        }
                    }
                }
            }
        }

        return res;
    }
}
