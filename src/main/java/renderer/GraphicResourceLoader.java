package renderer;

import org.lwjgl.BufferUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Scanner;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

public class GraphicResourceLoader {
    private static StringBuilder loadShaderSourceFromFile(String filePath) throws FileNotFoundException {
        File shaderFile = new File(GraphicResourceLoader.buildPath(filePath));
        Scanner scanner = new Scanner(shaderFile);
        StringBuilder str = new StringBuilder();
        while(scanner.hasNext()){
            str.append(scanner.nextLine()).append("\n");
        }
        return str;
    }

    private static int compileShader(CharSequence shaderSource, int type){
        int shader = glCreateShader(type);
        glShaderSource(shader, shaderSource);
        glCompileShader(shader);
        return shader;
    }

    public static int linkShaderProgram(String vertexShaderFilePath, String fragmentShaderFilePath) throws FileNotFoundException {
        CharSequence sourceVertexShader = GraphicResourceLoader.loadShaderSourceFromFile(vertexShaderFilePath);
        CharSequence sourceFragmentShader = GraphicResourceLoader.loadShaderSourceFromFile(fragmentShaderFilePath);
        int vertexShader = GraphicResourceLoader.compileShader(sourceVertexShader, GL_VERTEX_SHADER);
        int fragmentShader = GraphicResourceLoader.compileShader(sourceFragmentShader, GL_FRAGMENT_SHADER);
        int shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
        return shaderProgram;
    }
    //Доделать загрузку текстур
    public void loadTexture(String path){
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer components = BufferUtils.createIntBuffer(1);
        //stb
        //stbi_load("org/lwjgl/demo/opengl/textures/environment.jpg", width, height)
        //ByteBuffer data = stbi_load_from_memory(ioRe ioResourceToByteBuffer("org/lwjgl/demo/opengl/textures/environment.jpg", 1024), width, height, components, 4);
        int id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        //glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(), height.get(), 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
        //stbi_image_free(data);
    }

    private static String buildPath(String filePath) {
        return GraphicResourceLoader.class.getClassLoader().getResource("shaders/" + filePath).getPath();
    }
}
