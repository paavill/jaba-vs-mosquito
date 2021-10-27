package renderer;

import game_objects.blocks.BlockType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SideDataGenerator {

    private final int floatValuesOnSide;

    public SideDataGenerator(int floatValuesOnSide){
        this.floatValuesOnSide = floatValuesOnSide;
    }

    private Mesh genSideData(MeshSideType side, Mesh mesh, BlockType type){
        List<Float> vertex = (List<Float>)mesh.getVertex();
        List<Float>  colors = (List<Float>)mesh.getColors();
        List<Float>  normals = (List<Float>)mesh.getNormals();
        ArrayList<ArrayList<Float>> textures = mesh.getTextureCoords();
        int offset = side.getId();
        ArrayList<Float> topSideVertex = new ArrayList<Float>(vertex.subList(offset* floatValuesOnSide, offset* floatValuesOnSide + floatValuesOnSide));
        ArrayList<Float> topSideColors = new ArrayList<Float>(colors.subList(offset* floatValuesOnSide, offset* floatValuesOnSide + floatValuesOnSide));
        ArrayList<Float> topSideNormals = new ArrayList<Float>(normals.subList(offset* floatValuesOnSide, offset* floatValuesOnSide + floatValuesOnSide));
        ArrayList<ArrayList<Float>> topSideTextures = new ArrayList<ArrayList<Float>>();
        switch (side){
            case BOTTOM -> topSideTextures.add(textures.get(type.getSidesTexturesTypes().getBottomTextureType().getNumber()));
            case TOP -> topSideTextures.add(textures.get(type.getSidesTexturesTypes().getTopTextureType().getNumber()));
            case LEFT -> topSideTextures.add(textures.get(type.getSidesTexturesTypes().getLeftTextureType().getNumber()));
            case RIGHT -> topSideTextures.add(textures.get(type.getSidesTexturesTypes().getRightTextureType().getNumber()));
            case FRONT -> topSideTextures.add(textures.get(type.getSidesTexturesTypes().getNearTextureType().getNumber()));
            case BACK -> topSideTextures.add(textures.get(type.getSidesTexturesTypes().getFarTextureType().getNumber()));
        }

        return new Mesh(topSideVertex, topSideColors, topSideNormals, topSideTextures);
    }

    public HashMap<MeshSideType, Mesh> genSidesMeshes(Mesh mesh, BlockType type){
        HashMap<MeshSideType, Mesh> sides = new HashMap<>();
        sides.put(MeshSideType.FRONT, this.genSideData(MeshSideType.FRONT, mesh, type));
        sides.put(MeshSideType.BACK, this.genSideData(MeshSideType.BACK, mesh, type));
        sides.put(MeshSideType.LEFT, this.genSideData(MeshSideType.LEFT, mesh, type));
        sides.put(MeshSideType.RIGHT, this.genSideData(MeshSideType.RIGHT, mesh, type));
        sides.put(MeshSideType.BOTTOM, this.genSideData(MeshSideType.BOTTOM, mesh, type));
        sides.put(MeshSideType.TOP, this.genSideData(MeshSideType.TOP, mesh, type));
        return sides;
    }
}
