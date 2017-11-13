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

import java.lang.reflect.Method;

import org.junit.After;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testifyproject.TestContext;
import org.testifyproject.core.TestContextHolder;
import org.testifyproject.core.util.AnalyzerUtil;
import org.testifyproject.di.fixture.autowired.Greeter;
import org.testifyproject.di.fixture.common.WiredContract;
import org.testifyproject.di.fixture.instrument.ModuleTester;
import org.testifyproject.di.fixture.instrument.ScanTester;

/**
 *
 * @author saden
 */
public class ApplicationContextInterceptorTest {

    @After
    public void destroy() {
        TestContextHolder.INSTANCE.remove();
    }

    @Test
    public void verifyModuleLoading() throws Exception {
        Class<ModuleTester> testClass = ModuleTester.class;
        Method testMethod = testClass.getDeclaredMethod("verifyInjection");
        TestContext testContext = AnalyzerUtil.INSTANCE.analyzeAndCreate(testClass, testMethod);
        TestContextHolder.INSTANCE.set(testContext);

        AnnotationConfigApplicationContext applicationContext =
                new AnnotationConfigApplicationContext();

        applicationContext.refresh();

        WiredContract wiredContract = applicationContext.getBean(WiredContract.class);

        assertThat(wiredContract).isNotNull();
    }

    @Test
    public void verifyScanLoading() throws Exception {
        Class<ScanTester> testClass = ScanTester.class;
        Method testMethod = testClass.getDeclaredMethod("verifyInjection");
        TestContext testContext = AnalyzerUtil.INSTANCE.analyzeAndCreate(testClass, testMethod);
        TestContextHolder.INSTANCE.set(testContext);

        AnnotationConfigApplicationContext applicationContext =
                new AnnotationConfigApplicationContext();

        applicationContext.refresh();

        Greeter greeter = applicationContext.getBean(Greeter.class);

        assertThat(greeter).isNotNull();
    }
}
