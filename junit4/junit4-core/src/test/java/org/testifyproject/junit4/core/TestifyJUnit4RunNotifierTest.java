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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.MultipleFailureException;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 *
 * @author saden
 */
public class TestifyJUnit4RunNotifierTest {

    TestifyJUnit4RunNotifier sut;
    RunNotifier runNotifier;
    Description testDescription;

    @Before
    public void init() {
        runNotifier = mock(RunNotifier.class);
        testDescription = mock(Description.class);

        sut = TestifyJUnit4RunNotifier.of(runNotifier, testDescription);
    }

    @Test(expected = NullPointerException.class)
    public void givenNullFailureFireTestAssumptionsFailedShouldThrowException() {
        sut.fireTestAssumptionFailed(null);
    }

    @Test
    @SuppressWarnings("ThrowableResultIgnored")
    public void givenFailureWithMethodNameFireTestAssumptionsFailedShouldFireAssumptionFailed() {
        Failure failure = mock(Failure.class);
        Description failureDescription = mock(Description.class);
        String methodName = "methodName";
        Throwable exception = mock(Throwable.class);

        given(failure.getDescription()).willReturn(failureDescription);
        given(failureDescription.getMethodName()).willReturn(methodName);
        given(failure.getException()).willReturn(exception);

        sut.fireTestAssumptionFailed(failure);

        verify(failure).getDescription();
        verify(failureDescription).getMethodName();
        verify(failure).getException();
        verify(runNotifier).fireTestAssumptionFailed(failure);
    }

    @Test
    @SuppressWarnings("ThrowableResultIgnored")
    public void givenFailureWithoutMethodNameFireTestAssumptionsFailedShouldFireAssumptionFailed() {
        Failure failure = mock(Failure.class);
        Description failureDescription = mock(Description.class);
        Throwable exception = mock(Throwable.class);

        given(failure.getDescription()).willReturn(failureDescription);
        given(failure.getException()).willReturn(exception);

        sut.fireTestAssumptionFailed(failure);

        verify(failure).getDescription();
        verify(failureDescription).getMethodName();
        verify(failure).getException();
        verify(runNotifier).fireTestAssumptionFailed(failure);
    }

    @Test
    @SuppressWarnings("ThrowableResultIgnored")
    public void givenFailureWithMethodNameFireTestFailureShouldFireAssumptionFailed() {
        Failure failure = mock(Failure.class);
        Description failureDescription = mock(Description.class);
        String methodName = "methodName";
        Throwable exception = mock(Throwable.class);

        given(failure.getDescription()).willReturn(failureDescription);
        given(failureDescription.getMethodName()).willReturn(methodName);
        given(failure.getException()).willReturn(exception);

        sut.fireTestFailure(failure);

        verify(failure).getDescription();
        verify(failureDescription).getMethodName();
        verify(failure).getException();
        verify(runNotifier).fireTestFailure(failure);
    }

    @Test
    @SuppressWarnings("ThrowableResultIgnored")
    public void givenFailureWithoutMethodNameFireTestFailureShouldFireAssumptionFailed() {
        Failure failure = mock(Failure.class);
        Description failureDescription = mock(Description.class);
        Throwable exception = mock(Throwable.class);

        given(failure.getDescription()).willReturn(failureDescription);
        given(failure.getException()).willReturn(exception);

        sut.fireTestFailure(failure);

        verify(failure).getDescription();
        verify(failureDescription).getMethodName();
        verify(failure).getException();
        verify(runNotifier).fireTestFailure(failure);
    }

    @Test
    public void givenDescriptionFireTestIgnoredShouldFireTestIgnored() {
        Description description = mock(Description.class);

        sut.fireTestIgnored(description);

        verify(runNotifier).fireTestIgnored(description);
    }

    @Test
    public void givenDescriptionFireTestStartedShouldFireTestStarted() {
        Description description = mock(Description.class);

        sut.fireTestStarted(description);

        verify(runNotifier).fireTestStarted(description);
    }

    @Test
    public void givenDescriptionFireTestFinishedShouldFireTestFinished() {
        Description description = mock(Description.class);

        sut.fireTestFinished(description);

        verify(runNotifier).fireTestFinished(description);
    }

    @Test
    public void givenAssumptionViolatedExceptionAddFailedAssumptionShouldFireTestAssumptionFailed() {
        AssumptionViolatedException exception = mock(AssumptionViolatedException.class);

        sut.addFailedAssumption(exception);

        verify(runNotifier).fireTestAssumptionFailed(any(Failure.class));
    }

    @Test
    public void givenMultipleFailureExceptionAddFailureShouldAddFailure() {
        MultipleFailureException exception = mock(MultipleFailureException.class);
        Exception failure = mock(Exception.class);
        List<Throwable> failures = ImmutableList.of(failure);

        given(exception.getFailures()).willReturn(failures);
        sut.addFailure(exception);

        verify(runNotifier).fireTestFailure(any(Failure.class));
    }

}
