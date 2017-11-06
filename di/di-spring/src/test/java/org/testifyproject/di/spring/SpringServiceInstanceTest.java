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

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Provider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.BeanDefinitionDefaults;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.testifyproject.di.fixture.autowired.Greeting;
import org.testifyproject.di.fixture.autowired.impl.Haye;
import org.testifyproject.di.fixture.autowired.impl.Hello;
import org.testifyproject.di.fixture.common.InjectedGreeter;
import org.testifyproject.guava.common.reflect.TypeToken;

/**
 *
 * @author saden
 */
public class SpringServiceInstanceTest {

    SpringServiceInstance sut;
    AnnotationConfigApplicationContext context;

    @Before
    public void init() {
        context = new AnnotationConfigApplicationContext();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(
                (BeanDefinitionRegistry) context);
        BeanDefinitionDefaults beanDefinitionDefaults = scanner
                .getBeanDefinitionDefaults();
        beanDefinitionDefaults.setLazyInit(true);

        scanner.scan("org.testifyproject.di.fixture");
        context.refresh();
        sut = new SpringServiceInstance(context);
    }

    @After
    public void destroy() {
        sut.destroy();
    }

    @Test
    public void callToDestroyShouldDestroyApplicationContex() {
        sut.destroy();
    }

    @Test
    public void callToGetContextShouldReturnApplicationContex() {
        assertThat(sut.getContext())
                .isNotNull()
                .isSameAs(context);
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
    public void givenTypeGetServiceShouldReturnService() {
        Hello greeting = sut.getService(Hello.class);
        assertThat(greeting).isNotNull();
    }

    @Test
    public void givenContractTypeGetServiceShouldReturnService() {
        Greeting greeting = sut.getService(Greeting.class);
        assertThat(greeting).isNotNull();
    }

    @Test
    public void givenTypeAndNameGetServiceShouldReturnService() {
        Haye greeting = sut.getService(Haye.class, "haye");

        assertThat(greeting).isNotNull();
    }

    @Test
    public void givenApplicationContextGetServiceShouldReturnApplicationContext() {
        ApplicationContext result = sut.getService(ApplicationContext.class);

        assertThat(result).isEqualTo(context);
    }

    @Test
    public void givenApplicationContextGetServiceWithoutNameShouldReturnApplicationContext() {
        String name = "applicationContext";
        ApplicationContext result = sut.getService(ApplicationContext.class, name);

        assertThat(result).isEqualTo(context);
    }

    @Test
    public void givenTypeAndAnnotationGetServiceShouldReturnService() {
        Qualifier qualifier = new Qualifier() {
            @Override
            public String value() {
                return "Haye";
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return Qualifier.class;
            }
        };

        Haye greeting = sut.getService(Haye.class, new Annotation[]{qualifier});
        assertThat(greeting).isNotNull();
    }

    @Test
    public void givenProviderGetServiceShouldReturnProvider() {
        TypeToken<Provider<Hello>> type = new TypeToken<Provider<Hello>>() {
        };
        Provider<Hello> result = sut.getService(type.getType());

        assertThat(result).isNotNull();
        assertThat(result.get()).isNotNull();
    }

    @Test
    public void givenOptionalGetServiceShouldReturnOptional() {
        TypeToken<Optional<Hello>> type = new TypeToken<Optional<Hello>>() {
        };
        Optional<Hello> result = sut.getService(type.getType());

        assertThat(result).isNotEmpty();
    }

    @Test
    public void givenListGetServiceShouldReturnList() {
        TypeToken<List<Greeting>> type = new TypeToken<List<Greeting>>() {
        };
        List<Greeting> result = sut.getService(type.getType());

        assertThat(result).isNotEmpty();
    }

    @Test
    public void givenSetGetServiceShouldReturnSet() {
        TypeToken<Set<Greeting>> type = new TypeToken<Set<Greeting>>() {
        };
        Set<Greeting> result = sut.getService(type.getType());

        assertThat(result).isNotEmpty();
    }

    @Test
    public void givenMapGetServiceShouldReturnMap() {
        TypeToken<Map<String, Greeting>> type = new TypeToken<Map<String, Greeting>>() {
        };
        Map<String, Greeting> result = sut.getService(type.getType());
        assertThat(result).isNotEmpty();
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
