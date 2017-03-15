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

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import org.testifyproject.core.TestCategory;
import static org.testifyproject.junit4.core.TestifyJUnit4CategoryFilter.FILTER_DESCRIPTION;
import org.testifyproject.junit4.fixture.common.TestClass;
import org.testifyproject.junit4.fixture.filter.RequiresContainerTestClass;
import org.testifyproject.junit4.fixture.filter.RequiresResourceTestClass;

/**
 *
 * @author saden
 */
public class TestifyJUnit4CategoryFilterTest {

    TestifyJUnit4CategoryFilter cut;

    @Before
    public void init() {
        System.clearProperty("testify.categories");
        cut = TestifyJUnit4CategoryFilter.of(TestCategory.Level.Integration);
    }

    @Test(expected = NullPointerException.class)
    public void givenNullDescriptionShouldRunShouldThrowException() {
        Description description = null;

        cut.shouldRun(description);
    }

    @Test
    public void givenNoSystemPropertyShouldRunShouldReturnTrue() {
        Description description = mock(Description.class);
        Class testClass = TestClass.class;

        given(description.getTestClass()).willReturn(testClass);

        boolean result = cut.shouldRun(description);

        assertThat(result).isTrue();
    }

    @Test
    public void givenUnsupportedTestShouldRunShouldReturnFalse() {
        Description description = mock(Description.class);
        Class testClass = TestClass.class;

        given(description.getTestClass()).willReturn(testClass);
        System.setProperty("testify.categories", "unit");

        boolean result = cut.shouldRun(description);

        assertThat(result).isFalse();
    }

    @Test
    public void givenTestClassWithRequiredResourceShouldRunShouldReturnTrue() {
        Description description = mock(Description.class);
        Class testClass = RequiresResourceTestClass.class;

        given(description.getTestClass()).willReturn(testClass);
        System.setProperty("testify.categories", "integ");

        boolean result = cut.shouldRun(description);

        assertThat(result).isTrue();
    }

    @Test
    public void givenTestClassWithRequiredContainerShouldRunShouldReturnTrue() {
        Description description = mock(Description.class);
        Class testClass = RequiresContainerTestClass.class;

        given(description.getTestClass()).willReturn(testClass);
        System.setProperty("testify.categories", "integ");

        boolean result = cut.shouldRun(description);

        assertThat(result).isTrue();
    }

    @Test
    public void callToDescribeShouldReturnFilterDescription() {
        String result = cut.describe();

        assertThat(result).isEqualTo(FILTER_DESCRIPTION);
    }
}