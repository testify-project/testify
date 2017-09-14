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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.annotation.Name;
import org.testifyproject.core.annotation.DefaultName;

/**
 *
 * @author saden
 */
public class DefaultServiceInstanceTest {

    DefaultServiceInstance sut;
    Map<ServiceKey, Object> serviceContext;

    @Before
    public void init() {
        serviceContext = new HashMap<>();
        sut = new DefaultServiceInstance(serviceContext);
    }

    @Test
    public void callToGetContextShouldReturnServiceContext() {
        Object result = sut.getContext();
        assertThat(result).isEqualTo(serviceContext);
    }

    @Test
    public void givenNonExistentTypeAndNameGetServiceShouldReturnNull() {
        Object result = sut.getService(String.class, "test");

        assertThat(result).isNull();
    }

    @Test
    public void givenExistentTypeAndNameGetServiceShouldReturnService() {
        Type type = Object.class;
        String name = "test";
        ServiceKey key = ServiceKey.of(type, name);
        Object value = new Object();
        serviceContext.put(key, value);

        Object result = sut.getService(type, name);

        assertThat(result).isEqualTo(value);
    }

    @Test
    public void givenExistentTypeGetServiceShouldReturnService() {
        Type type = Object.class;
        String name = "test";
        ServiceKey key = ServiceKey.of(type, name);
        Object value = new Object();
        serviceContext.put(key, value);

        Object result = sut.getService(type);

        assertThat(result).isEqualTo(value);
    }

    @Test
    public void givenExistentTypeAndQualifierGetServiceShouldReturnService() {
        Type type = Object.class;
        String name = "test";
        ServiceKey key = ServiceKey.of(type, name);
        Object value = new Object();
        serviceContext.put(key, value);

        Object result = sut.getService(type, DefaultName.of(name));

        assertThat(result).isEqualTo(value);
    }

    @Test
    public void callToAddConstantShouldAddConstantToServiceContext() {
        Object instance = new Object();
        String name = "test";
        Class contract = Object.class;

        sut.addConstant(instance, name, contract);

        assertThat(serviceContext)
                .containsEntry(ServiceKey.of(contract, name), instance);
    }

    @Test
    public void callToReplaceShouldreplaceConstantInServiceContext() {
        Object instance = new Object();
        String name = "test";
        Class contract = Object.class;

        sut.replace(instance, name, contract);

        assertThat(serviceContext)
                .containsEntry(ServiceKey.of(contract, name), instance);
    }

    @Test
    public void callToGetNameQualifierShouldReturnName() {
        Set<Class<? extends Annotation>> result = sut.getNameQualifers();

        assertThat(result).containsExactly(Name.class);
    }

    @Test
    public void callToDestroyShouldClearServiceContext() {
        sut.destroy();

        assertThat(serviceContext).isEmpty();
    }

}
