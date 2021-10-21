import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

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
    private final static int sizeXZ = 64;
    private final static int sizeY = 128;
    private boolean changed = false;

    //Временное поле, надо убрать
    private int vertexCount;

    private Float[] toDrawVertexBuffer;
    private Float[] toDrawColorsBuffer;
    private Float[] toDrawNormalsBuffer;

    private short[][][] blocks = new short[sizeXZ][sizeY][sizeXZ];

    public Chunk(Vector3f position) {
        this.position = position;
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public void generate() {

        FloatBuffer bf = BufferUtils.createFloatBuffer(16);
        for (int x = 0; x < this.sizeXZ; x++) {
            for (int y = 0; y < this.sizeY; y++) {
                for (int z = 0; z < this.sizeXZ; z++) {
                    if (y < 10 + (Math.sin(x) + Math.cos(z))) {
                        blocks[x][y][z] = 1;
                    } else {
                        blocks[x][y][z] = 0;
                    }
                }
            }
        }
    }

    public void setBlock(int x, int y, int z, Block block) {
        this.changed = true;
        //доделать
    }

    public boolean getChanged() {
        return this.changed;
    }

    public void clear() {
        this.toDrawVertexBuffer = null;
        this.toDrawNormalsBuffer = null;
        this.toDrawColorsBuffer = null;
    }

    public Float[] getToDrawVertexBuffer() {
        return this.toDrawVertexBuffer;
    }

    private void addOffsetToAttributes(Float[] vertex, Float[] colors, Float[] normals, int xOffset, int yOffset, int zOffset) {
        for (int i = 0; i < vertex.length; i += 3) {
            vertex[i] += xOffset;
            vertex[i + 1] += yOffset;
            vertex[i + 2] += zOffset;
        }

    }

    private Float[][] getVisibleSidesOfBlocksVertex(int sideOffset, int xOffset, int yOffset, int zOffset) {
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
        this.addOffsetToAttributes(vertexArray, colorsArray, normalsArray, xOffset, yOffset, zOffset);
        Float[][] result = new Float[][]{
                vertexArray,
                colorsArray,
                normalsArray
        };
        return result;
    }

    //можно отрефакторить но пока лень
    public void genBlocksMash() {
        this.toDrawVertexBuffer = new Float[0];
        Collection<Float> vertexesC = new ArrayList<>();
        Collection<Float> colorsC = new ArrayList<>();
        Collection<Float> normalsC = new ArrayList<>();
        for (int x = 1; x < this.sizeXZ; x++) {
            for (int y = 1; y < this.sizeY; y++) {
                for (int z = 1; z < this.sizeXZ; z++) {
                    double s = glfwGetTime();
                    if (bl[blocks[x][y][z]].getType() == 0) {
                        if (bl[blocks[x - 1][y][z]].getType() != 0) {//1
                            Float[][] attributeArray = getVisibleSidesOfBlocksVertex(3, x - 1, y, z);
                            vertexesC.addAll(Arrays.asList(attributeArray[0]));
                            colorsC.addAll(Arrays.asList(attributeArray[1]));
                            normalsC.addAll(Arrays.asList(attributeArray[2]));
                        }
                        if (bl[blocks[x][y - 1][z]].getType() != 0) {//2
                            Float[][] attributeArray = getVisibleSidesOfBlocksVertex(5, x, y - 1, z);
                            vertexesC.addAll(Arrays.asList(attributeArray[0]));
                            colorsC.addAll(Arrays.asList(attributeArray[1]));
                            normalsC.addAll(Arrays.asList(attributeArray[2]));
                        }
                        if (bl[blocks[x][y][z - 1]].getType() != 0) {//3
                            Float[][] attributeArray = getVisibleSidesOfBlocksVertex(1, x, y, z - 1);
                            vertexesC.addAll(Arrays.asList(attributeArray[0]));
                            colorsC.addAll(Arrays.asList(attributeArray[1]));
                            normalsC.addAll(Arrays.asList(attributeArray[2]));
                        }
                    }
                    if (bl[blocks[x][y][z]].getType() != 0) {
                        if (bl[blocks[x - 1][y][z]].getType() == 0) {//4
                            Float[][] attributeArray = getVisibleSidesOfBlocksVertex(2, x, y, z);
                            vertexesC.addAll(Arrays.asList(attributeArray[0]));
                            colorsC.addAll(Arrays.asList(attributeArray[1]));
                            normalsC.addAll(Arrays.asList(attributeArray[2]));
                        }
                        if (bl[blocks[x][y - 1][z]].getType() == 0) {//5
                            Float[][] attributeArray = getVisibleSidesOfBlocksVertex(3, x, y, z);
                            vertexesC.addAll(Arrays.asList(attributeArray[0]));
                            colorsC.addAll(Arrays.asList(attributeArray[1]));
                            normalsC.addAll(Arrays.asList(attributeArray[2]));
                        }
                        if (bl[blocks[x][y][z - 1]].getType() == 0) {//6
                            Float[][] attributeArray = getVisibleSidesOfBlocksVertex(0, x, y, z);
                            vertexesC.addAll(Arrays.asList(attributeArray[0]));
                            colorsC.addAll(Arrays.asList(attributeArray[1]));
                            normalsC.addAll(Arrays.asList(attributeArray[2]));
                        }
                    }
                }

            }
        }
        Float[] arr = new Float[0];
        this.toDrawVertexBuffer = vertexesC.toArray(arr);
        this.vertexCount = this.toDrawVertexBuffer.length / 3;
        arr = new Float[0];
        this.toDrawColorsBuffer = colorsC.toArray(arr);
        arr = new Float[0];
        this.toDrawNormalsBuffer = normalsC.toArray(arr);
        vertexesC.clear();
        colorsC.clear();
        normalsC.clear();
    }

    public Float[] getToDrawColorsBuffer() {
        return toDrawColorsBuffer;
    }

    public Float[] getToDrawNormalsBuffer() {
        return toDrawNormalsBuffer;
    }

    public int getVertexCount() {
        return this.vertexCount;
    }
}
