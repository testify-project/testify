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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import javax.inject.Provider;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.testifyproject.FieldDescriptor;
import org.testifyproject.MockProvider;
import org.testifyproject.TestContext;
import org.testifyproject.guava.common.reflect.TypeToken;

/**
 * A custom Spring bean post processor used to get and createFake service
 * instances and reify the test class fields.
 *
 * @author saden
 */
public class SpringReifierPostProcessor implements InstantiationAwareBeanPostProcessor {

    private final TestContext testContext;

    SpringReifierPostProcessor(TestContext testContext) {
        this.testContext = testContext;
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) {
        return testContext.getSutDescriptor().map(sutDescriptor -> {
            MockProvider mockProvider = testContext.getMockProvider();

            for (FieldDescriptor fieldDescriptor : testContext.getTestDescriptor().getFieldDescriptors()) {
                if (!fieldDescriptor.getFake().isPresent()) {
                    continue;
                }

                Type fieldType = fieldDescriptor.getGenericType();
                TypeToken fieldTypeToken = TypeToken.of(fieldType);
                TypeToken rawTypeToken = getRawTypeToken(fieldType);

                if (fieldTypeToken.isSupertypeOf(beanClass)) {
                    Optional<Object> foundValue = fieldDescriptor.getValue(testContext.getTestInstance());

                    if (foundValue.isPresent()) {
                        return foundValue.get();
                    }
                } else if (rawTypeToken.isSupertypeOf(beanClass)) {
                    return mockProvider.createFake(beanClass);
                }
            }

            return null;
        }).orElse(null);

    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) {
        return true;
    }

    @Override
    public PropertyValues postProcessPropertyValues(
            PropertyValues pvs,
            PropertyDescriptor[] pds,
            Object bean,
            String beanName) {
        return pvs;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        Class<? extends Object> beanClass = bean.getClass();

        //XXX: DO NOT remove this  method and code as it is required to extract
        //collaborators before the sut class is proxied down stream. Once the
        //sut class is proxied we will not be able to access the sut class
        //fields with ease.
        testContext.getSutDescriptor().ifPresent(sutDescriptor -> {
            if (sutDescriptor.isSutClass(beanClass)) {
                sutDescriptor.setValue(testContext.getTestInstance(), bean);
            }
        });

        return bean;
    }

    /**
     * Given a type determine the raw type.
     *
     * @param type the type whose raw type is being determined
     * @return the raw type token
     */
    TypeToken getRawTypeToken(Type type) {
        TypeToken typeToken = TypeToken.of(type);
        Class rawType = typeToken.getRawType();

        if (typeToken.isSubtypeOf(Provider.class)) {
            TypeVariable<Class<Provider>> paramType = Provider.class.getTypeParameters()[0];
            rawType = typeToken.resolveType(paramType).getRawType();
        } else if (typeToken.isSubtypeOf(Optional.class)) {
            TypeVariable<Class<Optional>> paramType = Optional.class.getTypeParameters()[0];
            rawType = typeToken.resolveType(paramType).getRawType();
        } else if (typeToken.isSubtypeOf(Map.class)) {
            TypeVariable<Class<Map>> valueType = Map.class.getTypeParameters()[1];
            rawType = typeToken.resolveType(valueType).getRawType();
        } else if (typeToken.isSubtypeOf(Collection.class)) {
            TypeVariable<Class<Collection>> valueType = Collection.class.getTypeParameters()[0];
            rawType = typeToken.resolveType(valueType).getRawType();
        }

        return TypeToken.of(rawType);
    }

}
