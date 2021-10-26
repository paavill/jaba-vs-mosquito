package renderer;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Mesh {
    private final ArrayList<Float> vertex;
    private final ArrayList<Float> colors;
    private final ArrayList<Float> normals;
    private final ArrayList<Float> textureCoords;

    private int countOfInitAttr;

    public Mesh() {
        super();
        this.vertex = new ArrayList<>();
        this.colors = this.vertex;
        this.normals = this.colors;
        this.textureCoords = this.normals;
        this.countOfInitAttr = 0;
    }

    public Mesh(ArrayList<Float> vertex) {
        this.vertex = vertex;
        this.colors = new ArrayList<>();
        this.normals = this.colors;
        this.textureCoords = this.normals;
        this.countOfInitAttr = 1;
    }

    public Mesh(ArrayList<Float> vertex, ArrayList<Float> colors) {
        this.vertex = vertex;
        this.colors = colors;
        this.normals = new ArrayList<>();
        this.textureCoords = this.normals;
        this.countOfInitAttr = 2;
    }

    public Mesh(ArrayList<Float> vertex, ArrayList<Float> colors, ArrayList<Float> normals) {
        this.vertex = vertex;
        this.colors = colors;
        this.normals = normals;
        this.textureCoords = this.normals;
        this.countOfInitAttr = 3;
    }

    public Mesh(ArrayList<Float> vertex, ArrayList<Float> colors, ArrayList<Float> normals, ArrayList<Float> textureCoords) {
        this.vertex = vertex;
        this.colors = colors;
        this.normals = normals;
        this.textureCoords = textureCoords;
        this.countOfInitAttr = 4;
    }

    public Mesh(Mesh mesh) {
        this.vertex = new ArrayList<>(mesh.vertex);
        this.colors = new ArrayList<>(mesh.colors);
        this.normals = new ArrayList<>(mesh.normals);
        this.textureCoords = new ArrayList<>(mesh.textureCoords);
        this.countOfInitAttr = mesh.countOfInitAttr;
    }

    public int getCountOfInitAttribute() {
        return this.countOfInitAttr;
    }

    public ArrayList<Float> getVertex() {
        return this.vertex;
    }

    public ArrayList<Float> getColors() {
        return this.colors;
    }

    public ArrayList<Float> getNormals() {
        return this.colors;
    }

    public ArrayList<Float> getTextureCoords() {
        return this.textureCoords;
    }

}
