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

import java.lang.reflect.Method;

import org.junit.After;
import org.junit.Test;
import org.testifyproject.TestContext;
import org.testifyproject.core.TestContextHolder;
import org.testifyproject.core.util.AnalyzerUtil;
import org.testifyproject.di.fixture.common.Greeting;
import org.testifyproject.di.fixture.instrument.ModuleTester;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 *
 * @author saden
 */
public class GuiceInterceptorTest {

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

        Injector injector = Guice.createInjector();

        Greeting greeting = injector.getInstance(Greeting.class);

        assertThat(greeting).isNotNull();
    }

}
