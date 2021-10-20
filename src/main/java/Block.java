import java.util.Arrays;

import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

public class Block {
    private short type;
    private Mash mash;
    private boolean special;
    public Block(short type, Mash mash, boolean special)
    {
        this.type = type;
        this.mash = mash;
        this.special = special;
    }

    public Mash getSideMash(int offset){
        Float[] vertex = this.mash.getVertex();
        Float[] colors = this.mash.getColors();
        Float[] normals = this.mash.getNormals();

        Float[] topSideVertex = Arrays.copyOfRange(vertex, offset*18, offset*18 + 18);
        Float[] topSideColors = Arrays.copyOfRange(colors, offset*18, offset*18 + 18);
        Float[] topSideNormals = Arrays.copyOfRange(normals, offset*18, offset*18 + 18);
        return new Mash(topSideVertex, topSideColors, topSideNormals);
    }

    public int getType(){
        return this.type;
    }

    public Mash getMash(){
        return this.mash;
    }

    public boolean getSpecial(){
        return this.special;
    }
}
