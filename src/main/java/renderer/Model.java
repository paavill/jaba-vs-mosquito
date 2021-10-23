package renderer;

public class Model {

    private final Mesh mesh;

    public Model(Mesh mesh) {
        this.mesh = mesh;
    }

    public Mesh getMesh() {
        return mesh;
    }
}
