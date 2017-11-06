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
package org.testifyproject.di.hk2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.Collection;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Test;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Module;
import org.testifyproject.annotation.Scan;
import org.testifyproject.core.DefaultTestContextBuilder;
import org.testifyproject.core.TestContextHolder;
import org.testifyproject.core.annotation.DefaultModule;
import org.testifyproject.di.fixture.common.WiredService;
import org.testifyproject.di.fixture.module.TestModule;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 *
 * @author saden
 */
public class HK2InstrumentationTest {

    @Test
    public void givenTestContextWithModuleInjectorShouldContainModuleServices() throws Exception {
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        TestContext testContext = DefaultTestContextBuilder.builder()
                .testDescriptor(testDescriptor)
                .build();
        Module module = DefaultModule.of(TestModule.class);
        Collection<Module> modules = ImmutableList.of(module);
        Collection<Scan> scans = ImmutableList.of();
        TestContextHolder.INSTANCE.set(testContext);

        given(testDescriptor.getModules()).willReturn(modules);
        given(testDescriptor.getScans()).willReturn(scans);
        given(testDescriptor.getTestClassLoader()).willReturn(this.getClass().getClassLoader());

        ServiceLocator sut = ServiceLocatorUtilities.createAndPopulateServiceLocator();

        WiredService result = sut.getService(WiredService.class);
        assertThat(result).isNotNull();
        
        assertThat(sut.getService(TestContext.class)).isEqualTo(testContext);
    }

}
