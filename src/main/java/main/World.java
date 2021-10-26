package main;

import game_objects.Player;
import input.KeyBindings;
import org.joml.Vector3f;

public class World {

    private Player player;

    public World(Camera main, KeyBindings bindings) {

        ChunksManager.setRenderDistance(10);
        try {
            ChunksManager.generateChunks();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ChunksManager.addAllChunksToDraw();

        this.player = new Player(main, bindings);
        this.player.move(new Vector3f(10, 100,-10));
    }

    public void update() {
        player.update();
    }
}
