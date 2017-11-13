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
package org.testifyproject.server.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Set;

import javax.servlet.ServletContainerInitializer;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.guava.common.collect.ImmutableSet;

/**
 *
 * @author saden
 */
public class ServletInstanceTest {

    ServletInstance sut;
    Set<Class<?>> handlers;
    ServletContainerInitializer initializer;

    @Before
    public void init() {
        initializer = mock(ServletContainerInitializer.class);
        handlers = ImmutableSet.of();

        sut = new ServletInstance(initializer, handlers);
    }

    @Test
    public void verifySutAndBuilder() {
        assertThat(sut).isNotNull();
        assertThat(ServletInstance.builder()).isNotNull();
    }

    @Test
    public void callToGetInitializerShouldReturnInitializer() {
        assertThat(sut.getInitializer()).isEqualTo(initializer);
    }

    @Test
    public void callToGetHandlersShouldReturnHandlers() {
        assertThat(sut.getHandlers()).isEqualTo(handlers);
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
        ServletContainerInitializer tInitializer = mock(ServletContainerInitializer.class);
        Set<Class<?>> tHandlers = ImmutableSet.of();
        ServletInstance unequal = new ServletInstance(tInitializer, tHandlers);

        assertThat(sut).isNotEqualTo(unequal);
        assertThat(sut.hashCode()).isNotEqualTo(unequal.hashCode());
    }

    @Test
    public void givenSameInstancesShouldBeEqual() {
        assertThat(sut).isEqualTo(sut);
    }

    @Test
    public void givenEqualInstancesShouldBeEqual() {
        ServletInstance equal = new ServletInstance(initializer, handlers);

        assertThat(sut).isEqualTo(equal);
        assertThat(sut.hashCode()).isEqualTo(equal.hashCode());
    }

    @Test
    public void callToToStringShouldReturnHumanReadableString() {
        String result = sut.toString();

        assertThat(result).contains(
                "ServletInstance",
                "handlers",
                "initializer");
    }

}
