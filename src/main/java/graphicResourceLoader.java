import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class graphicResourceLoader {
    public static StringBuilder loadShader(String filePath) throws FileNotFoundException {
        File shaderFile = new File(filePath);
        Scanner scanner = new Scanner(shaderFile);
        StringBuilder str = new StringBuilder();
        while(scanner.hasNext()){
            str.append(scanner.nextLine()).append("\n");
        }
        return str;
    }
}
