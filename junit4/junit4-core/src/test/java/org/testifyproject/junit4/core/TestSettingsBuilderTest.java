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
package org.testifyproject.junit4.core;

import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.testifyproject.StartStrategy;
import org.testifyproject.TestRunner;
import org.testifyproject.core.TestCategory;
import org.testifyproject.guava.common.collect.ImmutableMap;

/**
 *
 * @author saden
 */
public class TestSettingsBuilderTest {

    TestSettingsBuilder cut;

    @Before
    public void init() {
        cut = TestSettingsBuilder.builder();
    }

    @Test
    public void callToBuildShouldReturnTestSettingsWithCategoriesFromSystemProperty() {
        String key = "testify.categories";
        String value = "unit,integration";

        System.setProperty(key, value);

        TestSettings result = cut.categories(value).build();

        assertThat(result).isNotNull();
        assertThat(result.getCategories()).isEqualTo(value);
        System.clearProperty(key);
    }

    @Test
    public void givenTestRunnerClassBuildShouldReturnTestSettings() {
        Class<TestRunner> setting = TestRunner.class;

        TestSettings result = cut.testRunnerClass(setting).build();

        assertThat(result).isNotNull();
        assertThat(result.getTestRunnerClass()).isEqualTo(setting);
    }

    @Test
    public void givenResourceStartStrategyBuildShouldReturnTestSettings() {
        StartStrategy setting = StartStrategy.EAGER;

        TestSettings result = cut.resourceStartStrategy(setting).build();

        assertThat(result).isNotNull();
        assertThat(result.getResourceStartStrategy()).isEqualTo(setting);
    }

    @Test
    public void givenDependencyBuildShouldReturnTestSettings() {
        String className = "className";
        String displaceName = "Display Name";

        TestSettings result = cut.dependency(className, displaceName).build();

        assertThat(result).isNotNull();
        assertThat(result.getDependencies()).containsEntry(className, displaceName);
    }

    @Test
    public void givenDependenciesBuildShouldReturnTestSettings() {
        String className = "className";
        String displaceName = "Display Name";
        Map<String, String> setting = ImmutableMap.of(className, displaceName);

        TestSettings result = cut.dependencies(setting).build();

        assertThat(result).isNotNull();
        assertThat(result.getDependencies()).containsEntry(className, displaceName);
    }

    @Test
    public void givenLevelBuildShouldReturnTestSettings() {
        TestCategory.Level setting = TestCategory.Level.INTEGRATION;

        TestSettings result = cut.level(setting).build();

        assertThat(result).isNotNull();
        assertThat(result.getLevel()).isEqualTo(setting);
    }

    @Test
    public void givenCategoriesBuildShouldReturnTestSettings() {
        String setting = "unit,integration";

        TestSettings result = cut.categories(setting).build();

        assertThat(result).isNotNull();
        assertThat(result.getCategories()).isEqualTo(setting);
    }

}
