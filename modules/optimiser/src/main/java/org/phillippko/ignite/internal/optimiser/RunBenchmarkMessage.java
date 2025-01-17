package org.phillippko.ignite.internal.optimiser;

import java.util.UUID;
import org.apache.ignite.internal.network.NetworkMessage;
import org.apache.ignite.internal.network.annotations.Transferable;
import org.jetbrains.annotations.Nullable;

@Transferable(OptimiserMessageGroup.RUN_BENCHMARK_TYPE)
interface RunBenchmarkMessage extends NetworkMessage {
    @Nullable String benchmarkFileName();

    UUID id();

    int iterations();

    String profile();

    @Nullable Long values();
}
