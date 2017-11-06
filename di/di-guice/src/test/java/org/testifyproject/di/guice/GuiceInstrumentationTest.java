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
package org.testifyproject.di.guice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.Collection;

import org.junit.Test;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Module;
import org.testifyproject.core.DefaultTestContextBuilder;
import org.testifyproject.core.TestContextHolder;
import org.testifyproject.core.annotation.DefaultModule;
import org.testifyproject.di.fixture.dynamic.DynamicContract;
import org.testifyproject.di.fixture.dynamic.DynamicModule;
import org.testifyproject.guava.common.collect.ImmutableList;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 *
 * @author saden
 */
public class GuiceInstrumentationTest {

    @Test
    public void givenTestContextWithModuleInjectorShouldContainModuleServices() throws Exception {
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        TestContext testContext = DefaultTestContextBuilder.builder()
                .testDescriptor(testDescriptor)
                .build();
        Module module = DefaultModule.of(DynamicModule.class);
        Collection<Module> modules = ImmutableList.of(module);
        TestContextHolder.INSTANCE.set(testContext);

        given(testDescriptor.getModules()).willReturn(modules);

        Injector sut = Guice.createInjector();

        DynamicContract result = sut.getInstance(DynamicContract.class);
        assertThat(result).isNotNull();
    }

}
