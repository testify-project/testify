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
import org.testifyproject.annotation.Sut;
import org.testifyproject.guava.common.collect.ImmutableSet;

/**
 *
 * @author saden
 */
public class ServletInstanceTest {

    @Sut
    ServletInstance sut;

    @Before
    public void init() {
        ServletContainerInitializer initializer = mock(ServletContainerInitializer.class);
        Set<Class<?>> handlers = ImmutableSet.of();
        sut = new ServletInstance(initializer, handlers);
    }

    @Test
    public void verifySut() {
        assertThat(sut).isNotNull();
        assertThat(ServletInstance.builder()).isNotNull();
    }

}
