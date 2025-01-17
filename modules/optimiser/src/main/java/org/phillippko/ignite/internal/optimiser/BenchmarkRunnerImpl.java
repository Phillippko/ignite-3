package org.phillippko.ignite.internal.optimiser;

import static java.lang.System.currentTimeMillis;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import org.apache.ignite.internal.logger.IgniteLogger;
import org.apache.ignite.internal.logger.Loggers;
import org.apache.ignite.sql.IgniteSql;
import org.apache.ignite.sql.ResultSet;
import org.apache.ignite.sql.SqlException;
import org.apache.ignite.sql.SqlRow;
import org.jetbrains.annotations.Nullable;

public class BenchmarkRunnerImpl implements BenchmarkRunner {
    private static final IgniteLogger LOG = Loggers.forClass(BenchmarkRunnerImpl.class);
    private final IgniteSql sql;

    public BenchmarkRunnerImpl(IgniteSql sql) {
        this.sql = sql;
    }


    @Override
    public String runBenchmark(
            @Nullable String benchmarkFilePath,
            int iterations,
            @Nullable Long values,
            String profile
    ) throws SqlException {
        StringBuilder resultBuilder = new StringBuilder();

        try {
            if (benchmarkFilePath != null) {
                runBenchmark(benchmarkFilePath, iterations, profile, resultBuilder);
            } else {
                runBenchmark(iterations, values, profile, resultBuilder);
            }
        } catch (Exception e) {
            LOG.error("Error running a benchmark", e);

            return "Error running a benchmark: " + e.getMessage();
        }

        LOG.info(resultBuilder.toString());

        return resultBuilder.toString();
    }

    private void runBenchmark(
            String benchmarkFilePath,
            int iterations,
            String profile,
            StringBuilder resultBuilder
    ) throws Exception {
        LOG.info("Benchmarking " + benchmarkFilePath + "...");

        Path path = Path.of(benchmarkFilePath);

        List<String> statements = Files.readAllLines(path);

        for (int i = 1; i <= iterations; i++) {
            long atStart = currentTimeMillis();

            for (String statement : statements) {
                executeSql(statement);
            }

            resultBuilder.append("Iteration ").append(i).append(" finished in ").append(currentTimeMillis() - atStart).append("MS\n");
        }
    }

    private void runBenchmark(int iterations, long values, String profile, StringBuilder resultBuilder) throws SqlException {
        LOG.info("Benchmarking profile " + profile + " inserting " + values + " values...");

        executeSql(String.format("CREATE ZONE IF NOT EXISTS ZONE_%s WITH STORAGE_PROFILES='%s'", profile, profile));

        executeSql("DROP TABLE IF EXISTS test_table");

        executeSql("CREATE TABLE test_table (id VARCHAR PRIMARY KEY) ZONE ZONE_" + profile);

        executeSql("DELETE FROM test_table");

        String insert = "INSERT INTO test_table (id) VALUES (?)";

        for (int i = 1; i <= iterations; i++) {
            long atStart = currentTimeMillis();

            for (int value = 0; value < values; value++) {
                executeSql(insert, UUID.randomUUID().toString());
            }

            resultBuilder.append("Iteration ").append(i).append(" finished in ").append(currentTimeMillis() - atStart).append("MS\n");
        }
    }

    private void executeSql(String query, Object... params) {
        try (ResultSet<SqlRow> ignored = sql.execute(null, query, params)) {
        }
    }
}
