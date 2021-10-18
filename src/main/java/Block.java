import org.joml.*;
import org.lwjgl.BufferUtils;

import java.lang.Math;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL30.*;

public class Block {
    private Vector3f position;
    private int type;
    private Collection<Integer> nonDrowedSides = new ArrayList<Integer>();
    private float toDraw[];
    private FloatBuffer fb;
    private int VAO = 0;
    private int VBO = 0;
    private float[] vert;

    public Block(){

    }
    public Block(Vector3f position,FloatBuffer fb, Mash data, Collection<Integer> nonDrowedSides, int type)
    {
        this.fb = fb;
        this.type = type;
        this.position = position;
        this.nonDrowedSides = nonDrowedSides;
        this.vert = Mash.vert;
        this.toDraw = Mash.toDraw;
        if(type!=0 && this.nonDrowedSides.size() != 6) {
            this.createVAO();
        }else {
            glBindVertexArray(this.VAO);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glDeleteBuffers(this.VBO);
            glBindVertexArray(0);
            glDeleteVertexArrays(this.VAO);
        }
    }

    public void clearCollection(){
        this.nonDrowedSides = null;
        this.toDraw = null;
        this.vert = null;
    }

    public  int getSize(){
        return this.nonDrowedSides.size();
    }

    public void move(Vector3f movementVector){
        this.position.add(movementVector);
    }

    public void addNonDrowedSide(int number){
        this.nonDrowedSides.add(number);
        this.toDraw = new float[0];
        if(type!=0 && this.nonDrowedSides.size() != 6) {
            this.createVAO();
        } else {
            glBindVertexArray(this.VAO);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glDeleteBuffers(this.VBO);
            glBindVertexArray(0);
            glDeleteVertexArrays(this.VAO);
        }
    }

    private float[] add(float[] first, float[] second){
        float[] newArray = Arrays.copyOf(first, first.length + second.length);
        for(int i = first.length; i < newArray.length; i++){
            newArray[i] = second[i - first.length];
        }
        return  newArray;
    }

    public int getType(){
        return this.type;
    }

    public Vector3f getPosition(){
        return this.position;
    }

    public void createVAO(){

        for(int i = 0; i < 6; i++){
            if(!this.nonDrowedSides.contains(i)) {
                this.toDraw = this.add(toDraw, Arrays.copyOfRange(this.vert, i* 54, i * 54 + 54));
            }
        }

        glBindVertexArray(this.VAO);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(this.VBO);
        glBindVertexArray(0);
        glDeleteVertexArrays(this.VAO);

        this.VBO = glGenBuffers();
        this.VAO = glGenVertexArrays();

        glBindVertexArray(VAO);

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER,this.toDraw,GL_STATIC_DRAW);

        glVertexAttribPointer(0,3,GL_FLOAT,false,36, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1,3,GL_FLOAT,false,36, 12);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2,3,GL_FLOAT,false,36, 24);
        glEnableVertexAttribArray(2);
        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER,0);

    };

    public void draw(int shaderProgram){
        if(this.type != 0 && this.nonDrowedSides.size() != 6) {
            Matrix4f model = new Matrix4f();
            model.translate(this.position);
            int atrPos = glGetUniformLocation(shaderProgram, "model");
            glUniformMatrix4fv(atrPos, false, model.get(fb));
            fb.clear();

            glBindVertexArray(this.VAO);
            glDrawArrays(GL_TRIANGLES, 0, this.toDraw.length / 9);
            glBindVertexArray(0);
        }
    }

}
