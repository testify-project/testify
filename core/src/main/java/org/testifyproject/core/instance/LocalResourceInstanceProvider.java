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

import java.util.Collection;
import java.util.List;

import org.testifyproject.Instance;
import org.testifyproject.LocalResourceInfo;
import org.testifyproject.LocalResourceInstance;
import org.testifyproject.TestContext;
import org.testifyproject.annotation.LocalResource;
import org.testifyproject.core.DefaultInstance;
import org.testifyproject.core.util.NamingUtil;
import org.testifyproject.extension.InstanceProvider;
import org.testifyproject.extension.annotation.IntegrationCategory;
import org.testifyproject.extension.annotation.SystemCategory;
import org.testifyproject.extension.annotation.UnitCategory;
import org.testifyproject.guava.common.collect.ImmutableList;
import org.testifyproject.tools.Discoverable;

/**
 * An implementation of PreInstanceProvider that provides local resource instances.
 *
 * @author saden
 */
@UnitCategory
@IntegrationCategory
@SystemCategory
@Discoverable
public class LocalResourceInstanceProvider implements InstanceProvider {

    @Override
    public List<Instance> get(TestContext testContext) {
        ImmutableList.Builder<Instance> builder = ImmutableList.builder();

        Collection<LocalResourceInfo> localResourceInstances = testContext
                .getLocalResources();

        localResourceInstances.forEach(resourceInstance -> {
            LocalResourceInstance<Object, Object> value = resourceInstance.getValue();
            LocalResource annotation = resourceInstance.getAnnotation();
            String name = annotation.name();

            if (name.isEmpty()) {
                name = NamingUtil.INSTANCE.createResourceName(value.getFqn());
            } else {
                name = NamingUtil.INSTANCE.createResourceName(name);
            }

            Instance<LocalResourceInstance> instance =
                    DefaultInstance.of(value, name, LocalResourceInstance.class);

            builder
                    .add(instance)
                    .add(value.getResource());

            value.getClient().ifPresent(builder::add);
        });

        return builder.build();
    }

}
