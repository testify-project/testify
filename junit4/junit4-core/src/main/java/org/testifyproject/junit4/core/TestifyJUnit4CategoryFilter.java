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

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.testifyproject.TestDescriptor;
import org.testifyproject.core.TestCategory;
import org.testifyproject.core.util.AnalyzerUtil;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 * JUnit 4 test category filter implementation.
 *
 * @author saden
 */
public class TestifyJUnit4CategoryFilter extends Filter {

    /**
     * Filter description.
     */
    public static final String FILTER_DESCRIPTION = "test categories filter";

    private final TestCategory.Level level;
    private final String testifyCategories;

    TestifyJUnit4CategoryFilter(TestCategory.Level level, String testifyCategories) {
        this.level = level;
        this.testifyCategories = testifyCategories;
    }

    /**
     * Create a test category filter instance.
     *
     * @param level test category level associated with the filter
     * @param testifyCategories testify categories associated with the filter
     * @return a filter instance.
     */
    public static final TestifyJUnit4CategoryFilter of(TestCategory.Level level, String testifyCategories) {
        return new TestifyJUnit4CategoryFilter(level, testifyCategories);
    }

    @Override
    public boolean shouldRun(Description description) {
        Class<?> testClass = description.getTestClass();
        TestDescriptor testDescriptor = AnalyzerUtil.INSTANCE.analyzeTestClass(testClass);
        Boolean shouldRun = true;

        if (testifyCategories != null) {
            String[] categories = testifyCategories.split(",");

            ImmutableList<Enum> desiredCategories = ImmutableList.<Enum>builder()
                    .addAll(TestCategory.find(TestCategory.Level.class, categories))
                    .addAll(TestCategory.find(TestCategory.Dynamic.class, categories))
                    .build();

            ImmutableList.Builder<Enum> applicableCategoriesBuilder = ImmutableList.<Enum>builder()
                    .add(level);

            if (!testDescriptor.getLocalResources().isEmpty()) {
                applicableCategoriesBuilder.add(TestCategory.Dynamic.LOCAL);
            }

            if (!testDescriptor.getVirtualResources().isEmpty()) {
                applicableCategoriesBuilder.add(TestCategory.Dynamic.VIRTUAL);
            }

            ImmutableList<Enum> applicableCategories = applicableCategoriesBuilder.build();

            if (!applicableCategories.containsAll(desiredCategories)) {
                shouldRun = false;
            }
        }

        return shouldRun;
    }

    @Override
    public String describe() {
        return FILTER_DESCRIPTION;
    }

}
