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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testifyproject.bytebuddy.implementation.MethodDelegation.withEmptyConfiguration;
import static org.testifyproject.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static org.testifyproject.bytebuddy.matcher.ElementMatchers.not;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.bytebuddy.description.method.MethodDescription;
import org.testifyproject.bytebuddy.description.type.TypeDescription;
import org.testifyproject.bytebuddy.dynamic.DynamicType;
import org.testifyproject.bytebuddy.implementation.MethodDelegation;
import org.testifyproject.bytebuddy.implementation.bind.MethodNameEqualityResolver;
import org.testifyproject.bytebuddy.implementation.bind.annotation.BindingPriority;
import org.testifyproject.bytebuddy.implementation.bind.annotation.Morph;
import org.testifyproject.bytebuddy.implementation.bind.annotation.TargetMethodAnnotationDrivenBinder;
import org.testifyproject.bytebuddy.matcher.ElementMatcher;
import org.testifyproject.bytebuddy.matcher.ElementMatchers;
import org.testifyproject.bytebuddy.utility.JavaModule;
import org.testifyproject.extension.InstrumentInstance;
import org.testifyproject.extension.InstrumentMorpher;

/**
 *
 * @author saden
 */
public class InstrumentTransformerTest {

    InstrumentTransformer sut;
    InstrumentInstance instance;

    @Before
    public void init() {
        instance = mock(InstrumentInstance.class);
        sut = new InstrumentTransformer(instance);
    }

    @Test
    public void callToTransformShouldTransform() {
        DynamicType.Builder<?> builder = mock(DynamicType.Builder.class);
        TypeDescription typeDescription = mock(TypeDescription.class);
        ClassLoader classLoader = InstrumentTransformer.class.getClassLoader();
        JavaModule module = mock(JavaModule.class);
        ElementMatcher.Junction<MethodDescription> elementMatcher = ElementMatchers.any();
        Object interceptor = new Object();
        DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition typeDefinition =
                mock(DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition.class);

        DynamicType.Builder.MethodDefinition.ImplementationDefinition methodDefinition =
                mock(DynamicType.Builder.MethodDefinition.ImplementationDefinition.class);
        MethodDelegation methodDelegation = withEmptyConfiguration()
                .withBinders(TargetMethodAnnotationDrivenBinder.ParameterBinder.DEFAULTS)
                .withBinders(Morph.Binder.install(InstrumentMorpher.class))
                .withResolvers(MethodNameEqualityResolver.INSTANCE)
                .withResolvers(BindingPriority.Resolver.INSTANCE)
                .filter(not(isDeclaredBy(Object.class)))
                .to(interceptor);

        given(builder.method(elementMatcher)).willReturn(methodDefinition);
        given(instance.getInterceptor()).willReturn(interceptor);
        given(methodDefinition.intercept(methodDelegation)).willReturn(typeDefinition);

        DynamicType.Builder<?> result =
                sut.transform(builder, typeDescription, classLoader, module);

        assertThat(result).isEqualTo(typeDefinition);
        verify(builder).method(elementMatcher);
        verify(instance).getInterceptor();
        verify(methodDefinition).intercept(methodDelegation);
    }

}
