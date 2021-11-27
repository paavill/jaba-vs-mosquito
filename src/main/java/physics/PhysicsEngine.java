package physics;

import org.joml.Vector3f;
import org.lwjgl.system.CallbackI;

public class PhysicsEngine {

    private final Vector3f gravity;

    public PhysicsEngine() {
        gravity = new Vector3f(0.0f, -0.0005f, 0.0f);
    }

    public void addForce(Rigidbody object, Vector3f force) {
        object.getVelocity().add(force);
    }

    public Vector3f tryMoveRigidbody(Rigidbody object, int[][][] cubes) {
        useGravity(object, cubes);
        if (true/*проверка на столкновение с блоками на равне с игроком*/) {
            //object.getCollider().clipToCollider(блок с которым столкновение, new Vector3f(object.getVelocity().x, 0f, 0f).normilize());
            //object.getCollider().clipToCollider(блок с которым столкновение, new Vector3f(0f, 0f, object.getVelocity().z).normilize());
            object.setVelocity(new Vector3f(0f, object.getVelocity().y, 0f));
        }

        object.update();
        return object.getCollider().getPosition();
    }


    private void useGravity(Rigidbody object, int[][][] cubes) {
        if (false/*проверка на столкновение с блоками ниже игрока или выше*/) {
            //object.getCollider().clipToCollider(блок с которым столкновение, new Vector3f(0f, object.getVelocity().y, 0f).normilize());
            object.setVelocity(new Vector3f(object.getVelocity().x, 0, object.getVelocity().z));
        } else {
            object.getVelocity().add(gravity);
        }
    }
}
