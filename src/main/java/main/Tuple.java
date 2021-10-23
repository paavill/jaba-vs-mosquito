package main;

public class Tuple<T1, T2> {
    public final T1 first;
    public final T2 second;

    public Tuple(T1 x, T2 y) {
        this.first = x;
        this.second = y;
    }

    public static <T1, T2> Tuple<T1, T2> from(T1 first, T2 second) {
        return new Tuple<T1, T2>(first, second);
    }
}