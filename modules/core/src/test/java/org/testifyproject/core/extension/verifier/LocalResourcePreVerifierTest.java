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
import org.testifyproject.annotation.LocalResource;
import org.testifyproject.fixture.resource.InvalidLocalResourceProvider;
import org.testifyproject.fixture.resource.ValidLocalResourceProvider;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 *
 * @author saden
 */
public class LocalResourcePreVerifierTest {

    LocalResourcePreVerifier sut;

    @Before
    public void init() {
        sut = new LocalResourcePreVerifier();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullTestContextVerifyShouldThrowException() {
        sut.verify(null);
    }

    @Test
    public void givenInvalidTestContextVerifyShouldThrowException() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        LocalResource localResource = mock(LocalResource.class);
        List<LocalResource> localResources = ImmutableList.of(localResource);
        Class provider = InvalidLocalResourceProvider.class;

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getLocalResources()).willReturn(localResources);
        given(localResource.value()).willReturn(provider);

        sut.verify(testContext);

        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getLocalResources();
        verify(localResource).value();
        verify(testContext).addError(anyString(), any());
    }

    @Test
    public void givenValidTestContextVerifyShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        LocalResource localResource = mock(LocalResource.class);
        List<LocalResource> localResources = ImmutableList.of(localResource);
        Class provider = ValidLocalResourceProvider.class;

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getLocalResources()).willReturn(localResources);
        given(localResource.value()).willReturn(provider);

        sut.verify(testContext);

        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getLocalResources();
        verify(localResource).value();

    }

}
