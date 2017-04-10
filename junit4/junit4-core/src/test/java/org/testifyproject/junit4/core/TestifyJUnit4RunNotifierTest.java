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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 *
 * @author saden
 */
public class TestifyJUnit4RunNotifierTest {

    TestifyJUnit4RunNotifier cut;
    RunNotifier runNotifier;
    Description description;

    @Before
    public void init() {
        runNotifier = mock(RunNotifier.class);
        description = mock(Description.class);

        cut = TestifyJUnit4RunNotifier.of(runNotifier, description);
    }

    @Test(expected = NullPointerException.class)
    public void givenNullFailureFireTestAssumptionsFailedShouldThrowException() {
        cut.fireTestAssumptionFailed(null);
    }

    @Test
    public void givenFailureFireTestAssumptionsFailedShouldFireAssumptionFailed() {
        Failure failure = mock(Failure.class);
        Description failureDescription = mock(Description.class);
        String methodName = "methodName";
        Throwable exception = mock(Throwable.class);

        given(failure.getDescription()).willReturn(failureDescription);
        given(failureDescription.getMethodName()).willReturn(methodName);
        given(failure.getException()).willReturn(exception);

        cut.fireTestAssumptionFailed(failure);
        
       verify(runNotifier).fireTestAssumptionFailed(failure);
    }

}
