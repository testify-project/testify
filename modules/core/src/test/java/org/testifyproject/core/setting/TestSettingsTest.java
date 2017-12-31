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

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.TestRunner;
import org.testifyproject.core.TestCategory;
import org.testifyproject.extension.annotation.UnitCategory;

/**
 *
 * @author saden
 */
public class TestSettingsTest {

    TestSettings sut;
    Map<String, Object> properties;

    @Before
    public void init() {
        properties = new HashMap<>();
        sut = new TestSettings(properties);
    }

    @Test
    public void callToGetTestRunnerClassShouldReturnClass() {
        Class<? extends TestRunner> testRunner = TestRunner.class;
        properties.put(TestSettingsProperties.TEST_RUNNER_CLASS, testRunner);

        Class<? extends TestRunner> result = sut.getTestRunnerClass();
        assertThat(result).isEqualTo(testRunner);
    }

    @Test
    public void callToGetTestCategoryShouldReturnTestCategory() {
        Class<UnitCategory> category = UnitCategory.class;
        properties.put(TestSettingsProperties.TEST_CATEGORY, category);

        Class<? extends Annotation> result = sut.getTestCategory();
        assertThat(result).isEqualTo(category);
    }

    @Test
    public void callToGetTestLevelShouldReturnTestLevel() {
        TestCategory.Level level = TestCategory.Level.UNIT;
        properties.put(TestSettingsProperties.TEST_LEVEL, level);

        TestCategory.Level result = sut.getTestLevel();
        assertThat(result).isEqualTo(level);
    }

}
