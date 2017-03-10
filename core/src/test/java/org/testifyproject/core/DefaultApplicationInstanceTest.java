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
package org.testifyproject.core;

import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import org.testifyproject.ApplicationInstance;
import org.testifyproject.TestContext;
import org.testifyproject.annotation.Application;

/**
 *
 * @author saden
 */
public class DefaultApplicationInstanceTest {

    ApplicationInstance<Object> cut;

    TestContext testContext;
    Application application;
    Object initializer;
    Set<Class<?>> handlers;

    public DefaultApplicationInstanceTest() {
        testContext = mock(TestContext.class);
        application = mock(Application.class);
        initializer = mock(Object.class);

        handlers = mock(Set.class);

        cut = DefaultApplicationInstance.of(testContext, application, initializer, handlers);
    }

    @Test
    public void validateCutInstance() {
        assertThat(cut).isNotNull();
        assertThat(cut.getTestContext()).isEqualTo(testContext);
        assertThat(cut.getApplication()).isEqualTo(application);
        assertThat(cut.getInitializer()).isEqualTo(initializer);
        assertThat(cut.getHandlers()).isEqualTo(handlers);
    }

    @Test
    public void givenNullInstancesShouldNotBeEqual() {
        assertThat(cut).isNotEqualTo(null);
    }

    @Test
    public void givenDifferentTypeInstancesShouldNotBeEqual() {
        String differentType = "instance";

        assertThat(cut).isNotEqualTo(differentType);
        assertThat(cut.hashCode()).isNotEqualTo(differentType.hashCode());
    }

    @Test
    public void givenUnequalInstancesShouldNotBeEqual() {
        ApplicationInstance<Object> unequal
                = DefaultApplicationInstance.of(null, null, null, null);

        assertThat(cut).isNotEqualTo(unequal);
        assertThat(cut.hashCode()).isNotEqualTo(unequal.hashCode());
    }

    @Test
    public void givenSameInstancesShouldBeEqual() {
        assertThat(cut).isEqualTo(cut);
    }

    @Test
    public void givenEqualInstancesShouldBeEqual() {
        ApplicationInstance<Object> equal
                = DefaultApplicationInstance.of(testContext, application, initializer, handlers);

        assertThat(cut).isEqualTo(equal);
        assertThat(cut.hashCode()).isEqualTo(equal.hashCode());
    }

    @Test
    public void callToToStringShouldReturnHumanReadableString() {
        String result = cut.toString();

        assertThat(result).contains(
                "DefaultApplicationInstance",
                "testContext",
                "application",
                "initializer",
                "handlers");
    }

}
