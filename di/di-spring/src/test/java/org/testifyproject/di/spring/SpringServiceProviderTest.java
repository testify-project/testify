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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;

/**
 *
 * @author saden
 */
@Ignore
public class SpringServiceProviderTest {

    SpringServiceProvider sut;

    @Before
    public void init() {
        sut = new SpringServiceProvider();
    }

    @Test
    public void callToCreateWithTestContextShouldCreateSpringApplicationContext() {
        TestContext testContext = mock(TestContext.class);
        TestConfigurer testConfigurer = mock(TestConfigurer.class);
        AnnotationConfigApplicationContext context =
                mock(AnnotationConfigApplicationContext.class);
        String testName = "testName";

        given(testContext.getName()).willReturn(testName);
        given(testContext.getTestConfigurer()).willReturn(testConfigurer);
        given(testConfigurer.configure(eq(testContext), any(
                AnnotationConfigApplicationContext.class)))
                .willAnswer((InvocationOnMock invocation) -> {
                    return invocation.getArguments()[1];
                });

        ConfigurableApplicationContext result = sut.create(testContext);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testName);
        assertThat(result.getDisplayName()).isEqualTo(testName);
    }

}
