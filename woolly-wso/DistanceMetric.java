package woollywso;

public interface DistanceMetric<T> {
    int compute(T a, T b);

    int lazyCompute(T a, T b, int threshold);
}