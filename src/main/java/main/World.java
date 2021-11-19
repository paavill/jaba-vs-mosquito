package main;

import game_objects.Player;
import input.KeyBindings;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

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

    public ArrayList<Chunk> update() {
        player.update();
        chunksManager.setPlayerPosition(player.getPosition());
        Callable task = () -> {
            return chunksManager.updateChunks();
        };
        FutureTask<String> future = new FutureTask<>(task);
        new Thread(future).start();
        return null;
    }

    public ChunksManager getChunksManager() {
        return chunksManager;
    }
}
