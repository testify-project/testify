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

import java.util.HashMap;
import java.util.Map;
import org.testifyproject.StartStrategy;
import static org.testifyproject.StartStrategy.UNDEFINED;
import org.testifyproject.TestRunner;
import org.testifyproject.core.TestCategory;
import org.testifyproject.guava.common.collect.ImmutableMap;

/**
 * A builder class for {@link TestSettings}.
 *
 * @author saden
 */
public class TestSettingsBuilder {

    private Class<? extends TestRunner> testRunnerClass;
    private StartStrategy startStrategy = UNDEFINED;
    private final ImmutableMap.Builder<String, String> dependencies = ImmutableMap.builder();
    private TestCategory.Level level;
    private String[] categories;
    private Map<String, Object> properties = new HashMap<>();

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

    public TestSettingsBuilder resourceStartStrategy(StartStrategy startStrategy) {
        this.startStrategy = startStrategy;
        return this;
    }

    public TestSettingsBuilder dependency(String className, String displayName) {
        this.dependencies.put(className, displayName);
        return this;
    }

    public TestSettingsBuilder dependencies(Map<String, String> dependencies) {
        this.dependencies.putAll(dependencies);
        return this;
    }

    public TestSettingsBuilder level(TestCategory.Level level) {
        this.level = level;
        return this;
    }

    public TestSettings build() {
        TestSettings testSettings = new TestSettings();

        testSettings.setDependencies(dependencies.build());
        testSettings.setLevel(level);
        testSettings.setResourceStartStrategy(startStrategy);
        testSettings.setTestRunnerClass(testRunnerClass);

        return testSettings;
    }

}
