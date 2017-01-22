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
package org.testify.di.spring;

import java.beans.PropertyDescriptor;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import javax.inject.Provider;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.testify.CutDescriptor;
import org.testify.FieldDescriptor;
import org.testify.MockProvider;
import org.testify.TestContext;
import org.testify.TestDescriptor;
import org.testify.core.util.ServiceLocatorUtil;
import org.testify.guava.common.reflect.TypeToken;

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
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        Object instance = null;
        Optional<CutDescriptor> descriptor = testContext.getCutDescriptor();

        if (descriptor.isPresent() && !descriptor.get().isCutClass(beanClass)) {
            Optional<FieldDescriptor> result = testDescriptor.findFieldDescriptor(beanClass);

            if (result.isPresent()) {
                FieldDescriptor fieldDescriptor = result.get();

                if (!fieldDescriptor.getFake().isPresent()) {
                    return instance;
                }

                TypeToken<?> fieldTypeToken = TypeToken.of(fieldDescriptor.getType());
                Class<?> rawType = getRawType(fieldTypeToken, fieldDescriptor);
                TypeToken<?> rawTypeToken = TypeToken.of(rawType);

                if (rawTypeToken.isSupertypeOf(beanClass)) {
                    MockProvider mockProvider = ServiceLocatorUtil.INSTANCE.getOne(MockProvider.class);

                    instance = mockProvider.createFake(beanClass);
                }
            }
        }

        return instance;
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        return true;
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {
        return pvs;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<? extends Object> beanClass = bean.getClass();

        //XXX: DO NOT remove this  method and code as it is required to extract
        //collaborators before the cut class is proxied down stream. Once the
        //cut class is proxied we will not be able to access the cut class
        //fields with ease.
        Optional<CutDescriptor> descriptor = testContext.getCutDescriptor();

        if (descriptor.isPresent() && descriptor.get().isCutClass(beanClass)) {
            testContext.getTestReifier().reify(testContext, bean);
        }

        return bean;
    }

    Class<?> getRawType(TypeToken<?> token, FieldDescriptor descriptor) {
        Class<?> rawType = descriptor.getType();

        if (token.isSubtypeOf(Provider.class)) {
            TypeVariable<Class<Provider>> paramType = Provider.class.getTypeParameters()[0];
            rawType = token.resolveType(paramType).getRawType();
        } else if (token.isSubtypeOf(Optional.class)) {
            TypeVariable<Class<Optional>> paramType = Optional.class.getTypeParameters()[0];
            rawType = token.resolveType(paramType).getRawType();
        } else if (token.isSubtypeOf(Map.class)) {
            TypeVariable<Class<Map>> valueType = Map.class.getTypeParameters()[1];
            rawType = token.resolveType(valueType).getRawType();
        } else if (token.isSubtypeOf(Collection.class)) {
            TypeVariable<Class<Collection>> valueType = Collection.class.getTypeParameters()[0];
            rawType = token.resolveType(valueType).getRawType();
        }

        return rawType;
    }

}
