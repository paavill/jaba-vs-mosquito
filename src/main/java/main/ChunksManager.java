package main;

import game_objects.blocks.BlockType;
import org.joml.Vector3f;
import renderer.MeshRenderer;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ChunksManager {

    private int renderDistance;

    private static final int CHUNK_SIZE_X = 16;
    private static final int CHUNK_SIZE_Z = 16;
    private static final int CHUNK_SIZE_Y = 256;

    private static final ArrayList<ArrayList<Chunk>> chunks = new ArrayList<>();

    private static ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 4);

    public ChunksManager(int renderDistance) {
        this.renderDistance = renderDistance;
    }

    public ArrayList<ArrayList<Chunk>> getAllChunks(){
        return this.chunks;
    }

    public Chunk getChunkByGlobalCoords(float x, float z){
        return chunks.get((int)x/CHUNK_SIZE_X).get((int)z/CHUNK_SIZE_Z);
    }

    private void createChunksInstances(){
        for (int x = 0; x < this.renderDistance; x++) {
            chunks.add(new ArrayList<Chunk>());
            for (int z = 0; z < this.renderDistance; z++) {
                chunks.get(x).add(
                        new Chunk(new Vector3f(x * CHUNK_SIZE_X, 0, z* CHUNK_SIZE_Z),
                                CHUNK_SIZE_X, CHUNK_SIZE_Y, CHUNK_SIZE_Z));
            }
        }
    }

    public void addAllChunksToDraw(){
        for (int x = 0; x < this.renderDistance; x++) {
            for (int z = 0; z < this.renderDistance; z++) {
                MeshRenderer.addObjectToDraw(chunks.get(x).get(z));
            }
        }
    }

    public void generateChunks() throws InterruptedException {
        this.createChunksInstances();
        for (int x = 0; x < this.renderDistance; x++) {
            for (int z = 0; z < this.renderDistance; z++) {
                final int _x = x;
                final int _z = z;
                Runnable runnable = () -> {
                    chunks.get(_x).get(_z).generate();
                    if(_x == 0 && _z == 1){
                        this.getChunkByGlobalCoords(0.23f, 16.f).setAllBloksType(BlockType.AIR);
                    }
                    chunks.get(_x).get(_z).genBlocksMash();
                };
                threadPool.execute(runnable);
            }
        }
        threadPool.shutdown();
        threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 4);

    }
}
