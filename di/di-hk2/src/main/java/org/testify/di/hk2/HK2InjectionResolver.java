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
package org.testify.di.hk2;

import org.testify.FieldDescriptor;
import org.testify.MockProvider;
import org.testify.TestContext;
import org.testify.TestDescriptor;
import org.testify.core.util.ServiceLocatorUtil;
import java.lang.reflect.Type;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InjectionResolver;
import static org.glassfish.hk2.api.InjectionResolver.SYSTEM_RESOLVER_NAME;
import org.glassfish.hk2.api.Rank;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;

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
        Type collaboratorType = injectee.getRequiredType();
        ActiveDescriptor<?> injecteeDescriptor = injectee.getInjecteeDescriptor();
        TestDescriptor testDescriptor = testContext.getTestDescriptor();

        Optional<FieldDescriptor> result;

        if (injecteeDescriptor == null || injecteeDescriptor.getName() == null) {
            result = testDescriptor.findFieldDescriptor(collaboratorType);
        } else {
            result = testDescriptor.findFieldDescriptor(collaboratorType, injecteeDescriptor.getName());
        }

        if (result.isPresent()) {
            FieldDescriptor fieldDescriptor = result.get();

            if (fieldDescriptor.getFake().isPresent()) {
                MockProvider mockProvider = ServiceLocatorUtil.INSTANCE.getOne(MockProvider.class);

                return mockProvider.createFake(fieldDescriptor.getType());
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

}
