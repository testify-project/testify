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

import java.lang.annotation.Annotation;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Module;
import org.testifyproject.core.DefaultTestContextBuilder;
import org.testifyproject.core.TestContextHolder;
import org.testifyproject.core.annotation.DefaultModule;
import org.testifyproject.di.fixture.common.Greeting;
import org.testifyproject.di.fixture.common.GreetingModule;
import org.testifyproject.di.fixture.common.GreetingQualfier;
import org.testifyproject.di.fixture.common.InjectedGreeter;
import org.testifyproject.di.fixture.common.impl.Caio;
import org.testifyproject.di.fixture.common.impl.Haye;
import org.testifyproject.di.fixture.common.impl.Hello;
import org.testifyproject.guava.common.collect.ImmutableList;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

/**
 *
 * @author saden
 */
public class GuiceServiceInstanceTest {

    private GuiceServiceInstance sut;

    @Before
    public void init() {
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        TestContext testContext = DefaultTestContextBuilder.builder()
                .testDescriptor(testDescriptor)
                .build();

        Module module = DefaultModule.of(GreetingModule.class);
        Collection<Module> modules = ImmutableList.of(module);
        TestContextHolder.INSTANCE.set(testContext);

        given(testDescriptor.getModules()).willReturn(modules);

        Injector injector = Guice.createInjector();
        sut = new GuiceServiceInstance(injector);
    }

    @Test
    public void callToGetContextShouldReturnInjector() {
        Object result = sut.getContext();
        assertThat(result).isInstanceOf(Injector.class);
    }

    @Test
    public void callToIsRunningShouldReturnTrue() {
        Boolean result = sut.isRunning();

        assertThat(result).isTrue();
    }

    @Test
    public void givenInjectableInstanceInjectShouldInjectInstanceFieldAndMethod() {
        InjectedGreeter injectedGreeter = new InjectedGreeter();

        assertThat(injectedGreeter.getField()).isNull();
        assertThat(injectedGreeter.getMethod()).isNull();

        sut.inject(injectedGreeter);

        assertThat(injectedGreeter.getField()).isNotNull();
        assertThat(injectedGreeter.getMethod()).isNotNull();
    }

    @Test
    public void givenContractTypeGetServiceShouldReturnService() {
        Greeting result = sut.getService(Greeting.class);
        assertThat(result).isNotNull().isInstanceOf(Hello.class);
    }

    @Test
    public void givenImplementationTypeGetServiceShouldReturnService() {
        Hello result = sut.getService(Hello.class);
        assertThat(result).isNotNull();
    }

    @Test
    public void givenContractTypeAndNameGetServiceShouldReturnService() {
        Greeting result = sut.getService(Greeting.class, "Haye");
        assertThat(result).isNotNull().isInstanceOf(Haye.class);
    }

    @Test
    public void givenContractTypeAndNamedAnnotationGetServiceShouldReturnService() {
        Greeting result = sut.getService(Greeting.class, Names.named("Haye"));
        assertThat(result).isNotNull().isInstanceOf(Haye.class);
    }

    @Test
    public void givenContractTypeAndNoAnnotationGetServiceShouldReturnService() {
        Annotation[] qualifiers = {};
        Hello result = sut.getService(Hello.class, qualifiers);
        assertThat(result).isNotNull();
    }

    @Test
    public void givenContractTypeAndQualifierGetServiceShouldReturnService() {
        GreetingQualfier qualifier = new GreetingQualfier() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return GreetingQualfier.class;
            }
        };
        Greeting result = sut.getService(Greeting.class, qualifier);
        assertThat(result).isNotNull().isInstanceOf(Caio.class);
    }

    @Test
    public void callToGetNameQualifersShouldReturnAnnotaitons() {
        assertThat(sut.getNameQualifers()).hasSize(2);
    }

    @Test
    public void callToGetCustomQualifiersShouldReturnAnnotaitons() {
        assertThat(sut.getCustomQualifiers()).hasSize(2);
    }
}
