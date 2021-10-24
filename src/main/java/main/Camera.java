package main;

import input.Controls;
import input.KeyBindings;
import org.joml.*;

import java.lang.Math;


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

    public Camera() {
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
        this.centerX = viewCenter.first;
        this.centerY = viewCenter.second;
    }

    public Matrix4f generateMatrix() {
        return new Matrix4f().lookAt(
                this.currentPosition,
                this.currentViewPoint,
                VERTICAL_CAMERA_VECTOR);
    }

    public void rotate(KeyBindings bindings) {
        if(!bindings.getState(Controls.SwitchCursor)) {
            bindings.setCursorPosition(new Tuple<>(this.centerX, this.centerY));
            Tuple<Float, Float> inputMouse = bindings.getMousePosition();
            float xOffset = inputMouse.first - this.centerX;
            float yOffset = this.centerY - inputMouse.second;

            xOffset *= this.cameraViewPointSpeed;
            yOffset *= this.cameraViewPointSpeed;

            this.yaw += xOffset;
            this.pitch += yOffset;

            if (this.pitch > 89.0f)
                this.pitch = 89.0f;
            if (this.pitch < -89.0f)
                this.pitch = -89.0f;

            Vector3f direction = new Vector3f();
            direction.x = (float) (Math.cos(Math.toRadians(this.yaw)) * Math.cos(Math.toRadians(this.pitch)));
            direction.y = (float) Math.sin(Math.toRadians(this.pitch));
            direction.z = (float) (Math.sin(Math.toRadians(this.yaw)) * Math.cos(Math.toRadians(this.pitch)));
            this.currentFront = direction.normalize();
            this.currentViewPoint = new Vector3f(this.currentPosition).add(this.currentFront);
        }
        bindings.setMousePosition(new Tuple<>(this.centerX, this.centerY));
    }

    public void move(KeyBindings bindings) {
        if (bindings.getState(Controls.Forward)) {
            this.moveByVector(new Vector3f(this.currentViewPoint).sub(this.currentPosition).normalize().mul(this.cameraMoveSpeed));
        }
        if (bindings.getState(Controls.Back)) {
            this.moveByVector(new Vector3f(this.currentViewPoint).sub(this.currentPosition).normalize().mul(this.cameraMoveSpeed).mul(-1));
        }
        if (bindings.getState(Controls.Left)) {
            Quaternionf q = new Quaternionf();
            q.rotateAxis((float) Math.toRadians(90), 0, 1, 0);
            this.moveByVector(new Vector3f(this.currentViewPoint).sub(this.currentPosition).rotate(q).normalize().mul(this.cameraMoveSpeed));
        }
        if (bindings.getState(Controls.Right)) {
            Quaternionf q = new Quaternionf();
            q.rotateAxis((float) Math.toRadians(-90), 0, 1, 0);
            this.moveByVector(new Vector3f(this.currentViewPoint).sub(this.currentPosition).rotate(q).normalize().mul(this.cameraMoveSpeed));
        }
        if (bindings.getState(Controls.Down)) {
            this.moveByVector(new Vector3f(0.f, -1.f, 0.f).mul(this.cameraMoveSpeed));
        }
        if (bindings.getState(Controls.Up)) {
            this.moveByVector(new Vector3f(0.f, 1.f, 0.f).mul(this.cameraMoveSpeed));
        }
    }

    public void moveByVector(Vector3f movementVector) {
        this.currentPosition.add(movementVector);
        this.currentViewPoint.add(movementVector);
    }

    public Tuple<Float, Float> getViewCenter() {
        return new Tuple(this.centerX, this.centerY);
    }
}

