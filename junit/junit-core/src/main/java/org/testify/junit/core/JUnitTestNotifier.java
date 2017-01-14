/*
 * Copyright 2016-2017 Sharmarke Aden.
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
package org.testify.junit.core;

import org.testify.trait.LoggingTrait;
import org.junit.internal.AssumptionViolatedException;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import org.junit.runners.model.MultipleFailureException;

/**
 * A JUnit test run notifier to handle logging and notification test lifecycle
 * events.
 *
 * @author saden
 */
public class JUnitTestNotifier extends RunNotifier implements LoggingTrait {

    private final RunNotifier notifier;
    private final Description description;

    public JUnitTestNotifier(RunNotifier notifier, Description description) {
        this.notifier = notifier;
        this.description = description;

    }

    @Override
    public void fireTestAssumptionFailed(Failure failure) {
        String methodName = failure.getDescription().getMethodName();

        if (methodName == null) {
            error("Test Class Assumption Failed");
        } else {
            error("Test Method Assumption Failed");
        }

        notifier.fireTestAssumptionFailed(failure);
    }

    @Override
    public void fireTestFailure(Failure failure) {
        String methodName = failure.getDescription().getMethodName();

        if (methodName == null) {
            error("Test Class Failed");
        } else {
            error("Test Method Failed");
        }

        notifier.fireTestFailure(failure);
    }

    @Override
    public void fireTestIgnored(Description description) {
        warn("Ignored");
        notifier.fireTestIgnored(description);
    }

    @Override
    public void fireTestStarted(Description description) throws StoppedByUserException {
        debug("Started");
        notifier.fireTestStarted(description);
    }

    @Override
    public void fireTestFinished(Description description) {
        debug("Finished");
        notifier.fireTestFinished(description);
    }

    public void addFailure(Throwable e) {
        if (e instanceof MultipleFailureException) {
            addMultipleFailureException((MultipleFailureException) e);
        } else {
            notifier.fireTestFailure(new Failure(description, e));
        }
    }

    private void addMultipleFailureException(MultipleFailureException mfe) {
        mfe.getFailures().stream().forEach((each) -> {
            addFailure(each);
        });
    }

    public void addFailedAssumption(AssumptionViolatedException e) {
        notifier.fireTestAssumptionFailed(new Failure(description, e));
    }

}
