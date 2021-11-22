package main;

import input.InputManager;
import input.KeyBindings;
import org.joml.Vector3f;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;
import renderer.Renderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.*;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glEnable;

public class Game {

    private Window window;
    private InputManager inputManager;
    private KeyBindings bindings;

    private Renderer renderer;
    private Camera camera;

    private World world;
    private ExecutorService threadPool = Executors.newSingleThreadExecutor();

    public void run() {
        init();
        try {
            loop();
        } catch (Exception exception) {

        }
        window.destroy();
        world.destroy();
        this.threadPool.shutdown();
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Failed to init GLFW.");
        }

        window = new Window("JabaCraft", 1024, 768);
        inputManager = new InputManager(window);
        bindings = new KeyBindings(inputManager);

        Tuple<Float, Float> center = new Tuple<Float, Float>(window.getExtent().first / 2,  window.getExtent().second / 2);

        camera = new Camera(
                new Vector3f(0f, 0f, 0f),
                center,
                -90.0f, -40.0f, 0.3f, 0.3f);

        renderer = new Renderer(window, camera);
        Chunk.setBlocksModels(new HashMap<>(BlocksModelsInitializer.init()));
        world = new World(camera, bindings);

        renderer.addObjectsToDraw(world);
    }

    private void loop() throws IOException, InterruptedException {

        //TODO: Добавить DeltaTime
        while (!window.shouldClose()) {

            inputManager.handleEvents();
            world.updateEntity();

            Runnable task = () -> {
                try {
                    world.update();
                } catch (ExecutionException |InterruptedException e) {
                    e.printStackTrace();
                }
            };
            this.threadPool.submit(task);


            renderer.addObjectsToDraw(world);
            renderer.deleteObjectsFromRender(world);

            renderer.render();

            float percent = (float)renderer.getToUpdateBuffSize()/600;
            if(1 - percent < 1) {
           //     world.getPlayer().getMainCamera().setCameraMoveSpeedPercentOfDefault(1 - percent);
            }

            window.update(bindings);


        }
    }
}