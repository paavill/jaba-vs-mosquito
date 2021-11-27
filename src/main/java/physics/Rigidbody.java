package physics;

import org.joml.Vector3f;

public class Rigidbody {

    private Collision collider;
    private Vector3f velocity;

    private Boolean isGrounded;

    public Rigidbody(Vector3f position, Vector3f extend) {
        collider = new Collision(position, extend);
        velocity = new Vector3f(0, 0, 0);
        isGrounded = false;
    }

    public Collision getCollider() {
        return collider;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }
    public void setVelocityWithoutGravity(float x, float z) {
        this.velocity.x = x;
        this.velocity.z = z;
    }

    public void update() {
        this.collider.move(this.velocity);
    }

    public void teleport(Vector3f point) {
        this.collider.teleport(point);
    }

    public boolean isOnGround() {
        return this.isGrounded;
    }

    public void setOnGround(boolean isGrounded) {
        this.isGrounded = isGrounded;
    }

    public void addForce(Vector3f force) {
        velocity.add(force);
    }
}
