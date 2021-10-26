package renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SideDataGenerator {

    private final int floatValuesOnSide;

    public SideDataGenerator(int floatValuesOnSide){
        this.floatValuesOnSide = floatValuesOnSide;
    }

    private Mesh genSideData(MeshSideType side, Mesh mesh){
        List<Float> vertex = (List<Float>)mesh.getVertex();
        List<Float>  colors = (List<Float>)mesh.getColors();
        List<Float>  normals = (List<Float>)mesh.getNormals();
        int offset = side.getId();
        ArrayList<Float> topSideVertex = new ArrayList<Float>(vertex.subList(offset* floatValuesOnSide, offset* floatValuesOnSide + floatValuesOnSide));
        ArrayList<Float> topSideColors = new ArrayList<Float>(colors.subList(offset* floatValuesOnSide, offset* floatValuesOnSide + floatValuesOnSide));
        ArrayList<Float> topSideNormals = new ArrayList<Float>(normals.subList(offset* floatValuesOnSide, offset* floatValuesOnSide + floatValuesOnSide));
        return new Mesh(topSideVertex, topSideColors, topSideNormals);
    }

    public HashMap<MeshSideType, Mesh> genSidesMeshes(Mesh mesh){
        HashMap<MeshSideType, Mesh> sides = new HashMap<>();
        sides.put(MeshSideType.FRONT, this.genSideData(MeshSideType.FRONT, mesh));
        sides.put(MeshSideType.BACK, this.genSideData(MeshSideType.BACK, mesh));
        sides.put(MeshSideType.LEFT, this.genSideData(MeshSideType.LEFT, mesh));
        sides.put(MeshSideType.RIGHT, this.genSideData(MeshSideType.RIGHT, mesh));
        sides.put(MeshSideType.BOTTOM, this.genSideData(MeshSideType.BOTTOM, mesh));
        sides.put(MeshSideType.TOP, this.genSideData(MeshSideType.TOP, mesh));
        return sides;
    }
}
