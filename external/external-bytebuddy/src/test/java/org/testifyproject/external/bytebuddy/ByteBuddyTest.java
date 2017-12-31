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
package org.testifyproject.external.bytebuddy;

import static org.assertj.core.api.Assertions.assertThat;

import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.not;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.bytebuddy.Morpher;
import org.testifyproject.external.fixture.Greeter;
import org.testifyproject.external.fixture.GreeterInterceptor;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.MethodNameEqualityResolver;
import net.bytebuddy.implementation.bind.annotation.BindingPriority;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.implementation.bind.annotation.TargetMethodAnnotationDrivenBinder;

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
                        .withBinders(Morph.Binder.install(Morpher.class))
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
                        .withBinders(Morph.Binder.install(Morpher.class))
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
