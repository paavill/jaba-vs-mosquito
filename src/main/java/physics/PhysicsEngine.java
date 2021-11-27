package physics;

import game_objects.blocks.BlockType;
import main.Tuple;
import org.joml.Vector3f;
import org.lwjgl.system.CallbackI;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PhysicsEngine {

    private final Vector3f gravity;

    public PhysicsEngine() {
        gravity = new Vector3f(0.0f, -0.005f, 0.0f);
    }

    public Vector3f tryMoveRigidbody(Rigidbody object, LinkedList<LinkedList<LinkedList<Tuple<Vector3f, BlockType>>>> blocks) {
        useGravity(object, blocks);
        Tuple<List<Collision>, Boolean> res = checkCollision(blocks, object, false);
        if (res.second) {
            //object.getCollider().clipToCollider(res.first, new Vector3f(object.getVelocity().x, 0f, 0f).normalize());
            res.first.forEach(block -> {
                float distance = object.getCollider().getCenter().distance(block.getCenter());
                double distX = object.getCollider().getExtend().x / 2.0 + block.getExtend().x / 2.0 + 0.25;
                double distZ = object.getCollider().getExtend().z / 2.0 + block.getExtend().z / 2.0 + 0.25;
                if (distance < distX && distance < distZ) {
                    object.setVelocity(new Vector3f(0f, object.getVelocity().y, 0f));
                }
            });

        }

        object.update();
        return object.getCollider().getPosition();
    }


    private void useGravity(Rigidbody object,  LinkedList<LinkedList<LinkedList<Tuple<Vector3f, BlockType>>>> blocks) {
        Tuple<List<Collision>, Boolean> res = checkCollision(blocks, object, true);
        if (res.second) {
            //object.getCollider().clipToCollider(res.first, new Vector3f(0f, object.getVelocity().y, 0f).normalize());
            res.first.forEach(block -> {
                //Тут фиксить
                if (block.getPosition().y < object.getCollider().getPosition().y) {
                    object.setOnGround(true);
                    object.setVelocity(new Vector3f(object.getVelocity().x, 0, object.getVelocity().z));
                }
            });
        } else {
            object.getVelocity().add(gravity);
        }
    }

    private Tuple<List<Collision>, Boolean> checkCollision(LinkedList<LinkedList<LinkedList<Tuple<Vector3f, BlockType>>>> blocks, Rigidbody rigidbody, boolean isGravity) {
        Tuple<List<Collision>, Boolean> res = new Tuple<List<Collision>, Boolean>(new ArrayList<Collision>(), false);

        for (LinkedList<LinkedList<Tuple<Vector3f, BlockType>>> i : blocks) {
            for (LinkedList<Tuple<Vector3f, BlockType>> j : i) {
                for (Tuple<Vector3f, BlockType> block : j) {
                    if (block.second != BlockType.AIR) {
                        Collision col = new Collision(block.first, new Vector3f(1.f, 1.f, 1.f));
                        Collision body =  new Collision(new Vector3f(rigidbody.getCollider().getPosition()), rigidbody.getCollider().getExtend());
                        if (isGravity) {
                            body.move(new Vector3f(0.f, rigidbody.getVelocity().y, 0.f));
                            if (body.isCollideWith(col)) {
                                res.second = true;
                                res.first.add(col);
                            }
                        } else {
                            body.move(new Vector3f(rigidbody.getVelocity().x, 0.f, rigidbody.getVelocity().z));
                            if (body.isCollideWith(col)) {
                                res.second = true;
                                res.first.add(col);
                            }
                        }
                    }
                }
            }
        }

        return res;
    }
}
