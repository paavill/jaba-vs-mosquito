import org.joml.Vector3f;
import org.junit.Assert;
import org.junit.Test;
import physics.Collision;

public class CollisionTest {

    @Test
    public void Test1() {
        Collision col1 = new Collision(new Vector3f(0f, 0f, 0f), new Vector3f(1f, 2f, 1f));
        Collision col2 = new Collision(new Vector3f(0f, -1f, 0f), new Vector3f(1f, 1f, 1f));

        Assert.assertEquals(true, col1.isCollideWith(col2));
    }

    @Test
    public void Test2() {
        Collision col1 = new Collision(new Vector3f(0f, 0f, 0f), new Vector3f(1f, 2f, 1f));
        Collision col2 = new Collision(new Vector3f(0f, -1.5f, 0f), new Vector3f(1f, 1f, 1f));

        Assert.assertEquals(true, col1.isCollideWith(col2));
    }

    @Test
    public void Test3() {
        Collision col1 = new Collision(new Vector3f(0f, 0f, 0f), new Vector3f(1f, 2f, 1f));
        Collision col2 = new Collision(new Vector3f(0f, -1.51f, 0f), new Vector3f(1f, 1f, 1f));

        Assert.assertEquals(false, col1.isCollideWith(col2));
    }

}
