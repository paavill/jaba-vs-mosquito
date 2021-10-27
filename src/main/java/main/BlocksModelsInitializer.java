package main;

import game_objects.blocks.Block;
import game_objects.blocks.BlockType;
import renderer.GraphicResourceLoader;
import renderer.Mesh;
import renderer.Texture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlocksModelsInitializer {
    private static List<Float> blockVertex = List.of(
            -0.5f, -0.5f, -0.5f, //1 ближняя по z
            0.5f, -0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            -0.5f, 0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,

            -0.5f, -0.5f, 0.5f, //2 дяльняя по z
            0.5f, -0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f,
            -0.5f, -0.5f, 0.5f,

            -0.5f, -0.5f, 0.5f, //3 лево
            -0.5f, -0.5f, -0.5f,
            -0.5f, 0.5f, -0.5f,
            -0.5f, 0.5f, -0.5f,
            -0.5f, 0.5f, 0.5f,
            -0.5f, -0.5f, 0.5f,

            0.5f, -0.5f, 0.5f,  //4 право
            0.5f, -0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,

            -0.5f, -0.5f, -0.5f,  //5 низ
            0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            -0.5f, -0.5f, 0.5f,
            -0.5f, -0.5f, -0.5f,

            -0.5f, 0.5f, -0.5f,  //6 верх
            0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, -0.5f);
    private static List<Float> blockColors = List.of(
            0.4f, 0.4f, 0.4f,
            0.4f, 0.4f, 0.4f,
            0.4f, 0.4f, 0.4f,
            0.4f, 0.4f, 0.4f,
            0.4f, 0.4f, 0.4f,
            0.4f, 0.4f, 0.4f,

            0.6f, 0.6f, 0.6f,
            0.6f, 0.6f, 0.6f,
            0.6f, 0.6f, 0.6f,
            0.6f, 0.6f, 0.6f,
            0.6f, 0.6f, 0.6f,
            0.6f, 0.6f, 0.6f,

            0.4f, 0.4f, 0.4f,
            0.4f, 0.4f, 0.4f,
            0.4f, 0.4f, 0.4f,
            0.4f, 0.4f, 0.4f,
            0.4f, 0.4f, 0.4f,
            0.4f, 0.4f, 0.4f,

            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,

            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,

            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f
    );

    private static List<Float> blockNormals = List.of(
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,

            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,

            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,

            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,

            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,

            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f
    );

    private static final Texture textureAtlas = GraphicResourceLoader
            .loadTexture("blocks.png", "");
    private static final Mesh blockMash = new Mesh(new ArrayList<>(blockVertex),
            new ArrayList<>(blockColors), new ArrayList<>(blockNormals), textureAtlas.genTexCords());
    private static final Mesh airMesh = new Mesh();
    private static final HashMap<BlockType, Block> blocks = new HashMap<>();
    private static final Block[] bl = {,
            };

    public static HashMap<BlockType, Block> init(){
        blocks.put(BlockType.AIR, new Block(BlockType.AIR, airMesh, false));
        blocks.put(BlockType.STONE, new Block(BlockType.STONE, blockMash, false));
        blocks.put(BlockType.GRASS, new Block(BlockType.GRASS, blockMash, false));
        return blocks;
    }

    public static Texture getTextureAtlas() {
        return textureAtlas;
    }
}
