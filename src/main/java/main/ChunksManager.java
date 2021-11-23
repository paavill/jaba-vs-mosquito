package main;

import game_objects.blocks.BlockType;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.*;

import org.lwjgl.glfw.*;

public class ChunksManager {

    private ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private LinkedList<Chunk> toDeleteChunks = new LinkedList<>();
    private LinkedList<Chunk> toUpdateMeshChunks = new LinkedList<>();
    private LinkedList<Chunk> toGenerate = new LinkedList<>();

    boolean leftRightOrNearFar = false;

    private int renderDistance;
    private static final int CHUNK_SIZE_X = 16;
    private static final int CHUNK_SIZE_Z = 16;
    private static final int CHUNK_SIZE_Y = 256;

    private Vector3f playerPosition = new Vector3f(0, 0 ,0);
    private final LinkedList<LinkedList<Chunk>> chunks = new LinkedList<>();


    public ChunksManager(int renderDistance) {
        this.renderDistance = renderDistance;
    }

    public LinkedList<Chunk> getAllChunks(){
        LinkedList<Chunk> toExport = new LinkedList<>();
        synchronized (this.chunks){
            this.chunks.forEach(e -> toExport.addAll(e));
        }
        return toExport;
    }

    public LinkedList<Chunk> getToUpdateMeshChunks() {
        return toUpdateMeshChunks;
    }

    public int getRenderDistance() {
        return renderDistance;
    }

    public void destroy(){
        this.threadPool.shutdown();
    }

    public LinkedList<Chunk> getAllChunkToDraw(){
        LinkedList<Chunk> toDraw = new LinkedList<>();
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

    public void modelsGeneration(final int _x, final int _z, Chunk extraNear, Chunk extraFar,
                                 Chunk extraRight, Chunk extraLeft){
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
            } else {
                left = extraLeft;
            }
            if (_z != 0) {
                far = chunks.get(_x).get(_z - 1);
            } else {
                far = extraFar;
            }
            if (_x != this.renderDistance - 1) {
                right = chunks.get(_x + 1).get(_z);
            } else {
                right = extraRight;
            }
            if (_z != this.renderDistance - 1) {
                near = chunks.get(_x).get(_z + 1);
            } else {
                near = extraNear;
            }
            chunks.get(_x).get(_z).genBlocksMash(
                    left,
                    right,
                    far,
                    near);
        }
    }

    private ArrayList<LinkedList<Chunk>> getExtraChunks(){
        ArrayList<LinkedList<Chunk>> result = new ArrayList<>();
        LinkedList<Chunk> extraLeft = new LinkedList<>();
        for (int z = 0; z < this.renderDistance; z++) {
            extraLeft.add(new Chunk(new Vector3f(
                    chunks.getFirst().get(z).getPosition().x - CHUNK_SIZE_X, 0,
                    chunks.getFirst().get(z).getPosition().z),
                    CHUNK_SIZE_X, CHUNK_SIZE_Y, CHUNK_SIZE_Z));
        }
        extraLeft.forEach(el -> el.generate());
        result.add(extraLeft);
        LinkedList<Chunk> extraRight = new LinkedList<>();
        for (int z = 0; z < this.renderDistance; z++) {
            extraRight.add(new Chunk(new Vector3f(
                    chunks.getLast().get(z).getPosition().x + CHUNK_SIZE_X, 0,
                    chunks.getLast().get(z).getPosition().z),
                    CHUNK_SIZE_X, CHUNK_SIZE_Y, CHUNK_SIZE_Z));
        }
        extraRight.forEach(el -> el.generate());
        result.add(extraRight);
        LinkedList<Chunk> extraFar = new LinkedList<>();
        for (LinkedList<Chunk> e:this.chunks) {
            extraFar.add(new Chunk(new Vector3f(
                    e.getFirst().getPosition().x, 0,
                    e.getFirst().getPosition().z - CHUNK_SIZE_Z),
                    CHUNK_SIZE_X, CHUNK_SIZE_Y, CHUNK_SIZE_Z));
        }
        extraFar.forEach(el -> el.generate());
        result.add(extraFar);
        LinkedList<Chunk> extraNear = new LinkedList<>();
        for (LinkedList<Chunk> e:this.chunks) {
            extraNear.add(new Chunk(new Vector3f(
                    e.getLast().getPosition().x, 0,
                    e.getLast().getPosition().z + CHUNK_SIZE_Z),
                    CHUNK_SIZE_X, CHUNK_SIZE_Y, CHUNK_SIZE_Z));
        }
        extraNear.forEach(el -> el.generate());
        result.add(extraNear);
        return result;
    }

    public void generateChunks() throws InterruptedException {
        this.createChunksInstances();
        ArrayList<Callable<Object>> toDo = new ArrayList<>();
        for (int x = 0; x < this.renderDistance; x++) {
            for (int z = 0; z < this.renderDistance; z++) {
                final int _x = x;
                final int _z = z;
                Runnable runnable = () -> {
                    chunks.get(_x).get(_z).generate();
                };
                toDo.add(Executors.callable(runnable));
            }
        }
        threadPool.invokeAll(toDo);

        this.getChunkByGlobalCoords(0.f, 0.f).setAllBloksType(BlockType.AIR);
        this.getChunkByGlobalCoords(-16.f, -16.f).setAllBloksType(BlockType.AIR);
        this.getChunkByGlobalCoords(-32.f, -32.f).setAllBloksType(BlockType.AIR);
        this.getChunkByGlobalCoords(-48.f, -48.f).setAllBloksType(BlockType.AIR);

        toDo.clear();
        ArrayList<LinkedList<Chunk>> extraChunks = this.getExtraChunks();
        for (int x = 0; x < this.renderDistance; x++) {
            for (int z = 0; z < this.renderDistance; z++) {
                final int _x = x;
                final int _z = z;
                Runnable runnable = () -> {
                    modelsGeneration(_x, _z, extraChunks.get(3).get(_x), extraChunks.get(2).get(_x),
                            extraChunks.get(1).get(_z),  extraChunks.get(0).get(_z));
                };
                toDo.add(Executors.callable(runnable));
            }
        }
        threadPool.invokeAll(toDo);
    }

    public LinkedList<Chunk> getToDeleteChunks(){
        LinkedList<Chunk> result = new LinkedList<>();
        for (Chunk e:this.toDeleteChunks) {
            result.add(e);
        }
        this.toDeleteChunks.clear();
        return result;
    }

    public void updateChunks() throws InterruptedException {

        int leftChunkCalcPos = (int)this.playerPosition.x/CHUNK_SIZE_X - this.renderDistance/2;
        int farChunkCalcPos = (int)this.playerPosition.z/CHUNK_SIZE_Z - this.renderDistance/2;
        int rightChunkCalcPos = (int)this.playerPosition.x/CHUNK_SIZE_Z + (int)Math.ceil(this.renderDistance/2);
        int nearChunkCalcPos = (int)this.playerPosition.z/CHUNK_SIZE_Z + (int)Math.ceil(this.renderDistance/2);
        ArrayList<Chunk> toDel = new ArrayList<>();
        //как это рефакторить даже не думал в душе не ***
        if (leftChunkCalcPos *
            CHUNK_SIZE_X + 3< chunks.get(0).get(0).getPosition().x && this.leftRightOrNearFar){
            toDel.addAll(chunks.getLast());
            synchronized (this.chunks){
                this.chunks.removeLast();
            }
            synchronized (this.toDeleteChunks) {
                this.toDeleteChunks.addAll(toDel);
            }
            LinkedList<Chunk> newChunks = new LinkedList<>();
            for (int z = 0; z < this.renderDistance; z++) {
                newChunks.add(new Chunk(new Vector3f(
                        chunks.getFirst().get(z).getPosition().x - CHUNK_SIZE_X, 0,
                        chunks.getFirst().get(z).getPosition().z),
                        CHUNK_SIZE_X, CHUNK_SIZE_Y, CHUNK_SIZE_Z));
            }
            synchronized (this.chunks) {
                chunks.add(0, newChunks);
            }

            ArrayList<Callable<Object>> toDo = new ArrayList<>();
            for (int x = 0; x < this.renderDistance; x++) {
                double r = GLFW.glfwGetTime();
                final int _x = x;
                Runnable runnable = () -> {
                    chunks.get(0).get(_x).generate();
                };
                toDo.add(Executors.callable(runnable));
            }
            threadPool.invokeAll(toDo);

            toDo.clear();

            ArrayList<LinkedList<Chunk>> extraChunks = this.getExtraChunks();
            for (int z = 0; z < this.renderDistance; z++) {
                final int _z = z;
                Runnable runnable = () -> {
                    modelsGeneration(0, _z,
                            extraChunks.get(3).get(0),
                            extraChunks.get(2).get(0),
                            extraChunks.get(1).get(_z),
                            extraChunks.get(0).get(_z));
                };
                toDo.add(Executors.callable(runnable));
            }
            threadPool.invokeAll(toDo);
        }

        if((rightChunkCalcPos - 1) *
                CHUNK_SIZE_X - 3 > chunks.get(this.renderDistance - 1).get(0).getPosition().x && this.leftRightOrNearFar){
            //System.out.println("x out right");
            toDel.addAll(chunks.getFirst());

            synchronized (this.chunks){
                this.chunks.removeFirst();
            }
            synchronized (this.toDeleteChunks) {
                this.toDeleteChunks.addAll(toDel);
            }
            LinkedList<Chunk> add = new LinkedList<>();
            for (int z = 0; z < this.renderDistance; z++) {
                add.add(new Chunk(new Vector3f(
                        chunks.getLast().get(z).getPosition().x + CHUNK_SIZE_X, 0,
                        chunks.getLast().get(z).getPosition().z),
                        CHUNK_SIZE_X, CHUNK_SIZE_Y, CHUNK_SIZE_Z));
            }
            synchronized (this.chunks) {
                chunks.add(add);
            }
            ArrayList<Callable<Object>> toDo = new ArrayList<>();
            for (int x = 0; x < this.renderDistance; x++) {
                double r = GLFW.glfwGetTime();
                final int _x = x;
                Runnable runnable = () -> {
                    chunks.getLast().get(_x).generate();
                };
                toDo.add(Executors.callable(runnable));
            }
            threadPool.invokeAll(toDo);

            ArrayList<LinkedList<Chunk>> extraChunks = this.getExtraChunks();
            toDo.clear();
            for (int z = 0; z < this.renderDistance; z++) {
                final int _z = z;
                Runnable runnable = () -> {
                    int xc = this.renderDistance - 1;
                    modelsGeneration(xc, _z,
                            extraChunks.get(3).get(xc),
                            extraChunks.get(2).get(xc),
                            extraChunks.get(1).get(_z),
                            extraChunks.get(0).get(_z));
                };
                toDo.add(Executors.callable(runnable));
            }
            threadPool.invokeAll(toDo);
        }

        if(farChunkCalcPos *
        CHUNK_SIZE_Z + 3 < chunks.get(0).get(0).getPosition().z && !this.leftRightOrNearFar){
            //System.out.println("x out far");
            for (LinkedList<Chunk> e: this.chunks) {
                toDel.add(e.getLast());
            }
            synchronized (this.chunks){
                for (LinkedList<Chunk> e: this.chunks) {
                    e.removeLast();
                }
            }
            synchronized (this.toDeleteChunks) {
                this.toDeleteChunks.addAll(toDel);
            }
            LinkedList<Chunk> add = new LinkedList<>();
            for (LinkedList<Chunk> e:this.chunks) {
                add.add(new Chunk(new Vector3f(
                        e.getFirst().getPosition().x, 0,
                        e.getFirst().getPosition().z - CHUNK_SIZE_Z),
                        CHUNK_SIZE_X, CHUNK_SIZE_Y, CHUNK_SIZE_Z));
            }
            synchronized (this.chunks) {
                for (LinkedList<Chunk> e:this.chunks) {
                    e.addFirst(add.getFirst());
                    add.removeFirst();
                }
            }
            ArrayList<Callable<Object>> toDo = new ArrayList<>();
            for (LinkedList<Chunk> e:this.chunks) {
                Runnable runnable = () -> {
                    e.getFirst().generate();
                };
                toDo.add(Executors.callable(runnable));
            }
            threadPool.invokeAll(toDo);
            //во всех методах можно добавить в threadPool
            ArrayList<LinkedList<Chunk>> extraChunks = this.getExtraChunks();
            toDo.clear();
            for (int x = 0; x < this.renderDistance; x++) {
                final int _x = x;
                Runnable runnable = () -> {
                    modelsGeneration(_x, 0,
                            extraChunks.get(3).get(_x),
                            extraChunks.get(2).get(_x),
                            extraChunks.get(1).get(0),
                            extraChunks.get(0).get(0));
                };
                toDo.add(Executors.callable(runnable));
            }
            threadPool.invokeAll(toDo);
        }

        if((nearChunkCalcPos - 1)*
        CHUNK_SIZE_Z - 3 > chunks.get(0).get(this.renderDistance - 1).getPosition().z && !this.leftRightOrNearFar){
            //System.out.println("x out near");
            for (LinkedList<Chunk> e: this.chunks) {
                toDel.add(e.getFirst());
            }
            synchronized (this.chunks){
                for (LinkedList<Chunk> e: this.chunks) {
                    e.removeFirst();
                }
            }
            synchronized (this.toDeleteChunks) {
                this.toDeleteChunks.addAll(toDel);
            }
            LinkedList<Chunk> add = new LinkedList<>();
            for (LinkedList<Chunk> e:this.chunks) {
                add.add(new Chunk(new Vector3f(
                        e.getLast().getPosition().x, 0,
                        e.getLast().getPosition().z + CHUNK_SIZE_Z),
                        CHUNK_SIZE_X, CHUNK_SIZE_Y, CHUNK_SIZE_Z));
            }
            synchronized (this.chunks) {
                for (LinkedList<Chunk> e:this.chunks) {
                    e.addLast(add.getFirst());
                    add.removeFirst();
                }
            }
            ArrayList<Callable<Object>> toDo = new ArrayList<>();
            for (LinkedList<Chunk> e:this.chunks) {
                Runnable runnable = () -> {
                    e.getLast().generate();
                };
                toDo.add(Executors.callable(runnable));
            }
            threadPool.invokeAll(toDo);
            ArrayList<LinkedList<Chunk>> extraChunks = this.getExtraChunks();
            toDo.clear();
            for (int x = 0; x < this.renderDistance; x++) {
                final int _x = x;
                Runnable runnable = () -> {
                    int zc = this.renderDistance - 1;
                    modelsGeneration(_x, this.renderDistance - 1,
                            extraChunks.get(3).get(_x),
                            extraChunks.get(2).get(_x),
                            extraChunks.get(1).get(zc),
                            extraChunks.get(0).get(zc));
                };
                toDo.add(Executors.callable(runnable));
            }
            threadPool.invokeAll(toDo);
        }

        this.leftRightOrNearFar = !this.leftRightOrNearFar;
    }

    public void updateChunksN() throws InterruptedException {

        int leftChunkCalcPos = (int)this.playerPosition.x/CHUNK_SIZE_X - this.renderDistance/2;
        int farChunkCalcPos = (int)this.playerPosition.z/CHUNK_SIZE_Z - this.renderDistance/2;
        int rightChunkCalcPos = (int)this.playerPosition.x/CHUNK_SIZE_Z + (int)Math.ceil(this.renderDistance/2);
        int nearChunkCalcPos = (int)this.playerPosition.z/CHUNK_SIZE_Z + (int)Math.ceil(this.renderDistance/2);
        ArrayList<Chunk> toDel = new ArrayList<>();
        //как это рефакторить даже не думал в душе не ***
        if (leftChunkCalcPos *
                CHUNK_SIZE_X + 3< chunks.get(0).get(0).getPosition().x && this.leftRightOrNearFar){
            toDel.addAll(chunks.getLast());

            this.toDeleteChunks.addAll(toDel);
            LinkedList<Chunk> newChunks = new LinkedList<>();

            for (int z = 0; z < this.renderDistance; z++) {
                newChunks.add(new Chunk(new Vector3f(
                        chunks.getFirst().get(z).getPosition().x - CHUNK_SIZE_X, 0,
                        chunks.getFirst().get(z).getPosition().z),
                        CHUNK_SIZE_X, CHUNK_SIZE_Y, CHUNK_SIZE_Z));
            }
            this.chunks.removeLast();
            chunks.add(0, newChunks);
            synchronized (this.toGenerate){
                this.toGenerate.addAll(newChunks);
            }

        }

        if((rightChunkCalcPos - 1) *
                CHUNK_SIZE_X - 3 > chunks.get(this.renderDistance - 1).get(0).getPosition().x && this.leftRightOrNearFar){
            //System.out.println("x out right");
            toDel.addAll(chunks.getFirst());

            this.toDeleteChunks.addAll(toDel);

            LinkedList<Chunk> add = new LinkedList<>();

            for (int z = 0; z < this.renderDistance; z++) {
                add.add(new Chunk(new Vector3f(
                        chunks.getLast().get(z).getPosition().x + CHUNK_SIZE_X, 0,
                        chunks.getLast().get(z).getPosition().z),
                        CHUNK_SIZE_X, CHUNK_SIZE_Y, CHUNK_SIZE_Z));
            }

            this.chunks.removeFirst();
            chunks.add(add);

            synchronized (this.toGenerate){
                this.toGenerate.addAll(add);
            }
        }

        if(farChunkCalcPos *
                CHUNK_SIZE_Z + 3 < chunks.get(0).get(0).getPosition().z && !this.leftRightOrNearFar){
            //System.out.println("x out far");

            for (LinkedList<Chunk> e : this.chunks) {
                toDel.add(e.getLast());
            }

            this.toDeleteChunks.addAll(toDel);

            LinkedList<Chunk> add = new LinkedList<>();
            for (LinkedList<Chunk> e : this.chunks) {
                add.add(new Chunk(new Vector3f(
                        e.getFirst().getPosition().x, 0,
                        e.getFirst().getPosition().z - CHUNK_SIZE_Z),
                        CHUNK_SIZE_X, CHUNK_SIZE_Y, CHUNK_SIZE_Z));
            }

            synchronized (this.toGenerate){
                this.toGenerate.addAll(add);
            }

            for (LinkedList<Chunk> e:this.chunks) {
                e.removeLast();
                e.addFirst(add.getFirst());
                add.removeFirst();
            }
        }

        if((nearChunkCalcPos - 1)*
                CHUNK_SIZE_Z - 3 > chunks.get(0).get(this.renderDistance - 1).getPosition().z && !this.leftRightOrNearFar){
            //System.out.println("x out near");
            for (LinkedList<Chunk> e : this.chunks) {
                toDel.add(e.getFirst());
            }

            this.toDeleteChunks.addAll(toDel);

            LinkedList<Chunk> add = new LinkedList<>();

            for (LinkedList<Chunk> e : this.chunks) {
                add.add(new Chunk(new Vector3f(
                        e.getLast().getPosition().x, 0,
                        e.getLast().getPosition().z + CHUNK_SIZE_Z),
                        CHUNK_SIZE_X, CHUNK_SIZE_Y, CHUNK_SIZE_Z));
            }

            synchronized (this.toGenerate){
                this.toGenerate.addAll(add);
            }

            for (LinkedList<Chunk> e:this.chunks) {
                e.removeFirst();
                e.addLast(add.getFirst());
                add.removeFirst();
            }
        }

        this.leftRightOrNearFar = !this.leftRightOrNearFar;
    }

    public void generateUpdatedChunks(){
        synchronized (this.toGenerate) {
            if (toGenerate.size() > 0) {
                Chunk toGen = this.toGenerate.getFirst();
                if (toGen.getChanged() && !toGen.isFinishChanged()) {
                    toGen.generate();
                    toGen.genBlocksMash(null, null, null, null);

                    this.toGenerate.remove(toGen);

                }
            }
        }
    }

    public void setPlayerPosition(Vector3f playerPosition) {
        this.playerPosition = playerPosition;
    }
}