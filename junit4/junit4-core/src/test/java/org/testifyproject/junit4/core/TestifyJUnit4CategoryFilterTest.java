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
import org.testifyproject.junit4.fixture.filter.LocalResourceTestClass;
import org.testifyproject.junit4.fixture.filter.VirtualResourceTestClass;

/**
 *
 * @author saden
 */
public class TestifyJUnit4CategoryFilterTest {

    TestifyJUnit4CategoryFilter sut;

    @Before
    public void init() {
        sut = TestifyJUnit4CategoryFilter.of(TestCategory.Level.INTEGRATION, null);
    }

    @Test(expected = NullPointerException.class)
    public void givenNullDescriptionShouldRunShouldThrowException() {
        sut = TestifyJUnit4CategoryFilter.of(TestCategory.Level.INTEGRATION, null);

        Description description = null;

        sut.shouldRun(description);
    }

    @Test
    public void givenNoSystemPropertyShouldRunShouldReturnTrue() {
        sut = TestifyJUnit4CategoryFilter.of(TestCategory.Level.INTEGRATION, null);

        Description description = mock(Description.class);
        Class testClass = TestClass.class;

        given(description.getTestClass()).willReturn(testClass);

        boolean result = sut.shouldRun(description);

        assertThat(result).isTrue();
    }

    @Test
    public void givenUnsupportedTestShouldRunShouldReturnFalse() {
        String[] categories = new String[]{"unit"};

        sut = TestifyJUnit4CategoryFilter.of(TestCategory.Level.INTEGRATION, categories);

        Description description = mock(Description.class);
        Class testClass = TestClass.class;

        given(description.getTestClass()).willReturn(testClass);

        boolean result = sut.shouldRun(description);

        assertThat(result).isFalse();
    }

    @Test
    public void givenTestClassWithLocalResourceShouldRunShouldReturnTrue() {
        String[] categories = new String[]{"local"};

        sut = TestifyJUnit4CategoryFilter.of(TestCategory.Level.INTEGRATION, categories);

        Description description = mock(Description.class);
        Class testClass = LocalResourceTestClass.class;

        given(description.getTestClass()).willReturn(testClass);
        System.setProperty("testify.categories", "local");

        boolean result = sut.shouldRun(description);

        assertThat(result).isTrue();
    }

    @Test
    public void givenTestClassWithVirtualResourceShouldRunShouldReturnTrue() {
        String[] categories = new String[]{"integration"};
        sut = TestifyJUnit4CategoryFilter.of(TestCategory.Level.INTEGRATION, categories);

        Description description = mock(Description.class);
        Class testClass = VirtualResourceTestClass.class;

        given(description.getTestClass()).willReturn(testClass);
        System.setProperty("testify.categories", "integration");

        boolean result = sut.shouldRun(description);

        assertThat(result).isTrue();
    }

    @Test
    public void callToDescribeShouldReturnFilterDescription() {
        String result = sut.describe();

        assertThat(result).isEqualTo(FILTER_DESCRIPTION);
    }
}
