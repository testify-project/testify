/*
 * Copyright 2016-2018 Testify Project.
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
package org.testifyproject.junit5.resolver;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Set;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.testifyproject.TestContext;
import org.testifyproject.core.extension.GetMetaAnnotations;
import org.testifyproject.junit5.TestifyExtension;

/**
 * A parameter resolver that provides the ability to resolve test method parameters annotated
 * with dependency injection framework specific annotations.
 *
 * @author saden
 */
public class ServiceParameterResolver implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext pc, ExtensionContext ec)
            throws ParameterResolutionException {
        ExtensionContext.Namespace namespace = create(TestifyExtension.class);
        ExtensionContext.Store store = ec.getStore(namespace);
        TestContext testContext = store.get(TestContext.class, TestContext.class);

        return testContext.getServiceInstance()
                .map(serviceInstance -> {
                    Set<Class<? extends Annotation>> nameQualifers =
                            serviceInstance.getNameQualifers();
                    Set<Class<? extends Annotation>> customQualifiers =
                            serviceInstance.getCustomQualifiers();

                    boolean match = nameQualifers.stream()
                            .anyMatch(pc::isAnnotated);

                    if (!match) {
                        match = customQualifiers.stream()
                                .anyMatch(pc::isAnnotated);
                    }

                    return match;
                })
                .orElse(false);
    }

    @Override
    public Object resolveParameter(ParameterContext pc, ExtensionContext ec)
            throws ParameterResolutionException {
        ExtensionContext.Namespace namespace = create(TestifyExtension.class);
        ExtensionContext.Store store = ec.getStore(namespace);
        TestContext testContext = store.get(TestContext.class, TestContext.class);
        Parameter parameter = pc.getParameter();

        return testContext.getServiceInstance()
                .map(serviceInstance -> {
                    Set<Class<? extends Annotation>> nameQualifers =
                            serviceInstance.getNameQualifers();
                    Set<Class<? extends Annotation>> customQualifiers =
                            serviceInstance.getCustomQualifiers();
                    Annotation[] fieldQualifiers =
                            new GetMetaAnnotations(parameter, nameQualifers, customQualifiers)
                                    .execute();

                    return serviceInstance.getService(
                            parameter.getParameterizedType(), fieldQualifiers);
                })
                .orElse(null);

    }

}
