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
package org.testifyproject.di.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.testifyproject.ServiceInstance;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Module;
import org.testifyproject.annotation.Scan;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 *
 * @author saden
 */
public class SpringServiceProviderTest {

    SpringServiceProvider sut;

    @Before
    public void init() {
        sut = new SpringServiceProvider();
    }

    @Test
    public void callToCreateWithTestContextShouldCreateSpringApplicationContext() {
        TestContext testContext = mock(TestContext.class);
        String testName = "testName";

        given(testContext.getName()).willReturn(testName);

        ConfigurableApplicationContext result = sut.create(testContext);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testName);
        assertThat(result.getDisplayName()).isEqualTo(testName);
    }

    @Test
    public void callToConfigureShouldReturnServiceInstance() {
        TestContext testContext = mock(TestContext.class);
        ConfigurableApplicationContext applicationContext = mock(
                ConfigurableApplicationContext.class);
        String testName = "testName";

        given(testContext.getName()).willReturn(testName);

        ServiceInstance result = sut.configure(testContext, applicationContext);

        assertThat(result).isNotNull();
        assertThat((ConfigurableApplicationContext) result.getContext()).isEqualTo(
                applicationContext);
        verify(applicationContext).setId(testName);
        verify(applicationContext).addBeanFactoryPostProcessor(any(
                SpringBeanFactoryPostProcessor.class));
    }

    @Test
    public void callToPostConfigureWithModulesAndScansShouldAddModulesAndScansToServiceInstance() {
        TestContext testContext = mock(TestContext.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);

        Module module = mock(Module.class);
        List<Module> modules = ImmutableList.of(module);

        Scan scan = mock(Scan.class);
        List<Scan> scans = ImmutableList.of(scan);

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getModules()).willReturn(modules);
        given(testDescriptor.getScans()).willReturn(scans);

        sut.postConfigure(testContext, serviceInstance);

        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getModules();
        verify(serviceInstance).addModules(module);
        verify(testDescriptor).getScans();
        verify(serviceInstance).addScans(scan);
    }
}
