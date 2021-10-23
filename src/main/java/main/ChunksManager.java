package main;

import org.joml.Vector3f;
import renderer.MeshRenderer;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ChunksManager implements Runnable {

    private static int RENDER_DISTANCE;

    private static final int CHUNK_SIZE_X = 16;
    private static final int CHUNK_SIZE_Z = 16;
    private static final int CHUNK_SIZE_Y = 256;

    private int currentChunkPositionX;
    private int currentChunkPositionZ;

    private static final ArrayList<ArrayList<Chunk>> chunks = new ArrayList<>();

    private static ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 4);

    public ChunksManager(int currentChunkPositionX, int currentChunkPositionZ) {
        this.currentChunkPositionX = currentChunkPositionX;
        this.currentChunkPositionZ = currentChunkPositionZ;
    }

    public static void setRenderDistance(int renderDistance) {
        ChunksManager.RENDER_DISTANCE = renderDistance;
    }

    private static void createChunksInstances(){
        for (int x = 0; x < ChunksManager.RENDER_DISTANCE; x++) {
            chunks.add(new ArrayList<Chunk>());
            for (int z = 0; z < ChunksManager.RENDER_DISTANCE; z++) {
                chunks.get(x).add(
                        new Chunk(new Vector3f(x * CHUNK_SIZE_X, 0, z* CHUNK_SIZE_Z),
                                CHUNK_SIZE_X, CHUNK_SIZE_Y, CHUNK_SIZE_Z));
            }
        }
    }

    public static void addAllChunksToDraw(){
        for (int x = 0; x < ChunksManager.RENDER_DISTANCE; x++) {
            for (int z = 0; z < ChunksManager.RENDER_DISTANCE; z++) {
                MeshRenderer.addObjectToDraw(chunks.get(x).get(z));
            }
        }
    }

    public static void generateChunks() throws InterruptedException {
        ChunksManager.createChunksInstances();
        for (int x = 0; x < ChunksManager.RENDER_DISTANCE; x++) {
            for (int z = 0; z < ChunksManager.RENDER_DISTANCE; z++) {
                threadPool.execute(new ChunksManager(x, z));
            }
        }
        threadPool.shutdown();
        threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
    }

    @Override
    public void run() {
        synchronized (chunks) {
            chunks.get(currentChunkPositionX).get(currentChunkPositionZ).generate();
            chunks.get(currentChunkPositionX).get(currentChunkPositionZ).genBlocksMash();
        }
    }
}
