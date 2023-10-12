# Benchmark for the Formally Verified Java Implementation of In-Place Super Scalar Sample Sort

The [`src`](src) directory contains the benchmark code.
The [algorithm implementation](https://github.com/jwiesler/ips4o-verify) is included as a submodule in [`external/ips4o-verify`](external/ips4o-verify).

## Usage

The code can be compiled using [ant](https://ant.apache.org), provided you have JDK 20 installed.
To run the benchmarks, use the following commands:
```
ant
java -cp bin edu.kit.algo2.ips4o.benchmark.Main
```
This will run all the benchmarks for input sizes 2 through 2<sup>27</sup>.
If you are only interested in a subset, you can append the following options:
* `--base <n>` sets the base for the input size to `<n>`. Default is 2.
* `--exponents <min> <max>` sets the minimum and maximum exponents for the input size. Defaults are 1 and 27, respectively.
  * For example, to run the benchmarks with input sizes `100, 1000, 10000`, use `--base 10 --exponents 2 4`.
* `--iterations <n>` sets the number of times each benchmark is repeated. Default is 5.
* `--distribution` sets the input distribution for which benchmarks should be run. Default is all.
  * Valid options are: `UNIFORM`, `ONES`, `SORTED`, `REVERSE`, `ALMOST_SORTED`, `UNSORTED_TAIL`, `EXPONENTIAL`, `ROOT_DUPS`, `ROOT_CENTER_DUPS`, `P78CENTER_DUPS`.
* `--algo <name>` sets the algorithm to be benchmarked. Default is `ips4o`.
  * Valid options are: `ips4o`, `std`.

