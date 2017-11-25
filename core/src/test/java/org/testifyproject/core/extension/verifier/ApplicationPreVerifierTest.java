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
package org.testifyproject.core.extension.verifier;

import static java.util.Optional.empty;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestifyException;
import org.testifyproject.annotation.Application;
import org.testifyproject.fixture.common.InvalidTestClass;
import org.testifyproject.fixture.verifier.InvalidGenericApplication;
import org.testifyproject.fixture.verifier.ValidGenericApplication;

/**
 *
 * @author saden
 */
public class ApplicationPreVerifierTest {

    ApplicationPreVerifier sut;

    @Before
    public void init() {
        sut = new ApplicationPreVerifier();
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
        Optional<Application> foundApplication = empty();

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getTestClassName()).willReturn(testClassName);
        given(testDescriptor.getApplication()).willReturn(foundApplication);

        try {
            sut.verify(testContext);
        } catch (Exception e) {
            verify(testContext).getTestDescriptor();
            verify(testDescriptor).getTestClassName();
            verify(testDescriptor).getApplication();
            throw e;
        }

    }

    @Test(expected = TestifyException.class)
    public void givenInvalidApplicationVerifyShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        String testClassName = InvalidTestClass.class.getSimpleName();
        Application application = mock(Application.class);
        Optional<Application> foundApplication = Optional.of(application);
        Class value = InvalidGenericApplication.class;
        String start = "start";
        String stop = "stop";

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getTestClassName()).willReturn(testClassName);
        given(testDescriptor.getApplication()).willReturn(foundApplication);
        given(application.value()).willReturn(value);
        given(application.start()).willReturn(start);
        given(application.stop()).willReturn(stop);
        try {
            sut.verify(testContext);
        } catch (Exception e) {
            verify(testContext).getTestDescriptor();
            verify(testDescriptor).getTestClassName();
            verify(testDescriptor).getApplication();
            throw e;
        }
    }

    @Test
    public void givenValidApplicationVerifyShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        String testClassName = InvalidTestClass.class.getSimpleName();
        Application application = mock(Application.class);
        Optional<Application> foundApplication = Optional.of(application);
        Class value = ValidGenericApplication.class;
        String start = "start";
        String stop = "stop";

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getTestClassName()).willReturn(testClassName);
        given(testDescriptor.getApplication()).willReturn(foundApplication);
        given(application.value()).willReturn(value);
        given(application.start()).willReturn(start);
        given(application.stop()).willReturn(stop);

        sut.verify(testContext);

        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getTestClassName();
        verify(testDescriptor).getApplication();
    }

}
