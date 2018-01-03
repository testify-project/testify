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
package org.testifyproject;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.testifyproject.annotation.LocalResource;

/**
 *
 * @author saden
 */
public class LocalResourceProviderTest {

    LocalResourceProvider sut;

    @Before
    public void init() {
        sut = mock(LocalResourceProvider.class, Answers.CALLS_REAL_METHODS);
    }

    @Test
    public void callToStopShouldDoNothing() throws Exception {
        TestContext testContext = mock(TestContext.class);
        LocalResource resource = mock(LocalResource.class);
        LocalResourceInstance instance = mock(LocalResourceInstance.class);
        Set dataFiles = mock(Set.class);

        sut.load(testContext, resource, instance, dataFiles);

        verify(sut).load(testContext, resource, instance, dataFiles);
        verifyNoMoreInteractions(sut, testContext, resource, dataFiles);
    }
}
