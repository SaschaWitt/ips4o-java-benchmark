package edu.kit.algo2.ips4o.benchmark;

public class Main {
    public static void main(String[] args) {
        BenchmarkRunner runner = new BenchmarkRunner();
        runner.parseArgs(args);
        BenchmarkRunner.Result[] results = runner.run();

        for (BenchmarkRunner.Result result : results) {
            // result.printTimes();
            System.out.println(result.toString());
        }
    }
}
