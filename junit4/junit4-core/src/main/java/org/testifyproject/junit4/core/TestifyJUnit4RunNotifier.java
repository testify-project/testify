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

import org.junit.internal.AssumptionViolatedException;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import org.junit.runners.model.MultipleFailureException;
import org.testifyproject.trait.LoggingTrait;

/**
 * A JUnit test run notifier to handle logging and notification test lifecycle
 * events.
 *
 * @author saden
 */
public class TestifyJUnit4RunNotifier extends RunNotifier implements LoggingTrait {

    private final RunNotifier runNotifier;
    private final Description testDescription;

    /**
     * Create new instance of TestifyJUnit4RunNotifier.
     *
     * @param runNotifier the underlying notifier
     * @param testDescription the test description
     * @return a test notifier instance
     */
    public static TestifyJUnit4RunNotifier of(RunNotifier runNotifier, Description testDescription) {
        return new TestifyJUnit4RunNotifier(runNotifier, testDescription);
    }

    TestifyJUnit4RunNotifier(RunNotifier runNotifier, Description testDescription) {
        this.runNotifier = runNotifier;
        this.testDescription = testDescription;
    }

    @Override
    public void fireTestAssumptionFailed(Failure failure) {
        Description description = failure.getDescription();
        String methodName = description.getMethodName();
        Throwable throwable = failure.getException();

        if (methodName == null) {
            error("Test class assumption failed", throwable);
        } else {
            error("Test method assumption failed", throwable);
        }

        runNotifier.fireTestAssumptionFailed(failure);
    }

    @Override
    public void fireTestFailure(Failure failure) {
        Description description = failure.getDescription();
        String methodName = description.getMethodName();
        Throwable throwable = failure.getException();

        if (methodName == null) {
            error("Test method '{}' failed", methodName, throwable);
        } else {
            error("Test method failed", throwable);
        }

        runNotifier.fireTestFailure(failure);
    }

    @Override
    public void fireTestIgnored(Description description) {
        warn("Test ignored");
        runNotifier.fireTestIgnored(description);
    }

    @Override
    public void fireTestStarted(Description description) throws StoppedByUserException {
        debug("Test started");
        runNotifier.fireTestStarted(description);
    }

    @Override
    public void fireTestFinished(Description description) {
        debug("Test finished");
        runNotifier.fireTestFinished(description);
    }

    public void addFailure(Throwable t) {
        if (t instanceof MultipleFailureException) {
            addMultipleFailureException((MultipleFailureException) t);
        } else {

            Failure failure = new Failure(testDescription, t);
            runNotifier.fireTestFailure(failure);
        }
    }

    private void addMultipleFailureException(MultipleFailureException mfe) {
        mfe.getFailures().stream().forEach((each) -> {
            addFailure(each);
        });
    }

    public void addFailedAssumption(AssumptionViolatedException e) {
        Failure failure = new Failure(testDescription, e);
        runNotifier.fireTestAssumptionFailed(failure);
    }

}
