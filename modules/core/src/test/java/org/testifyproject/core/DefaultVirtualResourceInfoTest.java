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
import org.testifyproject.ResourceInfo;
import org.testifyproject.VirtualResourceInstance;
import org.testifyproject.VirtualResourceProvider;
import org.testifyproject.annotation.VirtualResource;

/**
 *
 * @author saden
 */
public class DefaultVirtualResourceInfoTest {

    ResourceInfo sut;

    VirtualResource annotation;
    VirtualResourceProvider provider;
    VirtualResourceInstance value;

    @Before
    public void init() {
        annotation = mock(VirtualResource.class);
        provider = mock(VirtualResourceProvider.class);
        value = mock(VirtualResourceInstance.class);

        sut = DefaultVirtualResourceInfo.of(annotation, provider, value);
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
        sut = DefaultVirtualResourceInfo.of(annotation, provider, value);

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
        ResourceInfo unequal = DefaultVirtualResourceInfo.of(annotation, provider, null);

        assertThat(sut).isNotEqualTo(unequal);
        assertThat(sut.hashCode()).isNotEqualTo(unequal.hashCode());
    }

    @Test
    public void givenSameInstancesShouldBeEqual() {
        assertThat(sut).isEqualTo(sut);
    }

    @Test
    public void givenEqualInstancesShouldBeEqual() {
        ResourceInfo equal = DefaultVirtualResourceInfo.of(annotation, provider, value);

        assertThat(sut).isEqualTo(equal);
        assertThat(sut.hashCode()).isEqualTo(equal.hashCode());
    }

    @Test
    public void callToToStringShouldReturnHumanReadableString() {
        String result = sut.toString();

        assertThat(result)
                .contains("DefaultVirtualResourceInfo", "annotation", "provider", "value");
    }

}
