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

import java.lang.reflect.Parameter;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.testifyproject.MockProvider;
import org.testifyproject.TestContext;
import org.testifyproject.annotation.Fake;
import org.testifyproject.junit5.TestifyExtension;

/**
 * A parameter resolver that provides the ability to resolve test method parameters annotated
 * with {@link Fake}.
 *
 * @author saden
 */
public class FakeParameterResolver implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext pc, ExtensionContext ec)
            throws ParameterResolutionException {
        return pc.getParameter().getAnnotation(Fake.class) != null;
    }

    @Override
    public Object resolveParameter(ParameterContext pc, ExtensionContext ec)
            throws ParameterResolutionException {
        ExtensionContext.Namespace namespace = create(TestifyExtension.class);
        ExtensionContext.Store store = ec.getStore(namespace);

        TestContext testContext = store.get(TestContext.class, TestContext.class);
        MockProvider mockProvider = testContext.getMockProvider();
        Parameter parameter = pc.getParameter();

        return mockProvider.createFake(parameter.getType());
    }

}
