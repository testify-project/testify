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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.MethodDescriptor;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 *
 * @author saden
 */
public class ConfigHandlerPreVerifierTest {

    ConfigHandlerPreVerifier sut;

    @Before
    public void init() {
        sut = new ConfigHandlerPreVerifier();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullTestContextVerifyShouldThrowException() {
        sut.verify(null);
    }

    @Test
    public void givenConfigHandlerWithoutParamterVerifyShouldThrowException() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        MethodDescriptor configHandler = mock(MethodDescriptor.class);
        List<MethodDescriptor> configHandlers = ImmutableList.of(configHandler);
        List<Class> paramterTypes = ImmutableList.of();
        String methodName = "configMethod";
        String testName = "TestClass";
        Class returnType = String.class;

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getConfigHandlers()).willReturn(configHandlers);
        given(configHandler.getParameterTypes()).willReturn(paramterTypes);
        given(configHandler.getName()).willReturn(methodName);
        given(configHandler.getDeclaringClassName()).willReturn(testName);
        given(configHandler.getReturnType()).willReturn(returnType);

        sut.verify(testContext);

        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getConfigHandlers();
        verify(configHandler).getParameterTypes();
        verify(testContext).addError(anyBoolean(), anyString(), any());
        verifyNoMoreInteractions(testContext, testDescriptor);
    }

    @Test
    public void givenConfigHandlerWithTwoParamterVerifyShouldThrowException() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        MethodDescriptor configHandler = mock(MethodDescriptor.class);
        List<MethodDescriptor> configHandlers = ImmutableList.of(configHandler);
        List<Class> paramterTypes = ImmutableList.of(Integer.class, String.class);
        String methodName = "configMethod";
        String testName = "TestClass";
        Class returnType = String.class;

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getConfigHandlers()).willReturn(configHandlers);
        given(configHandler.getParameterTypes()).willReturn(paramterTypes);
        given(configHandler.getName()).willReturn(methodName);
        given(configHandler.getDeclaringClassName()).willReturn(testName);
        given(configHandler.getReturnType()).willReturn(returnType);

        sut.verify(testContext);

        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getConfigHandlers();
        verify(configHandler).getParameterTypes();
        verify(testContext, times(2)).addError(anyBoolean(), anyString(), any());
        verifyNoMoreInteractions(testContext, testDescriptor);
    }

    @Test
    public void givenConfigHandlerWithOneParamterVerifyShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        MethodDescriptor configHandler = mock(MethodDescriptor.class);
        List<MethodDescriptor> configHandlers = ImmutableList.of(configHandler);
        List<Class> paramterTypes = ImmutableList.of(String.class);
        String methodName = "configMethod";
        String testName = "TestClass";
        Class returnType = String.class;

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getConfigHandlers()).willReturn(configHandlers);
        given(configHandler.getParameterTypes()).willReturn(paramterTypes);
        given(configHandler.getName()).willReturn(methodName);
        given(configHandler.getDeclaringClassName()).willReturn(testName);
        given(configHandler.getReturnType()).willReturn(returnType);

        sut.verify(testContext);

        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getConfigHandlers();
        verify(configHandler).getParameterTypes();
        verify(configHandler).getName();
        verify(configHandler).getDeclaringClassName();
        verify(configHandler).getReturnType();
        verify(testContext, times(2)).addError(anyBoolean(), anyString(), any());
        verifyNoMoreInteractions(testContext, testDescriptor);
    }

    @Test
    public void givenConfigHandlerDifferentReturnTypeVerifyShouldThrowException() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        MethodDescriptor configHandler = mock(MethodDescriptor.class);
        List<MethodDescriptor> configHandlers = ImmutableList.of(configHandler);
        List<Class> paramterTypes = ImmutableList.of(String.class);
        String methodName = "configMethod";
        String testName = "TestClass";
        Class returnType = Integer.class;

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getConfigHandlers()).willReturn(configHandlers);
        given(configHandler.getParameterTypes()).willReturn(paramterTypes);
        given(configHandler.getName()).willReturn(methodName);
        given(configHandler.getDeclaringClassName()).willReturn(testName);
        given(configHandler.getReturnType()).willReturn(returnType);

        sut.verify(testContext);

        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getConfigHandlers();
        verify(configHandler).getParameterTypes();
        verify(configHandler).getName();
        verify(configHandler).getDeclaringClassName();
        verify(configHandler).getReturnType();
        verify(testContext, times(2)).addError(anyBoolean(), anyString(), any());
        verifyNoMoreInteractions(testContext, testDescriptor);
    }

}
