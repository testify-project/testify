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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

/**
 *
 * @author saden
 */
public class TestCategoryTest {

    @Test(expected = NullPointerException.class)
    public void givenNullCategoriesLevelFindShouldThrowException() {
        String[] categories = null;
        TestCategory.find(TestCategory.Level.class, categories);
    }

    @Test
    public void givenEmptyCategoriesLevelFindShouldReturnEmptyList() {
        String[] categories = {};
        List<Enum> result = TestCategory.find(TestCategory.Level.class, categories);

        assertThat(result).isEmpty();
    }

    @Test
    public void givenInvalidCategoriesLevelFindShouldLogAWarning() {
        String[] categories = {
            "invalid"
        };

        TestCategory.find(TestCategory.Level.class, categories);
    }

    @Test
    public void givenValidCategoriesLevelFindShouldReturn() {
        String[] categories = {
            "unit",
            "integration",
            "system"
        };

        List<Enum> result = TestCategory.find(TestCategory.Level.class, categories);

        assertThat(result).contains(TestCategory.Level.UNIT,
                TestCategory.Level.INTEGRATION,
                TestCategory.Level.SYSTEM
        );
    }

    @Test(expected = NullPointerException.class)
    public void givenNullCategoriesDynamicFindShouldThrowException() {
        String[] categories = null;
        TestCategory.find(TestCategory.Dynamic.class, categories);
    }

    @Test
    public void givenEmptyCategoriesDynamicFindShouldReturnEmptyList() {
        String[] categories = {};
        List<Enum> result = TestCategory.find(TestCategory.Dynamic.class, categories);

        assertThat(result).isEmpty();
    }

    @Test
    public void givenInvalidCategoriesDynamicFindShouldThrowException() {
        String[] categories = {
            "local",
            "virtual",
            "remote"
        };

        List<Enum> result = TestCategory.find(TestCategory.Dynamic.class, categories);

        assertThat(result).contains(
                TestCategory.Dynamic.LOCAL,
                TestCategory.Dynamic.VIRTUAL,
                TestCategory.Dynamic.REMOTE
        );
    }

    @Test
    public void verifyTestCategoryLevelEnums() {
        assertThat(TestCategory.Level.values()).containsExactly(
                TestCategory.Level.UNIT,
                TestCategory.Level.INTEGRATION,
                TestCategory.Level.SYSTEM
        );
    }

    @Test
    public void verifyTestCategoryDynamicEnums() {
        assertThat(TestCategory.Dynamic.values()).containsExactly(
                TestCategory.Dynamic.LOCAL,
                TestCategory.Dynamic.VIRTUAL,
                TestCategory.Dynamic.REMOTE
        );
    }

}
