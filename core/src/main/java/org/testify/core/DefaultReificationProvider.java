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
package org.testify.core;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.testify.CutDescriptor;
import org.testify.MethodDescriptor;
import org.testify.MockProvider;
import org.testify.ReificationProvider;
import org.testify.RequiresProvider;
import org.testify.ServiceInstance;
import org.testify.TestContext;
import org.testify.TestDescriptor;
import org.testify.TestReifier;
import org.testify.core.util.ServiceLocatorUtil;
import org.testify.tools.Discoverable;

/**
 * An implementation of the ReificationProvider SPI contract.
 *
 * @author saden
 */
@Discoverable
public class DefaultReificationProvider implements ReificationProvider {

    private ServiceLocatorUtil serviceLocatorUtil = ServiceLocatorUtil.INSTANCE;
    private Queue<RequiresProvider> requiresProviders = new ConcurrentLinkedQueue<>();

    public DefaultReificationProvider() {
    }

    public DefaultReificationProvider(ServiceLocatorUtil serviceLocatorUtil,
            Queue<RequiresProvider> requiresProviders) {
        this.requiresProviders = requiresProviders;
        this.serviceLocatorUtil = serviceLocatorUtil;
    }

    @Override
    public void start(TestContext testContext, ServiceInstance serviceInstance) {
        if (testContext.getStartResources()) {
            List<RequiresProvider> foundRequiresProvider = serviceLocatorUtil.findAll(RequiresProvider.class);

            foundRequiresProvider.parallelStream().forEach(requiresProvider -> {
                requiresProvider.start(testContext, serviceInstance);
                requiresProviders.add(requiresProvider);
            });
        }

        //XXX: Some DI framework (i.e. Spring) require that the service isntance
        //context be initialized. We need to do the initialization after
        //the requires have started so that requires can dynamically addConfigHandler to services
        //that are available for injection (i.e. DataSource from InMemoryHSQLResource).
        serviceInstance.init();

        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        Object testInstance = testContext.getTestInstance();
        Optional<CutDescriptor> cutDescriptorResult = testContext.getCutDescriptor();

        Set<Class<? extends Annotation>> nameQualifers = serviceInstance.getNameQualifers();
        Set<Class<? extends Annotation>> customQualifiers = serviceInstance.getCustomQualifiers();
        Set<Class<? extends Annotation>> injectionAnnotations = serviceInstance.getInjectionAnnotations();

        MockProvider mockProvider = serviceLocatorUtil.getOne(MockProvider.class);

        //if we are performing unit or integration test then get the cut class
        //from the service instance.
        if (cutDescriptorResult.isPresent()) {
            CutDescriptor cutDescriptor = cutDescriptorResult.get();
            Class type = cutDescriptor.getType();

            Annotation[] qualifierAnnotations = cutDescriptor.getMetaAnnotations(nameQualifers, customQualifiers);

            Object cutInstance = serviceInstance.getService(type, qualifierAnnotations);

            cutDescriptor.setValue(testInstance, cutInstance);
            TestReifier testReifier = testContext.getTestReifier();
            Optional<MethodDescriptor> collaboratorMethod = testDescriptor.getCollaboratorProvider();

            if (collaboratorMethod.isPresent()) {
                MethodDescriptor methodDescriptor = collaboratorMethod.get();
                Optional<Object> collaboratorMethodResult;
                Optional<Object> methodInstance = methodDescriptor.getInstance();

                if (methodInstance.isPresent()) {
                    collaboratorMethodResult = methodDescriptor.invoke(methodInstance.get());
                } else {
                    collaboratorMethodResult = methodDescriptor.invoke(testInstance);
                }

                if (collaboratorMethodResult.isPresent()) {
                    Object resultValue = collaboratorMethodResult.get();
                    Object[] collaborators;

                    if (resultValue.getClass().isArray()) {
                        collaborators = (Object[]) resultValue;
                    } else if (resultValue instanceof Collection) {
                        collaborators = ((Collection) resultValue).stream().toArray();
                    } else {
                        collaborators = new Object[]{};
                    }

                    testReifier.reify(testContext, cutInstance, collaborators);
                }
            } else {
                testReifier.reify(testContext, cutInstance);
            }

            if (cutDescriptor.isVirtualCut()) {
                cutInstance = mockProvider.createVirtual(cutDescriptor.getType(), cutInstance);
            }

            testContext.addProperty(TestContextProperties.CUT_INSTANCE, cutInstance);
        }

        //if there are any fields on the test class that are not collaborators
        //of the cut class and are annotated with DI supported injection
        //annotations then get the services and initialize the test fields.
        testDescriptor.getFieldDescriptors()
                .parallelStream()
                .filter(p -> !p.getValue(testInstance).isPresent() && p.hasAnyAnnotations(injectionAnnotations))
                .forEach(fieldDescriptor -> {
                    Class type = fieldDescriptor.getType();
                    Annotation[] qualifierAnnotations = fieldDescriptor.getMetaAnnotations(nameQualifers, customQualifiers);

                    Object instance = serviceInstance.getService(type, qualifierAnnotations);

                    if (instance != null) {

                        if (fieldDescriptor.getVirtual().isPresent()) {
                            instance = mockProvider.createVirtual(fieldDescriptor.getType(), instance);
                        }

                        fieldDescriptor.setValue(testInstance, instance);
                    }

                });
    }

    @Override
    public void destroy(TestContext testContext, ServiceInstance serviceInstance) {
        if (testContext.getStartResources()) {
            requiresProviders.forEach(RequiresProvider::stop);
        }
    }

}
