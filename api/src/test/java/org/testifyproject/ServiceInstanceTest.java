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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.lang.annotation.Annotation;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;

/**
 *
 * @author saden
 */
public class ServiceInstanceTest {

    ServiceInstance sut;

    @Before
    public void init() {
        sut = mock(ServiceInstance.class, Answers.CALLS_REAL_METHODS);
    }

    @Test
    public void callToIsRunningShouldReturnFalse() {
        Boolean result = sut.isRunning();

        assertThat(result).isFalse();
        verify(sut).isRunning();
    }

    @Test
    public void callToDestroyShouldDoNothing() {
        sut.destroy();

        verify(sut).destroy();
    }

    @Test
    public void callToInjectShouldDoNothing() {
        Object instance = mock(Object.class);

        sut.inject(instance);

        verify(sut).inject(instance);
        verifyZeroInteractions(instance);
    }

    @Test
    public void callToGetNameQualifiersShouldReturnAnnotations() {
        Set<Class<? extends Annotation>> result = sut.getNameQualifers();

        assertThat(result).isEmpty();
        verify(sut).getNameQualifers();
    }

    @Test
    public void callToGetCustomQualifiersShouldReturnAnnotations() {
        Set<Class<? extends Annotation>> result = sut.getCustomQualifiers();

        assertThat(result).isEmpty();
        verify(sut).getCustomQualifiers();
    }
}
