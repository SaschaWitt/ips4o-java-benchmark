package edu.kit.algo2.ips4o.benchmark;

public class StdBenchmark extends Benchmark {
    public StdBenchmark(int size, Distribution dist, int iterations) {
        super(size, dist, iterations);
    }

    protected void sort(int[] vec) {
        java.util.Arrays.sort(vec, 0, vec.length);
    }
}
