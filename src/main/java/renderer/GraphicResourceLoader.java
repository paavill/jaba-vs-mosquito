package renderer;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Scanner;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.stb.STBImage.*;

public class GraphicResourceLoader {
    private static StringBuilder loadShaderSourceFromFile(String filePath, String folderPath) throws FileNotFoundException {
        File shaderFile = new File(GraphicResourceLoader.buildPath(filePath, folderPath));
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

    public static int linkShaderProgram(String vertexShaderFilePath, String fragmentShaderFilePath, String folderPath) throws FileNotFoundException {
        CharSequence sourceVertexShader = GraphicResourceLoader.loadShaderSourceFromFile(vertexShaderFilePath, folderPath);
        CharSequence sourceFragmentShader = GraphicResourceLoader.loadShaderSourceFromFile(fragmentShaderFilePath, folderPath);
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
    public static Texture loadTexture(String path, String folderPath){
        ByteBuffer image;
        int width, height;
        String filePath = buildPath(path, folderPath);
        File f = new File(filePath);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            /* Prepare image buffers */
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            /* Load image */
            stbi_set_flip_vertically_on_load(true);
            image = stbi_load(f.getPath(), w, h, comp, 4);
            if (image == null) {
                throw new RuntimeException("Failed to load a texture file!"+"!!!!" + stbi_failure_reason());
            }

            /* Get width and height of image */
            width = w.get();
            height = h.get();
        }
        Texture texture = new Texture(width, height, image);
        return texture;
    }

    private static String buildPath(String filePath, String folderPath) {
        return GraphicResourceLoader.class.getClassLoader().getResource(folderPath + filePath).getPath();
    }
}
