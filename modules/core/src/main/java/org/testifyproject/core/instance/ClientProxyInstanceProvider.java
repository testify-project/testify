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
package org.testifyproject.core.instance;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.testifyproject.ClientInstance;
import org.testifyproject.ClientProvider;
import org.testifyproject.SutDescriptor;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Application;
import org.testifyproject.annotation.Discoverable;
import org.testifyproject.core.TestContextProperties;
import org.testifyproject.core.extension.instrument.DefaultProxyInstance;
import org.testifyproject.extension.ProxyInstance;
import org.testifyproject.extension.ProxyInstanceProvider;
import org.testifyproject.extension.annotation.SystemCategory;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 * An implementation of PreInstanceProvider that provides the test context.
 *
 * @author saden
 */
@SystemCategory
@Discoverable
public class ClientProxyInstanceProvider implements ProxyInstanceProvider {

    @Override
    public List<ProxyInstance> get(TestContext testContext) {
        ImmutableList.Builder<ProxyInstance> builder = ImmutableList.builder();

        testContext.<ClientProvider>findProperty(TestContextProperties.CLIENT_PROVIDER)
                .ifPresent(clientProvider -> {
                    builder.add(createClientInstance(testContext));
                    builder.add(createClient(testContext, clientProvider.getClientType()));
                    Class clientSupplierType = clientProvider.getClientSupplierType();

                    if (clientSupplierType != null) {
                        builder.add(createClientSupplier(testContext, clientSupplierType));
                    }
                });

        return builder.build();
    }

    ProxyInstance createClientInstance(TestContext testContext) {
        Supplier<ClientInstance> supplier = () ->
                testContext.getProperty(TestContextProperties.CLIENT_INSTANCE);

        return DefaultProxyInstance.of(ClientInstance.class, supplier);
    }

    ProxyInstance createClient(TestContext testContext, Class clientType) {
        Supplier<?> supplier = () ->
                testContext.getProperty(TestContextProperties.CLIENT);

        TestDescriptor testDescriptor = testContext.getTestDescriptor();

        Class contract = testDescriptor.getApplication()
                .map(Application::clientContract)
                .orElse(clientType);

        String name = testDescriptor.getApplication()
                .map(Application::clientName)
                .orElse(null);

        Optional<SutDescriptor> foundSutDescriptor = testContext.getSutDescriptor();

        if (foundSutDescriptor.isPresent()) {
            SutDescriptor sutDescriptor = foundSutDescriptor.get();

            if (sutDescriptor.isSubtypeOf(clientType)) {
                contract = sutDescriptor.getType();
            }
        }

        return DefaultProxyInstance.of(contract, name, supplier);
    }

    ProxyInstance createClientSupplier(TestContext testContext, Class clientSupplierType) {
        Supplier<?> supplier = () ->
                testContext.getProperty(TestContextProperties.CLIENT_SUPPLIER);

        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        Class contract = testDescriptor.getApplication()
                .map(Application::clientContract)
                .orElse(clientSupplierType);

        String name = testDescriptor.getApplication()
                .map(Application::clientSupplierName)
                .orElse(null);

        Optional<SutDescriptor> foundSutDescriptor = testContext.getSutDescriptor();

        if (foundSutDescriptor.isPresent()) {
            SutDescriptor sutDescriptor = foundSutDescriptor.get();

            if (sutDescriptor.isSubtypeOf(clientSupplierType)) {
                contract = sutDescriptor.getType();
            }
        }

        return DefaultProxyInstance.of(contract, name, supplier);
    }

}
