import java.util.*;

import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

public class Block {
    private short type;
    private Mesh mash;
    private boolean special;
    public Block(short type, Mesh mash, boolean special)
    {
        this.type = type;
        this.mash = mash;
        this.special = special;
    }

    public Mesh getSideMash(int offset){
        List<Float> vertex = (List<Float>) this.mash.getVertex();
        List<Float>  colors = (List<Float>)this.mash.getColors();
        List<Float>  normals = (List<Float>)this.mash.getNormals();

        ArrayList<Float> topSideVertex = new ArrayList<Float>(vertex.subList(offset*18, offset*18 + 18));
        ArrayList<Float> topSideColors = new ArrayList<Float>(colors.subList(offset*18, offset*18 + 18));
        ArrayList<Float> topSideNormals = new ArrayList<Float>(normals.subList(offset*18, offset*18 + 18));
        return new Mesh(topSideVertex, topSideColors, topSideNormals);
    }

    public int getType(){
        return this.type;
    }

    public Mesh getMash(){
        ArrayList<Float> v = new ArrayList<Float>(this.mash.getVertex().size());
        Collections.copy(v, (List<Float>) this.mash.getVertex());
        ArrayList<Float> c = new ArrayList<Float>(this.mash.getColors().size());
        Collections.copy(v, (List<Float>) this.mash.getColors());
        ArrayList<Float> n = new ArrayList<Float>(this.mash.getNormals().size());
        Collections.copy(v, (List<Float>) this.mash.getNormals());
        return new Mesh(v, c, n);
    }

    public boolean getSpecial(){
        return this.special;
    }
}
