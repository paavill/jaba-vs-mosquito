import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Renderer {

    private long window;

    public void run() {
        System.out.println("Привет LWJGL " + Version.getVersion() + "!");

        init();
        loop();

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
        //вынести лямбу в InputHandler
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
            }
        });

        glfwSetFramebufferSizeCallback(window, InputHandler::resizeWindow);

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
    }

    private void loop() {
        GL.createCapabilities();

        String shadersCompileExceptionsMessages = "";
        int logMessage[] = new int[1];
        CharSequence sourceVertexShader = new CharSequence() {
            @Override
            public int length() {
                return 0;
            }

            @Override
            public char charAt(int index) {
                return 0;
            }

            @Override
            public CharSequence subSequence(int start, int end) {
                return null;
            }
        };
        CharSequence sourceFragmentShader = new CharSequence() {
            @Override
            public int length() {
                return 0;
            }

            @Override
            public char charAt(int index) {
                return 0;
            }

            @Override
            public CharSequence subSequence(int start, int end) {
                return null;
            }
        };

        sourceVertexShader = "#version 330 core\n" +
                "layout (location = 0) in vec3 aPos;\n" +
                " \n" +
                "void main()\n" +
                "{\n" +
                "    gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);\n" +
                "}";

        sourceFragmentShader = "#version 330 core\n" +
                "out vec4 FragColor;\n" +
                "void main()\n"+
                "{\n" +
                "   FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);\n"+
                "}\n\0";

        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, sourceVertexShader);
        glCompileShader(vertexShader);

        shadersCompileExceptionsMessages += "Vertex shader compiling: " + glGetShaderInfoLog(vertexShader) + "\n";

        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, sourceFragmentShader);
        glCompileShader(fragmentShader);

        shadersCompileExceptionsMessages += "Fragment shader compiling: " + glGetShaderInfoLog(fragmentShader) + "\n";

        int shederProgram = glCreateProgram();
        glAttachShader(shederProgram, vertexShader);
        glAttachShader(shederProgram, fragmentShader);
        glLinkProgram(shederProgram);

        shadersCompileExceptionsMessages += "Shader program linking: " + glGetProgramInfoLog(shederProgram) + "\n";

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        glUseProgram(shederProgram);

        System.out.println(shadersCompileExceptionsMessages);

        float vertices[] = {
                -0.5f, -0.6f, 0.0f, // левая вершина
                0.5f, -0.5f, 0.0f, // правая вершина
                0.4f,  0.5f, 0.0f  // верхняя вершина
        };

        int VBO[] = new int[1];
        int VAO[] = new int[1];
        glGenBuffers(VBO);
        glGenVertexArrays(VAO);

        glBindVertexArray(VAO[0]);
        glBindBuffer(GL_ARRAY_BUFFER, VBO[0]);
        glBufferData(GL_ARRAY_BUFFER,vertices,GL_STATIC_DRAW);

        glVertexAttribPointer(0,3,GL_FLOAT,false,0, 0);
        glEnableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER,0);
        glBindVertexArray(0);

        while (!glfwWindowShouldClose(window)) {
            glClearColor(
                    (float) Math.abs(Math.sin(glfwGetTime())),
                    (float) Math.abs(Math.cos(glfwGetTime())),
                    (float) Math.abs(Math.sin(2.0 * glfwGetTime())),
                    0.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            glBindVertexArray(VAO[0]); // поскольку у нас есть только один VAO, то нет необходимости связывать его каждый раз (но мы сделаем это, чтобы всё было немного организованнее)
            glDrawArrays(GL_TRIANGLES, 0, 3);
            glfwSwapBuffers(window);

            glfwPollEvents();
        }
    }
}