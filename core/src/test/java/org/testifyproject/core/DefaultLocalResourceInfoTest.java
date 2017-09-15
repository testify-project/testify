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
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.LocalResourceInstance;
import org.testifyproject.LocalResourceProvider;
import org.testifyproject.ResourceInfo;
import org.testifyproject.annotation.LocalResource;

/**
 *
 * @author saden
 */
public class DefaultLocalResourceInfoTest {

    ResourceInfo sut;

    LocalResource annotation;
    LocalResourceProvider provider;
    LocalResourceInstance value;

    @Before
    public void init() {
        annotation = mock(LocalResource.class);
        provider = mock(LocalResourceProvider.class);
        value = mock(LocalResourceInstance.class);

        sut = DefaultLocalResourceInfo.of(annotation, provider, value);
    }

    @Test
    public void validateSutInstance() {
        assertThat(sut).isNotNull();
        assertThat((Object) sut.getAnnotation()).isEqualTo(annotation);
        assertThat((Object) sut.getProvider()).isEqualTo(provider);
        assertThat((Object) sut.getValue()).isEqualTo(value);
    }

    @Test
    public void givenInstanceOfShouldReturn() {
        sut = DefaultLocalResourceInfo.of(annotation, provider, value);

        assertThat(sut).isNotNull();
        assertThat((Object) sut.getAnnotation()).isEqualTo(annotation);
        assertThat((Object) sut.getProvider()).isEqualTo(provider);
        assertThat((Object) sut.getValue()).isEqualTo(value);
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
        ResourceInfo unequal = DefaultLocalResourceInfo.of(annotation, provider, null);

        assertThat(sut).isNotEqualTo(unequal);
        assertThat(sut.hashCode()).isNotEqualTo(unequal.hashCode());
    }

    @Test
    public void givenSameInstancesShouldBeEqual() {
        assertThat(sut).isEqualTo(sut);
    }

    @Test
    public void givenEqualInstancesShouldBeEqual() {
        ResourceInfo equal = DefaultLocalResourceInfo.of(annotation, provider, value);

        assertThat(sut).isEqualTo(equal);
        assertThat(sut.hashCode()).isEqualTo(equal.hashCode());
    }

    @Test
    public void callToToStringShouldReturnHumanReadableString() {
        String result = sut.toString();

        assertThat(result)
                .contains("DefaultLocalResourceInfo", "annotation", "provider", "value");
    }

}
