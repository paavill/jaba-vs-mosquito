import org.joml.SimplexNoise;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Chunk {
    //сделать загрузку из файла
    //и вообще инициализация должна быть не здесь (абстракции...)

    static Float[] blockVertex = new Float[]{
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
            -0.5f, 0.5f, -0.5f};
    static Float[] blockCollors = {
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
    };

    static Float[] blockNormales = {
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
    };

    private static Mash blockMash = new Mash(blockVertex, blockCollors, blockNormales);
    private static Mash m = new Mash();
    private static Block[] bl = {new Block((short) 0, m, false), new Block((short) 1, blockMash, false)};

    private Vector3f position;
    private final static int sizeXZ = 16;
    private final static int sizeY = 256;
    private boolean changed = false;

    //Временное поле, надо убрать
    private int vertexCount;
    private static int chunkOffset = 0;

    private Collection<Float> vertexesC = new ArrayList<>();
    private Collection<Float> colorsC = new ArrayList<>();
    private Collection<Float> normalsC = new ArrayList<>();

    private short[][][] blocks = new short[sizeXZ][sizeY][sizeXZ];

    public Chunk(Vector3f position) {
        this.position = position;
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

    public void generate() {
        Random rand = new Random((long)glfwGetTime());
        FloatBuffer bf = BufferUtils.createFloatBuffer(16);
        for (int x = 0; x < this.sizeXZ; x++) {
            for (int y = 0; y < this.sizeY; y++) {
                for (int z = 0; z < this.sizeXZ; z++) {
                    if (y < 20 + 5*SimplexNoise.noise((x+chunkOffset)/30.f, (z+chunkOffset)/30.f)) {
                        blocks[x][y][z] = 1;
                    } else {
                        blocks[x][y][z] = 0;
                    }
                }
            }
        }
        chunkOffset += 16;
    }

    public void clearChunkToDrawBuffers() {
        this.vertexesC.clear();
        this.colorsC.clear();
        this.normalsC.clear();
    }

    private void addOffsetToAttributes(Float[] vertex, int xOffset, int yOffset, int zOffset) {
        for (int i = 0; i < vertex.length; i += 3) {
            vertex[i] += xOffset;
            vertex[i + 1] += yOffset;
            vertex[i + 2] += zOffset;
        }

    }

    private Float[][] getVisibleSidesOfBlocks(int sideOffset, int xOffset, int yOffset, int zOffset) {
        Float[] vertexArray;
        Float[] colorsArray;
        Float[] normalsArray;
        Mash currentMash;
        if (!bl[blocks[xOffset][yOffset][zOffset]].getSpecial()) {
            currentMash = bl[blocks[xOffset][yOffset][zOffset]].getSideMash(sideOffset);
        } else {
            currentMash = bl[blocks[xOffset][yOffset][zOffset]].getMash();
        }
        vertexArray = currentMash.getVertex();
        colorsArray = currentMash.getColors();
        normalsArray = currentMash.getNormals();
        this.addOffsetToAttributes(vertexArray, xOffset, yOffset, zOffset);
        Float[][] result = new Float[][]{
                vertexArray,
                colorsArray,
                normalsArray
        };
        return result;
    }

    private void addAttributesDataToCollections(int sideOffset, int xOffset, int yOffset, int zOffset) {
        Float[][] attributeArray = getVisibleSidesOfBlocks(sideOffset, xOffset, yOffset, zOffset);
        vertexesC.addAll(Arrays.asList(attributeArray[0]));
        colorsC.addAll(Arrays.asList(attributeArray[1]));
        normalsC.addAll(Arrays.asList(attributeArray[2]));
    }

    //можно отрефакторить но пока лень
    public void genBlocksMash() {
        for (int x = 0; x < this.sizeXZ; x++) {
            for (int y = 0; y < this.sizeY; y++) {
                for (int z = 0; z < this.sizeXZ; z++) {
                    double s = glfwGetTime();
                    if (bl[blocks[x][y][z]].getType() == 0) {
                        if (x != 0) {
                            if (bl[blocks[x - 1][y][z]].getType() != 0) {//1
                                this.addAttributesDataToCollections(3, x - 1, y, z);
                            }
                        }
                        if (y != 0) {
                            if (bl[blocks[x][y - 1][z]].getType() != 0) {//2
                                this.addAttributesDataToCollections(5, x, y - 1, z);
                            }
                        }
                        if (z != 0) {
                            if (bl[blocks[x][y][z - 1]].getType() != 0) {//3
                                this.addAttributesDataToCollections(1, x, y, z - 1);
                            }
                        }
                    }
                    if (bl[blocks[x][y][z]].getType() != 0) {
                        if (x != 0) {
                            if (bl[blocks[x - 1][y][z]].getType() == 0) {//4
                                this.addAttributesDataToCollections(2, x, y, z);
                            }
                        }
                        if (y != 0) {
                            if (bl[blocks[x][y - 1][z]].getType() == 0) {//5
                                this.addAttributesDataToCollections(3, x, y, z);
                            }
                        }
                        if (z != 0) {
                            if (bl[blocks[x][y][z - 1]].getType() == 0) {//6
                                this.addAttributesDataToCollections(0, x, y, z);
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