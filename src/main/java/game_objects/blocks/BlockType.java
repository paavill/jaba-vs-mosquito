package game_objects.blocks;

import renderer.AtlasTextureType;

import java.util.List;

public enum BlockType {
    STONE(1, new BlockSideTextureType(AtlasTextureType.STONE_ALL_SIDES)),
    GRASS(2, new BlockSideTextureType(AtlasTextureType.GRASS_TOP, AtlasTextureType.GRASS_OTHER_SIDES,
            AtlasTextureType.GRASS_OTHER_SIDES, AtlasTextureType.GRASS_OTHER_SIDES, AtlasTextureType.GRASS_OTHER_SIDES,
            AtlasTextureType.DIRT_ALL_SIDES)),
    AIR(0, new BlockSideTextureType());

    private final int id;
    private final BlockSideTextureType sidesTexturesTypes;

    BlockType(int id, BlockSideTextureType sidesTexturesTypes){
        this.id = id;
        this.sidesTexturesTypes = sidesTexturesTypes;
    }

    public BlockSideTextureType getSidesTexturesTypes() {
        return sidesTexturesTypes;
    }

    public int getId(){
        return this.id;
    }
}
