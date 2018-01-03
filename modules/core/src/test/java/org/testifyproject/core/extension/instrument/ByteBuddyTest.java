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
import static org.testifyproject.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static org.testifyproject.bytebuddy.matcher.ElementMatchers.not;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.bytebuddy.ByteBuddy;
import org.testifyproject.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import org.testifyproject.bytebuddy.implementation.MethodDelegation;
import org.testifyproject.bytebuddy.implementation.bind.MethodNameEqualityResolver;
import org.testifyproject.bytebuddy.implementation.bind.annotation.BindingPriority;
import org.testifyproject.bytebuddy.implementation.bind.annotation.Morph;
import org.testifyproject.bytebuddy.implementation.bind.annotation.TargetMethodAnnotationDrivenBinder;
import org.testifyproject.extension.InstrumentMorpher;
import org.testifyproject.fixture.instrument.Greeter;
import org.testifyproject.fixture.instrument.GreeterInterceptor;

/**
 *
 * @author saden
 */
public class ByteBuddyTest {

    ByteBuddy sut;

    @Before
    public void init() {
        sut = new ByteBuddy();
    }

    @Test
    public void givenSubclassedTypeCallToModifiedGreetingShouldReturnUppercaseHello()
            throws Exception {
        GreeterInterceptor interceptor = new GreeterInterceptor();
        Greeter greeter = sut.subclass(Greeter.class)
                .method(not(isDeclaredBy(Object.class)))
                .intercept(MethodDelegation.withEmptyConfiguration()
                        .withBinders(TargetMethodAnnotationDrivenBinder.ParameterBinder.DEFAULTS)
                        .withBinders(Morph.Binder.install(InstrumentMorpher.class))
                        .withResolvers(MethodNameEqualityResolver.INSTANCE)
                        .withResolvers(BindingPriority.Resolver.INSTANCE)
                        .filter(not(isDeclaredBy(Object.class)))
                        .to(interceptor)
                )
                .make()
                .load(ByteBuddyTest.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded()
                .newInstance();

        String result = greeter.modifiedGreeting();
        assertThat(result).isEqualTo("HELLO");
    }

    @Test
    public void givenSubclassedTypeCallToUnmodifiedGreetingShouldReturnLowercaseHello()
            throws Exception {
        GreeterInterceptor interceptor = new GreeterInterceptor();
        Greeter greeter = sut.subclass(Greeter.class)
                .method(not(isDeclaredBy(Object.class)))
                .intercept(MethodDelegation.withEmptyConfiguration()
                        .withBinders(TargetMethodAnnotationDrivenBinder.ParameterBinder.DEFAULTS)
                        .withBinders(Morph.Binder.install(InstrumentMorpher.class))
                        .withResolvers(MethodNameEqualityResolver.INSTANCE)
                        .withResolvers(BindingPriority.Resolver.INSTANCE)
                        .filter(not(isDeclaredBy(Object.class)))
                        .to(interceptor)
                )
                .make()
                .load(ByteBuddyTest.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded()
                .newInstance();

        String result = greeter.unmodifiedGreeting();
        assertThat(result).isEqualTo("hello");
    }

}
