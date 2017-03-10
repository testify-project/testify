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
package org.testifyproject;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

/**
 *
 * @author saden
 */
public class TestCategoryTest {

    @Test(expected = NullPointerException.class)
    public void givenNullCategoriesLevelFindShouldThrowException() {
        String[] categories = null;
        TestCategory.Level.find(categories);
    }

    @Test
    public void givenEmptyCategoriesLevelFindShouldReturnEmptyList() {
        String[] categories = {};
        List<TestCategory.Level> result = TestCategory.Level.find(categories);

        assertThat(result).isEmpty();
    }

    @Test
    public void givenInvalidCategoriesLevelFindShouldThrowException() {
        String[] categories = {
            "unit",
            "integration",
            "system"
        };

        List<TestCategory.Level> result = TestCategory.Level.find(categories);

        assertThat(result).contains(
                TestCategory.Level.Unit,
                TestCategory.Level.Integration,
                TestCategory.Level.System
        );
    }

    @Test(expected = NullPointerException.class)
    public void givenNullCategoriesDynamicFindShouldThrowException() {
        String[] categories = null;
        TestCategory.Dynamic.find(categories);
    }

    @Test
    public void givenEmptyCategoriesDynamicFindShouldReturnEmptyList() {
        String[] categories = {};
        List<TestCategory.Dynamic> result = TestCategory.Dynamic.find(categories);

        assertThat(result).isEmpty();
    }

    @Test
    public void givenInvalidCategoriesDynamicFindShouldThrowException() {
        String[] categories = {
            "resource",
            "container"
        };

        List<TestCategory.Dynamic> result = TestCategory.Dynamic.find(categories);

        assertThat(result).contains(
                TestCategory.Dynamic.Resource,
                TestCategory.Dynamic.Container
        );
    }

    @Test
    public void verifyTestCategoryLevelEnums() {
        assertThat(TestCategory.Level.values()).containsExactly(
                TestCategory.Level.Unit,
                TestCategory.Level.Integration,
                TestCategory.Level.System
        );
    }

    @Test
    public void verifyTestCategoryLevelIntegration() {
        TestCategory.Level cut = TestCategory.Level.Integration;

        assertThat(cut.contains("int")).isTrue();
        assertThat(cut.contains("integ")).isTrue();
        assertThat(cut.contains("integration")).isTrue();
    }

    @Test
    public void verifyTestCategoryLevelSystem() {
        TestCategory.Level cut = TestCategory.Level.System;

        assertThat(cut.contains("sys")).isTrue();
        assertThat(cut.contains("system")).isTrue();
    }

    @Test
    public void verifyTestCategoryDynamicEnums() {
        assertThat(TestCategory.Dynamic.values()).containsExactly(
                TestCategory.Dynamic.Resource,
                TestCategory.Dynamic.Container
        );
    }

    @Test
    public void verifyTestCategoryDynamicResource() {
        TestCategory.Dynamic cut = TestCategory.Dynamic.Resource;

        assertThat(cut.contains("res")).isTrue();
        assertThat(cut.contains("resource")).isTrue();
    }

    @Test
    public void verifyTestCategoryDynamicContainer() {
        TestCategory.Dynamic cut = TestCategory.Dynamic.Container;

        assertThat(cut.contains("con")).isTrue();
        assertThat(cut.contains("container")).isTrue();
    }
}
