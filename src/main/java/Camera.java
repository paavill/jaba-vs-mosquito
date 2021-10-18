import org.joml.*;

import java.lang.Math;

import static org.lwjgl.glfw.GLFW.*;

import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;

public class Camera {
    private float yaw;

    private float pitch;

    private float centerX = 0;
    private float centerY = 0;

    private final Vector3f VERTICAL_CAMERA_VECTOR = new Vector3f(0.f, 1.f, 0.f);

    private Vector3f currentPosition;
    private Vector3f currentViewPoint;

    private Vector3f currentFront;

    private float cameraMoveSpeed = 0;
    private float cameraViewPointSpeed = 0;

    private  Matrix4f lookAt = new Matrix4f();

    public Camera(){
        this.yaw = -90;
        this.pitch = 0;
        this.cameraMoveSpeed = 0.3f;
        this.cameraViewPointSpeed = 0.3f;
        this.currentPosition = new Vector3f(0.f, 0.f, 3.f);
        this.centerX = 0;
        this.centerY = 0;
    }

    public Camera(Vector3f position, Tuple<Float, Float> viewCenter, float yaw, float pitch, float cameraMoveSpeed, float cameraViewPointSpeed) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.cameraMoveSpeed = cameraMoveSpeed;
        this.cameraViewPointSpeed = cameraViewPointSpeed;
        this.currentPosition = position;
        this.centerX = viewCenter.x;
        this.centerY = viewCenter.y;
    }

    private void generateMatrix() {
        this.lookAt = new Matrix4f().lookAt(
                this.currentPosition,
                this.currentViewPoint,
                VERTICAL_CAMERA_VECTOR);
    }

    public void update(){
        move();
        rotate();
    }

    public void rotate(){
        Tuple<Float, Float> inputMouse = InputValues.getMousePos();
        float xOffset = inputMouse.x - this.centerX;
        float yOffset = this.centerY - inputMouse.y;

        xOffset *= this.cameraViewPointSpeed;
        yOffset *= this.cameraViewPointSpeed;

        this.yaw   += xOffset;
        this.pitch += yOffset;

        if(this.pitch > 89.0f)
            this.pitch = 89.0f;
        if(this.pitch < -89.0f)
            this.pitch = -89.0f;

        Vector3f direction = new Vector3f();
        direction.x = (float)(Math.cos(Math.toRadians(this.yaw)) * Math.cos(Math.toRadians(this.pitch)));
        direction.y = (float)Math.sin(Math.toRadians(this.pitch));
        direction.z = (float)(Math.sin(Math.toRadians(this.yaw)) * Math.cos(Math.toRadians(this.pitch)));
        this.currentFront = direction.normalize();
        this.currentViewPoint = new Vector3f(this.currentPosition).add(this.currentFront);
        this.generateMatrix();
    }

    public void move(){
        if(InputValues.getStateByKey(GLFW_KEY_W)){
            this.moveByVector(new Vector3f(this.currentViewPoint).sub(this.currentPosition).normalize().mul(this.cameraMoveSpeed));
        }
        if(InputValues.getStateByKey(GLFW_KEY_S)){
            this.moveByVector(new Vector3f(this.currentViewPoint).sub(this.currentPosition).normalize().mul(this.cameraMoveSpeed).mul(-1));
        }
        if(InputValues.getStateByKey(GLFW_KEY_A)){
            Quaternionf q = new Quaternionf();
            q.rotateAxis((float) Math.toRadians(90), 0, 1, 0);
            this.moveByVector(new Vector3f(this.currentViewPoint).sub(this.currentPosition).rotate(q).normalize().mul(this.cameraMoveSpeed));
        }
        if(InputValues.getStateByKey(GLFW_KEY_D)){
            Quaternionf q = new Quaternionf();
            q.rotateAxis((float) Math.toRadians(-90), 0, 1, 0);
            this.moveByVector(new Vector3f(this.currentViewPoint).sub(this.currentPosition).rotate(q).normalize().mul(this.cameraMoveSpeed));
        }
        if(InputValues.getStateByKey(GLFW_KEY_LEFT_SHIFT)){
            this.moveByVector(new Vector3f(0.f, -1.f, 0.f).mul(this.cameraMoveSpeed));
        }
        if(InputValues.getStateByKey(GLFW_KEY_SPACE)){
            this.moveByVector(new Vector3f(0.f, 1.f, 0.f).mul(this.cameraMoveSpeed));
        }
    }

    public  void moveByVector(Vector3f movementVector) {
        this.currentPosition.add(movementVector);
        this.currentViewPoint.add(movementVector);
        this.generateMatrix();
    }

    public  void moveAbsolute(Vector3f position) {
        this.currentPosition = position;
        this.currentViewPoint = position.add(this.currentFront);
        this.generateMatrix();
    }

    public Matrix4f getLookAt() {
        return this.lookAt;
    }

    public Tuple<Float, Float> getViewCenter(){
        return new Tuple<Float, Float>(this.centerX, this.centerY);
    }
}
