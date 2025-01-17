package org.phillippko.ignite.internal.optimiser;

import org.jetbrains.annotations.Nullable;

public interface BenchmarkRunner {
    String runBenchmark(@Nullable String benchmarkFilePath, int iterations, @Nullable Long values, String profile);
}
