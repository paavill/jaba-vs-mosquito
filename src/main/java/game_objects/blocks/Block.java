package game_objects.blocks;

import game_objects.BlockType;
import renderer.Mesh;
import renderer.MeshSideType;
import renderer.SideDataGenerator;

import java.util.*;

public class Block {
    private final BlockType type;
    private final Mesh mesh;
    private final boolean special;
    private final int floatValuesOnSide = 18;
    private final SideDataGenerator sideDataGenerator;
    private final HashMap<MeshSideType, Mesh> sides;

    public Block(BlockType type, Mesh mash, boolean special) {
        this.type = type;
        this.mesh = mash;
        this.special = special;
        if(special || type == BlockType.AIR){
           this.sideDataGenerator = null;
           this.sides = new HashMap<>();
        } else {
            this.sideDataGenerator = new SideDataGenerator(this.floatValuesOnSide);
            this.sides = this.sideDataGenerator.genSidesMeshes(this.mesh);
        }
    }

    public Mesh getSideMesh(MeshSideType side){
        return new Mesh(this.sides.get(side));
    }

    public BlockType getType(){
        return this.type;
    }

    public Mesh getMesh(){
        ArrayList<Float> v = new ArrayList<Float>(this.mesh.getVertex());
        ArrayList<Float> c = new ArrayList<Float>(this.mesh.getColors());
        ArrayList<Float> n = new ArrayList<Float>(this.mesh.getNormals());
        return new Mesh(v, c, n);
    }

    public boolean getSpecial(){
        return this.special;
    }
}
