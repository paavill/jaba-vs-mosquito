package renderer;

import main.Tuple;
import org.joml.Vector3f;

public interface IRenderable {
    public Tuple<Model, Tuple<Vector3f, Vector3f>> getModel();
}
