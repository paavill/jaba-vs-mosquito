package renderer;

public enum AtlasTextureType {
    //number показывает какая текстура по счету, если считать, что левая нижняя первая
    //текстура над ней - вторая, далее третья и т.д
    GRASS_TOP(15),
    GRASS_OTHER_SIDES(31),
    DIRT_ALL_SIDES(47),
    STONE_ALL_SIDES(63);

    AtlasTextureType(int number){
        this.number = number;
    };

    private final int number;

    public int getNumber() {
        return number;
    }
}
