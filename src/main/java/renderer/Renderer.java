package renderer;

import main.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.*;
import org.lwjgl.opengl.GL;

import java.io.*;
import java.nio.*;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.glfw.GLFW.*;

public class Renderer {

    private final Window window;
    private final Camera renderCamera;

    private final Matrix4f projection;

    private int chunkShaderProgram;
    private Texture texture;

    public Renderer(Window window, Camera renderCamera) {
        this.window = window;
        GL.createCapabilities();
        this.renderCamera = renderCamera;
        this.projection = new Matrix4f().perspective((float) Math.toRadians(77), 1024.f / 768.f, 0.1f, 1000.f);
        glEnable(GL_DEPTH_TEST);
        try {
            this.chunkShaderProgram = GraphicResourceLoader.linkShaderProgram("VERTEX_SHADER.glsl", "FRAGMENT_SHADER.glsl", "shaders/");
            //this.texture = GraphicResourceLoader.loadTexture("blocks.png", "/");
            //создание текстурного астласа должно быть здесь, но в силу плохой архитекруты
            //загрузки текстур (со стороны paavill), создание пока что не тут.
        } catch (Exception ex) {

        }
    }

    public void render(World world) throws IOException, InterruptedException {
        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        int atrPos;

        MeshRenderer.setShaderProgram(chunkShaderProgram);

        glUseProgram(chunkShaderProgram);

        fb.clear();
        atrPos = glGetUniformLocation(chunkShaderProgram, "view");
        glUniformMatrix4fv(atrPos, false, renderCamera.generateMatrix().get(fb));

        fb.clear();
        atrPos = glGetUniformLocation(chunkShaderProgram, "projection");
        glUniformMatrix4fv(atrPos, false, projection.get(fb));

        atrPos = glGetUniformLocation(chunkShaderProgram, "lightPos");
        Vector3f vec = renderCamera.getCurrentPosition();
        glUniform3f(atrPos, vec.x, vec.y, vec.z);

        BlocksModelsInitializer.getTextureAtlas().bind();
        MeshRenderer.drawAll();

        glUseProgram(0);
        glClearColor(0.0f, 0.749f, 1.f, 0.0f);
        window.swapBuffers();

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    }
}