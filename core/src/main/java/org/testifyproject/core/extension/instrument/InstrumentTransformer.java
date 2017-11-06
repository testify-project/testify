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
package org.testifyproject.core.extension.instrument;

import static org.testifyproject.bytebuddy.implementation.MethodDelegation.withEmptyConfiguration;
import static org.testifyproject.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static org.testifyproject.bytebuddy.matcher.ElementMatchers.not;

import org.testifyproject.bytebuddy.agent.builder.AgentBuilder;
import org.testifyproject.bytebuddy.description.type.TypeDescription;
import org.testifyproject.bytebuddy.dynamic.DynamicType;
import org.testifyproject.bytebuddy.implementation.bind.MethodNameEqualityResolver;
import org.testifyproject.bytebuddy.implementation.bind.annotation.BindingPriority;
import org.testifyproject.bytebuddy.implementation.bind.annotation.Morph;
import org.testifyproject.bytebuddy.implementation.bind.annotation.TargetMethodAnnotationDrivenBinder;
import org.testifyproject.bytebuddy.matcher.ElementMatchers;
import org.testifyproject.bytebuddy.utility.JavaModule;
import org.testifyproject.extension.InstrumentInstance;
import org.testifyproject.extension.InstrumentMorpher;

/**
 * An implementation of the bytebuddy transformer contract that adds a transformer for an
 * instrument instance.
 *
 * @author saden
 */
public class InstrumentTransformer implements AgentBuilder.Transformer {

    private final InstrumentInstance instance;

    InstrumentTransformer(InstrumentInstance instance) {
        this.instance = instance;
    }

    public static AgentBuilder.Transformer of(InstrumentInstance instance) {
        return new InstrumentTransformer(instance);
    }

    @Override
    public DynamicType.Builder<?> transform(
            DynamicType.Builder<?> builder,
            TypeDescription typeDescription,
            ClassLoader classLoader,
            JavaModule module) {
        return builder.method(ElementMatchers.any())
                .intercept(withEmptyConfiguration()
                        .withBinders(TargetMethodAnnotationDrivenBinder.ParameterBinder.DEFAULTS)
                        .withBinders(Morph.Binder.install(InstrumentMorpher.class))
                        .withResolvers(MethodNameEqualityResolver.INSTANCE)
                        .withResolvers(BindingPriority.Resolver.INSTANCE)
                        .filter(not(isDeclaredBy(Object.class)))
                        .to(instance.getInterceptor()));
    }

}
