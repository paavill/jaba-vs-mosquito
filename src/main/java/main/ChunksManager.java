package main;

import game_objects.blocks.BlockType;
import org.joml.Vector3f;
import renderer.MeshRenderer;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ChunksManager {

    private int renderDistance;

    private static final int CHUNK_SIZE_X = 16;
    private static final int CHUNK_SIZE_Z = 16;
    private static final int CHUNK_SIZE_Y = 256;

    private Vector3f playerPosition = new Vector3f(0, 0 ,0);

    private final ArrayList<ArrayList<Chunk>> chunks = new ArrayList<>();

    private ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 4);

    public ChunksManager(int renderDistance) {
        this.renderDistance = renderDistance;
    }

    public ArrayList<ArrayList<Chunk>> getAllChunks(){
        return this.chunks;
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
            chunks.add(new ArrayList<Chunk>());
            for (int z = 0; z < this.renderDistance; z++) {
                chunks.get(x).add(
                        new Chunk(new Vector3f((x + leftChunkCalcPos ) * CHUNK_SIZE_X, 0,
                                (z + farChunkCalcPos)* CHUNK_SIZE_Z),
                                CHUNK_SIZE_X, CHUNK_SIZE_Y, CHUNK_SIZE_Z));
            }
        }
    }

    public void getFunc(final int _x, final int _z){
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
        threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 4);
        this.getChunkByGlobalCoords(0.f, 0.f).setAllBloksType(BlockType.AIR);
        this.getChunkByGlobalCoords(-16.f, -16.f).setAllBloksType(BlockType.AIR);
        this.getChunkByGlobalCoords(-32.f, -32.f).setAllBloksType(BlockType.AIR);
        this.getChunkByGlobalCoords(-48.f, -48.f).setAllBloksType(BlockType.AIR);
        for (int x = 0; x < this.renderDistance; x++) {
            for (int z = 0; z < this.renderDistance; z++) {
                final int _x = x;
                final int _z = z;
                Runnable runnable = () -> {
                    getFunc(_x, _z);
                };
                threadPool.execute(runnable);
            }
        }
        threadPool.shutdown();
        threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 4);
    }

    public ArrayList<Chunk> updateChunks(){
        int leftChunkCalcPos = (int)this.playerPosition.x/CHUNK_SIZE_X - this.renderDistance/2;
        int farChunkCalcPos = (int)this.playerPosition.z/CHUNK_SIZE_Z - this.renderDistance/2;
        int rightChunkCalcPos = (int)this.playerPosition.x/CHUNK_SIZE_Z + (int)Math.ceil(this.renderDistance/2);
        int nearChunkCalcPos = (int)this.playerPosition.z/CHUNK_SIZE_Z + (int)Math.ceil(this.renderDistance/2);
        ArrayList<Chunk> toDel = new ArrayList<>();
        if(leftChunkCalcPos *
                CHUNK_SIZE_X + 3< chunks.get(0).get(0).getPosition().x){

            for(int x = 0; x < this.renderDistance - 1; x++){
                chunks.set(x+1, chunks.get(x));
            }


            for (int z = 0; z < this.renderDistance; z++) {
                toDel.add(chunks.get(0).get(z));
                chunks.get(0).set(z,
                            new Chunk(new Vector3f(( leftChunkCalcPos ) * CHUNK_SIZE_X, 0,
                                    (z + farChunkCalcPos)* CHUNK_SIZE_Z),
                                    CHUNK_SIZE_X, CHUNK_SIZE_Y, CHUNK_SIZE_Z));
            }
            for (int x = 0; x < this.renderDistance; x++) {
                chunks.get(0).get(x).generate();
            }
            for (int x = 0; x < this.renderDistance; x++) {
                getFunc(0, x);
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
        return toDel;
    }

    public void setPlayerPosition(Vector3f playerPosition) {
        this.playerPosition = playerPosition;
    }
}
