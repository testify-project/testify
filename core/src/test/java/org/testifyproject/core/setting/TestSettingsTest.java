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
package org.testifyproject.core.setting;

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
public class TestSettingsTest {

    TestSettings sut;

    @Before
    public void init() {
        sut = new TestSettings();
    }

    @Test
    public void validateTestRunnerClassProperty() {
        Class<? extends TestRunner> setting = TestRunner.class;
        sut.setTestRunnerClass(setting);

        Class<? extends TestRunner> result = sut.getTestRunnerClass();
        assertThat(result).isEqualTo(setting);
    }

    @Test
    public void validateResourceStartStrategyProperty() {
        StartStrategy setting = StartStrategy.EAGER;
        sut.setResourceStartStrategy(setting);

        StartStrategy result = sut.getResourceStartStrategy();
        assertThat(result).isEqualTo(setting);
    }

    @Test
    public void validateDependenciesProperty() {
        Map<String, String> setting = ImmutableMap.of();
        sut.setDependencies(setting);

        Map<String, String> result = sut.getDependencies();
        assertThat(result).isEqualTo(setting);
    }

    @Test
    public void validateLevelProperty() {
        TestCategory.Level setting = TestCategory.Level.UNIT;
        sut.setLevel(setting);

        TestCategory.Level result = sut.getLevel();
        assertThat(result).isEqualTo(setting);
    }

}
