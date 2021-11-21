package renderer;

import main.BlocksModelsInitializer;
import main.Chunk;
import main.Tuple;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;

import java.nio.FloatBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL30.*;

public class MeshRenderer {

    private int shaderProgram = 0;
    private static FloatBuffer fb = BufferUtils.createFloatBuffer(16);

    public MeshRenderer(int shaderProgram){
        this.shaderProgram = shaderProgram;
    }

    public void setShaderProgram(int sh) {
        shaderProgram = sh;
    }

    private float[] arrayWrapperToSimple(Float[] array) {
        float[] buff = new float[array.length];
        for (int i = 0; i < buff.length; i++) {
            buff[i] = array[i].floatValue();
        }
        return buff;
    }

    public void deleteVAO(Tuple<Integer, Integer[]> toDelete){
        glBindVertexArray(0);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(3);
        glDeleteVertexArrays(toDelete.first);
        for (Integer e: toDelete.second) {
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glDeleteBuffers(e);
        }

    }

    public Tuple<Integer, Integer[]> getVAO(Chunk object) {
        int[] VBOs = new int[4];
        glGenBuffers(VBOs);
        int VAO = glGenVertexArrays();
        Float[] tempArray = new Float[0];
        float[][] attributeData = {
                arrayWrapperToSimple(object.getToDrawVertexBuffer().toArray(tempArray)),
                arrayWrapperToSimple(object.getToDrawColorsBuffer().toArray(tempArray)),
                arrayWrapperToSimple(object.getToDrawNormalsBuffer().toArray(tempArray)),
                arrayWrapperToSimple(object.getTexC().toArray(tempArray))
        };
        if(object.getToDrawVertexBuffer().size() == 0){
            int i = 0;
        }
        object.getToDrawVertexBuffer().clear();
        object.getToDrawColorsBuffer().clear();
        object.getToDrawNormalsBuffer().clear();
        object.getTexC().clear();

        glBindVertexArray(VAO);
        for (int i = 0; i < 3; i++) {
            glBindBuffer(GL_ARRAY_BUFFER, VBOs[i]);
            glBufferData(GL_ARRAY_BUFFER, attributeData[i], GL_STATIC_DRAW);
            glVertexAttribPointer(i, 3, GL_FLOAT, false, 0, 0);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
        }
        glBindBuffer(GL_ARRAY_BUFFER, VBOs[3]);
        glBufferData(GL_ARRAY_BUFFER, attributeData[3], GL_STATIC_DRAW);
        glVertexAttribPointer(3, 2, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        Integer[] vbos = new Integer[VBOs.length];
        for(int i = 0; i < vbos.length; i++){
            vbos[i] = VBOs[i];
        }
        Tuple<Integer, Integer[]> result = new Tuple<>(VAO,vbos);
        return result;
    }

    public void drawAll(Map<Chunk, Tuple<Integer, Integer[]>> objectsToRender) {
        objectsToRender.forEach((o, v) -> draw(o, v.first));
    }

    private void draw(Chunk obj, int VAO) {
        Matrix4f model = new Matrix4f();
        model.translate(obj.getPosition());
        fb.clear();
        int atrPos = glGetUniformLocation(shaderProgram, "model");
        glUniformMatrix4fv(atrPos, false, model.get(fb));

        glBindVertexArray(VAO);
        BlocksModelsInitializer.getTextureAtlas().bind();
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);
        glDrawArrays(GL_TRIANGLES, 0, obj.getVertexCount());
        BlocksModelsInitializer.getTextureAtlas().unBind();
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(3);
        glBindVertexArray(0);
    }
}
