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

    public static void addObjectToDraw(Chunk object){
        int VAO = getVAO(object);
        objectsToRender.put(object, VAO);
    }

    private static float[] arrayWrapperToSimple(Float[] array){
        float[] buff = new float[array.length];
        for(int i = 0; i < buff.length; i++){
            buff[i] = array[i].floatValue();
        }
        return  buff;
    }

    private static int getVAO(Chunk object){
        int VBO0 = glGenBuffers();
        int VBO1 = glGenBuffers();
        int VBO2 = glGenBuffers();
        int VAO = glGenVertexArrays();

        float[] wrapBuffVertex = arrayWrapperToSimple(object.getToDrawVertexBuffer());
        float[] wrapBuffColors = arrayWrapperToSimple(object.getToDrawColorsBuffer());
        float[] wrapBuffNormals = arrayWrapperToSimple(object.getToDrawNormalsBuffer());

        object.clear();
        glBindVertexArray(VAO);

        glBindBuffer(GL_ARRAY_BUFFER, VBO0);
        glBufferData(GL_ARRAY_BUFFER, wrapBuffVertex, GL_STATIC_DRAW);
        glVertexAttribPointer(0,3,GL_FLOAT,false,0, 0);
        glEnableVertexAttribArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ARRAY_BUFFER, VBO1);
        glBufferData(GL_ARRAY_BUFFER, wrapBuffColors, GL_STATIC_DRAW);
        glVertexAttribPointer(1,3,GL_FLOAT,false,0, 0);
        glEnableVertexAttribArray(1);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ARRAY_BUFFER, VBO2);
        glBufferData(GL_ARRAY_BUFFER, wrapBuffNormals, GL_STATIC_DRAW);
        glVertexAttribPointer(2,3,GL_FLOAT,false,0, 0);
        glEnableVertexAttribArray(2);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER,0);
        wrapBuffVertex = null;
        wrapBuffColors = null;
        wrapBuffNormals = null;
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
        int count = obj.getVertexCount();
        glDrawArrays(GL_TRIANGLES, 0, count);
        glBindVertexArray(0);
    }
}
