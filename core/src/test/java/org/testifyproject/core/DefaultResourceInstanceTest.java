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

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import org.testifyproject.LocalResourceInstance;
import org.testifyproject.LocalResourceProvider;
import org.testifyproject.ResourceInstance;
import org.testifyproject.annotation.LocalResource;

/**
 *
 * @author saden
 */
public class DefaultResourceInstanceTest {

    ResourceInstance<LocalResource, LocalResourceProvider, LocalResourceInstance> sut;

    LocalResource annotation;
    LocalResourceProvider provider;
    LocalResourceInstance value;

    @Before
    public void init() {
        annotation = mock(LocalResource.class);
        provider = mock(LocalResourceProvider.class);
        value = mock(LocalResourceInstance.class);

        sut = DefaultResourceInstance.of(annotation, provider, value);
    }

    @Test
    public void validateSutInstance() {
        assertThat(sut).isNotNull();
        assertThat(sut.getAnnotation()).isEqualTo(annotation);
        assertThat(sut.getProvider()).isEqualTo(provider);
        assertThat(sut.getValue()).isEqualTo(value);
    }

    @Test
    public void givenInstanceOfShouldReturn() {
        sut = DefaultResourceInstance.of(annotation, provider, value);

        assertThat(sut).isNotNull();
        assertThat(sut.getAnnotation()).isEqualTo(annotation);
        assertThat(sut.getProvider()).isEqualTo(provider);
        assertThat(sut.getValue()).isEqualTo(value);
    }

    @Test
    public void givenNullInstancesShouldNotBeEqual() {
        assertThat(sut).isNotEqualTo(null);
    }

    @Test
    public void givenDifferentTypeInstancesShouldNotBeEqual() {
        String differentType = "instance";

        assertThat(sut).isNotEqualTo(differentType);
        assertThat(sut.hashCode()).isNotEqualTo(differentType.hashCode());
    }

    @Test
    public void givenUnequalInstancesShouldNotBeEqual() {
        ResourceInstance unequal = DefaultResourceInstance.of(annotation, provider, null);

        assertThat(sut).isNotEqualTo(unequal);
        assertThat(sut.hashCode()).isNotEqualTo(unequal.hashCode());
    }

    @Test
    public void givenSameInstancesShouldBeEqual() {
        assertThat(sut).isEqualTo(sut);
    }

    @Test
    public void givenEqualInstancesShouldBeEqual() {
        ResourceInstance equal = DefaultResourceInstance.of(annotation, provider, value);

        assertThat(sut).isEqualTo(equal);
        assertThat(sut.hashCode()).isEqualTo(equal.hashCode());
    }

    @Test
    public void callToToStringShouldReturnHumanReadableString() {
        String result = sut.toString();

        assertThat(result).contains("DefaultResourceInstance", "annotation", "provider", "value");
    }

}
