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
import static org.mockito.Mockito.mock;

import java.lang.annotation.Annotation;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.core.annotation.DefaultModule;
import org.testifyproject.di.fixture.common.Greeting;
import org.testifyproject.di.fixture.common.GreetingModule;
import org.testifyproject.di.fixture.common.GreetingQualfier;
import org.testifyproject.di.fixture.common.InjectedGreeter;
import org.testifyproject.di.fixture.common.impl.Caio;
import org.testifyproject.di.fixture.common.impl.Haye;
import org.testifyproject.di.fixture.common.impl.Hello;
import org.testifyproject.di.fixture.dynamic.DynamicConstant;
import org.testifyproject.di.fixture.dynamic.DynamicContract;
import org.testifyproject.di.fixture.dynamic.DynamicModule;
import org.testifyproject.di.fixture.dynamic.FixtureModule;

import com.google.inject.Injector;
import com.google.inject.name.Names;

/**
 *
 * @author saden
 */
public class GuiceServiceInstanceTest {

    private Injector injector;
    private GuiceServiceInstance sut;

    @Before
    public void init() {
        sut = new GuiceServiceInstance(injector);
        DefaultModule module = new DefaultModule(GreetingModule.class);
        sut.addModules(module);
    }

    @Test
    public void callToGetContextShouldReturnInjector() {
        Object result = sut.getContext();
        assertThat(result).isSameAs(injector);
    }

    @Test
    public void callToIsRunningShouldReturnTrue() {
        sut.updateInjector();
        Boolean result = sut.isRunning();

        assertThat(result).isTrue();
    }

    @Test
    public void givenInjectableInstanceAndNonRunningInjectorInjectShouldDoNothing() {
        InjectedGreeter injectedGreeter = new InjectedGreeter();

        assertThat(injectedGreeter.getField()).isNull();
        assertThat(injectedGreeter.getMethod()).isNull();

        sut.inject(injectedGreeter);

        assertThat(injectedGreeter.getField()).isNull();
        assertThat(injectedGreeter.getMethod()).isNull();
    }

    @Test
    public void givenInjectableInstanceInjectShouldInjectInstanceFieldAndMethod() {
        InjectedGreeter injectedGreeter = new InjectedGreeter();

        assertThat(injectedGreeter.getField()).isNull();
        assertThat(injectedGreeter.getMethod()).isNull();

        sut.updateInjector();
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
    public void givenInstanceAddConstantShouldAddConstant() {
        String value = "test";
        DynamicConstant dynamicConstant = new DynamicConstant(value);
        sut.addConstant(dynamicConstant, null, null);

        DynamicConstant result = sut.getService(DynamicConstant.class);

        assertThat(result).isNotNull();
        assertThat(result.getValue()).isEqualTo(value);
    }

    @Test
    public void givenInstanceAndNameAddConstantShouldAddConstant() {
        String value = "test";
        DynamicConstant dynamicConstant = new DynamicConstant(value);
        sut.addConstant(dynamicConstant, "constant", null);

        DynamicConstant result = sut.getService(DynamicConstant.class, Names.named(
                "constant"));

        assertThat(result).isNotNull();
        assertThat(result.getValue()).isEqualTo(value);
    }

    @Test
    public void givenInstanceAndNameAndContractAddConstantShouldAddConstant() {
        String value = "test";
        DynamicConstant dynamicConstant = new DynamicConstant(value);
        sut.addConstant(dynamicConstant, "constant", DynamicContract.class);

        DynamicContract result = sut.getService(DynamicContract.class, Names.named(
                "constant"));

        assertThat(result).isNotNull();
        assertThat(result.getValue()).isEqualTo(value);
    }

    @Test
    public void givenInstanceAndContractShouldRepleaceService() {
        Greeting instance = mock(Greeting.class);
        sut.replace(instance, null, Greeting.class);

        Greeting result = sut.getService(Greeting.class);

        assertThat(result).isSameAs(instance);
    }

    @Test
    public void givenInstanceAndImplementationShouldRepleaceService() {
        Hello instance = mock(Hello.class);
        sut.replace(instance, null, Hello.class);

        Hello result = sut.getService(Hello.class);

        assertThat(result).isSameAs(instance);
    }

    @Test
    public void givenInstanceAndNameAndContranctShouldRepleaceService() {
        String name = "Haye";
        Greeting instance = mock(Greeting.class);
        sut.replace(instance, name, Greeting.class);

        Greeting result = sut.getService(Greeting.class, Names.named(name));

        assertThat(result).isSameAs(instance);
    }

    @Test
    public void givenModuleAddModuleShouldAddModule() {
        DefaultModule module = new DefaultModule(DynamicModule.class);
        sut.addModules(module);

        DynamicContract result = sut.getService(DynamicContract.class);
        assertThat(result).isNotNull();

        Greeting greeting = sut.getService(Greeting.class);
        assertThat(greeting).isNotNull().isInstanceOf(Hello.class);
    }

    @Test
    public void givenFixtureModuleAddModuleShouldAddModule() {
        DefaultModule module = new DefaultModule(FixtureModule.class);
        sut.addModules(module);

        DynamicContract result = sut.getService(DynamicContract.class);
        assertThat(result).isNotNull();

        Greeting greeting = sut.getService(Greeting.class);
        assertThat(greeting).isNotNull().isInstanceOf(Hello.class);
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
