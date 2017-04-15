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

import com.google.inject.Injector;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.testifyproject.ServiceInstance;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Module;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 *
 * @author saden
 */
public class GuiceServiceProviderTest {
    
    GuiceServiceProvider cut;
    
    @Before
    public void init() {
        cut = new GuiceServiceProvider();
    }
    
    @Test
    public void givenTestContextCreateShouldReturnInjector() {
        TestContext testContext = mock(TestContext.class);
        
        Injector result = cut.create(testContext);
        
        assertThat(result).isNotNull();
    }
    
    @Test
    public void givenTestContextAndInjectorConfigureShouldReturnInjector() {
        TestContext testContext = mock(TestContext.class);
        Injector injector = mock(Injector.class);
        
        ServiceInstance result = cut.configure(testContext, injector);
        
        assertThat(result).isNotNull();
        assertThat((Injector) result.getContext()).isEqualTo(injector);
    }
    
    @Test
    public void givenTestContextAndServiceInstancePostConfigureShouldAddModules() {
        TestContext testContext = mock(TestContext.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Module module = mock(Module.class);
        List<Module> modules = ImmutableList.of(module);
        
        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getModules()).willReturn(modules);
        
        cut.postConfigure(testContext, serviceInstance);
        
        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getModules();
        verify(serviceInstance).addModules(module);
    }
    
}
