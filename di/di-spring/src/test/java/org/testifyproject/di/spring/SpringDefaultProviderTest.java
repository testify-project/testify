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
package org.testifyproject.di.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.lang.annotation.Annotation;

import javax.inject.Named;
import javax.inject.Provider;

import org.junit.Test;
import org.testifyproject.ServiceInstance;

/**
 *
 * @author saden
 */
public class SpringDefaultProviderTest {

    @Test
    public void givenServiceInstanceAndTypeGetShouldReturn() {
        ServiceInstance serviceInstance = mock(ServiceInstance.class);
        Class type = Object.class;
        Object service = mock(Object.class);

        given(serviceInstance.getService(type)).willReturn(service);

        Provider sut = SpringDefaultProvider.of(serviceInstance, type);

        Object result = sut.get();
        assertThat(result).isEqualTo(service);
        verify(serviceInstance).getService(type);
    }

    @Test
    public void givenServiceInstanceAndTypeAndQualifiersGetShouldReturn() {
        ServiceInstance serviceInstance = mock(ServiceInstance.class);
        Class type = Object.class;
        Annotation[] qualifiers = new Annotation[]{mock(Named.class)};
        Object service = mock(Object.class);

        given(serviceInstance.getService(type, qualifiers)).willReturn(service);

        Provider sut = SpringDefaultProvider.of(serviceInstance, type, qualifiers);

        Object result = sut.get();
        assertThat(result).isEqualTo(service);
        verify(serviceInstance).getService(type, qualifiers);
    }

}
