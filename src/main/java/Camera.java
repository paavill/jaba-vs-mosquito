import org.joml.*;
import org.lwjgl.BufferUtils;

import java.lang.Math;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;

public class Camera {
    public static float yaw = -90;

    public static float pitch = 0;

    public static float lastX = 0;
    public static float lastY = 0;

    private static final Vector3f VERTICAL_CAMERA_VECTOR = new Vector3f(0.f, 1.f, 0.f);

    private static Vector3f currentPosition = new Vector3f();
    private static Vector3f currentViewPoint = new Vector3f();

    private static Vector3f currentFront = new Vector3f();

    private static float cameraMoveSpeed = 0;
    private static float cameraViewPointSpeed = 0;

    private static Matrix4f lookAt = new Matrix4f();

    private Camera() {
    }

    ;

    public static void init(long window, Vector3f position, Vector3f viewPoint, float cameraMoveSpeed, float cameraViewPointSpeed) {
        Camera.cameraMoveSpeed = cameraMoveSpeed;
        Camera.cameraViewPointSpeed = cameraViewPointSpeed;
        Camera.setLookAt(position, viewPoint);
        Camera.generateMatrix();

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        glfwGetWindowSize(window, width, height);
        Camera.lastX = width.get()/2;
        Camera.lastY = height.get()/2;
    }

    public static void defaultInit(long window) {
        Camera.init(window, new Vector3f(0.f, 0.f, 3.f),
                new Vector3f(0.f, 0.f, 0.f), 0.3f, 0.3f);
    }

    private static void generateMatrix() {
        Camera.lookAt = new Matrix4f().lookAt(
                Camera.currentPosition,
                Camera.currentViewPoint,
                VERTICAL_CAMERA_VECTOR);
    }

    private static void setLookAt(Vector3f position, Vector3f viewPoint) {
        Camera.currentPosition = position;
        Camera.currentViewPoint = viewPoint;
    }

    private static void updateLookAt(Vector3f position, Vector3f viewPoint) {
        Camera.currentPosition.add(position);
        Camera.currentViewPoint.add(viewPoint);
    }

    public static void moveFront() {
        Camera.moveByVector(new Vector3f(Camera.currentViewPoint).sub(Camera.currentPosition).normalize().mul(Camera.cameraMoveSpeed));
    }

    public static void moveBack(){
        Camera.moveByVector(new Vector3f(Camera.currentViewPoint).sub(Camera.currentPosition).normalize().mul(Camera.cameraMoveSpeed).mul(-1));
    }

    public static void moveRight(){
        Quaternionf q = new Quaternionf();
        q.rotateAxis((float) Math.toRadians(-90), 0, 1, 0);
        Camera.moveByVector(new Vector3f(Camera.currentViewPoint).sub(Camera.currentPosition).rotate(q).normalize().mul(Camera.cameraMoveSpeed));
    }

    public static void moveLeft(){
        Quaternionf q = new Quaternionf();
        q.rotateAxis((float) Math.toRadians(90), 0, 1, 0);
        Camera.moveByVector(new Vector3f(Camera.currentViewPoint).sub(Camera.currentPosition).rotate(q).normalize().mul(Camera.cameraMoveSpeed));
    }

    public static void moveByVector(Vector3f movementVector) {
        Camera.updateLookAt(movementVector, movementVector);
        Camera.generateMatrix();
    }

    public static void moveAbsolute(Vector3f position) {
        Camera.setLookAt(position, new Vector3f(position).add(Camera.currentFront));
        Camera.generateMatrix();
    }

    public static Matrix4f getLookAt() {
        return Camera.lookAt;
    }

    public static Vector3f getCurrentFront() {
        return currentFront;
    }

    public static void setCurrentFront(Vector3f currentFront) {
        Camera.currentFront = currentFront;
        Camera.currentViewPoint = new Vector3f(Camera.currentPosition).add(Camera.currentFront);
        Camera.generateMatrix();
    }
}
