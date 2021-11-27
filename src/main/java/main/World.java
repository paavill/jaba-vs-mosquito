package main;

import game_objects.Player;
import input.KeyBindings;
import org.joml.Vector3f;
import physics.PhysicsEngine;

import java.util.LinkedList;
import java.util.concurrent.*;

public class World {

    private Player player;
    private PhysicsEngine engine;
    private ChunksManager chunksManager = new ChunksManager(10);

    public World(Camera main, KeyBindings bindings) {
        try {
            this.chunksManager.generateChunks();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        engine = new PhysicsEngine();

        this.player = new Player(main, bindings);
        this.player.teleport(new Vector3f(-1, 100,-1));
    }

    public LinkedList<Chunk> getToDelete(){
        return chunksManager.getToDeleteChunks();
    }

    public void destroy(){
        this.chunksManager.destroy();
    }

    public void updateEntity(){
        player.update(engine, chunksManager.getToCollisionAreaByGlobalCoords(player.getPosition(), 4));
        chunksManager.setPlayerPosition(player.getPosition());
    }

    public void update() throws ExecutionException, InterruptedException {
        chunksManager.updateChunksN();
    }

    public void generateObjects(){
        chunksManager.generateUpdatedChunks();
    }

    public ChunksManager getChunksManager() {
        return chunksManager;
    }

    public Player getPlayer(){
        return this.player;
    }
}
