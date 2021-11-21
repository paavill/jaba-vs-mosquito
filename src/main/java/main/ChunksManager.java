package main;

import game_objects.blocks.BlockType;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.lwjgl.glfw.*;

public class ChunksManager {

    private ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private ArrayList<ArrayList<Chunk>> toDeleteChunks = new ArrayList<>();
    private ArrayList<ArrayList<Chunk>> toUpdateMeshChunks = new ArrayList<>();

    private int renderDistance;
    private static final int CHUNK_SIZE_X = 16;
    private static final int CHUNK_SIZE_Z = 16;
    private static final int CHUNK_SIZE_Y = 256;

    private Vector3f playerPosition = new Vector3f(0, 0 ,0);
    private final LinkedList<LinkedList<Chunk>> chunks = new LinkedList<>();


    public ChunksManager(int renderDistance) {
        this.renderDistance = renderDistance;
    }

    public LinkedList<LinkedList<Chunk>> getAllChunks(){
        return this.chunks;
    }

    public ArrayList<Chunk> getAllChunkToDraw(){
        ArrayList<Chunk> toDraw = new ArrayList<>();
        synchronized (this.chunks) {

            for (int x = 0; x < chunks.size(); x++) {
                for (int z = 0; z < chunks.get(0).size(); z++) {
                    if (chunks.get(x).get(z).isChanged() && chunks.get(x).get(z).isFinishChanged()) {
                        Chunk chunk = chunks.get(x).get(z);
                        toDraw.add(chunk);
                        chunk.setChanged(false);
                    }
                }
            }
        }
        return toDraw;
    }

    public ArrayList<ArrayList<Chunk>> getToUpdateMeshChunks() {
        return toUpdateMeshChunks;
    }

    public Chunk getChunkByGlobalCoords(float x, float z){
        int leftChunkCalcPos = (int)this.playerPosition.x/CHUNK_SIZE_X - this.renderDistance/2;
        int farChunkCalcPos = (int)this.playerPosition.z/CHUNK_SIZE_Z - this.renderDistance/2;
        return chunks.get((int)x/CHUNK_SIZE_X - leftChunkCalcPos).get((int)z/CHUNK_SIZE_Z - farChunkCalcPos);
    }

    private void createChunksInstances(){
        int leftChunkCalcPos = (int)this.playerPosition.x/CHUNK_SIZE_X - this.renderDistance/2;
        int farChunkCalcPos = (int)this.playerPosition.z/CHUNK_SIZE_Z - this.renderDistance/2;
        for (int x = 0; x < this.renderDistance; x++) {
            chunks.add(new LinkedList<>());
            for (int z = 0; z < this.renderDistance; z++) {
                chunks.get(x).add(
                        new Chunk(new Vector3f((x + leftChunkCalcPos ) * CHUNK_SIZE_X, 0,
                                (z + farChunkCalcPos)* CHUNK_SIZE_Z),
                                CHUNK_SIZE_X, CHUNK_SIZE_Y, CHUNK_SIZE_Z));
            }
        }
    }

    public void modelsGeneration(final int _x, final int _z){
        if(_x != 0 && _x != this.renderDistance - 1 && _z != 0 && _z != this.renderDistance -1){
            chunks.get(_x).get(_z).genBlocksMash(chunks.get(_x - 1).get(_z),
                    chunks.get(_x + 1).get(_z),
                    chunks.get(_x).get(_z - 1), chunks.get(_x).get(_z + 1));
        } else {
            Chunk left = null;
            Chunk right = null;
            Chunk far = null;
            Chunk near = null;
            if (_x != 0) {
                left = chunks.get(_x - 1).get(_z);
            }
            if (_z != 0) {
                far = chunks.get(_x).get(_z - 1);
            }
            if (_x != this.renderDistance - 1) {
                right = chunks.get(_x + 1).get(_z);
            }
            if (_z != this.renderDistance - 1) {
                near = chunks.get(_x).get(_z + 1);
            }
            chunks.get(_x).get(_z).genBlocksMash(
                    left,
                    right,
                    far,
                    near);
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
                };
                threadPool.execute(runnable);
            }
        }

        threadPool.shutdown();
        threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.getChunkByGlobalCoords(0.f, 0.f).setAllBloksType(BlockType.AIR);
        this.getChunkByGlobalCoords(-16.f, -16.f).setAllBloksType(BlockType.AIR);
        this.getChunkByGlobalCoords(-32.f, -32.f).setAllBloksType(BlockType.AIR);
        this.getChunkByGlobalCoords(-48.f, -48.f).setAllBloksType(BlockType.AIR);
        for (int x = 0; x < this.renderDistance; x++) {
            for (int z = 0; z < this.renderDistance; z++) {
                final int _x = x;
                final int _z = z;
                Runnable runnable = () -> {
                    modelsGeneration(_x, _z);
                };
                threadPool.execute(runnable);
            }
        }
        threadPool.shutdown();
        threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 4);
    }

    public ArrayList<ArrayList<Chunk>> getToDeleteChunks(){
        return this.toDeleteChunks;
    }

    public void updateChunks() throws InterruptedException {

        int leftChunkCalcPos = (int)this.playerPosition.x/CHUNK_SIZE_X - this.renderDistance/2;
        int farChunkCalcPos = (int)this.playerPosition.z/CHUNK_SIZE_Z - this.renderDistance/2;
        int rightChunkCalcPos = (int)this.playerPosition.x/CHUNK_SIZE_Z + (int)Math.ceil(this.renderDistance/2);
        int nearChunkCalcPos = (int)this.playerPosition.z/CHUNK_SIZE_Z + (int)Math.ceil(this.renderDistance/2);
        ArrayList<Chunk> toDel = new ArrayList<>();
        if(leftChunkCalcPos *
                CHUNK_SIZE_X + 3< chunks.get(0).get(0).getPosition().x) {
            toDel.addAll(chunks.get(this.renderDistance - 1));
            synchronized (this.chunks){
                while (this.chunks.size() >= this.renderDistance) {
                    this.chunks.removeLast();
                }
            }
        }
        if(!toDel.isEmpty()){
            this.toDeleteChunks.add(toDel);
        }
        if(leftChunkCalcPos *
            CHUNK_SIZE_X + 3< chunks.get(0).get(0).getPosition().x){
            LinkedList<Chunk> add = new LinkedList<>();
            for (int z = 0; z < this.renderDistance; z++) {
                add.add(new Chunk(new Vector3f((leftChunkCalcPos) * CHUNK_SIZE_X, 0,
                        chunks.get(1).get(z).getPosition().z),
                        CHUNK_SIZE_X, CHUNK_SIZE_Y, CHUNK_SIZE_Z));
            }
            synchronized (this.chunks) {
                chunks.add(0, add);
            }
            double e1;
            for (int x = 0; x < this.renderDistance; x++) {
                double r = GLFW.glfwGetTime();
                final int _x = x;
                Runnable runnable = () -> {
                    chunks.get(0).get(_x).generate();
                };
                threadPool.execute(runnable);
                 e1 = GLFW.glfwGetTime() - r;
            }
            threadPool.shutdown();
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
            threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 4);
            double e;
            for (int x = 0; x < this.renderDistance; x++) {
                final int _x = x;
                Runnable runnable = () -> {
                    modelsGeneration(0, _x);
                };
                threadPool.execute(runnable);
                //modelsGeneration(0, x);
                double r = GLFW.glfwGetTime();
                modelsGeneration(1, x);
                e = GLFW.glfwGetTime() - r;

            }
        }

        if((rightChunkCalcPos - 1) *
                CHUNK_SIZE_X - 3 > chunks.get(this.renderDistance - 1).get(0).getPosition().x){
            System.out.println("x out right");
        }
        if(farChunkCalcPos *
        CHUNK_SIZE_Z + 3 < chunks.get(0).get(0).getPosition().z){
            System.out.println("x out far");
        }
        if((nearChunkCalcPos - 1)*
        CHUNK_SIZE_Z - 3 > chunks.get(0).get(this.renderDistance - 1).getPosition().z){
            System.out.println("x out near");
        }
    }

    public void setPlayerPosition(Vector3f playerPosition) {
        this.playerPosition = playerPosition;
    }
}
