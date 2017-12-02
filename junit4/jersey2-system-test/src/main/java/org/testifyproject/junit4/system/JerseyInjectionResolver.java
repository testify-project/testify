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
package org.testifyproject.junit4.system;

import static org.glassfish.hk2.api.InjectionResolver.SYSTEM_RESOLVER_NAME;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.IterableProvider;
import org.glassfish.hk2.api.Rank;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.utilities.NamedImpl;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.testifyproject.FieldDescriptor;
import org.testifyproject.MockProvider;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.guava.common.reflect.TypeToken;

/**
 * A custom HK2 injection resolve used to get and createFake service instances and reify the
 * test class fields.
 *
 * @author saden
 */
@Singleton
@Rank(Integer.MAX_VALUE)
public class JerseyInjectionResolver implements InjectionResolver<Inject> {

    private final TestContext testContext;
    private final InjectionManager injectionManager;

    @Inject
    JerseyInjectionResolver(TestContext testContext, InjectionManager injectionManager) {
        this.testContext = testContext;
        this.injectionManager = injectionManager;
    }

    @Override
    public Object resolve(Injectee injectee, ServiceHandle root) {
        return testContext.getSutDescriptor().map(sutDescriptor -> {
            TestDescriptor testDescriptor = testContext.getTestDescriptor();
            MockProvider mockProvider = testContext.getMockProvider();
            Object testInstance = testContext.getTestInstance();
            Type requiredType = injectee.getRequiredType();

            for (FieldDescriptor fieldDescriptor : testDescriptor.getFieldDescriptors()) {
                if (fieldDescriptor.getFake().isPresent()) {
                    Type fieldType = fieldDescriptor.getGenericType();
                    Optional<Object> foundValue = fieldDescriptor.getValue(testInstance);

                    if (TypeToken.of(fieldType).isSupertypeOf(requiredType) && foundValue
                            .isPresent()) {
                        return foundValue.get();
                    } else if (getRawTypeToken(fieldType).isSupertypeOf(requiredType)) {
                        return mockProvider.createFake(TypeToken.of(requiredType)
                                .getRawType());
                    }
                }
            }

            return null;
        }).orElseGet(() -> findThreeThirtyService(injectee, root));

    }

    Object findThreeThirtyService(Injectee injectee, ServiceHandle root) {
        //TODO: we need to be able to get the asutal injectee resolver for types
        //other than @Inject. HK2 no longer provides ability to do that via API
        //will file a bug.
        InjectionResolver threeThirtyResolver = injectionManager.getInstance(
                InjectionResolver.class,
                new NamedImpl(SYSTEM_RESOLVER_NAME)
        );

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
     * Given a type determine the raw type.
     *
     * @param type the type whose raw type is being determined
     * @return the raw type, or the original type
     */
    TypeToken getRawTypeToken(Type type) {
        TypeToken typeToken = TypeToken.of(type);
        Class rawType = typeToken.getRawType();

        if (typeToken.isSubtypeOf(IterableProvider.class)) {
            TypeVariable<Class<IterableProvider>> paramType = IterableProvider.class
                    .getTypeParameters()[0];
            rawType = typeToken.resolveType(paramType).getRawType();
        } else if (typeToken.isSubtypeOf(Provider.class)) {
            TypeVariable<Class<Provider>> paramType =
                    Provider.class.getTypeParameters()[0];
            rawType = typeToken.resolveType(paramType).getRawType();
        }

        return TypeToken.of(rawType);
    }

}
