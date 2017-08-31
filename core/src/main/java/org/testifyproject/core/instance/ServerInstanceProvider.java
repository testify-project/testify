/*
 * Copyright 2016-2017 Testify Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.testifyproject.core.instance;

import java.util.List;
import java.util.Optional;
import org.testifyproject.Instance;
import org.testifyproject.InstanceProvider;
import org.testifyproject.ServerInstance;
import org.testifyproject.TestContext;
import org.testifyproject.core.DefaultInstance;
import org.testifyproject.core.TestContextProperties;
import org.testifyproject.extension.annotation.SystemCategory;
import org.testifyproject.guava.common.collect.ImmutableList;
import org.testifyproject.tools.Discoverable;

/**
 * An implementation of InstanceProvider that provides the test context.
 *
 * @author saden
 */
@SystemCategory
@Discoverable
public class ServerInstanceProvider implements InstanceProvider {

    @Override
    public List<Instance> get(TestContext testContext) {
        ImmutableList.Builder<Instance> builder = ImmutableList.builder();

        Optional<ServerInstance<Object>> foundServerInstance
                = testContext.findProperty(TestContextProperties.APP_SERVER_INSTANCE);

        foundServerInstance.ifPresent(serverInstance -> {
            builder.add(DefaultInstance.of(serverInstance, ServerInstance.class));
            builder.add(serverInstance.getServer());
        });

        return builder.build();
    }

}
