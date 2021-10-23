package main;

import game_objects.Player;
import input.KeyBindings;

public class World {

    private Player player;

    public World(Camera main, KeyBindings bindings) {

        ChunksManager.setRenderDistance(32);
        try {
            ChunksManager.generateChunks();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ChunksManager.addAllChunksToDraw();

        this.player = new Player(main, bindings);
    }

    public void update() {
        player.update();
    }
}
