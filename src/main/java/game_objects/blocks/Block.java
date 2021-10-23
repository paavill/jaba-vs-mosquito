package game_objects.blocks;

import renderer.Mesh;

import java.util.*;

public class Block {
    private short type;
    private Mesh mesh;
    private boolean special;

    public Block(short type, Mesh mash, boolean special) {
        this.type = type;
        this.mesh = mash;
        this.special = special;
    }

    public Mesh getSideMesh(int offset){
        List<Float> vertex = (List<Float>) this.mesh.getVertex();
        List<Float>  colors = (List<Float>)this.mesh.getColors();
        List<Float>  normals = (List<Float>)this.mesh.getNormals();

        ArrayList<Float> topSideVertex = new ArrayList<Float>(vertex.subList(offset*18, offset*18 + 18));
        ArrayList<Float> topSideColors = new ArrayList<Float>(colors.subList(offset*18, offset*18 + 18));
        ArrayList<Float> topSideNormals = new ArrayList<Float>(normals.subList(offset*18, offset*18 + 18));
        return new Mesh(topSideVertex, topSideColors, topSideNormals);
    }

    public int getType(){
        return this.type;
    }

    public Mesh getMesh(){
        ArrayList<Float> v = new ArrayList<Float>(this.mesh.getVertex().size());
        Collections.copy(v, (List<Float>) this.mesh.getVertex());
        ArrayList<Float> c = new ArrayList<Float>(this.mesh.getColors().size());
        Collections.copy(v, (List<Float>) this.mesh.getColors());
        ArrayList<Float> n = new ArrayList<Float>(this.mesh.getNormals().size());
        Collections.copy(v, (List<Float>) this.mesh.getNormals());
        return new Mesh(v, c, n);
    }

    public boolean getSpecial(){
        return this.special;
    }
}
