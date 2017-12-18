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
package org.testifyproject.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.LocalResourceInfo;
import org.testifyproject.MethodDescriptor;
import org.testifyproject.MockProvider;
import org.testifyproject.RemoteResourceInfo;
import org.testifyproject.ServiceInstance;
import org.testifyproject.SutDescriptor;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestRunner;
import org.testifyproject.TestifyException;
import org.testifyproject.VirtualResourceInfo;
import org.testifyproject.extension.annotation.UnitCategory;

/**
 *
 * @author saden
 */
public class DefaultTestContextTest {

    TestContext sut;
    Class<? extends Annotation> testCategory;
    Object testInstance;
    TestDescriptor testDescriptor;
    MethodDescriptor methodDescriptor;
    TestRunner testRunner;
    TestConfigurer testConfigurer;
    MockProvider mockProvider;
    Map<String, Object> properties;

    @Before
    public void init() {
        testCategory = UnitCategory.class;
        testInstance = new Object();
        testDescriptor = mock(TestDescriptor.class);
        methodDescriptor = mock(MethodDescriptor.class);
        testConfigurer = mock(TestConfigurer.class);
        testRunner = mock(TestRunner.class);
        mockProvider = mock(MockProvider.class);
        properties = mock(Map.class, delegatesTo(new HashMap<>()));

        sut = spy(DefaultTestContextBuilder.builder()
                .testCategory(testCategory)
                .testInstance(testInstance)
                .testDescriptor(testDescriptor)
                .testMethodDescriptor(methodDescriptor)
                .testRunner(testRunner)
                .testConfigurer(testConfigurer)
                .mockProvider(mockProvider)
                .properties(properties)
                .build());
    }

    @Test
    public void callToGetPropertiesShouldReturnProperties() {
        Map<String, Object> result = sut.getProperties();

        assertThat(result).isEqualTo(properties);
    }

    @Test
    public void callToGetTestNameShouldReturnClassName() {
        sut.getTestName();

        verify(testDescriptor).getTestClassName();
    }

    @Test
    public void callToGetMethodNameShouldReturnMethodName() {
        sut.getMethodName();

        verify(methodDescriptor).getName();
    }

    @Test
    public void callToGetNameShouldReturnName() {
        sut.getName();

        verify(methodDescriptor).getName();
        verify(testDescriptor).getTestClassName();
    }

    @Test
    public void callToGetTestClassShouldReturnReturnTestClass() {
        sut.getTestClass();

        verify(testDescriptor).getTestClass();
    }

    @Test
    public void callToGetTestClassLoaderShouldReturnTestClassLoader() {
        sut.getTestClassLoader();

        verify(testDescriptor).getTestClassLoader();
    }

    @Test
    public void callToGetResourceStartStrategyShouldReturn() {
        Class<? extends Annotation> result = sut.getTestCategory();

        assertThat(result).isEqualTo(testCategory);
    }

    @Test
    public void callToGetTestInstanceShouldReturn() {
        Object result = sut.getTestInstance();

        assertThat(result).isEqualTo(testInstance);
    }

    @Test
    public void callToGetTestDescriptorShouldReturn() {
        TestDescriptor result = sut.getTestDescriptor();

        assertThat(result).isEqualTo(testDescriptor);
    }

    @Test
    public void callToGetTestRunnerShouldReturn() {
        TestRunner result = sut.getTestRunner();

        assertThat(result).isEqualTo(testRunner);
    }

    @Test
    public void callToGetFinalReifierShouldReturn() {
        TestConfigurer result = sut.getTestConfigurer();

        assertThat(result).isEqualTo(testConfigurer);
    }

    @Test
    public void callToGetMockProviderShouldReturn() {
        MockProvider result = sut.getMockProvider();

        assertThat(result).isEqualTo(mockProvider);
    }

    @Test
    public void callToGetSutDescriptorShouldReturn() {
        Optional<SutDescriptor> result = sut.getSutDescriptor();

        assertThat(result).isEmpty();
    }

    @Test
    public void callToGetLocalResourceInstancesShouldReturn() {
        Collection<LocalResourceInfo> result = sut.getLocalResources();

        assertThat(result).isEmpty();
    }

    @Test
    public void callToGetVirtualResourceInstancesShouldReturn() {
        Collection<VirtualResourceInfo> result = sut.getVirtualResources();

        assertThat(result).isEmpty();
    }

    @Test
    public void callToGetRemoteResourceInstancesShouldReturn() {
        Collection<RemoteResourceInfo> result = sut.getRemoteResources();

        assertThat(result).isEmpty();
    }

    @Test
    public void callToGetServiceInstanceShouldReturn() {
        Optional<ServiceInstance> result = sut.getServiceInstance();

        assertThat(result).isEmpty();
    }

    @Test
    public void callToGetSutInstanceShouldReturn() {
        Optional<Object> result = sut.getSutInstance();

        assertThat(result).isEmpty();
    }

    @Test
    public void callToAddErrorShouldAddErrorMessage() {
        String messageFormat = "Error Message: {}";
        String args = "error";
        String message = "Error Message: error";

        sut.addError(messageFormat, args);

        verify(sut).addCollectionElement(TestContextProperties.TEST_ERRORS, message);
    }

    @Test
    public void callToAddErrorWithTrueConditionShouldAddErrorMessage() {
        String messageFormat = "Error Message: {}";
        String args = "error";
        String message = "Error Message: error";

        sut.addError(true, messageFormat, args);

        verify(sut).addCollectionElement(TestContextProperties.TEST_ERRORS, message);
    }

    @Test
    public void callToAddErrorWithFalseConditionShouldNotAddErrorMessage() {
        String messageFormat = "Error Message: {}";
        String args = "error";
        String message = "Error Message: error";

        sut.addError(false, messageFormat, args);

        verify(sut, times(0)).addCollectionElement(TestContextProperties.TEST_ERRORS, message);
    }

    @Test
    public void callToAddWarningShouldAddWarningMessage() {
        String messageFormat = "Warning Message: {}";
        String args = "warning";
        String message = "Warning Message: warning";

        sut.addWarning(messageFormat, args);

        verify(sut).addCollectionElement(TestContextProperties.TEST_WARNINGS, message);
    }

    @Test
    public void callToAddWarningWithTrueConditionShouldAddWarningMessage() {
        String messageFormat = "Warning Message: {}";
        String args = "warning";
        String message = "Warning Message: warning";

        sut.addWarning(true, messageFormat, args);

        verify(sut).addCollectionElement(TestContextProperties.TEST_WARNINGS, message);
    }

    @Test
    public void callToAddWarningWithFalseConditionShouldNotAddWarningMessage() {
        String messageFormat = "Warning Message: {}";
        String args = "warning";
        String message = "Warning Message: warning";

        sut.addWarning(false, messageFormat, args);

        verify(sut, times(0)).addCollectionElement(TestContextProperties.TEST_WARNINGS, message);
    }

    @Test
    public void callToGetErrorsShouldReturnTestErrors() {
        Collection<String> result = sut.getErrors();

        assertThat(result).isEmpty();
        verify(sut).findCollection(TestContextProperties.TEST_ERRORS);
    }

    @Test
    public void callToGetWarningsShouldReturnTestWarnings() {
        Collection<String> result = sut.getWarnings();

        assertThat(result).isEmpty();
        verify(sut).findCollection(TestContextProperties.TEST_WARNINGS);
    }

    @Test
    public void callToVerifyWithWarningMessagesShouldPrintWarnings() {
        String messageFormat = "Warning Message: {}";
        String args = "warning";

        sut.addWarning(false, messageFormat, args);

        sut.verify();

        verify(sut).getWarnings();
    }

    @Test(expected = TestifyException.class)
    public void callToVerifyWithErrorMessagesShouldThrowException() {
        String messageFormat = "Error Message: {}";
        String args = "error";
        String message = "Error Message: error";

        sut.addError(true, messageFormat, args);

        sut.verify();

        verify(sut).getErrors();
    }

    @Test
    public void givenNullInstancesShouldNotBeEqual() {
        TestContext instance = null;

        assertThat(sut).isNotEqualTo(instance);
    }

    @Test
    public void givenDifferentTypeInstancesShouldNotBeEqual() {
        String differentType = "instance";

        assertThat(sut).isNotEqualTo(differentType);
        assertThat(sut.hashCode()).isNotEqualTo(differentType.hashCode());
    }

    @Test
    public void givenUnequalInstancesShouldNotBeEqual() {
        TestContext uneuqual = DefaultTestContextBuilder.builder()
                .testCategory(testCategory)
                .testInstance(testInstance)
                .testDescriptor(testDescriptor)
                .testMethodDescriptor(null)
                .testConfigurer(testConfigurer)
                .properties(properties)
                .build();

        assertThat(sut).isNotEqualTo(uneuqual);
        assertThat(sut.hashCode()).isNotEqualTo(uneuqual.hashCode());
    }

    @Test
    public void givenSameInstancesShouldBeEqual() {
        assertThat(sut).isEqualTo(sut);
    }

    @Test
    public void givenEqualInstancesShouldBeEqual() {
        TestContext equal1 = DefaultTestContextBuilder.builder()
                .testCategory(testCategory)
                .testInstance(testInstance)
                .testDescriptor(testDescriptor)
                .testMethodDescriptor(methodDescriptor)
                .testRunner(testRunner)
                .testConfigurer(testConfigurer)
                .mockProvider(mockProvider)
                .properties(properties)
                .build();

        TestContext equal2 = DefaultTestContextBuilder.builder()
                .testCategory(testCategory)
                .testInstance(testInstance)
                .testDescriptor(testDescriptor)
                .testMethodDescriptor(methodDescriptor)
                .testRunner(testRunner)
                .testConfigurer(testConfigurer)
                .mockProvider(mockProvider)
                .properties(properties)
                .build();

        assertThat(equal1).isEqualTo(equal2);
        assertThat(equal1.hashCode()).isEqualTo(equal2.hashCode());
    }

    @Test
    public void callToToStringShouldReturnHumanReadableString() {
        String result = sut.toString();

        assertThat(result).contains(
                "DefaultTestContext",
                "testDescriptor",
                "methodDescriptor"
        );
    }
}
