package renderer;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class Texture {

    private final int id;

    private int width;

    private int height;

    public Texture(int width, int height, ByteBuffer data){
        this.id = glGenTextures();
        this.width = width;
        this.height = height;
        this.bind();
        this.setParameter(GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        this.setParameter(GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        this.setParameter(GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        this.setParameter(GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        this.uploadData(GL_RGB, width, height, GL_RGBA, data);
        glGenerateMipmap(GL_TEXTURE_2D);
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public void setParameter(int name, int value) {
        glTexParameteri(GL_TEXTURE_2D, name, value);
    }

    public void uploadData(int internalFormat, int width, int height, int format, ByteBuffer data) {
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, GL_UNSIGNED_BYTE, data);
    }

    public void delete() {
        glDeleteTextures(id);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ArrayList<ArrayList<Float>> genTexCords(){
        ArrayList<ArrayList<Float>> texCord = new ArrayList<>();
        int offset = 16;
        int counter = 0;
        for(int w = 0; w < this.width; w+=16){
            for(int h = 0; h < this.height; h+=16){
                texCord.add(new ArrayList<>());
                texCord.get(counter).add((float)w/this.width);
                texCord.get(counter).add((float)h/this.height);
                texCord.get(counter).add((float)(w+offset)/this.width);
                texCord.get(counter).add((float)h/this.height);
                texCord.get(counter).add((float)(w+offset)/this.width);
                texCord.get(counter).add((float)(h+offset)/this.height);
                texCord.get(counter).add((float)(w+offset)/this.width);
                texCord.get(counter).add((float)(h+offset)/this.height);
                texCord.get(counter).add((float)w/this.width);
                texCord.get(counter).add((float)(h+offset)/this.height);
                texCord.get(counter).add((float)w/this.width);
                texCord.get(counter).add((float)h/this.height);
                counter++;
            }
        }
        return texCord;
    }
}
