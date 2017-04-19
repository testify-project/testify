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
package org.testifyproject.core;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.testifyproject.ResourceProvider;
import org.testifyproject.ServiceInstance;
import org.testifyproject.StartStrategy;
import org.testifyproject.TestContext;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 *
 * @author saden
 */
public class DefaultTestResourcesProviderTest {

    DefaultTestResourcesProvider cut;
    ServiceLocatorUtil serviceLocatorUtil;
    Queue resourceProviders;

    @Before
    public void init() {
        serviceLocatorUtil = mock(ServiceLocatorUtil.class);
        resourceProviders = new ConcurrentLinkedQueue();

        cut = new DefaultTestResourcesProvider(serviceLocatorUtil, resourceProviders);
    }

    @Test
    public void callToDefaultConstructorShouldReturnNewInstance() {
        cut = new DefaultTestResourcesProvider();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullTestContextStartShouldThrowException() {
        TestContext testContext = null;
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        cut.start(testContext, serviceInstance);
    }

    @Test
    public void givenEagerResourceStrategyStartShouldStartResources() {
        TestContext testContext = mock(TestContext.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);
        ResourceProvider resourceProvider = mock(ResourceProvider.class);
        List<ResourceProvider> foundResourceProviders = ImmutableList.of(resourceProvider);
        StartStrategy resourceStartStrategy = StartStrategy.EAGER;

        given(testContext.getResourceStartStrategy()).willReturn(resourceStartStrategy);
        given(serviceLocatorUtil.findAll(ResourceProvider.class)).willReturn(foundResourceProviders);

        cut.start(testContext, serviceInstance);

        assertThat(resourceProviders).contains(resourceProvider);
        verify(resourceProvider).start(testContext, serviceInstance);
    }

    @Test
    public void givenEagerResourceStrategyStopShouldStopResources() {
        TestContext testContext = mock(TestContext.class);
        ResourceProvider resourceProvider = mock(ResourceProvider.class);
        resourceProviders.add(resourceProvider);
        StartStrategy resourceStartStrategy = StartStrategy.EAGER;

        given(testContext.getResourceStartStrategy()).willReturn(resourceStartStrategy);

        cut.stop(testContext);

        verify(resourceProvider).stop();
    }
}
