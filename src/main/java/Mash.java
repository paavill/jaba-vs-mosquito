public class Mash {
    private Float[] vertex;
    private Float[] colors;
    private Float[] normals;

    private int countOfInitAttr;

    public Mash(){
        super();
        this.vertex = new Float[0];
        this.colors = this.vertex;
    }

    public Mash(Float[] vertex){
        this.vertex = vertex;
        this.colors = new Float[0];
        this.countOfInitAttr = 1;
    }

    public Mash(Float[] vertex, Float[] colors){
        this.vertex = vertex;
        this.colors = colors;
        this.countOfInitAttr = 2;
    }

    public Mash(Float[] vertex, Float[] colors, Float[] normals){
        this.vertex = vertex;
        this.colors = colors;
        this.normals = normals;
        this.countOfInitAttr = 3;
    }

    public int getCountOfInitAttribute(){
        return  this.countOfInitAttr;
    }

    public Float[] getVertex(){
        return this.vertex;
    }

    public int getVertexCount(){
        return this.vertex.length/3;
    }

    public Float[] getColors(){
        return this.colors;
    }

    public int getColorsCount(){
        return this.colors.length/3;
    }

    public Float[] getNormals(){
        return this.colors;
    }

    public int getNormalsCount(){
        return this.colors.length/3;
    }
}
