package physics;

import org.joml.Vector3f;

public class Collision {

    //Пока что одинаковые значения, а так если позиция игрока - его ноги, то коллайдер вышего него (центр отвечает)
    //или оно нахрен не нужно
    //потом решу
    private Vector3f center;
    private Vector3f position;

    private Vector3f extend;

    public Collision(Vector3f position, Vector3f extend) {
        this.position = position;
        this.center = position;
        this.extend = extend;
    }

    public Vector3f getCenter() {
        return center;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getExtend() {
        return extend;
    }

    public boolean isCollideWith(Collision other) {
        Vector3f thisMin = new Vector3f(
                center.x - extend.x / 2,
                center.y - extend.y / 2,
                center.z - extend.z / 2
        );
        Vector3f otherMin = new Vector3f(
                other.getCenter().x - other.getExtend().x / 2,
                other.getCenter().y - other.getExtend().y / 2,
                other.getCenter().z - other.getExtend().z / 2
        );
        float t = 0.0f;
        t = thisMin.x - otherMin.x;
        if ((t > other.getExtend().x) || (-t > extend.x)) {
            return false;
        }
        t = thisMin.y - otherMin.y;
        if ((t > other.getExtend().y) || (-t > extend.y)) {
            return false;
        }
        t = thisMin.z - otherMin.z;
        if ((t > other.getExtend().z) || (-t > extend.z)) {
            return false;
        }

        return true;
    }

    public void move(Vector3f dir) {
        center = center.add(dir);
        position = position.add(dir);
    }

    //Вектор Dir Всегда нормализован и имеет только одну координату
    public Vector3f clipToCollider(Collision collider, Vector3f dir) {
        Vector3f own = new Vector3f(position.x * dir.x, position.y * dir.y, position.z * dir.z);
        Vector3f other = new Vector3f(collider.getPosition().x * dir.x, collider.getPosition().y * dir.y,  collider.getPosition().z * dir.z);
        float ownExtend = new Vector3f(extend.x * dir.x, extend.y * dir.y, extend.z * dir.z).length() / 2.0f;
        float otherExtend = new Vector3f(collider.getExtend().x * dir.x, collider.getExtend().y * dir.y,  collider.getExtend().z * dir.z).length() / 2.0f;
        float distanceToClip = own.distance(other) - (ownExtend + otherExtend);

        Vector3f pos = new Vector3f(position).add(new Vector3f(dir).mul(distanceToClip));

        this.position = pos;
        this.center = pos;

        return pos;
    }

    public void teleport(Vector3f position) {
        this.position = position;
        this.center = position;
    }

}
