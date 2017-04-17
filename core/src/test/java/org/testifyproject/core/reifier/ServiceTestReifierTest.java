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
package org.testifyproject.core.reifier;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.testifyproject.FieldDescriptor;
import org.testifyproject.ServiceInstance;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Real;
import org.testifyproject.annotation.Virtual;
import org.testifyproject.guava.common.collect.ImmutableList;
import org.testifyproject.guava.common.collect.ImmutableSet;

/**
 *
 * @author saden
 */
public class ServiceTestReifierTest {

    ServiceTestReifier cut;

    @Before
    public void init() {
        cut = new ServiceTestReifier();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullTestContextReifyShouldThrowException() {
        cut.reify(null);

    }

    @Test
    public void givenTestContextWithoutServiceInstanceReifyShoulDoNothing() {
        TestContext testContext = mock(TestContext.class);
        Optional<ServiceInstance> foundServiceInstace = Optional.empty();

        given(testContext.getServiceInstance()).willReturn(foundServiceInstace);

        cut.reify(testContext);

        verify(testContext).getServiceInstance();
        verifyNoMoreInteractions(testContext);

    }

    @Test
    public void givenTestDescriptorWithoutFieldsReifyShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);
        Optional<ServiceInstance> foundServiceInstace = Optional.of(serviceInstance);
        Object testInstance = mock(Object.class);
        Set<Class<? extends Annotation>> nameQualifiers = ImmutableSet.of();
        Set<Class<? extends Annotation>> customQualifiers = ImmutableSet.of();
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Collection<FieldDescriptor> fieldDescriptors = ImmutableList.of();

        given(testContext.getServiceInstance()).willReturn(foundServiceInstace);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(serviceInstance.getNameQualifers()).willReturn(nameQualifiers);
        given(serviceInstance.getCustomQualifiers()).willReturn(customQualifiers);
        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getFieldDescriptors()).willReturn(fieldDescriptors);

        cut.reify(testContext);

        verify(testContext).getServiceInstance();
        verify(testContext).getTestInstance();
        verify(serviceInstance).getNameQualifers();
        verify(serviceInstance).getCustomQualifiers();
        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getFieldDescriptors();
        verifyNoMoreInteractions(testContext, serviceInstance, testDescriptor);

    }

    @Test
    public void givenTestDescriptorWithRealFieldWithoutValueReifySetAndInitField() {
        TestContext testContext = mock(TestContext.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);
        Optional<ServiceInstance> foundServiceInstace = Optional.of(serviceInstance);
        Object testInstance = mock(Object.class);
        Set<Class<? extends Annotation>> nameQualifiers = ImmutableSet.of();
        Set<Class<? extends Annotation>> customQualifiers = ImmutableSet.of();
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        Collection<FieldDescriptor> fieldDescriptors = ImmutableList.of(fieldDescriptor);
        Optional<Object> foundValue = Optional.empty();
        Class fieldType = Object.class;
        Annotation[] fieldQualifiers = {};
        Object value = new Object();
        Virtual virtual = mock(Virtual.class);
        Optional<Virtual> foundVirtual = Optional.of(virtual);

        given(testContext.getServiceInstance()).willReturn(foundServiceInstace);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(serviceInstance.getNameQualifers()).willReturn(nameQualifiers);
        given(serviceInstance.getCustomQualifiers()).willReturn(customQualifiers);
        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getFieldDescriptors()).willReturn(fieldDescriptors);
        given(fieldDescriptor.getValue(testInstance)).willReturn(foundValue);
        given(fieldDescriptor.hasAnyAnnotations(Real.class)).willReturn(true);
        given(fieldDescriptor.getType()).willReturn(fieldType);
        given(fieldDescriptor.getMetaAnnotations(nameQualifiers, customQualifiers)).willReturn(fieldQualifiers);
        given(serviceInstance.getService(fieldType, fieldQualifiers)).willReturn(value);

        cut.reify(testContext);

        verify(testContext).getServiceInstance();
        verify(testContext).getTestInstance();
        verify(serviceInstance).getNameQualifers();
        verify(serviceInstance).getCustomQualifiers();
        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getFieldDescriptors();
        verify(fieldDescriptor).getValue(testInstance);
        verify(fieldDescriptor).hasAnyAnnotations(Real.class);
        verify(fieldDescriptor).getType();
        verify(fieldDescriptor).getMetaAnnotations(nameQualifiers, customQualifiers);
        verify(serviceInstance).getService(fieldType, fieldQualifiers);
        verify(fieldDescriptor).setValue(testInstance, value);
        verify(fieldDescriptor).init(value);

        verifyNoMoreInteractions(testContext, serviceInstance, testDescriptor);

    }
}
