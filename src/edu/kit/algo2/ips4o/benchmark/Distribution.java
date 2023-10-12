package edu.kit.algo2.ips4o.benchmark;

import java.util.Random;
import java.util.stream.IntStream;

public enum Distribution {
    UNIFORM(c -> new Random(c).ints(c).toArray()),
    ONES(c -> IntStream.generate(() -> 1).limit(c).toArray()),
    SORTED(c -> IntStream.rangeClosed(1, c).toArray()),
    REVERSE(c -> IntStream.iterate(c, i -> i - 1).limit(c).toArray()),
    ALMOST_SORTED(c -> {
        int[] v = IntStream.rangeClosed(1, c).toArray();
        final double prob = c > 0 ? Math.sqrt(c) / c : 1;
        final Random rng = new Random(c);
        for (int i = 0; i < v.length - 1; ++i) {
            if (rng.nextDouble(1.0) < prob) {
                ++v[i];
                --v[i + 1];
                ++i;
            }
        }
        return v;
    }),
    UNSORTED_TAIL(c -> {
        int[] v = IntStream.rangeClosed(1, c).toArray();
        final int tail = (int) Math.pow(c, 7.0 / 8.0);
        final Random rng = new Random(c);
        for (int i = tail; i < v.length; ++i) {
            v[i] = rng.nextInt(tail, c + 1);
        }
        return v;
    }),
    EXPONENTIAL(c -> {
        int[] v = new int[c];
        final int log = c > 0 ? (int) Math.ceil(Math.log(c) / Math.log(2)) + 1 : 1;
        final Random rng = new Random(c);
        for (int i = 0; i < v.length; ++i) {
            final int range = 1 << rng.nextInt(log);
            v[i] = range + rng.nextInt(range);
        }
        return v;
    }),
    ROOT_DUPS(c -> {
        final int root = c > 0 ? (int) Math.sqrt(c) : 1;
        final Random rng = new Random(c);
        return IntStream.generate(() -> rng.nextInt(root)).limit(c).toArray();
    }),
    ROOT_CENTER_DUPS(c -> {
        int[] v = new int[c];
        final int log = c > 0 ? (int) (Math.log(c) / Math.log(2) - 1) : 0;
        final Random rng = new Random(c);
        for (int i = 0; i < v.length; ++i) {
            final int x = rng.nextInt();
            v[i] = (x * x + (1 << log)) % (2 << log);
        }
        return v;
    }),
    P78CENTER_DUPS(c -> {
        int[] v = new int[c];
        final int log = c > 0 ? (int) (Math.log(c) / Math.log(2) - 1) : 0;
        final Random rng = new Random(c);
        for (int i = 0; i < v.length; ++i) {
            int x = rng.nextInt();
            x = (x * x) % (2 << log);
            x = (x * x) % (2 << log);
            v[i] = (x * x + (1 << log)) % (2 << log);
        }
        return v;
    });

    private interface Generator {
        public int[] generate(int count);
    }

    private Generator generator;

    Distribution(Generator gen) {
        this.generator = gen;
    }

    public int[] generate(int count) {
        return generator.generate(count);
    }
}
