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
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import org.testifyproject.Instance;
import org.testifyproject.LocalResourceInstance;
import org.testifyproject.LocalResourceProvider;
import org.testifyproject.ResourceInstance;
import org.testifyproject.TestContext;
import org.testifyproject.annotation.LocalResource;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 *
 * @author saden
 */
public class LocalResourceInstanceProviderTest {

    LocalResourceInstanceProvider sut;

    @Before
    public void init() {
        sut = new LocalResourceInstanceProvider();
    }

    @Test
    public void givenTestContextWithLocalResourceInstancesWithoutNameGetShouldReturnInstnaces() {
        TestContext testContext = mock(TestContext.class);
        ResourceInstance<LocalResource, LocalResourceProvider, LocalResourceInstance> resourceInstance
                = mock(ResourceInstance.class);
        List<ResourceInstance<LocalResource, LocalResourceProvider, LocalResourceInstance>> localResourcesInstances
                = ImmutableList.of(resourceInstance);
        LocalResourceInstance value = mock(LocalResourceInstance.class);
        LocalResource annotation = mock(LocalResource.class, Answers.RETURNS_MOCKS);
        String fqn = "test";
        Instance resource = mock(Instance.class);
        Instance client = mock(Instance.class);
        Optional<Instance<Object>> foundClient = Optional.of(client);

        given(testContext.getLocalResourceInstances()).willReturn(localResourcesInstances);
        given(resourceInstance.getValue()).willReturn(value);
        given(resourceInstance.getAnnotation()).willReturn(annotation);
        given(annotation.name()).willReturn("");
        given(value.getFqn()).willReturn(fqn);
        given(value.getResource()).willReturn(resource);
        given(value.getClient()).willReturn(foundClient);

        List<Instance> result = sut.get(testContext);

        assertThat(result).hasSize(3).contains(resource, client);
    }
    
    @Test
    public void givenTestContextWithLocalResourceInstancesWithNameGetShouldReturnInstnaces() {
        TestContext testContext = mock(TestContext.class);
        ResourceInstance<LocalResource, LocalResourceProvider, LocalResourceInstance> resourceInstance
                = mock(ResourceInstance.class);
        List<ResourceInstance<LocalResource, LocalResourceProvider, LocalResourceInstance>> localResourcesInstances
                = ImmutableList.of(resourceInstance);
        LocalResourceInstance value = mock(LocalResourceInstance.class);
        LocalResource annotation = mock(LocalResource.class, Answers.RETURNS_MOCKS);
        Instance resource = mock(Instance.class);
        Instance client = mock(Instance.class);
        Optional<Instance<Object>> foundClient = Optional.of(client);

        given(testContext.getLocalResourceInstances()).willReturn(localResourcesInstances);
        given(resourceInstance.getValue()).willReturn(value);
        given(resourceInstance.getAnnotation()).willReturn(annotation);
        given(annotation.name()).willReturn("test");
        given(value.getResource()).willReturn(resource);
        given(value.getClient()).willReturn(foundClient);

        List<Instance> result = sut.get(testContext);

        assertThat(result).hasSize(3).contains(resource, client);
    }

}
