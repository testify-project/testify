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

import java.util.Collection;
import java.util.Optional;
import static java.util.Optional.empty;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.testifyproject.SutDescriptor;
import org.testifyproject.FieldDescriptor;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestifyException;
import org.testifyproject.annotation.Real;
import org.testifyproject.fixture.common.InvalidTestClass;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 *
 * @author saden
 */
public class RealConfigurationVerifierTest {

    RealConfigurationVerifier sut;

    @Before
    public void init() {
        sut = new RealConfigurationVerifier();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullTestContextVerifyShouldThrowException() {
        sut.verify(null);
    }

    @Test(expected = TestifyException.class)
    public void givenInvalidTestContextVerifyShouldThrowException() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        String testClassName = InvalidTestClass.class.getSimpleName();
        Optional<SutDescriptor> foundSutDescriptor = empty();
        Collection<FieldDescriptor> fieldDescriptors = ImmutableList.of();

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getTestClassName()).willReturn(testClassName);
        given(testContext.getSutDescriptor()).willReturn(foundSutDescriptor);
        given(testDescriptor.getFieldDescriptors()).willReturn(fieldDescriptors);

        try {
            sut.verify(testContext);
        } catch (Exception e) {
            verify(testContext).getTestDescriptor();
            verify(testDescriptor).getTestClassName();
            verify(testContext).getSutDescriptor();
            verify(testDescriptor).getFieldDescriptors();
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
        Collection<FieldDescriptor> fieldDescriptors = ImmutableList.of();

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getTestClassName()).willReturn(testClassName);
        given(testContext.getSutDescriptor()).willReturn(foundSutDescriptor);
        given(testDescriptor.getFieldDescriptors()).willReturn(fieldDescriptors);

        sut.verify(testContext);

        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getTestClassName();
        verify(testContext).getSutDescriptor();
        verify(testDescriptor).getFieldDescriptors();
    }

    @Test
    public void givenRealFieldTestContextVerifyShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        String testClassName = InvalidTestClass.class.getSimpleName();
        Optional<SutDescriptor> foundSutDescriptor = Optional.empty();
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        Collection<FieldDescriptor> fieldDescriptors = ImmutableList.of(fieldDescriptor);
        Real real = mock(Real.class);
        Optional<Real> foundReal = Optional.of(real);

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getTestClassName()).willReturn(testClassName);
        given(testContext.getSutDescriptor()).willReturn(foundSutDescriptor);
        given(testDescriptor.getFieldDescriptors()).willReturn(fieldDescriptors);
        given(fieldDescriptor.getReal()).willReturn(foundReal);

        sut.verify(testContext);

        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getTestClassName();
        verify(testContext).getSutDescriptor();
        verify(testDescriptor).getFieldDescriptors();
        verify(fieldDescriptor).getReal();
    }
}
