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

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.inject.Provider;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.BeanDefinitionDefaults;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.testifyproject.core.annotation.DefaultModule;
import org.testifyproject.core.annotation.DefaultScan;
import org.testifyproject.di.fixture.autowired.Greeting;
import org.testifyproject.di.fixture.autowired.impl.Haye;
import org.testifyproject.di.fixture.autowired.impl.Hello;
import org.testifyproject.di.fixture.common.CreatedService;
import org.testifyproject.di.fixture.common.InjectedGreeter;
import org.testifyproject.di.fixture.common.WiredContract;
import org.testifyproject.di.fixture.common.WiredService;
import org.testifyproject.di.fixture.module.TestModule;
import org.testifyproject.guava.common.reflect.TypeToken;

/**
 *
 * @author saden
 */
public class SpringServiceInstanceTest {

    SpringServiceInstance cut;
    AnnotationConfigApplicationContext context;

    @Before
    public void init() {
        context = new AnnotationConfigApplicationContext();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner((BeanDefinitionRegistry) context);
        BeanDefinitionDefaults beanDefinitionDefaults = scanner.getBeanDefinitionDefaults();
        beanDefinitionDefaults.setLazyInit(true);

        scanner.scan("org.testifyproject.di.fixture");
        context.refresh();
        cut = new SpringServiceInstance(context);
    }

    @After
    public void destroy() {
        cut.destroy();
    }

    @Test
    public void callToDestroyShouldDestroyApplicationContex() {
        cut.destroy();
    }

    @Test
    public void callToGetContextShouldReturnApplicationContex() {
        assertThat(cut.getContext())
                .isNotNull()
                .isSameAs(context);
    }

    @Test
    public void callToIsRunningShouldReturnTrue() {
        Boolean result = cut.isRunning();

        assertThat(result).isTrue();
    }

    @Test
    public void callToInitShouldInitApplicationContex() {
        ConfigurableApplicationContext context = new AnnotationConfigApplicationContext();

        cut = new SpringServiceInstance(context);
        cut.init();
    }

    @Test
    public void givenInjectableInstanceInjectShouldInjectInstanceFieldAndMethod() {
        InjectedGreeter injectedGreeter = new InjectedGreeter();

        assertThat(injectedGreeter.getField()).isNull();
        assertThat(injectedGreeter.getMethod()).isNull();

        cut.inject(injectedGreeter);

        assertThat(injectedGreeter.getField()).isNotNull();
        assertThat(injectedGreeter.getMethod()).isNotNull();
    }

    @Test
    public void givenTypeGetServiceShouldReturnService() {
        Hello greeting = cut.getService(Hello.class);
        assertThat(greeting).isNotNull();
    }

    @Test
    public void givenContractTypeGetServiceShouldReturnService() {
        Greeting greeting = cut.getService(Greeting.class);
        assertThat(greeting).isNotNull();
    }

    @Test
    public void givenTypeAndNameGetServiceShouldReturnService() {
        Haye greeting = cut.getService(Haye.class, "haye");

        assertThat(greeting).isNotNull();
    }

    @Test
    public void givenApplicationContextGetServiceShouldReturnApplicationContext() {
        ApplicationContext result = cut.getService(ApplicationContext.class);

        assertThat(result).isEqualTo(context);
    }

    @Test
    public void givenApplicationContextGetServiceWithoutNameShouldReturnApplicationContext() {
        String name = "applicationContext";
        ApplicationContext result = cut.getService(ApplicationContext.class, name);

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

        Haye greeting = cut.getService(Haye.class, new Annotation[]{qualifier});
        assertThat(greeting).isNotNull();
    }

    @Test
    public void givenServiceInstanceAddConstantShouldAddTheService() {
        CreatedService service = new CreatedService("greeting");
        cut.addConstant(service, null, null);

        CreatedService result = context.getBean(CreatedService.class);
        assertThat(result).isNotNull();
    }

    @Test
    public void givenValidParamsReplaceIWithConstantShouldRepalceService() {
        String name = "newgreeting";
        Hello constant = new Hello();

        cut.replace(constant, name, Greeting.class);

        Greeting result = context.getBean(name, Greeting.class);
        assertThat(result).isSameAs(constant);
    }

    @Test
    public void givenConstantReplaceShouldReplaceAllInstances() {
        Hello constant = new Hello();
        String name = null;
        Class contract = null;

        cut.replace(constant, name, contract);

        Greeting result = context.getBean("Hello", Hello.class);
        assertThat(result).isSameAs(constant);
    }

    @Test
    public void givenConstantWithNameReplaceShouldReplaceAllInstances() {
        Hello constant = new Hello();
        String name = "hello";
        Class contract = null;

        cut.replace(constant, name, contract);

        Greeting result = context.getBean(name, Hello.class);
        assertThat(result).isSameAs(constant);
    }

    @Test
    public void givenConstantWithContractReplaceShouldReplaceAllInstances() {
        Hello constant = new Hello();
        String name = null;
        Class contract = Hello.class;

        cut.replace(constant, name, contract);

        Greeting result = context.getBean("Hello", Hello.class);
        assertThat(result).isSameAs(constant);
    }

    @Test
    public void givenConstantWithNameAndContractReplaceShouldReplaceAllInstances() {
        Hello constant = new Hello();
        String name = "hello";
        Class contract = Greeting.class;

        cut.replace(constant, name, contract);

        Greeting result = context.getBean(name, Hello.class);
        assertThat(result).isSameAs(constant);
    }

    @Test
    public void givenModuleAddModuleShouldAddModule() {
        DefaultModule module = new DefaultModule(TestModule.class);
        cut.addModules(module);

        WiredContract contract = context.getBean(WiredContract.class);
        WiredService service = context.getBean(WiredService.class);

        assertThat(contract).isNotNull();
        assertThat(service).isNotNull();
    }

    @Test
    public void givenPackageAddResourceShouldAddServices() {
        context = new AnnotationConfigApplicationContext();
        cut = new SpringServiceInstance(context);

        cut.addScans(new DefaultScan("org.testifyproject.di.fixture.module"));
        context.refresh();

        WiredContract greeting = cut.getService(WiredContract.class);
        assertThat(greeting).isNotNull();
    }

    @Test
    public void givenProviderGetServiceShouldReturnProvider() {
        TypeToken<Provider<Hello>> type = new TypeToken<Provider<Hello>>() {
        };
        Provider<Hello> result = cut.getService(type.getType());

        assertThat(result).isNotNull();
        assertThat(result.get()).isNotNull();
    }

    @Test
    public void givenOptionalGetServiceShouldReturnOptional() {
        TypeToken<Optional<Hello>> type = new TypeToken<Optional<Hello>>() {
        };
        Optional<Hello> result = cut.getService(type.getType());

        assertThat(result).isNotEmpty();
    }

    @Test
    public void givenListGetServiceShouldReturnList() {
        TypeToken<List<Greeting>> type = new TypeToken<List<Greeting>>() {
        };
        List<Greeting> result = cut.getService(type.getType());

        assertThat(result).isNotEmpty();
    }

    @Test
    public void givenSetGetServiceShouldReturnSet() {
        TypeToken<Set<Greeting>> type = new TypeToken<Set<Greeting>>() {
        };
        Set<Greeting> result = cut.getService(type.getType());

        assertThat(result).isNotEmpty();
    }

    @Test
    public void givenMapGetServiceShouldReturnMap() {
        TypeToken<Map<String, Greeting>> type = new TypeToken<Map<String, Greeting>>() {
        };
        Map<String, Greeting> result = cut.getService(type.getType());
        assertThat(result).isNotEmpty();
    }

    @Test
    public void callToGetNameQualifersShouldReturnAnnotaitons() {
        assertThat(cut.getNameQualifers()).hasSize(2);
    }

    @Test
    public void callToGetCustomQualifiersShouldReturnAnnotaitons() {
        assertThat(cut.getCustomQualifiers()).hasSize(2);
    }

}
