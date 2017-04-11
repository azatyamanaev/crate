/*
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */

package io.crate.planner.consumer;

import io.crate.planner.node.dql.Collect;
import io.crate.planner.projection.FilterProjection;
import io.crate.planner.projection.Projection;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.SQLExecutor;
import io.crate.testing.T3;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.Matchers.instanceOf;

public class GlobalAggregatePlannerTest extends CrateDummyClusterServiceUnitTest {

    @Test
    public void testAggregateOnSubQueryHasNoFilterProjectionWithoutWhereAndHaving() throws Exception {
        SQLExecutor e = SQLExecutor.builder(clusterService).addDocTable(T3.T1_INFO).build();

        Collect plan = e.plan("select sum(x) from (select x from t1 order by x limit 10) ti");
        for (Projection projection : plan.collectPhase().projections()) {
            assertThat(projection, Matchers.not(instanceOf(FilterProjection.class)));
        }
    }
}
