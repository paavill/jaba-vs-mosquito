import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.io.*;
import java.nio.*;
import java.util.ArrayList;
import java.util.Collection;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Renderer {

    private long window;
    private static final int FRAME_TIME_MS = 1000 / 60;

    public void run() {
        System.out.println("Привет LWJGL " + Version.getVersion() + "!");

        init();
        try {
            loop();
        } catch (IOException | InterruptedException e) {
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

        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }

    private void loop() throws IOException, InterruptedException {
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);

        String shadersCompileExceptionsMessages = "";

        int shaderProgram = GraphicResourceLoader.linkShaderProgram("src/main/java/VERTEX_SHADER.glsl", "src/main/java/FRAGMENT_SHADER.glsl");

        shadersCompileExceptionsMessages += "Shader program linking: " + (glGetProgramInfoLog(shaderProgram) == "" ? "OK" : "") + "\n";

        System.out.println(shadersCompileExceptionsMessages);

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        glfwGetWindowSize(window, width, height);

        Camera camera = new Camera(new Vector3f(0f, 0f, 3.f),
                new Tuple<Float, Float>((float) width.get() / 2, (float) height.get() / 2),
                -90, 0, 0.3f, 0.3f);

        InputValues.setMousePos(camera.getViewCenter().x, camera.getViewCenter().y);


        Collection<Chunk> chunks = new ArrayList<Chunk>();
        for (int x = 0; x < 7; x++) {
            for (int z = 0; z < 7; z++) {
                chunks.add(new Chunk(new Vector3f(64 * x, 0, 64 * z)));
            }
        }

        chunks.forEach(chunk -> chunk.generate());
        //пофиксить чрезмерную трату памяти в следующем методе
        chunks.forEach(chunk -> chunk.genBlocksMash());

        chunks.forEach(chunk -> MashRenderer.addObjectToDraw(chunk));
        System.gc();

        double start;
        double end;
        double delta = 0;

        Matrix4f projection = new Matrix4f();
        projection.perspective((float) Math.toRadians(77), 1024.f / 768.f, 0.1f, 1000.f);

        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        int atrPos;

        MashRenderer.setShaderProgram(shaderProgram);
        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        while (!glfwWindowShouldClose(window)) {

            start = glfwGetTime();

            if (!InputValues.getStateByKey(GLFW_KEY_LEFT_CONTROL)) {
                camera.update();
                glfwSetCursorPos(window, camera.getViewCenter().x, camera.getViewCenter().y);
                InputValues.setMousePos(camera.getViewCenter().x, camera.getViewCenter().y);
                glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
            } else {
                glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            }

            glUseProgram(shaderProgram);

            fb.clear();
            atrPos = glGetUniformLocation(shaderProgram, "view");
            glUniformMatrix4fv(atrPos, false, camera.getLookAt().get(fb));

            fb.clear();
            atrPos = glGetUniformLocation(shaderProgram, "projection");
            glUniformMatrix4fv(atrPos, false, projection.get(fb));

            atrPos = glGetUniformLocation(shaderProgram, "lightPos");
            glUniform3f(atrPos, 3, 20, 3);

            MashRenderer.drawAll();

            glUseProgram(0);
            glClearColor(0.0f, 0.749f, 1.f, 0.0f);
            glfwSwapBuffers(window);

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glfwPollEvents();

            end = glfwGetTime();
            delta = start + FRAME_TIME_MS - end;
            if (delta > 0) {
                Thread.sleep(Math.round(delta));
            }
        }
    }
}