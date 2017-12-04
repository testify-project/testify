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
import java.util.Map;

import org.testifyproject.TestRunner;
import org.testifyproject.core.TestCategory;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A class that encapsulates test settings.
 *
 * @author saden
 */
@ToString
@EqualsAndHashCode
public class TestSettings {

    private Class<? extends TestRunner> testRunnerClass;
    private Class<? extends Annotation> testCategory;
    private TestCategory.Level level;
    private String[] categories;
    private Map<String, Object> properties;

    TestSettings() {
    }

    public Class<? extends TestRunner> getTestRunnerClass() {
        return testRunnerClass;
    }

    void setTestRunnerClass(Class<? extends TestRunner> testRunnerClass) {
        this.testRunnerClass = testRunnerClass;
    }

    public Class<? extends Annotation> getTestCategory() {
        return testCategory;
    }

    void setTestCategory(Class<? extends Annotation> testCategory) {
        this.testCategory = testCategory;
    }

    public TestCategory.Level getLevel() {
        return level;
    }

    void setLevel(TestCategory.Level level) {
        this.level = level;
    }

}
