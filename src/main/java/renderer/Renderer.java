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
import java.util.*;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL33.*;

public class Renderer {

    private final Window window;
    private final Camera renderCamera;
    private final MeshRenderer chunkRenderer;
    private final Map<Chunk, Tuple<Integer, Integer[]>> objectsToRender = new HashMap<>();
    private final HashSet<Chunk> toDeleteBuffSet = new HashSet<>();
    private final GLCapabilities capabilities;

    private final LinkedList<Chunk> toDeleteBuff = new LinkedList<>();
    private final LinkedList<Chunk> toUpdateBuff = new LinkedList<>();

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

    public int getToDeleteBuffSize(){
        return this.toDeleteBuff.size();
    }

    public int getToUpdateBuffSize(){
        return this.toUpdateBuff.size();
    }

    public void deleteObjectsFromRender(World world){
        ChunksManager manager = world.getChunksManager();
        this.toDeleteBuff.addAll(manager.getToDeleteChunks());
        double start = 0;
        double end = 0;
        double delta = end - start;
        while (toDeleteBuff.size() > 0 && delta < 4){
            start = GLFW.glfwGetTime();
            Chunk chunk = toDeleteBuff.getFirst();
            synchronized (chunk) {
                Tuple<Integer, Integer[]> toDeleteVaos = this.objectsToRender.get(chunk);
                if (toDeleteVaos != null) {
                    this.chunkRenderer.deleteVAO(toDeleteVaos);
                    this.objectsToRender.remove(chunk, toDeleteVaos);
                }
                this.toDeleteBuff.remove(chunk);
            }
            end = GLFW.glfwGetTime();
            delta += end - start;
        }
    }

    public void  addObjectsToDraw(World world){
        ChunksManager manager = world.getChunksManager();
        this.toUpdateBuff.addAll(manager.getChunksToDraw());
        LinkedList<Chunk> chunks =  world.getChunksManager().getAllChunks();
        for (Chunk e:manager.getChunksToDraw()) {
            if(chunks.indexOf(e) != -1 && this.toUpdateBuff.indexOf(e) == -1 && this.toDeleteBuff.indexOf(e) == -1){
                this.toUpdateBuff.add(e);
            }
        }
        double start = 0;
        double end = 0;
        double delta = end - start;
        while (toUpdateBuff.size() > 0 && delta < 2){
            start = GLFW.glfwGetTime();
            Chunk chunk = toUpdateBuff.getFirst();
            Tuple<Integer, Integer[]> newVao;
            //коммон парт не может быть удалена, тупая идея! может и умная, но все ломается
            if (this.objectsToRender.get(chunk) != null) {
                this.toDeleteBuff.add(chunk);
                //Tuple<Integer, Integer[]> toUpdateVao = this.objectsToRender.get(chunk);
                //this.chunkRenderer.deleteVAO(toUpdateVao);
                //newVao = this.chunkRenderer.getVAO(chunk);
                //this.objectsToRender.replace(chunk, newVao);
                //this.toUpdateBuff.remove(chunk);
            } else {
                newVao = this.chunkRenderer.getVAO(chunk);
                objectsToRender.put(chunk, newVao);
                this.toUpdateBuff.remove(chunk);
            }
            chunk.setAddedToRender(true);
            this.toUpdateBuff.remove(chunk);

            end = GLFW.glfwGetTime();
            delta += end - start;
        }
    }

    public void deleteExtraObjectsToDraw(World world){
        System.out.println("1.1");
        int count = world.getChunksManager().getRenderDistance();
        System.out.println("1.2");
        int toGenSize = world.getChunksManager().getToGenerate().size();
        System.out.println("1.3");
        count *= count;
        System.out.println("1.4");
        LinkedList<Chunk> chunks =  world.getChunksManager().getAllChunks();
        System.out.println("1.5");
        for (Chunk e:this.objectsToRender.keySet()) {
            if(chunks.indexOf(e) == -1){
                this.toDeleteBuff.add(e);
            }
        }
        System.out.println("1.6");
        while (this.objectsToRender.size() > count && this.toDeleteBuff.size() > 0 /*&& toGenSize == 0 && this.toUpdateBuff.size() == 0*/){
            Chunk chunk = this.toDeleteBuff.getFirst();

            synchronized (chunk) {
                if(chunk != null) {
                    if (chunk.isAddedToRender()) {
                        Tuple<Integer, Integer[]> vaoToDel = this.objectsToRender.get(chunk);
                        if (vaoToDel != null) {
                            this.objectsToRender.remove(chunk);
                            this.chunkRenderer.deleteVAO(vaoToDel);
                        }
                    }
                }
                toDeleteBuff.remove(chunk);
            }
            toGenSize = world.getChunksManager().getToGenerate().size();
        }
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

        window.swapBuffers();

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    }
}