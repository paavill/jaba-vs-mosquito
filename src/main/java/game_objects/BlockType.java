package game_objects;

public enum BlockType {
    STONE(1),
    AIR(0);

    private final int id;

    BlockType(int id){
        this.id = id;
    }

    public int getId(){
        return this.id;
    }
}
