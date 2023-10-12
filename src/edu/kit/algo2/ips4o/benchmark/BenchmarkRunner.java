package edu.kit.algo2.ips4o.benchmark;

import java.lang.IllegalArgumentException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ForkJoinPool;

public class BenchmarkRunner {

    public String algo = "ips4o";
    public int base = 2;
    public int minExp = 1;
    public int maxExp = 27;
    public int iterations = 5;
    public boolean doWarmup = true;
    public Distribution distribution = null;
    public boolean parallel = false;
    public int parallel_factor = 1;

    public void parseArgs(String[] args) {
        for (int i = 0; i < args.length; ++i) {
            switch (args[i]) {
                case "-a": case "--algo":
                    if (i + 1 >= args.length)
                        throw new IllegalArgumentException("Missing argument to -a");
                    algo = args[++i];
                    break;
                case "-b": case "--base":
                    if (i + 1 >= args.length)
                        throw new IllegalArgumentException("Missing argument to -b");
                    base = Integer.parseInt(args[++i]);
                    break;
                case "-e": case "--exponents":
                    if (i + 2 >= args.length)
                        throw new IllegalArgumentException("Missing argument to -e");
                    minExp = Integer.parseInt(args[++i]);
                    maxExp = Integer.parseInt(args[++i]);
                    break;
                case "-i": case "--iterations":
                    if (i + 1 >= args.length)
                        throw new IllegalArgumentException("Missing argument to -i");
                    iterations = Integer.parseInt(args[++i]);
                    break;
                case "-d": case "--distribution":
                    if (i + 1 >= args.length)
                        throw new IllegalArgumentException("Missing argument to -d");
                    distribution = Distribution.valueOf(args[++i]);
                    break;
                case "-W": case "--no-warmup":
                    doWarmup = false;
                    break;
                case "-p": case "--parallel":
                    parallel = true;
                    break;
                case "--parallel-factor":
                    if (i + 1 >= args.length)
                        throw new IllegalArgumentException("Missing argument to --parallel-factor");
                    parallel_factor = Integer.parseInt(args[++i]);
                    parallel = true;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown option: " + args[i]);
            }
        }
    }

    private Benchmark makeBenchmark(int size, Distribution dist, int iterations) {
        if ("ips4o".equals(algo)) {
            return new Ips4oBenchmark(size, dist, iterations);
        } else if ("std".equals(algo)) {
            return new StdBenchmark(size, dist, iterations);
        } else {
            throw new IllegalArgumentException("Invalid algorithm: " + algo);
        }
    }

    private void runWarmup() {
        if (!doWarmup) return;
        Benchmark bench = makeBenchmark(1 << 25, Distribution.UNIFORM, 3);
        bench.run();
    }

    private Result[] runDistribution(Distribution dist) {
        if (parallel) {
            final int threads = ForkJoinPool.getCommonPoolParallelism();
            ArrayList<Result> results = new ArrayList<Result>((maxExp - minExp + 1) * (threads * parallel_factor));

            for (int exp = minExp; exp <= maxExp; ++exp) {
                final int size = (int) Math.pow(base, exp);
                ForkJoinPool.commonPool()
                    .invokeAll(Collections.nCopies(threads * parallel_factor,
                        () -> makeBenchmark(size, dist, iterations).run()))
                    .forEach(f -> {
                        Result result = new Result();
                        try {
                            result.times = (long[]) f.get();
                        } catch (Exception ex) {
                            System.err.println("Unexpected Exception: " + ex.toString());
                        }
                        result.size = size;
                        result.distribution = dist;
                        results.add(result);
                    });
            }
            return results.toArray(new Result[1]);
        } else {
            Result[] results = new Result[maxExp - minExp + 1];
            for (int exp = minExp; exp <= maxExp; ++exp) {
                final int size = (int) Math.pow(base, exp);
                Result result = new Result();
                Benchmark bench = makeBenchmark(size, dist, iterations);
                result.times = bench.run();
                result.size = size;
                result.distribution = dist;
                results[exp - minExp] = result;
            }
            return results;
        }
    }

    public Result[] run() {
        runWarmup();
        Result[] results;
        if (distribution != null) {
            return runDistribution(distribution);
        } else {
            results = new Result[Distribution.values().length * (maxExp - minExp + 1)];
            for (int i = 0; i < Distribution.values().length; ++i) {
                System.arraycopy(runDistribution(Distribution.values()[i]), 0,
                                 results, i * (maxExp - minExp + 1), maxExp - minExp + 1);
            }
        }
        return results;
    }

    public class Result {
        long[] times;
        int size;
        Distribution distribution;

        public void printTimes() {
            for (int i = 0; i < times.length; ++i) {
                System.out.printf("%s\t%d\t%d ns\n", distribution, size, times[i]);
            }
        }

        public String toString() {
            long mean = 0;
            for (int i = 0; i < times.length; ++i)
                mean += times[i];
            mean /= times.length;
            double std = 0;
            for (int i = 0; i < times.length; ++i)
                std += (times[i] - mean) * (times[i] - mean);
            if (times.length > 1)
                std /= (times.length - 1);
            std = Math.sqrt(std);
            double ste = std / Math.sqrt(times.length);
            return String.format("algo: %6s  distribution: %-17s  size: %10d  mean time (ns): %12d  est. std. error: %10d",
                    algo, distribution, size, mean, (long) ste);
        }
    }
}
