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
import org.junit.Before;
import org.junit.Test;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.testifyproject.ResourceProvider;
import org.testifyproject.TestContext;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 *
 * @author saden
 */
public class DefaultTestResourcesProviderTest {

    DefaultTestResourcesProvider sut;
    ServiceLocatorUtil serviceLocatorUtil;

    @Before
    public void init() {
        serviceLocatorUtil = mock(ServiceLocatorUtil.class);

        sut = new DefaultTestResourcesProvider(serviceLocatorUtil);
    }

    @Test
    public void callToDefaultConstructorShouldReturnNewInstance() {
        sut = new DefaultTestResourcesProvider();
    }

    @Test
    public void givenNullTestContextStartShouldDoNothing() {
        TestContext testContext = null;

        sut.start(testContext);
    }

    @Test
    public void givenTestContextWithResourcesStartShouldStartResources() {
        TestContext testContext = mock(TestContext.class);
        ResourceProvider resourceProvider = mock(ResourceProvider.class);
        List<ResourceProvider> foundResourceProviders = ImmutableList.of(resourceProvider);

        given(serviceLocatorUtil.findAll(ResourceProvider.class)).willReturn(foundResourceProviders);

        sut.start(testContext);

        verify(resourceProvider).start(testContext);
        verify(testContext).addCollectionElement(TestContextProperties.RESOURCE_PROVIDERS, resourceProvider);
    }

    @Test
    public void givenTestContextStopShouldStopResourceProviders() {
        TestContext testContext = mock(TestContext.class);
        ResourceProvider resourceProvider = mock(ResourceProvider.class);
        List<ResourceProvider> foundResourceProviders = ImmutableList.of(resourceProvider);

        given(testContext.<ResourceProvider>findCollection(TestContextProperties.RESOURCE_PROVIDERS)).willReturn(foundResourceProviders);

        sut.stop(testContext);

        verify(resourceProvider).stop(testContext);
    }

}
