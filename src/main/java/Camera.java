import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Camera {
    private static final Vector3f VERTICAL_CAMERA_VECTOR = new Vector3f(0.f, 1.f, 0.f);

    private static Vector3f currentPosition = new Vector3f();
    private static Vector3f currentRotation = new Vector3f();
    private static Vector3f currentFront = new Vector3f(0.f, 0.f, -1.f);

    private static float cameraMoveSpeed = 0;
    private static float cameraRotationSpeed = 0;

    private static Matrix4f lookAt = new Matrix4f();

    private Camera() {
    }

    ;

    public static void init(Vector3f position, Vector3f rotation, float cameraMoveSpeed, float cameraRotationSpeed) {
        Camera.cameraMoveSpeed = cameraMoveSpeed;
        Camera.cameraRotationSpeed = cameraRotationSpeed;
        Camera.setLookAt(position, rotation);
        Camera.generateMatrix();
    }

    public static void defaultInit() {
        Camera.init(new Vector3f(0.f, 0.f, 3.f),
                new Vector3f(0.f, 0.f, 0.f), 1.f, 1.f);
    }

    private static void generateMatrix() {
        Camera.lookAt = new Matrix4f().lookAt(
                Camera.currentPosition,
                Camera.currentRotation,
                VERTICAL_CAMERA_VECTOR);
    }

    private static void setLookAt(Vector3f position, Vector3f rotation) {
        Camera.currentPosition = position;
        Camera.currentRotation = rotation.add(currentFront);
    }

    private static void updateLookAt(Vector3f position, Vector3f rotation) {
        Camera.currentPosition.add(position);
        Camera.currentRotation.add(rotation.add(currentFront));
    }

    public static void move(int direct){
        Camera.moveByVector(new Vector3f(Camera.currentRotation).mul(direct));
    }

    public static void moveByVector(Vector3f movementVector) {
        Camera.updateLookAt(movementVector, new Vector3f());
        Camera.generateMatrix();
    }

    public static void moveAbsolute(Vector3f position) {
        Camera.init(position, Camera.currentRotation, Camera.cameraMoveSpeed, Camera.cameraRotationSpeed);
    }

    public static void rotateByVector(Vector3f rotationVector) {
        Camera.updateLookAt(new Vector3f(), rotationVector);
        Camera.generateMatrix();
    }

    public static void rotateAbsolute(Vector3f rotation) {
        Camera.init(Camera.currentPosition, rotation, Camera.cameraMoveSpeed, Camera.cameraRotationSpeed);
    }

    public static Matrix4f getLookAt() {
        return Camera.lookAt;
    }
}
