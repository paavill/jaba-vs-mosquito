package renderer;

import java.util.ArrayList;
import java.util.Collection;

public class Mesh {
    private ArrayList<Float> vertex;
    private ArrayList<Float> colors;
    private ArrayList<Float> normals;

    private int countOfInitAttr;

    public Mesh(){
        super();
        this.vertex = new ArrayList<>();
        this.colors = this.vertex;
        this.normals = this.colors;
        this.countOfInitAttr = 0;
    }

    public Mesh(ArrayList<Float> vertex){
        this.vertex = vertex;
        this.colors = new ArrayList<>();
        this.countOfInitAttr = 1;
    }

    public Mesh(ArrayList<Float> vertex, ArrayList<Float> colors){
        this.vertex = vertex;
        this.colors = colors;
        this.countOfInitAttr = 2;
    }

    public Mesh(ArrayList<Float> vertex, ArrayList<Float> colors, ArrayList<Float> normals){
        this.vertex = vertex;
        this.colors = colors;
        this.normals = normals;
        this.countOfInitAttr = 3;
    }

    public int getCountOfInitAttribute(){
        return  this.countOfInitAttr;
    }

    public ArrayList<Float> getVertex(){
        return this.vertex;
    }


    public int getVertexCount(){
        return this.vertex.size()/3;
    }

    public ArrayList<Float> getColors(){
        return this.colors;
    }

    public int getColorsCount(){
        return this.colors.size()/3;
    }

    public ArrayList<Float> getNormals(){
        return this.colors;
    }

    public int getNormalsCount(){
        return this.colors.size()/3;
    }
}
