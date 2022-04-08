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

namespace Apache.Ignite.Tests.Compute
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Net;
    using System.Threading.Tasks;
    using Ignite.Compute;
    using Network;
    using NUnit.Framework;

    /// <summary>
    /// Tests <see cref="ICompute"/>.
    /// </summary>
    public class ComputeTests : IgniteTestsBase
    {
        private const string ConcatJob = "org.apache.ignite.internal.runner.app.client.ItThinClientComputeTest$ConcatJob";

        private const string NodeNameJob = "org.apache.ignite.internal.runner.app.client.ItThinClientComputeTest$NodeNameJob";

        private const string ErrorJob = "org.apache.ignite.internal.runner.app.client.ItThinClientComputeTest$ErrorJob";

        private const string EchoJob = "org.apache.ignite.internal.runner.app.client.ItThinClientComputeTest$EchoJob";

        private const string PlatformTestNodeRunner = "org.apache.ignite.internal.runner.app.PlatformTestNodeRunner";

        [Test]
        public async Task TestGetClusterNodes()
        {
            var res = (await Client.GetClusterNodesAsync()).OrderBy(x => x.Name).ToList();

            Assert.AreEqual(2, res.Count);

            Assert.IsNotEmpty(res[0].Id);
            Assert.IsNotEmpty(res[1].Id);

            Assert.AreEqual(3344, res[0].Address.Port);
            Assert.AreEqual(3345, res[1].Address.Port);

            Assert.AreNotEqual(IPAddress.None, res[0].Address.Address);
            Assert.AreNotEqual(IPAddress.None, res[1].Address.Address);
            Assert.AreEqual(res[0].Address.Address, res[1].Address.Address);
        }

        [Test]
        public async Task TestExecuteOnSpecificNode()
        {
            var res1 = await Client.Compute.ExecuteAsync<string>(await GetNodeAsync(0), NodeNameJob, "-", 11);
            var res2 = await Client.Compute.ExecuteAsync<string>(await GetNodeAsync(1), NodeNameJob, ":", 22);

            Assert.AreEqual(PlatformTestNodeRunner + "-_11", res1);
            Assert.AreEqual(PlatformTestNodeRunner + "_2:_22", res2);
        }

        [Test]
        public async Task TestExecuteOnRandomNode()
        {
            var res = await Client.Compute.ExecuteAsync<string>(await Client.GetClusterNodesAsync(), NodeNameJob);

            CollectionAssert.Contains(new[] { PlatformTestNodeRunner, PlatformTestNodeRunner + "_2" }, res);
        }

        [Test]
        public void TestExecuteResultTypeMismatchThrowsInvalidCastException()
        {
            Assert.ThrowsAsync<InvalidCastException>(async () =>
                await Client.Compute.ExecuteAsync<Guid>(await Client.GetClusterNodesAsync(), NodeNameJob));
        }

        [Test]
        public async Task TestBroadcastOneNode()
        {
            var nodes = await GetNodeAsync(0);

            IDictionary<IClusterNode, Task<string>> taskMap = Client.Compute.BroadcastAsync<string>(nodes, NodeNameJob, "123");
            var res = await taskMap[nodes[0]];

            Assert.AreEqual(1, taskMap.Count);
            Assert.AreSame(nodes[0], taskMap.Keys.Single());

            Assert.AreEqual(PlatformTestNodeRunner + "123", res);
        }

        [Test]
        public async Task TestBroadcastAllNodes()
        {
            var nodes = await Client.GetClusterNodesAsync();

            IDictionary<IClusterNode, Task<string>> taskMap = Client.Compute.BroadcastAsync<string>(nodes, NodeNameJob, "123");
            var res1 = await taskMap[nodes[0]];
            var res2 = await taskMap[nodes[1]];

            Assert.AreEqual(2, taskMap.Count);

            Assert.AreEqual(nodes[0].Name + "123", res1);
            Assert.AreEqual(nodes[1].Name + "123", res2);
        }

        [Test]
        public async Task TestExecuteWithArgs()
        {
            var res = await Client.Compute.ExecuteAsync<string>(await Client.GetClusterNodesAsync(), ConcatJob, 1.1, Guid.Empty, "3");

            Assert.AreEqual("1.1_00000000-0000-0000-0000-000000000000_3", res);
        }

        [Test]
        public void TestJobErrorPropagatesToClientWithClassAndMessage()
        {
            var ex = Assert.ThrowsAsync<IgniteClientException>(async () =>
                await Client.Compute.ExecuteAsync<string>(await Client.GetClusterNodesAsync(), ErrorJob, "unused"));

            Assert.AreEqual("class org.apache.ignite.tx.TransactionException: Custom job error", ex!.Message);
        }

        // TODO: Support all types (IGNITE-15431).
        [Test]
        public async Task TestAllSupportedArgTypes()
        {
            await Test(byte.MinValue);
            await Test(byte.MaxValue, -1);
            await Test(sbyte.MinValue);
            await Test(sbyte.MaxValue);
            await Test(short.MinValue);
            await Test(short.MaxValue);
            await Test(ushort.MinValue);
            await Test(ushort.MaxValue, -1);
            await Test(int.MinValue);
            await Test(int.MaxValue);
            await Test(uint.MinValue);
            await Test(uint.MaxValue, -1);
            await Test(long.MinValue);
            await Test(long.MaxValue);
            await Test(ulong.MinValue);
            await Test(ulong.MaxValue, -1);
            await Test(float.MinValue);
            await Test(float.MaxValue);
            await Test(double.MinValue);
            await Test(double.MaxValue);
            await Test("Ignite 🔥");
            await Test(Guid.NewGuid());

            async Task Test(object val, object? expected = null)
            {
                var res = await Client.Compute.ExecuteAsync<object>(await Client.GetClusterNodesAsync(), EchoJob, val);

                Assert.AreEqual(expected ?? val, res);
            }
        }

        private async Task<List<IClusterNode>> GetNodeAsync(int index) =>
            (await Client.GetClusterNodesAsync()).OrderBy(n => n.Name).Skip(index).Take(1).ToList();
    }
}
