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
package org.testifyproject.core.verifier;

import java.util.Optional;
import static java.util.Optional.empty;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.testifyproject.SutDescriptor;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestifyException;
import org.testifyproject.fixture.common.InvalidTestClass;

/**
 *
 * @author saden
 */
public class SutConfigurationVerifierTest {

    SutConfigurationVerifier sut;

    @Before
    public void init() {
        sut = new SutConfigurationVerifier();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullTestContextReifyShouldThrowException() {
        sut.verify(null);
    }

    @Test(expected = TestifyException.class)
    public void givenInvalidTestContextVerifyShouldThrowException() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        String testClassName = InvalidTestClass.class.getSimpleName();
        Optional<SutDescriptor> foundSutDescriptor = empty();

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getTestClassName()).willReturn(testClassName);
        given(testContext.getSutDescriptor()).willReturn(foundSutDescriptor);

        try {
            sut.verify(testContext);
        } catch (Exception e) {
            verify(testContext).getTestDescriptor();
            verify(testDescriptor).getTestClassName();
            verify(testContext).getSutDescriptor();
            throw e;
        }

    }

    @Test
    public void givenSutFieldTestContextVerifyShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        String testClassName = InvalidTestClass.class.getSimpleName();
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        Optional<SutDescriptor> foundSutDescriptor = Optional.of(sutDescriptor);

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getTestClassName()).willReturn(testClassName);
        given(testContext.getSutDescriptor()).willReturn(foundSutDescriptor);

        sut.verify(testContext);

        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getTestClassName();
        verify(testContext).getSutDescriptor();
    }
}
