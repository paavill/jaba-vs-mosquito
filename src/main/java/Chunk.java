import org.joml.SimplexNoise;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.*;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Chunk {
    //сделать загрузку из файла
    //и вообще инициализация должна быть не здесь (абстракции...)

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

    private static final Mash blockMash = new Mash(new ArrayList<>(blockVertex), new ArrayList<>(blockCollors), new ArrayList<>(blockNormales));
    private static final Mash m = new Mash();
    private static final Block[] bl = {new Block((short) 0, m, false),
            new Block((short) 1, blockMash, false)};

    private final Vector3f position;
    private final static int sizeXZ = 16;
    private final static int sizeY = 256;
    private boolean changed = false;

    private int vertexCount = 0;

    //Начать предоставлять извне, поскольку хранить в чанке невыгодно
    //а статик не позволит сделать многопоточку (предоставлять при создании отдельного потока)
    //но пока статик, чтобы показать минимальную занимаемую память
    private static Collection<Float> vertexesC = new ArrayList<>();
    private static Collection<Float> colorsC = new ArrayList<>();
    private static Collection<Float> normalsC = new ArrayList<>();

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
                    if (y < 60 + 60*SimplexNoise.noise((x+this.position.x)/30.f, (z+this.position.z)/30.f)) {
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
            vertex.set(i+1, vertex.get(i+1) + yOffset);
            vertex.set(i+2, vertex.get(i+2) + zOffset);
        }

    }

    private Collection<ArrayList<Float>> getVisibleSidesOfBlocks(int sideOffset, int xOffset, int yOffset, int zOffset) {
        Mash currentMash;
        if (!bl[blocks[xOffset][yOffset][zOffset]].getSpecial()) {
            currentMash = bl[blocks[xOffset][yOffset][zOffset]].getSideMash(sideOffset);
        } else {
            currentMash = bl[blocks[xOffset][yOffset][zOffset]].getMash();
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

    private void addAttributesDataToCollections(int sideOffset, int xOffset, int yOffset, int zOffset) {
        ArrayList<ArrayList<Float>> attributeArray = (ArrayList<ArrayList<Float>>)getVisibleSidesOfBlocks(sideOffset, xOffset, yOffset, zOffset);
        vertexesC.addAll(attributeArray.get(0));
        colorsC.addAll(attributeArray.get(1));
        normalsC.addAll(attributeArray.get(2));
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