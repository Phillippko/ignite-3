/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.internal.compute;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.apache.ignite.compute.IgniteCompute;
import org.apache.ignite.compute.JobStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Internal compute facade.
 */
public interface IgniteComputeInternal extends IgniteCompute {
    /**
     * Gets job status by id.
     *
     * @param jobId Job id.
     * @return Job status or {@code null} if there's no status registered for this id.
     */
    CompletableFuture<@Nullable JobStatus> statusAsync(UUID jobId);

    /**
     * Cancels compute job.
     *
     * @param jobId Job id.
     * @return The future which will be completed with {@code true} when the job is cancelled, {@code false} when the job couldn't be
     *         cancelled (either it's not yet started, or it's already completed), or {@code null} if there's no job with the specified id.
     */
    CompletableFuture<@Nullable Boolean> cancelAsync(UUID jobId);

    /**
     * Changes compute job priority.
     *
     * @param jobId Job id.
     * @param newPriority New priority.
     * @return The future which will be completed with {@code true} when the priority is changed, {@code false} when the priority couldn't
     *         be changed (it's already executing or completed), or {@code null} if there's no job with the specified id.
     */
    CompletableFuture<@Nullable Boolean> changePriorityAsync(UUID jobId, int newPriority);
}