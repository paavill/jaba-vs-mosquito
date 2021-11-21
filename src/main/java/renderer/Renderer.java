package renderer;

import main.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.*;
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

    public void deleteObjectsFromRender(ArrayList<ArrayList<Chunk>> toDelete){
        if(toDelete.size() > 0) {
            ArrayList<Chunk> toDeleteC = new ArrayList<>();
            int size = toDelete.size();
            for(int i = 0; i < size; i++){
                ArrayList<Tuple<Integer, Integer[]>> toDeleteVaos = new ArrayList<>();
                toDeleteC = toDelete.get(i);
                toDeleteC.forEach(e -> toDeleteVaos.add(this.objectsToRender.get(e)));
                for (Tuple<Integer, Integer[]> e: toDeleteVaos) {
                    this.chunkRenderer.deleteVAO(e);
                }
                toDeleteC.forEach(e -> this.objectsToRender.remove(e, this.objectsToRender.get(e)));
            }
            toDelete.remove(toDeleteC);
        }
    }

    public void addObjectsToDraw(World world){
        ChunksManager manager = world.getChunksManager();
        ArrayList<Chunk> chunks = manager.getAllChunkToDraw();
        for(int x = 0; x < chunks.size(); x++){
                Chunk chunk = chunks.get(x);
                Tuple<Integer, Integer[]> t = this.chunkRenderer.getVAO(chunk);
                if(t.first > 450){
                    System.out.println(t.first);
                }
                objectsToRender.put(chunk, t);
        }
    }

    public GLCapabilities getCapabilities() {
        return capabilities;
    }

    public void render(World world) throws IOException, InterruptedException {
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