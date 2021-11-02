package game_objects.blocks;

import renderer.AtlasTextureType;

public class BlockSideTextureType {
    private AtlasTextureType topTexture;
    private AtlasTextureType leftTexture;
    private AtlasTextureType rightTexture;
    private AtlasTextureType farTexture;
    private AtlasTextureType nearTexture;
    private AtlasTextureType bottomTexture;

    public BlockSideTextureType(AtlasTextureType topTexture, AtlasTextureType leftTexture,
                                AtlasTextureType rightTexture, AtlasTextureType farTexture,
                                AtlasTextureType nearTexture, AtlasTextureType bottomTexture) {
        this.topTexture = topTexture;
        this.leftTexture = leftTexture;
        this.rightTexture = rightTexture;
        this.farTexture = farTexture;
        this.nearTexture = nearTexture;
        this.bottomTexture = bottomTexture;
    }

    public BlockSideTextureType(AtlasTextureType topTexture) {
        this.topTexture = topTexture;
        this.leftTexture = topTexture;
        this.rightTexture = topTexture;
        this.farTexture = topTexture;
        this.nearTexture = topTexture;
        this.bottomTexture = topTexture;
    }

    public BlockSideTextureType(){

    }

    public AtlasTextureType getTopTextureType() {
        return topTexture;
    }

    public AtlasTextureType getLeftTextureType() {
        return leftTexture;
    }

    public AtlasTextureType getRightTextureType() {
        return rightTexture;
    }

    public AtlasTextureType getFarTextureType() {
        return farTexture;
    }

    public AtlasTextureType getNearTextureType() {
        return nearTexture;
    }

    public AtlasTextureType getBottomTextureType() {
        return bottomTexture;
    }
}
