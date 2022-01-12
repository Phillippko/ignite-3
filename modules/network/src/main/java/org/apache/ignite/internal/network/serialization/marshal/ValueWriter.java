/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.internal.network.serialization.marshal;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Knows how to write a value to a {@link DataOutputStream}.
 */
interface ValueWriter<T> {
    /**
     * Writes the given value to a {@link DataOutputStream}.
     *
     * @param value     value to write
     * @param output    where to write to
     * @param context   marshalling context
     * @throws IOException      if an I/O problem occurs
     * @throws MarshalException if another problem occurs
     */
    void write(T value, DataOutputStream output, MarshallingContext context) throws IOException, MarshalException;
}
