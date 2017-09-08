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
import org.testifyproject.Instance;
import org.testifyproject.ResourceInstance;
import org.testifyproject.TestContext;
import org.testifyproject.VirtualResourceInstance;
import org.testifyproject.VirtualResourceProvider;
import org.testifyproject.annotation.VirtualResource;
import org.testifyproject.core.DefaultInstance;
import org.testifyproject.core.util.NamingUtil;
import org.testifyproject.extension.annotation.IntegrationCategory;
import org.testifyproject.extension.annotation.SystemCategory;
import org.testifyproject.extension.annotation.UnitCategory;
import org.testifyproject.guava.common.collect.ImmutableList;
import org.testifyproject.tools.Discoverable;
import org.testifyproject.extension.PreInstanceProvider;

/**
 * An implementation of PreInstanceProvider that provides virtual resource
 instances.
 *
 * @author saden
 */
@UnitCategory
@IntegrationCategory
@SystemCategory
@Discoverable
public class VirtualResourceInstanceProvider implements PreInstanceProvider {

    @Override
    public List<Instance> get(TestContext testContext) {
        ImmutableList.Builder<Instance> builder = ImmutableList.builder();

        List<ResourceInstance<VirtualResource, VirtualResourceProvider, VirtualResourceInstance>> virtualResourceInstances
                = testContext.getVirtualResourceInstances();

        virtualResourceInstances.forEach(resource -> {
            VirtualResourceInstance<Object> virtualResource = resource.getValue();
            VirtualResource annotation = resource.getAnnotation();

            String name = annotation.name();

            if (name.isEmpty()) {
                name = NamingUtil.INSTANCE.createResourceName(virtualResource.getFqn());
            } else {
                name = NamingUtil.INSTANCE.createResourceName(name);
            }

            builder.add(DefaultInstance.of(virtualResource, name, VirtualResourceInstance.class));
            builder.add(virtualResource.getResource());
        });

        return builder.build();
    }

}
