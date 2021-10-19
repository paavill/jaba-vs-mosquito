import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class Chunk {
    private Vector3f position;
    private final int sizeXZ = 32;
    private final int sizeY = 128;
    private boolean changed = false;

    private Mash mash;

    private Block[][][] blocks = new Block[sizeXZ][sizeY][sizeXZ];

    public Chunk(Vector3f position) {
        this.position = position;
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public void generate() {
        //сделать загрузку из файла
        //и вообще инициализация должна быть не здесь (абстракции...)
        Float[] blockVertex = new Float[]{
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
        Float[] blockCollors = {
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
                0.5f, 0.5f, 0.5f,
                0.5f, 0.5f, 0.5f
        };

        Float[] blockNormales = {
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
        Mash blockMash = new Mash(blockVertex, blockCollors, blockNormales);
        FloatBuffer bf = BufferUtils.createFloatBuffer(16);
        for (int x = 0; x < this.sizeXZ; x++) {
            for (int y = 0; y < this.sizeY; y++) {
                for (int z = 0; z < this.sizeXZ; z++) {
                    if (y < 10 + Math.sin(x) + Math.cos(z)) {
                        blocks[x][y][z] = new Block(1, blockMash, false);
                    } else {
                        blocks[x][y][z] = new Block(0, new Mash(), false);
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

    public Mash getMash() {
        return this.mash;
    }

    private void addOffsetToVertex(Float[] vertex, int xOffset, int yOffset, int zOffset) {
        for (int i = 0; i < vertex.length; i += 3) {
            vertex[i] += xOffset;
            vertex[i + 1] += yOffset;
            vertex[i + 2] += zOffset;
        }
    }

    private Float[] getVisibleSidesOfBlocksVertex(int sideOffset, int xOffset, int yOffset, int zOffset) {
        Float[] vertexArray;
        Float[] collorsArray;
        Float[] normalesArray;
        if (!blocks[xOffset][yOffset][zOffset].getSpecial()) {
            vertexArray = blocks[xOffset][yOffset][zOffset].getSideMash(sideOffset).getVertex();
            this.addOffsetToVertex(vertexArray, xOffset, yOffset, zOffset);
        } else {
            vertexArray = blocks[xOffset][yOffset][zOffset].getMash().getVertex();
            this.addOffsetToVertex(vertexArray, xOffset, yOffset, zOffset);
        }
        return vertexArray;
    }

    //можно отрефакторить но пока лень
    public void genBlocksMash() {
        this.mash = new Mash();
        Collection<Float> points = new ArrayList<>();
        for (int x = 0; x < this.sizeXZ; x++) {
            for (int y = 0; y < this.sizeY; y++) {
                for (int z = 0; z < this.sizeXZ; z++) {
                    if (blocks[x][y][z].getType() == 0) {
                        if (x != 0) {
                            if (blocks[x - 1][y][z].getType() != 0) {//1
                                Float[] vertexArray = getVisibleSidesOfBlocksVertex(3, x - 1, y, z);
                                points.addAll(Arrays.asList(vertexArray));
                            }
                        }
                        if (y != 0) {
                            if (blocks[x][y - 1][z].getType() != 0) {//2
                                Float[] vertexArray = getVisibleSidesOfBlocksVertex(5, x, y - 1, z);
                                points.addAll(Arrays.asList(vertexArray));
                            }
                        }
                        if (z != 0) {
                            if (blocks[x][y][z - 1].getType() != 0) {//3
                                Float[] vertexArray = getVisibleSidesOfBlocksVertex(0, x, y, z - 1);
                                points.addAll(Arrays.asList(vertexArray));
                            }
                        }
                        if (x < this.sizeXZ - 1) {
                            if (blocks[x + 1][y][z].getType() != 0) {//4
                                Float[] vertexArray = getVisibleSidesOfBlocksVertex(2, x + 1, y, z);
                                points.addAll(Arrays.asList(vertexArray));
                            }
                        }
                        if (y < this.sizeY - 1) {
                            if (blocks[x][y + 1][z].getType() != 0) {//5
                                Float[] vertexArray = getVisibleSidesOfBlocksVertex(4, x, y + 1, z);
                                points.addAll(Arrays.asList(vertexArray));
                            }
                        }
                        if (z < this.sizeXZ - 1) {
                            if (blocks[x][y][z + 1].getType() != 0) {//6
                                Float[] vertexArray = getVisibleSidesOfBlocksVertex(1, x, y, z + 1);
                                points.addAll(Arrays.asList(vertexArray));
                            }
                        }
                    }
                }
            }
        }
        Float[] arr = new Float[0];
        arr = points.toArray(arr);
        this.mash = new Mash(arr);
    }
}
