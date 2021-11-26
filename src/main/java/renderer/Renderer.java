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

import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
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

    private Matrix4f projection;

    private int chunkShaderProgram;
    private Texture texture;

    private int depthShader;
    private ShadowMap shadowMap;
    private Light light;

    public Renderer(Window window, Camera renderCamera) {
        this.window = window;
        this.capabilities = GL.createCapabilities();
        glfwSetWindowSizeCallback(window.getWindowDescriptor(), window::resizeWindowCallback);
        this.renderCamera = renderCamera;
        this.projection = new Matrix4f().perspective((float) Math.toRadians(77), 1024.f / 768.f, 0.1f, 1000.f);
        glEnable(GL_DEPTH_TEST);
        try {
            this.chunkShaderProgram = GraphicResourceLoader.linkShaderProgram("VERTEX_SHADER.glsl", "FRAGMENT_SHADER.glsl", "shaders/");
            System.out.println("Chunk:\n"+glGetProgramInfoLog(this.chunkShaderProgram));
            this.depthShader = GraphicResourceLoader.linkShaderProgram("shadow_depth_vs.glsl", "shadow_depth_fs.glsl", "shadow_depth_gs.glsl", "shaders/");
            System.out.println("Depth:\n"+glGetProgramInfoLog(this.depthShader));
            this.texture = GraphicResourceLoader.loadTexture("blocks.png", "");
            //создание текстурного астласа должно быть здесь, но в силу плохой архитекруты
            //загрузки текстур (со стороны paavill), создание пока что не тут.
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.chunkRenderer = new MeshRenderer(this.chunkShaderProgram);
        this.shadowMap = new ShadowMap();
        this.shadowMap.init(window.getExtent().first.intValue(), window.getExtent().second.intValue());
        this.light = new Light(renderCamera.getCurrentPosition());
    }

    public int getToDeleteBuffSize(){
        return this.toDeleteBuff.size();
    }

    public int getToUpdateBuffSize(){
        return this.toUpdateBuff.size();
    }

    public GLCapabilities getCapabilities() {
        return capabilities;
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
        int count = world.getChunksManager().getRenderDistance();
        int toGenSize = world.getChunksManager().getToGenerate().size();
        count *= count;
        LinkedList<Chunk> chunks =  world.getChunksManager().getAllChunks();
        for (Chunk e:this.objectsToRender.keySet()) {
            if(chunks.indexOf(e) == -1){
                this.toDeleteBuff.add(e);
            }
        }
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
        this.light.setPosition(new Vector3f(this.renderCamera.getCurrentPosition()).add(new Vector3f(this.renderCamera.getCurrentFront()).normalize().mul(10.f)));
        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        int atrPos;

        glClearColor(0.0f, 0.749f, 1.f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        Matrix4f shadowProj = new Matrix4f().perspective((float) (Math.PI / 2), ((float)shadowMap.getWidth())/shadowMap.getHeight(), 1.0f, 600.0f);
        List<Matrix4f> shadowTransforms = new ArrayList<>();
        shadowTransforms.add(new Matrix4f(shadowProj).mul(new Matrix4f().lookAt(
                light.getPosition(),
                light.getPosition().add(new Vector3f(1.0f, 0.0f, 0.0f)),
                new Vector3f(0.0f, -1.0f, 0.0f))));

        shadowTransforms.add(new Matrix4f(shadowProj).mul(new Matrix4f().lookAt(
                light.getPosition(),
                light.getPosition().add(new Vector3f(-1.0f, 0.0f, 0.0f)),
                new Vector3f(0.0f, -1.0f, 0.0f))));

        shadowTransforms.add(new Matrix4f(shadowProj).mul(new Matrix4f().lookAt(
                light.getPosition(),
                light.getPosition().add(new Vector3f(0.0f, 1.0f, 0.0f)),
                new Vector3f(0.0f, 0.0f, 1.0f))));

        shadowTransforms.add(new Matrix4f(shadowProj).mul(new Matrix4f().lookAt(
                light.getPosition(),
                light.getPosition().add(new Vector3f(0.0f, -1.0f, 0.0f)),
                new Vector3f(0.0f, 0.0f, -1.0f))));

        shadowTransforms.add(new Matrix4f(shadowProj).mul(new Matrix4f().lookAt(
                light.getPosition(),
                light.getPosition().add(new Vector3f(0.0f, 0.0f, 1.0f)),
                new Vector3f(0.0f, -1.0f, 0.0f))));

        shadowTransforms.add(new Matrix4f(shadowProj).mul(new Matrix4f().lookAt(
                light.getPosition(),
                light.getPosition().add(new Vector3f(0.0f, 0.0f, -1.0f)),
                new Vector3f(0.0f, -1.0f, 0.0f))));

        glBindFramebuffer(GL_FRAMEBUFFER, this.shadowMap.getFbo());
        glViewport(0, 0, this.shadowMap.getWidth(), this.shadowMap.getHeight());
        glClear(GL_DEPTH_BUFFER_BIT);
        glUseProgram(depthShader);
        this.chunkRenderer.setShaderProgram(depthShader);
        for (int i = 0; i < 6; ++i) {
            atrPos = glGetUniformLocation(depthShader, "shadowMatrices[" +  i + "]");
            fb.clear();
            glUniformMatrix4fv(atrPos, false, shadowTransforms.get(i).get(fb));
        }
        atrPos = glGetUniformLocation(depthShader, "far_plane");
        glUniform1f(atrPos, 600.0f);
        atrPos = glGetUniformLocation(depthShader, "lightPos");
        glUniform3f(atrPos, this.light.getPosition().x, this.light.getPosition().y, this.light.getPosition().z);

        renderScene();

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glUseProgram(0);
        this.chunkRenderer.setShaderProgram(0);

        glUseProgram(chunkShaderProgram);
        glViewport(0, 0, this.window.getExtent().first.intValue(), this.window.getExtent().second.intValue());
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        this.chunkRenderer.setShaderProgram(chunkShaderProgram);
        atrPos = glGetUniformLocation(chunkShaderProgram, "view");
        glUniformMatrix4fv(atrPos, false, renderCamera.generateMatrix().get(fb));
        Float w = this.window.getExtent().first;
        Float h = this.window.getExtent().second;
        this.projection = new Matrix4f().perspective((float) Math.toRadians(77),
                w / h, 0.1f, 1000.f);
        fb.clear();
        atrPos = glGetUniformLocation(chunkShaderProgram, "projection");
        glUniformMatrix4fv(atrPos, false, projection.get(fb));

        atrPos = glGetUniformLocation(chunkShaderProgram, "lightPos");
        glUniform3f(atrPos, this.light.getPosition().x, this.light.getPosition().y, this.light.getPosition().z);

        atrPos = glGetUniformLocation(chunkShaderProgram, "viewPos");
        glUniform3f(atrPos, this.renderCamera.getCurrentPosition().x, this.renderCamera.getCurrentPosition().y, this.renderCamera.getCurrentPosition().z);

        atrPos = glGetUniformLocation(chunkShaderProgram, "far_plane");
        glUniform1f(atrPos, 600.0f);


        glUniform1i(glGetUniformLocation(chunkShaderProgram, "ourTexture"), 0);
        int i = glGetUniformLocation(chunkShaderProgram, "depthMap");
        glUniform1i(i, 1);

        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_CUBE_MAP, this.shadowMap.getDepthCubemap());
        glActiveTexture(GL_TEXTURE0);
        this.texture.bind();

        renderScene();

        glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
        this.texture.unBind();
        glUseProgram(0);
        this.chunkRenderer.setShaderProgram(0);

        window.swapBuffers();
    }

    private void renderScene() {
        this.chunkRenderer.drawAll(this.objectsToRender);
    }
}