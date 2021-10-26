package main;

import game_objects.BlockType;
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
    static List<Float> blockVertex = List.of(
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

            -0.5f, 0.5f, 0.5f, //3 лево
            -0.5f, 0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
            -0.5f, -0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f,

            0.5f, 0.5f, 0.5f,  //4 право
            0.5f, 0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,

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
    static List<Float> blockCollors = List.of(
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

    static List<Float> blockNormales = List.of(
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

    private static final Mesh blockMash = new Mesh(new ArrayList<>(blockVertex), new ArrayList<>(blockCollors), new ArrayList<>(blockNormales));
    private static final Mesh m = new Mesh();
    private static final Block[] bl = {new Block(BlockType.AIR, m, false),
            new Block(BlockType.STONE, blockMash, false)};

    private final Vector3f position;
    private final int sizeX;
    private final int sizeZ;
    private final int sizeY;
    private boolean changed = false;

    private int vertexCount = 0;

    private Collection<Float> vertexesC = new ArrayList<>();
    private Collection<Float> colorsC = new ArrayList<>();
    private Collection<Float> normalsC = new ArrayList<>();

    private short[][][] blocks;

    public Chunk(Vector3f position, int sizeX, int sizeY, int sizeZ) {
        this.position = position;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.blocks = new short[sizeX][sizeY][sizeZ];
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
        return 70 + 10 * SimplexNoise.noise((x + this.position.x) / 90.f, (z + this.position.z) / 70.f) +
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
                        blocks[x][y][z] = 1;
                    } else {
                        blocks[x][y][z] = 0;
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
        if (!bl[blocks[xOffset][yOffset][zOffset]].getSpecial()) {
            currentMash = bl[blocks[xOffset][yOffset][zOffset]].getSideMesh(sideOffset);
        } else {
            currentMash = bl[blocks[xOffset][yOffset][zOffset]].getMesh();
        }
        ArrayList<Float> vertexArray = currentMash.getVertex();
        ArrayList<Float> colorsArray = currentMash.getColors();
        ArrayList<Float> normalsArray = currentMash.getNormals();
        this.addOffsetToAttributes(vertexArray, xOffset, yOffset, zOffset);
        Collection<ArrayList<Float>> result = new ArrayList<ArrayList<Float>>();
        result.add(vertexArray);
        result.add(colorsArray);
        result.add(normalsArray);
        return result;
    }

    private void addAttributesDataToCollections(MeshSideType sideOffset, int xOffset, int yOffset, int zOffset) {
        ArrayList<ArrayList<Float>> attributeArray = (ArrayList<ArrayList<Float>>) getVisibleSidesOfBlocks(sideOffset, xOffset, yOffset, zOffset);
        vertexesC.addAll(attributeArray.get(0));
        colorsC.addAll(attributeArray.get(1));
        normalsC.addAll(attributeArray.get(2));
    }

    //можно отрефакторить но пока лень
    public void genBlocksMash() {
        for (int x = 0; x < this.sizeX; x++) {
            for (int y = 0; y < this.sizeY; y++) {
                for (int z = 0; z < this.sizeZ; z++) {
                    double s = glfwGetTime();
                    if (bl[blocks[x][y][z]].getType().getId() == 0) {
                        if (x != 0) {
                            if (bl[blocks[x - 1][y][z]].getType().getId() != 0) {//1
                                this.addAttributesDataToCollections(MeshSideType.RIGHT, x - 1, y, z);
                            }
                        }
                        if (y != 0) {
                            if (bl[blocks[x][y - 1][z]].getType().getId() != 0) {//2
                                this.addAttributesDataToCollections(MeshSideType.TOP, x, y - 1, z);
                            }
                        }
                        if (z != 0) {
                            if (bl[blocks[x][y][z - 1]].getType().getId() != 0) {//3
                                this.addAttributesDataToCollections(MeshSideType.BACK, x, y, z - 1);
                            }
                        }
                    }
                    if (bl[blocks[x][y][z]].getType().getId() != 0) {
                        if (x != 0) {
                            if (bl[blocks[x - 1][y][z]].getType().getId() == 0) {//4
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
                            if (bl[blocks[x][y - 1][z]].getType().getId() == 0) {//5
                                this.addAttributesDataToCollections(MeshSideType.RIGHT, x, y, z);
                            }
                        }
                        if (z != 0) {
                            if (bl[blocks[x][y][z - 1]].getType().getId() == 0) {//6
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

    public int getVertexCount() {
        return this.vertexCount;
    }
}