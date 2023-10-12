package edu.kit.algo2.ips4o.benchmark;

import de.wiesler.Sorter;
import de.wiesler.Storage;

public class Ips4oBenchmark extends Benchmark {
    public Ips4oBenchmark(int size, Distribution dist, int iterations) {
        super(size, dist, iterations);
    }

    private Storage storage;

    protected void init() {
        storage = new Storage();
    }

    protected void sort(int[] vec) {
        // de.wiesler.Functions.rng = new java.util.SplittableRandom(23);
        Sorter.sort(vec, 0, vec.length, storage);
    }
}
