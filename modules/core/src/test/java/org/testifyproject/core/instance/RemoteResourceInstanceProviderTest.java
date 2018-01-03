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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.testifyproject.Instance;
import org.testifyproject.RemoteResourceInfo;
import org.testifyproject.RemoteResourceInstance;
import org.testifyproject.TestContext;
import org.testifyproject.annotation.RemoteResource;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 *
 * @author saden
 */
public class RemoteResourceInstanceProviderTest {

    RemoteResourceInstanceProvider sut;

    @Before
    public void init() {
        sut = new RemoteResourceInstanceProvider();
    }

    @Test
    public void givenTestContextWithRemoteResourceInstancesWithoutNameGetShouldReturnInstnaces() {
        TestContext testContext = mock(TestContext.class);
        RemoteResourceInfo resourceInstance = mock(RemoteResourceInfo.class);
        List<RemoteResourceInfo> remoteResourcesInstances =
                ImmutableList.of(resourceInstance);
        RemoteResourceInstance value = mock(RemoteResourceInstance.class);
        RemoteResource annotation = mock(RemoteResource.class, Answers.RETURNS_MOCKS);
        String fqn = "test";
        Instance resource = mock(Instance.class);

        given(testContext.getRemoteResources()).willReturn(remoteResourcesInstances);
        given(resourceInstance.getValue()).willReturn(value);
        given(resourceInstance.getAnnotation()).willReturn(annotation);
        given(annotation.name()).willReturn("");
        given(value.getFqn()).willReturn(fqn);
        given(value.getResource()).willReturn(resource);

        List<Instance> result = sut.get(testContext);

        assertThat(result).hasSize(2).contains(resource);
    }

    @Test
    public void givenTestContextWithRemoteResourceInstancesWithNameGetShouldReturnInstnaces() {
        TestContext testContext = mock(TestContext.class);
        RemoteResourceInfo resourceInstance = mock(RemoteResourceInfo.class);
        List<RemoteResourceInfo> remoteResourcesInstances =
                ImmutableList.of(resourceInstance);
        RemoteResourceInstance value = mock(RemoteResourceInstance.class);
        RemoteResource annotation = mock(RemoteResource.class, Answers.RETURNS_MOCKS);
        Instance resource = mock(Instance.class);

        given(testContext.getRemoteResources()).willReturn(remoteResourcesInstances);
        given(resourceInstance.getValue()).willReturn(value);
        given(resourceInstance.getAnnotation()).willReturn(annotation);
        given(annotation.name()).willReturn("test");
        given(value.getResource()).willReturn(resource);

        List<Instance> result = sut.get(testContext);

        assertThat(result).hasSize(2).contains(resource);
    }

}
