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
package org.testifyproject.processor;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaFileObjects.forResource;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.junit.Test;

/**
 *
 * @author saden
 */
public class DiscoverableProcessorTest {

    @Test
    public void givenDiscoverableServiceProcessorShouldGenerateService()
            throws MalformedURLException, URISyntaxException {

        assertAbout(javaSource())
                .that(forResource("org/testifyproject/fixture/DiscoverableService.java"))
                .processedWith(new DiscoverableProcessor())
                .compilesWithoutError()
                .and()
                .generatesFiles(
                        forResource("META-INF/services/org.testifyproject.fixture.DiscoverableContract")
                );
    }

    @Test
    public void givenDiscoverableExplicitServiceWithExplictyContractProcessorShouldGenerateService()
            throws MalformedURLException, URISyntaxException {

        assertAbout(javaSource())
                .that(forResource("org/testifyproject/fixture/DiscoverableExplicitService.java"))
                .processedWith(new DiscoverableProcessor())
                .compilesWithoutError()
                .and()
                .generatesFiles(
                        forResource("META-INF/services/org.testifyproject.fixture.DiscoverableExplicitContract")
                );
    }

    @Test
    public void givenDiscoverableMultiContractServiceWithMultipleContractsProcessorShouldGenerateService()
            throws MalformedURLException, URISyntaxException {

        assertAbout(javaSource())
                .that(forResource("org/testifyproject/fixture/DiscoverableMultiContractService.java"))
                .processedWith(new DiscoverableProcessor())
                .compilesWithoutError()
                .and()
                .generatesFiles(
                        forResource("META-INF/services/org.testifyproject.fixture.DiscoverableMultipleContract1"),
                        forResource("META-INF/services/org.testifyproject.fixture.DiscoverableMultipleContract2")
                );
    }
}
