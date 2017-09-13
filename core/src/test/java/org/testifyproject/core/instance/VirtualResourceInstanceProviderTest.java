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
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import org.testifyproject.Instance;
import org.testifyproject.ResourceInstance;
import org.testifyproject.TestContext;
import org.testifyproject.VirtualResourceInstance;
import org.testifyproject.VirtualResourceProvider;
import org.testifyproject.annotation.VirtualResource;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 *
 * @author saden
 */
public class VirtualResourceInstanceProviderTest {

    VirtualResourceInstanceProvider sut;

    @Before
    public void init() {
        sut = new VirtualResourceInstanceProvider();
    }

    @Test
    public void givenTestContextWithVirtualResourceInstancesWithoutNameGetShouldReturnInstnaces() {
        TestContext testContext = mock(TestContext.class);
        ResourceInstance<VirtualResource, VirtualResourceProvider, VirtualResourceInstance> resourceInstance
                = mock(ResourceInstance.class);
        List<ResourceInstance<VirtualResource, VirtualResourceProvider, VirtualResourceInstance>> virtualResourcesInstances
                = ImmutableList.of(resourceInstance);
        VirtualResourceInstance value = mock(VirtualResourceInstance.class);
        VirtualResource annotation = mock(VirtualResource.class, Answers.RETURNS_MOCKS);
        String fqn = "test";
        Instance resource = mock(Instance.class);

        given(testContext.getVirtualResourceInstances()).willReturn(virtualResourcesInstances);
        given(resourceInstance.getValue()).willReturn(value);
        given(resourceInstance.getAnnotation()).willReturn(annotation);
        given(annotation.name()).willReturn("");
        given(value.getFqn()).willReturn(fqn);
        given(value.getResource()).willReturn(resource);

        List<Instance> result = sut.get(testContext);

        assertThat(result).hasSize(2).contains(resource);
    }

    @Test
    public void givenTestContextWithVirtualResourceInstancesWithNameGetShouldReturnInstnaces() {
        TestContext testContext = mock(TestContext.class);
        ResourceInstance<VirtualResource, VirtualResourceProvider, VirtualResourceInstance> resourceInstance
                = mock(ResourceInstance.class);
        List<ResourceInstance<VirtualResource, VirtualResourceProvider, VirtualResourceInstance>> virtualResourcesInstances
                = ImmutableList.of(resourceInstance);
        VirtualResourceInstance value = mock(VirtualResourceInstance.class);
        VirtualResource annotation = mock(VirtualResource.class, Answers.RETURNS_MOCKS);
        Instance resource = mock(Instance.class);

        given(testContext.getVirtualResourceInstances()).willReturn(virtualResourcesInstances);
        given(resourceInstance.getValue()).willReturn(value);
        given(resourceInstance.getAnnotation()).willReturn(annotation);
        given(annotation.name()).willReturn("test");
        given(value.getResource()).willReturn(resource);

        List<Instance> result = sut.get(testContext);

        assertThat(result).hasSize(2).contains(resource);
    }

}
