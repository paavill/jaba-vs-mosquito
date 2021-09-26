import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Game {

    private long window;

    private static final double MS_PER_FRAME = 1000.0 / 60.0;

    public void run() {
        init();
        cycle();

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Не удалось иницилизировать GLFW.");
        }

        glfwDefaultWindowHints();

        window = glfwCreateWindow(1024, 768, "Hello, World!", NULL, NULL);

        if (window == NULL) {
            throw new RuntimeException("Не удалось создать окно!");
        }

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
            }
        });

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
    }

    private void cycle() {
        GL.createCapabilities();

        while (!glfwWindowShouldClose(window)) {

            glfwPollEvents();

            double start_time = glfwGetTime();

            glClearColor(
                    (float) Math.abs(Math.sin(start_time)),
                    (float) Math.abs(Math.cos(start_time)),
                    (float) Math.abs(Math.sin(2.0 * start_time)),
                    0.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            glfwSwapBuffers(window);
            try {
                Thread.sleep(Math.round(start_time + MS_PER_FRAME - glfwGetTime()));
            } catch (Exception error) {

            }
        }
    }
}