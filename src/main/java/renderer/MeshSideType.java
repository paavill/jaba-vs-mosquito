package renderer;

public enum MeshSideType {
    FRONT(0), //Ближняя по -z
    BACK(1), //Дальная по +z
    LEFT(2), //Левая -x
    RIGHT(3), //Правая +x
    BOTTOM(4), //Верхняя +y
    TOP(5); //-y

    private final int id;

    MeshSideType(int id){
        this.id = id;
    }

    public int getId(){
        return this.id;
    }
}
