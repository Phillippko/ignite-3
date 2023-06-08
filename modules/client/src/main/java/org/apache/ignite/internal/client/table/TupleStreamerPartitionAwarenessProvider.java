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

package org.apache.ignite.internal.client.table;

import org.apache.ignite.table.Tuple;

/**
 * Partition awareness provider for data streamer.
 */
class TupleStreamerPartitionAwarenessProvider extends AbstractStreamerPartitionAwarenessProvider<Tuple> {
    TupleStreamerPartitionAwarenessProvider(ClientTable tbl) {
        super(tbl);
    }

    @Override
    int colocationHash(ClientSchema schema, Tuple item) {
        return ClientTupleSerializer.getColocationHash(schema, item);
    }
}
