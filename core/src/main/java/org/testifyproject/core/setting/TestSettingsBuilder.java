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

import java.lang.annotation.Annotation;

import org.testifyproject.TestRunner;
import org.testifyproject.core.TestCategory;

/**
 * A builder class for {@link TestSettings}.
 *
 * @author saden
 */
public class TestSettingsBuilder {

    private Class<? extends TestRunner> testRunnerClass;
    private Class<? extends Annotation> testCategory;
    private TestCategory.Level level;

    /**
     * Create a new test settings builder instance.
     *
     * @return a TestSettingsBuilder instance.
     */
    public static TestSettingsBuilder builder() {
        return new TestSettingsBuilder();
    }

    public TestSettingsBuilder testRunnerClass(Class<? extends TestRunner> testRunnerClass) {
        this.testRunnerClass = testRunnerClass;
        return this;
    }

    public TestSettingsBuilder testCategory(Class<? extends Annotation> testCategory) {
        this.testCategory = testCategory;
        return this;
    }

    public TestSettingsBuilder level(TestCategory.Level level) {
        this.level = level;
        this.testCategory = TestCategory.find(level);
        return this;
    }

    public TestSettings build() {
        TestSettings testSettings = new TestSettings();

        testSettings.setLevel(level);
        testSettings.setTestCategory(testCategory);
        testSettings.setTestRunnerClass(testRunnerClass);

        return testSettings;
    }

}
