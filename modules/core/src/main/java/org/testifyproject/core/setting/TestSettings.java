/*
 * Copyright 2016-2018 Testify Project.
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
import org.testifyproject.trait.PropertiesReader;
import org.testifyproject.trait.PropertiesWriter;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A class that encapsulates test settings.
 *
 * @author saden
 */
@ToString
@EqualsAndHashCode
public class TestSettings implements PropertiesReader, PropertiesWriter {

    private final Map<String, Object> properties;

    public TestSettings(Map<String, Object> properties) {
        this.properties = properties;
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

    public Class<? extends TestRunner> getTestRunnerClass() {
        return getProperty(TestSettingsProperties.TEST_RUNNER_CLASS);
    }

    public Class<? extends Annotation> getTestCategory() {
        return getProperty(TestSettingsProperties.TEST_CATEGORY);
    }

    public TestCategory.Level getTestLevel() {
        return getProperty(TestSettingsProperties.TEST_LEVEL);
    }

}
