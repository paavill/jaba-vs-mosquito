import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import org.lwjgl.system.windows.WINDOWPLACEMENT;

import java.io.*;
import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Renderer {

    private long window;
    private static int FRAME_TIME_MS = 1000/60;

    public void run() {
        System.out.println("Привет LWJGL " + Version.getVersion() + "!");

        init();
        try {
            loop();
        }catch (IOException | InterruptedException e){
            System.out.println(e.getMessage());
        }

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

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

        glfwSetKeyCallback(window, InputHandler::keyCallBack);

        glfwSetFramebufferSizeCallback(window, InputHandler::resizeWindow);

        glfwSetCursorPosCallback(window, InputHandler::mouseCallBack);

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        //glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }

    private void loop() throws IOException, InterruptedException {
        GL.createCapabilities();

        String shadersCompileExceptionsMessages = "";

        int shaderProgram = GraphicResourceLoader.linkShaderProgram("src/main/java/VERTEX_SHADER.glsl","src/main/java/FRAGMENT_SHADER.glsl");

        shadersCompileExceptionsMessages += "Shader program linking: " + (glGetProgramInfoLog(shaderProgram) == "" ? "OK":"") + "\n";

        System.out.println(shadersCompileExceptionsMessages);

        float vertices[] = {
                -0.5f, -0.5f, 0.5f, // левая нижняя вершина
                0.5f, -0.5f, 0.5f, // правая нижняя вершина
                0.5f,  -0.5f, -0.5f,  // левая верхняя вершина
                -0.5f, -0.5f, -0.5f, // правая верхняя вершина
                -0.5f, 0.5f, 0.5f, // левая нижняя вершина
                0.5f, 0.5f, 0.5f, // правая нижняя вершина
                0.5f,  0.5f, -0.5f,  // левая верхняя вершина
                -0.5f, 0.5f, -0.5f // левая верхняя вершина
        };

        int indexes[] = {
                0,1,4,
                4,5,1,
                1,5,2,
                2,6,5,
                6,2,7,
                7,2,3,
                3,7,4,
                4,3,0,
                0,3,1,
                1,2,3,
                4,7,5,
                5,6,7
        };



        Block bl = new Block(new Vector3f(0,0,0), vertices, indexes);
        int VAO = bl.createVAO();

        double start;
        double end;
        double delta = 0;

        Camera.defaultInit(window);
        glfwSetCursorPos(window, Camera.lastX,Camera.lastY);

        Matrix4f model = new Matrix4f();
        Matrix4f projection = new Matrix4f();
        projection.perspective((float) Math.toRadians(45), 1024.f/768.f, 0.1f,100.f);

        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        int atrPos;

        while (!glfwWindowShouldClose(window)) {
            InputHandler.moveCamera(window);
            start = glfwGetTime();
            glClearColor(
                    (float) Math.abs(Math.sin(glfwGetTime()/10)),
                    (float) Math.abs(Math.cos(glfwGetTime()/10)),
                    (float) Math.abs(Math.sin(glfwGetTime()/10)),
                    0.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            glUseProgram(shaderProgram);

            fb.clear();
            atrPos = glGetUniformLocation(shaderProgram, "model");
            fb = BufferUtils.createFloatBuffer(16);
            glUniformMatrix4fv(atrPos, false, model.get(fb));

            fb.clear();
            fb = BufferUtils.createFloatBuffer(16);
            atrPos = glGetUniformLocation(shaderProgram, "view");

            //Camera.moveAbsolute(new Vector3f((float) Math.sin(start)*5, 0, (float) Math.cos(start)*5));

            glUniformMatrix4fv(atrPos, false, Camera.getLookAt().get(fb));

            fb.clear();
            fb = BufferUtils.createFloatBuffer(16);
            atrPos = glGetUniformLocation(shaderProgram, "projection");
            glUniformMatrix4fv(atrPos, false, projection.get(fb));

            glBindVertexArray(VAO);

            glDrawElements(GL_TRIANGLES, indexes.length, GL_UNSIGNED_INT, 0L);

            //glDrawArrays(GL_TRIANGLES, 0, 3);

            glfwSwapBuffers(window);

            glUseProgram(0);
            glBindVertexArray(0);

            glfwPollEvents();
            end = glfwGetTime();
            delta = start + FRAME_TIME_MS - end;
            if(delta > 0){
                Thread.sleep(Math.round(delta));
            }
        }
    }
}