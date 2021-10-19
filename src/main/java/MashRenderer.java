import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL30.*;

public class MashRenderer {
    //будет не Chunk а интерфейс
    private static Map<Chunk, Integer> objectsToRender = new HashMap<>();
    private static int shaderProgram = 0;
    private static FloatBuffer fb = BufferUtils.createFloatBuffer(16);

    public static void setShaderProgram(int sh){
        shaderProgram = sh;
    }

    public static void addObjectToDrow(Chunk object){
        int VAO = getVAO(object);
        objectsToRender.put(object, VAO);
    }

    private static int getVAO(Chunk object){
        int VBO = glGenBuffers();
        int VAO = glGenVertexArrays();

        glBindVertexArray(VAO);
        Float[] buff = object.getMash().getVertex();
        float[] tbuff = new float[buff.length];
        for(int i = 0; i < buff.length; i++){
            tbuff[i] = buff[i].floatValue();
        }
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, tbuff, GL_STATIC_DRAW);

        glVertexAttribPointer(0,3,GL_FLOAT,false,12, 0);
        glEnableVertexAttribArray(0);

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER,0);
        return VAO;
    }

    public static void drawAll(){
        glUseProgram(shaderProgram);
        objectsToRender.forEach((o, v) -> draw(o,v));
        glUseProgram(0);
    }

    private static void draw(Chunk obj, int VAO){
        Matrix4f model = new Matrix4f();
        model.translate(obj.getPosition());
        fb.clear();
        int atrPos = glGetUniformLocation(shaderProgram, "model");
        glUniformMatrix4fv(atrPos, false, model.get(fb));

        glBindVertexArray(VAO);
        int count = obj.getMash().getVertexCount();
        glDrawArrays(GL_TRIANGLES, 0, count);
        glBindVertexArray(0);
    }
}
