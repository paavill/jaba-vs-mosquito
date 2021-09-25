import org.joml.*;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Block {
    private Vector3f position;
    private float[] vert;
    private int[] ind;
    public Block(Vector3f position, float[] vert, int[] ind) {
        this.position = position;
        this.vert = vert;
        this.ind = ind;
    }

    public int createVAO(){

        int VBO = glGenBuffers();
        int VAO = glGenVertexArrays();
        int EBO = glGenBuffers();

        glBindVertexArray(VAO);

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER,this.vert,GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, this.ind, GL_STATIC_DRAW);

        glVertexAttribPointer(0,3,GL_FLOAT,false,0, 0);
        glEnableVertexAttribArray(0);


        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER,0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        return VAO;
    };

}
