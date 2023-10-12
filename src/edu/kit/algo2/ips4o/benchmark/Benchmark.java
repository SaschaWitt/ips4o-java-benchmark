package edu.kit.algo2.ips4o.benchmark;

public abstract class Benchmark {
    public int size;

    public Distribution distribution;

    public int iterations;

    private int[] values;

    public Benchmark(int size) {
        this(size, Distribution.UNIFORM);
    }

    public Benchmark(int size, Distribution dist) {
        this(size, dist, 5);
    }

    public Benchmark(int size, Distribution dist, int iterations) {
        this.size = size;
        this.distribution = dist;
        this.iterations = iterations;
        values = distribution.generate(size);
    }

    public long[] run() {
        long[] times = new long[iterations];

        init();

        for (int i = 0; i < iterations; ++i) {
            int[] vec = java.util.Arrays.copyOf(values, values.length);

            final long startTime = System.nanoTime();
            sort(vec);
            final long endTime = System.nanoTime();

            times[i] = endTime - startTime;
            System.out.printf("%s\t%d\t%d ns\n", distribution, size, times[i]);
        }

        return times;
    }

    protected void init() {}
    protected abstract void sort(int[] vec);
}
