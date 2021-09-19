import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glCompileShader;

public class GraphicResourceLoader {
    public static StringBuilder loadShaderSourceFromFile(String filePath) throws FileNotFoundException {
        File shaderFile = new File(filePath);
        Scanner scanner = new Scanner(shaderFile);
        StringBuilder str = new StringBuilder();
        while(scanner.hasNext()){
            str.append(scanner.nextLine()).append("\n");
        }
        return str;
    }

    public static int compileShader(CharSequence shaderSource, int type){
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
}
