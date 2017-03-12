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

import java.util.concurrent.Callable;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.MethodNameEqualityResolver;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.BindingPriority;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.TargetMethodAnnotationDrivenBinder;
import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.not;
import org.junit.Test;

public class FallbackInterceptorST {

    @Test
    public void test() throws InstantiationException, IllegalAccessException {
        GreeterInterceptor interceptor = new GreeterInterceptor();
        Greeter greeter = new ByteBuddy().subclass(Greeter.class)
                .method(not(isDeclaredBy(Object.class)))
                .intercept(MethodDelegation.withEmptyConfiguration()
                        .withBinders(TargetMethodAnnotationDrivenBinder.ParameterBinder.DEFAULTS)
                        .withResolvers(MethodNameEqualityResolver.INSTANCE, BindingPriority.Resolver.INSTANCE)
                        .filter(not(isDeclaredBy(Object.class)))
                        .to(interceptor)
                )
                .make()
                .load(FallbackInterceptorST.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded()
                .newInstance();

        String result = greeter.greet();
    }

    public static class Greeter {

        public String hello() {
            return "hello";
        }

        public String world() {
            return "world";
        }

        public String greet() {
            return hello() + " " + world();
        }
    }

    public static class GreeterInterceptor {

        @RuntimeType
        @BindingPriority(Integer.MAX_VALUE)
        public Object _default(@SuperCall Callable<?> zuper, @AllArguments Object[] args) throws Exception {
            return zuper.call();
        }

        @RuntimeType
        public String hello(@SuperCall Callable<String> zuper) throws Exception {
            return zuper.call().toUpperCase();
        }

        @RuntimeType
        public String world(@SuperCall Callable<String> zuper) throws Exception {
            return zuper.call().toUpperCase();
        }

    }

}
