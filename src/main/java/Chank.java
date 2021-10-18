import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;

public class Chank {
    private Vector3f position;
    private final int sizeXZ = 32;
    private final int sizeY = 128;

    private Block[][][] blocks = new Block[sizeXZ][sizeY][sizeXZ];

    public Chank(Vector3f position){
            this.position = position;
    }

    public void generate(){
        Mash mash = new Mash();
        FloatBuffer bf = BufferUtils.createFloatBuffer(16);
        for(int x = 0; x < this.sizeXZ; x++){
            for(int y = 0; y < this.sizeY; y++){
                for(int z = 0; z < this.sizeXZ; z++){
                    if(y < 128){
                        blocks[x][y][z]= new Block(new Vector3f(x,y,z), bf,mash, new ArrayList<Integer>(), 1);
                    } else {
                        blocks[x][y][z]= new Block(new Vector3f(x,y,z), bf ,mash, new ArrayList<Integer>(), 0);
                    }

                    if(z!=0){
                        if(blocks[x][y][z-1].getType() != 0 && blocks[x][y][z].getType() != 0){
                            blocks[x][y][z-1].addNonDrowedSide(1);
                            blocks[x][y][z].addNonDrowedSide(0);
                        }
                    }
                    if(y!=0){
                        if(blocks[x][y - 1][z].getType() != 0 && blocks[x][y][z].getType() != 0) {
                            blocks[x][y - 1][z].addNonDrowedSide(5);
                            blocks[x][y][z].addNonDrowedSide(4);
                        }
                    }
                    if(x!=0){
                        if(blocks[x - 1][y][z].getType() != 0 && blocks[x][y][z].getType() != 0) {
                            blocks[x - 1][y][z].addNonDrowedSide(3);
                            blocks[x][y][z].addNonDrowedSide(2);
                        }
                    }
                    blocks[x][y][z].move(this.position);
                    mash.toDraw = new float[0];
                }
            }
        }
    }

    public void draw(int shaderProgram){
        for(int x = 0; x < this.sizeXZ; x++){
            for(int y = 0; y < this.sizeY; y++){
                for(int z = 0; z < this.sizeXZ; z++){
                    blocks[x][y][z].draw(shaderProgram);
                }
            }
        }
    }

    public Vector3f getLastPos(){
        return this.blocks[this.sizeXZ][0][this.sizeXZ].getPosition();
    }
}
