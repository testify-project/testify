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
package org.testifyproject.di.hk2;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InjectionResolver;
import static org.glassfish.hk2.api.InjectionResolver.SYSTEM_RESOLVER_NAME;
import org.glassfish.hk2.api.IterableProvider;
import org.glassfish.hk2.api.Rank;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.testifyproject.CutDescriptor;
import org.testifyproject.FieldDescriptor;
import org.testifyproject.MockProvider;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.guava.common.reflect.TypeToken;

/**
 * A custom HK2 injection resolve used to get and createFake service instances
 * and reify the test class fields.
 *
 * @author saden
 */
@Singleton
@Rank(Integer.MAX_VALUE)
public class HK2InjectionResolver implements InjectionResolver<Inject> {

    private final TestContext testContext;
    private final ServiceLocator serviceLocator;

    @Inject
    HK2InjectionResolver(TestContext testContext, ServiceLocator serviceLocator) {
        this.testContext = testContext;
        this.serviceLocator = serviceLocator;
    }

    @Override
    public Object resolve(Injectee injectee, ServiceHandle root) {
        Type requiredType = injectee.getRequiredType();
        ActiveDescriptor<?> injecteeDescriptor = injectee.getInjecteeDescriptor();
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        MockProvider mockProvider = testContext.getMockProvider();
        Object testInstance = testContext.getTestInstance();
        Optional<CutDescriptor> cutDescriptor = testContext.getCutDescriptor();

        if (cutDescriptor.isPresent()) {
            for (FieldDescriptor fieldDescriptor : testDescriptor.getFieldDescriptors()) {
                if (!fieldDescriptor.getFake().isPresent()) {
                    continue;
                }

                Class<?> fieldDescriptorType = fieldDescriptor.getType();
                TypeToken<?> fieldDescriptorToken = TypeToken.of(fieldDescriptor.getGenericType());

                Class<?> rawType = getRawType(fieldDescriptorToken, fieldDescriptorType);
                TypeToken<?> rawTypeToken = TypeToken.of(rawType);

                if (rawTypeToken.isSupertypeOf(requiredType)) {
                    Optional<Object> value = fieldDescriptor.getValue(testInstance);
                    Object instance;

                    if (value.isPresent()) {
                        instance = value.get();

                        if (!mockProvider.isMock(instance)) {
                            instance = mockProvider.createVirtual(fieldDescriptorType, instance);
                        }
                    } else {
                        instance = mockProvider.createFake(fieldDescriptorType);
                    }

                    return instance;
                }
            }

        }

        return findThreeThirtyService(injectee, root);
    }

    Object findThreeThirtyService(Injectee injectee, ServiceHandle root) {
        //TODO: we need to be able to get the acutal injectee resolver for types
        //other than @Inject. HK2 no longer provides ability to do that via API
        //will file a bug.
        InjectionResolver threeThirtyResolver = serviceLocator.getService(InjectionResolver.class, SYSTEM_RESOLVER_NAME);

        return threeThirtyResolver.resolve(injectee, root);
    }

    @Override
    public boolean isConstructorParameterIndicator() {
        return false;
    }

    @Override
    public boolean isMethodParameterIndicator() {
        return false;
    }

    /**
     * GIven a type token and default type determine the raw type of the type
     * token by spring supported generic classes.
     *
     * @param typeToken the type token that will be inspected
     * @return the raw type
     */
    Class<?> getRawType(TypeToken<?> typeToken, Class<?> fieldDescriptorType) {
        Class<?> rawType = fieldDescriptorType;

        if (typeToken.isSubtypeOf(IterableProvider.class)) {
            TypeVariable<Class<IterableProvider>> paramType = IterableProvider.class.getTypeParameters()[0];
            rawType = typeToken.resolveType(paramType).getRawType();
        } else if (typeToken.isSubtypeOf(Provider.class)) {
            TypeVariable<Class<Provider>> paramType = Provider.class.getTypeParameters()[0];
            rawType = typeToken.resolveType(paramType).getRawType();
        }

        return rawType;
    }

}
