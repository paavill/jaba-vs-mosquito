package main;

import input.Controls;
import input.KeyBindings;
import org.lwjgl.glfw.*;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {

    private static final Integer DEFAULT_WIDTH = 800;
    private static final Integer DEFAULT_HEIGHT = 640;

    private final Long windowDescriptor;
    private Tuple extent;
    private boolean close = false;

    public Window(String title, Integer width, Integer height) {
        this.extent = new Tuple<Float, Float>(Float.valueOf(width), Float.valueOf(height));

        glfwDefaultWindowHints();

        this.windowDescriptor = glfwCreateWindow(width, height, title, NULL, NULL);
        if (windowDescriptor == NULL) {
            throw new RuntimeException("Can't create window");
        }

        glfwSetFramebufferSizeCallback(windowDescriptor, this::resizeWindowCallback);

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(windowDescriptor, pWidth, pHeight);

            GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(
                    windowDescriptor,
                    (videoMode.width() - pWidth.get(0)) / 2,
                    (videoMode.height() - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(windowDescriptor);
        glfwSwapInterval(1);

        hideCursor();
    }

    public Window(String title, Tuple<Integer, Integer> size) {
        this(title, size.first, size.second);
    }

    public Window(String title) {
        this(title, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public void swapBuffers() {
        glfwSwapBuffers(this.windowDescriptor);
    }

    public Boolean shouldClose() {
        return glfwWindowShouldClose(this.windowDescriptor);
    }

    public Long getWindowDescriptor() {
        return this.windowDescriptor;
    }

    public void destroy() {
        glfwFreeCallbacks(this.windowDescriptor);
        glfwDestroyWindow(this.windowDescriptor);
    }

    public void showCursor() {
        glfwSetInputMode(windowDescriptor, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
    }

    public void hideCursor() {
        glfwSetInputMode(windowDescriptor, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }

    private void resizeWindowCallback(long window, int width, int height) {
        glViewport(0, 0, width, height);
        this.extent = new Tuple(width, height);
    }

    public Tuple<Float, Float> getExtent() {
        return extent;
    }

    public void update(KeyBindings bindings){
        if(bindings.getState(Controls.CloseWindow)){
            close = true;
            glfwSetWindowShouldClose(this.windowDescriptor, true);
        }
        if(bindings.getState(Controls.SwitchCursor)){
            this.showCursor();
        } else {
            this.hideCursor();
        }
    }
}
