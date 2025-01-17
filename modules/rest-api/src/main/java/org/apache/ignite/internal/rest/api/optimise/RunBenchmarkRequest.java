package org.apache.ignite.internal.rest.api.optimise;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.ignite.internal.tostring.S;
import org.jetbrains.annotations.Nullable;

@Schema(description = "Run benchmark.")
public class RunBenchmarkRequest {
    private final @Nullable String benchmarkFilePath;
    private final @Nullable String nodeName;
    private final int iterations;
    private final @Nullable Long values;
    private final String profile;

    /** Constructor. */
    @JsonCreator
    public RunBenchmarkRequest(
            @JsonProperty("nodeName") @Nullable String nodeName,
            @JsonProperty("benchmarkFilePath") @Nullable String benchmarkFilePath,
            @JsonProperty("iterations") int iterations,
            @JsonProperty("values") @Nullable Long values,
            @JsonProperty("profile") String profile
    ) {
        this.benchmarkFilePath = benchmarkFilePath;
        this.nodeName = nodeName;
        this.iterations = iterations;
        this.values = values;
        this.profile = profile;
    }

    @JsonGetter
    public @Nullable String benchmarkFilePath() {
        return benchmarkFilePath;
    }

    @JsonGetter
    @Nullable
    public String nodeName() {
        return nodeName;
    }

    @JsonGetter
    public int iterations() {
        return iterations;
    }

    @JsonGetter
    public @Nullable Long values() {
        return values;
    }

    @JsonGetter
    public String profile() {
        return profile;
    }

    @Override
    public String toString() {
        return S.toString(this);
    }
}
