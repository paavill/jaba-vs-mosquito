package main;

import game_objects.Player;
import input.KeyBindings;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.*;

public class World {

    private Player player;
    private ChunksManager chunksManager = new ChunksManager(10);


    public World(Camera main, KeyBindings bindings) {

        try {
            this.chunksManager.generateChunks();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.player = new Player(main, bindings);
        this.player.move(new Vector3f(0, 100,0));
    }

    public LinkedList<Chunk> getToDelete(){
        return chunksManager.getToDeleteChunks();
    }

    public void destroy(){
        this.chunksManager.destroy();
    }

    public void updateEntity(){
        player.update();
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
