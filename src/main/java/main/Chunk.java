package main;

import game_objects.blocks.BlockType;
import game_objects.blocks.Block;
import org.joml.SimplexNoise;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import renderer.Mesh;
import renderer.MeshSideType;

import java.nio.FloatBuffer;
import java.util.*;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Chunk {

    private final Vector3f position;
    private final int sizeX;
    private final int sizeZ;
    private final int sizeY;
    private boolean changed = true;
    private boolean finishChanged = false;
    private boolean moved = false;
    private boolean finishGenerated = false;
    private boolean addedToRender = false;

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

    public Chunk(Chunk chunk){
        this.position = chunk.position;
        this.sizeX = chunk.sizeX;
        this.sizeY = chunk.sizeY;
        this.sizeZ = chunk.sizeZ;
        this.changed = chunk.changed;
        this.finishChanged = chunk.finishChanged;
        this.vertexCount = chunk.vertexCount;
        this.blocks = new BlockType[sizeX][sizeY][sizeZ];
    }

    public Chunk(Vector3f position, int sizeX, int sizeY, int sizeZ) {
        this.position = position;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.blocks = new BlockType[sizeX][sizeY][sizeZ];
    }

    public boolean isFinishGenerated() {
        return finishGenerated;
    }

    public boolean isFinishChanged() {
        return finishChanged;
    }

    public boolean isAddedToRender() {
        return addedToRender;
    }

    public void setAddedToRender(boolean addedToRender) {
        this.addedToRender = addedToRender;
    }

    public void setFinishChanged(boolean finishChanged) {
        this.finishChanged = finishChanged;
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public void setBlock(int x, int y, int z, Block block) {
        this.changed = true;
        //доделать
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public boolean getChanged() {
        return this.changed;
    }

    public boolean isMoved() {
        return moved;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
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
        if(!this.finishGenerated) {
            FloatBuffer bf = BufferUtils.createFloatBuffer(16);
            for (int x = 0; x < this.sizeX; x++) {
                for (int y = 0; y < this.sizeY; y++) {
                    for (int z = 0; z < this.sizeZ; z++) {
                        if (this.generationPredicate(x, y, z)) {
                            blocks[x][y][z] = BlockType.STONE;
                        } else {
                            blocks[x][y][z] = BlockType.AIR;
                            if (y != 0) {
                                if (blocks[x][y - 1][z] != BlockType.AIR) {
                                    blocks[x][y - 1][z] = BlockType.GRASS;
                                }
                            }
                        }
                    }
                }
            }
            this.changed = true;
            this.finishGenerated = true;
        }
    }

    public void setAllBloksType(BlockType type) {
        for (int x = 0; x < this.sizeX; x++) {
            for (int y = 0; y < this.sizeY; y++) {
                for (int z = 0; z < this.sizeZ; z++) {
                    blocks[x][y][z] = type;
                }
            }
        }
        //changed = true;
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
    public void genBlocksMash(Chunk left, Chunk right, Chunk far, Chunk near) {
        if(changed && finishGenerated) {
            this.colorsC.clear();
            this.vertexesC.clear();
            this.normalsC.clear();
            this.texC.clear();
            this.vertexCount = 0;
            for (int x = 0; x < this.sizeX; x++) {
                for (int y = 0; y < this.sizeY; y++) {
                    for (int z = 0; z < this.sizeZ; z++) {
//                        double s = glfwGetTime();
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
                                    if (right != null) {
                                        if (right.getBlocks()[0][y][z].getId() == BlockType.AIR.getId()) {
                                            this.addAttributesDataToCollections(MeshSideType.RIGHT, x, y, z);
                                        }
                                    }
                                }
                            } else if (x == 0) {
                                if (left != null) {
                                    if (left.getBlocks()[this.sizeX - 1][y][z].getId() == BlockType.AIR.getId()) {
                                        this.addAttributesDataToCollections(MeshSideType.LEFT, x, y, z);
                                    }
                                }
                            }
                            if (y != 0) {
                                if (blocks[x][y - 1][z].getId() == 0) {//5
                                    this.addAttributesDataToCollections(MeshSideType.BOTTOM, x, y, z);
                                }
                            }
                            if (z != 0) {
                                if (blocks[x][y][z - 1].getId() == 0) {//6
                                    this.addAttributesDataToCollections(MeshSideType.FRONT, x, y, z);
                                }
                                if (z == this.sizeZ - 1) {
                                    if (near != null) {
                                        if (near.getBlocks()[x][y][0].getId() == BlockType.AIR.getId()) {
                                            this.addAttributesDataToCollections(MeshSideType.BACK, x, y, z);
                                        }
                                    }
                                }
                            } else if (z == 0) {
                                if (far != null) {
                                    if (far.getBlocks()[x][y][this.sizeZ - 1].getId() == BlockType.AIR.getId()) {
                                        this.addAttributesDataToCollections(MeshSideType.FRONT, x, y, z);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            this.vertexCount = this.vertexesC.size() / 3;
            this.finishChanged = true;
        }
    }

    public BlockType[][][] getBlocks() {
        return blocks;
    }

    public Collection<Float> getToDrawColorsBuffer() {
        return this.colorsC;
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