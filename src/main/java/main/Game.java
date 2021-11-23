package main;

import input.InputManager;
import input.KeyBindings;
import org.joml.Vector3f;
import org.lwjgl.glfw.*;
import renderer.Renderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.*;

import static org.lwjgl.glfw.GLFW.*;

public class Game {

    private Window window;
    private InputManager inputManager;
    private KeyBindings bindings;

    private Renderer renderer;
    private Camera camera;

    private int FPS = 60;
    private int msPearFrame = 1000/FPS;
    private double realFps = Double.MAX_VALUE;

    private World world;
    private ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();
    private ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public void run() {
        init();
        try {
            loop();
        } catch (Exception exception) {

        }
        window.destroy();
        world.destroy();
        this.singleThreadPool.shutdown();
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
                -90.0f, -40.0f, 0.1f, 0.3f);

        renderer = new Renderer(window, camera);
        Chunk.setBlocksModels(new HashMap<>(BlocksModelsInitializer.init()));
        world = new World(camera, bindings);
    }

    private void loop() throws IOException, InterruptedException {
        double start;
        double end;
        double delta = 0;
        double er = 0;

        //TODO: Добавить DeltaTime
        while (!window.shouldClose()) {
            System.out.println("0");
            start = GLFW.glfwGetTime();
            System.out.println("1");
            inputManager.handleEvents();
            System.out.println("2");
            float toD = (float) delta;
            System.out.println("3");
            world.getPlayer().getMainCamera().setCameraMoveSpeedPercentOfDefault(toD);
            System.out.println("4");
            world.updateEntity();
            System.out.println("5");


            Runnable task = () -> {
                try {
                    //внутри метода надо менять метод для перехода по вериям генерации

                    world.update();

                } catch (ExecutionException |InterruptedException e) {
                    e.printStackTrace();
                }
            };
            System.out.println("6");
            this.singleThreadPool.submit(task);
            System.out.println("7");

            Runnable task2 = () -> {
                world.generateObjects();
            };
            System.out.println("8");
            for(int i = 0; i < Runtime.getRuntime().availableProcessors() - 5; i++){
                this.threadPool.execute(task2);
            }
            System.out.println("9");

            renderer.addObjectsToDraw(world);
            System.out.println("10");
            //не использовать при работе второй версии загрузки
            //renderer.deleteObjectsFromRender(world);
            renderer.deleteExtraObjectsToDraw(world);
            System.out.println("11");

            renderer.render();
            System.out.println("12");

            window.update(bindings);
            System.out.println("13");
            end = GLFW.glfwGetTime();
            System.out.println("14");

            delta = (end - start)*1000;
            System.out.println("15");
            if(delta < msPearFrame){
                Thread.sleep(msPearFrame - (long)(delta));
            }
            System.out.println("16");

            double sh = GLFW.glfwGetTime() - start;
            this.realFps = Math.min(1000/(sh*1000), this.realFps);
            glfwSetWindowTitle(window.getWindowDescriptor(),"JabaCraft fps:" + String.valueOf(1000/(sh*1000)) + " delta: " + String.valueOf(delta));
            //System.out.print(this.realFps);
            //System.out.print("   ");
            //System.out.println(1000/(sh*1000));
        }
    }
}