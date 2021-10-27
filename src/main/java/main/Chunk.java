package main;

import game_objects.blocks.BlockType;
import game_objects.blocks.Block;
import org.joml.SimplexNoise;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import renderer.GraphicResourceLoader;
import renderer.Mesh;
import renderer.MeshSideType;
import renderer.Texture;

import java.nio.FloatBuffer;
import java.util.*;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Chunk {

    private final Vector3f position;
    private final int sizeX;
    private final int sizeZ;
    private final int sizeY;
    private boolean changed = false;

    private int vertexCount = 0;

    private Collection<Float> vertexesC = new ArrayList<>();
    private Collection<Float> colorsC = new ArrayList<>();
    private Collection<Float> normalsC = new ArrayList<>();
    private Collection<Float> texC = new ArrayList<>();

    private static HashMap<BlockType, Block> blocksModels;

    private BlockType[][][] blocks;

    public static void setBlocksModels(HashMap<BlockType, Block> blocks) {
        blocksModels = blocks;
    }

    public Chunk(Vector3f position, int sizeX, int sizeY, int sizeZ) {
        this.position = position;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.blocks = new BlockType[sizeX][sizeY][sizeZ];
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public void setBlock(int x, int y, int z, Block block) {
        this.changed = true;
        //доделать
    }

    public boolean getChanged() {
        return this.changed;
    }

    private float generatingFunction(int x, int z) {
        SimplexNoise s = new SimplexNoise();
        return 70 + 20 * SimplexNoise.noise((x + this.position.x) / 90.f, (z + this.position.z) / 70.f) +
                60 * SimplexNoise.noise((x + this.position.x) / 400.f, (z + this.position.z) / 400.f);
    }

    private boolean generationPredicate(int x, int y, int z) {
        return y < this.generatingFunction(x, z);
    }

    public void generate() {
        Random rand = new Random((long) glfwGetTime());
        FloatBuffer bf = BufferUtils.createFloatBuffer(16);
        for (int x = 0; x < this.sizeX; x++) {
            for (int y = 0; y < this.sizeY; y++) {
                for (int z = 0; z < this.sizeZ; z++) {
                    if (this.generationPredicate(x, y, z)) {
                        blocks[x][y][z] = BlockType.STONE;
                    } else {
                        blocks[x][y][z] = BlockType.AIR;
                        if(y != 0){
                            if(blocks[x][y-1][z] != BlockType.AIR) {
                                blocks[x][y - 1][z] = BlockType.GRASS;
                            }
                        }
                    }
                }
            }
        }
    }

    private void addOffsetToAttributes(ArrayList<Float> vertex, int xOffset, int yOffset, int zOffset) {
        for (int i = 0; i < vertex.size(); i += 3) {
            vertex.set(i, vertex.get(i) + xOffset);
            vertex.set(i + 1, vertex.get(i + 1) + yOffset);
            vertex.set(i + 2, vertex.get(i + 2) + zOffset);
        }
    }

    private Collection<ArrayList<Float>> getVisibleSidesOfBlocks(MeshSideType sideOffset, int xOffset, int yOffset, int zOffset) {
        Mesh currentMash;
        Block currentBlock = Chunk.blocksModels.get(blocks[xOffset][yOffset][zOffset]);
        if (!currentBlock.getSpecial()) {
            currentMash = currentBlock.getSideMesh(sideOffset);
        } else {
            currentMash = currentBlock.getMesh();
        }
        ArrayList<Float> vertexArray = currentMash.getVertex();
        ArrayList<Float> colorsArray = currentMash.getColors();
        ArrayList<Float> normalsArray = currentMash.getNormals();
        ArrayList<ArrayList<Float>> tex = currentMash.getTextureCoords();
        this.addOffsetToAttributes(vertexArray, xOffset, yOffset, zOffset);
        Collection<ArrayList<Float>> result = new ArrayList<ArrayList<Float>>();
        result.add(vertexArray);
        result.add(colorsArray);
        result.add(normalsArray);
        result.add(tex.get(0));
        return result;
    }

    private void addAttributesDataToCollections(MeshSideType sideOffset, int xOffset, int yOffset, int zOffset) {
        ArrayList<ArrayList<Float>> attributeArray = (ArrayList<ArrayList<Float>>) getVisibleSidesOfBlocks(sideOffset, xOffset, yOffset, zOffset);
        vertexesC.addAll(attributeArray.get(0));
        colorsC.addAll(attributeArray.get(1));
        normalsC.addAll(attributeArray.get(2));
        texC.addAll(attributeArray.get(3));
    }

    //можно отрефакторить но пока лень
    public void genBlocksMash() {
        for (int x = 0; x < this.sizeX; x++) {
            for (int y = 0; y < this.sizeY; y++) {
                for (int z = 0; z < this.sizeZ; z++) {
                    double s = glfwGetTime();
                    if (blocks[x][y][z].getId() == 0) {
                        if (x != 0) {
                            if (blocks[x - 1][y][z].getId() != 0) {//1
                                this.addAttributesDataToCollections(MeshSideType.RIGHT, x - 1, y, z);
                            }
                        }
                        if (y != 0) {
                            if (blocks[x][y - 1][z].getId() != 0) {//2
                                this.addAttributesDataToCollections(MeshSideType.TOP, x, y - 1, z);
                            }
                        }
                        if (z != 0) {
                            if (blocks[x][y][z - 1].getId() != 0) {//3
                                this.addAttributesDataToCollections(MeshSideType.BACK, x, y, z - 1);
                            }
                        }
                    }
                    if (blocks[x][y][z].getId() != 0) {
                        if (x != 0) {
                            if (blocks[x - 1][y][z].getId() == 0) {//4
                                this.addAttributesDataToCollections(MeshSideType.LEFT, x, y, z);
                            }
                            if (x == this.sizeX - 1) {
                                if (!this.generationPredicate(x + 1, y, z)) {
                                    this.addAttributesDataToCollections(MeshSideType.RIGHT, x, y, z);
                                }
                            }
                        } else if (x == 0) {
                            if (!this.generationPredicate(x - 1, y, z)) {
                                this.addAttributesDataToCollections(MeshSideType.LEFT, x, y, z);
                            }
                        }
                        if (y != 0) {
                            if (blocks[x][y - 1][z].getId() == 0) {//5
                                this.addAttributesDataToCollections(MeshSideType.RIGHT, x, y, z);
                            }
                        }
                        if (z != 0) {
                            if (blocks[x][y][z - 1].getId() == 0) {//6
                                this.addAttributesDataToCollections(MeshSideType.FRONT, x, y, z);
                            }
                            if (z == this.sizeZ - 1) {
                                if (!this.generationPredicate(x, y, z+1)) {
                                    this.addAttributesDataToCollections(MeshSideType.BACK, x, y, z);
                                }
                            }
                        } else if (z == 0) {
                            if (!this.generationPredicate(x, y, z - 1)) {
                                this.addAttributesDataToCollections(MeshSideType.FRONT, x, y, z);
                            }
                        }
                    }
                }
            }
        }
        this.vertexCount = this.vertexesC.size() / 3;
    }

    public Collection<Float> getToDrawColorsBuffer() {
        return this.vertexesC;
    }

    public Collection<Float> getToDrawNormalsBuffer() {
        return this.normalsC;
    }

    public Collection<Float> getToDrawVertexBuffer() {
        return this.vertexesC;
    }

    public Collection<Float> getTexC() {
        return texC;
    }

    public int getVertexCount() {
        return this.vertexCount;
    }
}