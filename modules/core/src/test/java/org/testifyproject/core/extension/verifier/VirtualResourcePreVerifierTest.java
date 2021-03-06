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
package org.testifyproject.core.extension.verifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.VirtualResource;
import org.testifyproject.fixture.resource.InvalidVirtualResourceProvider;
import org.testifyproject.fixture.resource.ValidVirtualResourceProvider;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 *
 * @author saden
 */
public class VirtualResourcePreVerifierTest {

    VirtualResourcePreVerifier sut;

    @Before
    public void init() {
        sut = new VirtualResourcePreVerifier();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullTestContextVerifyShouldThrowException() {
        sut.verify(null);
    }

    @Test
    public void givenInvalidTestContextVerifyShouldThrowException() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        VirtualResource virtualResource = mock(VirtualResource.class);
        List<VirtualResource> virtualResources = ImmutableList.of(virtualResource);
        Class provider = InvalidVirtualResourceProvider.class;

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getVirtualResources()).willReturn(virtualResources);
        given(virtualResource.provider()).willReturn(provider);

        sut.verify(testContext);

        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getVirtualResources();
        verify(virtualResource).provider();
        verify(testContext).addError(anyString(), any());
    }

    @Test
    public void givenValidTestContextVerifyShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        VirtualResource virtualResource = mock(VirtualResource.class);
        List<VirtualResource> virtualResources = ImmutableList.of(virtualResource);
        Class provider = ValidVirtualResourceProvider.class;

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getVirtualResources()).willReturn(virtualResources);
        given(virtualResource.provider()).willReturn(provider);

        sut.verify(testContext);

        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getVirtualResources();
        verify(virtualResource).provider();
    }

}
