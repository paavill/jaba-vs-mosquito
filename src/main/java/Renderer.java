import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import org.lwjgl.system.windows.WINDOWPLACEMENT;

import java.io.*;
import java.nio.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Renderer {

    private long window;
    private static int FRAME_TIME_MS = 1000 / 60;

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

        //  Arrays.copyOfRange()
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        glfwGetWindowSize(window, width, height);

        Camera camera = new Camera(new Vector3f(0f, 0f, 3.f),
                                    new Tuple<Float, Float>((float) width.get() / 2, (float) height.get() / 2),
                                    -90, 0, 0.3f, 0.3f);

        InputValues.setMousePos(camera.getViewCenter().x, camera.getViewCenter().y);


        Collection<Chank> chanks = new ArrayList<Chank>();
        for(int x = 0; x < 1; x++){
            for(int z = 0; z < 1; z++){
                chanks.add(new Chank(new Vector3f(32*x,0,32*z)));
            }
        }

       chanks.forEach(chank -> chank.generate());

        double start;
        double end;
        double delta = 0;

        Matrix4f projection = new Matrix4f();
        projection.perspective((float) Math.toRadians(77), 1024.f / 768.f, 0.1f, 100.f);

        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        int atrPos;

        while (!glfwWindowShouldClose(window)) {

            start = glfwGetTime();

            camera.update();

            glfwSetCursorPos(window, camera.getViewCenter().x, camera.getViewCenter().y);
            InputValues.setMousePos(camera.getViewCenter().x, camera.getViewCenter().y);

            glUseProgram(shaderProgram);

            fb.clear();
            atrPos = glGetUniformLocation(shaderProgram, "view");
            glUniformMatrix4fv(atrPos, false, camera.getLookAt().get(fb));

            fb.clear();
            atrPos = glGetUniformLocation(shaderProgram, "projection");
            glUniformMatrix4fv(atrPos, false, projection.get(fb));

            atrPos = glGetUniformLocation(shaderProgram, "lightPos");
            glUniform3f(atrPos, 3, 10, 3);


            chanks.forEach(chank -> chank.draw(shaderProgram));

            //glDrawElements(GL_TRIANGLES, indexes.length, GL_UNSIGNED_INT, 0L);

            glUseProgram(0);
            glClearColor(0.1f, 0.1f, 0.1f, 0.0f);
            glfwSwapBuffers(window);

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glfwPollEvents();

            double d[] = new double[1];
            //glGetVertexAttribdv(0, GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING	,d);

            //System.out.println(d[0]);
            end = glfwGetTime();
            delta = start + FRAME_TIME_MS - end;
            if (delta > 0) {
                //Thread.sleep(Math.round(delta));
            }
        }
    }
}