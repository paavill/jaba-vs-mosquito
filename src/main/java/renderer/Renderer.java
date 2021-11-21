package renderer;

import main.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

import java.io.*;
import java.nio.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.glfw.GLFW.*;

public class Renderer {

    private final Window window;
    private final Camera renderCamera;
    private final MeshRenderer chunkRenderer;
    private final Map<Chunk, Tuple<Integer, Integer[]>> objectsToRender = new HashMap<>();
    private final GLCapabilities capabilities;

    private final ArrayList<Chunk> toDeleteBuff = new ArrayList<>();
    private final ArrayList<Chunk> toUpdateBuff = new ArrayList<>();

    private final Matrix4f projection;

    private int chunkShaderProgram;
    private Texture texture;

    public Renderer(Window window, Camera renderCamera) {
        this.window = window;
        this.capabilities = GL.createCapabilities();
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
        this.chunkRenderer = new MeshRenderer(this.chunkShaderProgram);
    }

    public void deleteObjectsFromRender(World world){
        ChunksManager manager = world.getChunksManager();
        this.toDeleteBuff.addAll(manager.getToDeleteChunks());
        double start = org.lwjgl.glfw.GLFW.glfwGetTime();
        while (toDeleteBuff.size() > 0){
            Chunk toDeleteC = toDeleteBuff.get(toDeleteBuff.size() - 1);
            synchronized (toDeleteC) {
                Tuple<Integer, Integer[]> toDeleteVaos = this.objectsToRender.get(toDeleteC);

                if (toDeleteVaos != null) {
                    this.chunkRenderer.deleteVAO(toDeleteVaos);
                    this.objectsToRender.remove(toDeleteC, toDeleteVaos);
                }
                this.toDeleteBuff.remove(toDeleteC);
            }
        }
    }

    public void addObjectsToDraw(World world){
        ChunksManager manager = world.getChunksManager();
        this.toUpdateBuff.addAll(manager.getAllChunkToDraw());
        while (toUpdateBuff.size() > 0){
            Chunk chunk = toUpdateBuff.get(this.toUpdateBuff.size() - 1);
            synchronized (chunk) {
                Tuple<Integer, Integer[]> t = this.chunkRenderer.getVAO(chunk);
                if (t.first > 450) {
                    System.out.println(t.first);
                }
                if (toDeleteBuff.indexOf(chunk) == -1) {
                    if (this.objectsToRender.get(chunk) != null) {
                        Tuple<Integer, Integer[]> toUpdate = this.objectsToRender.get(chunk);
                        this.chunkRenderer.deleteVAO(toUpdate);
                        this.objectsToRender.replace(chunk, t);
                    } else {
                        objectsToRender.put(chunk, t);
                    }
                    this.toUpdateBuff.remove(chunk);
                } else {
                    if (this.objectsToRender.get(chunk) != null) {
                        Tuple<Integer, Integer[]> toUpdate = this.objectsToRender.get(chunk);
                        this.chunkRenderer.deleteVAO(toUpdate);
                        this.toDeleteBuff.remove(chunk);
                    }
                }
            }
        }
    }

    public GLCapabilities getCapabilities() {
        return capabilities;
    }

    public void render() throws IOException, InterruptedException {
        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        int atrPos;

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


        this.chunkRenderer.drawAll(this.objectsToRender);

        glUseProgram(0);
        glClearColor(0.0f, 0.749f, 1.f, 0.0f);
        //glfwMakeContextCurrent(window.getWindowDescriptor());
        window.swapBuffers();
        //glfwMakeContextCurrent(0);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    }
}