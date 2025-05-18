package bktree;

public interface DistanceMetric<T> {
    int compute(T a, T b);
}