package main;

import game_objects.Player;
import input.KeyBindings;
import org.joml.Vector3f;

public class World {

    private Player player;
    private ChunksManager chunksManager = new ChunksManager(20);

    public World(Camera main, KeyBindings bindings) {

        try {
            this.chunksManager.generateChunks();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.chunksManager.addAllChunksToDraw();

        this.player = new Player(main, bindings);
        this.player.move(new Vector3f(10, 100,-10));
    }

    public void update() {
        player.update();
    }
}
