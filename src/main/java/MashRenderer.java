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
        int[] VBOs = new int[3];
        glGenBuffers(VBOs);
        int VAO = glGenVertexArrays();
        Float[] tempArray = new Float[0];
        float[][] attributeData = {
                arrayWrapperToSimple(object.getToDrawVertexBuffer().toArray(tempArray)),
                arrayWrapperToSimple(object.getToDrawColorsBuffer().toArray(tempArray)),
                arrayWrapperToSimple(object.getToDrawNormalsBuffer().toArray(tempArray))
        };
        object.getToDrawVertexBuffer().clear();
        object.getToDrawColorsBuffer().clear();
        object.getToDrawNormalsBuffer().clear();

        glBindVertexArray(VAO);
        for(int i = 0; i < 3; i++){
            glBindBuffer(GL_ARRAY_BUFFER, VBOs[i]);
            glBufferData(GL_ARRAY_BUFFER, attributeData[i], GL_STATIC_DRAW);
            glVertexAttribPointer(i,3,GL_FLOAT,false,0, 0);
            glEnableVertexAttribArray(i);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
        }

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
        glDrawArrays(GL_TRIANGLES, 0, obj.getVertexCount());
        glBindVertexArray(0);
    }
}
