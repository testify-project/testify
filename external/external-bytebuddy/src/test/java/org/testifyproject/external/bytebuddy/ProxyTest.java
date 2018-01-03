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

import static java.lang.String.format;

import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.utility.RandomString;

/**
 *
 * @author saden
 */
public class ProxyTest {

    @Test
    public void testSomeMethod() throws InstantiationException, IllegalAccessException {
        Class<ProxyedInterface> type = ProxyedInterface.class;
        ClassLoader classLoader = getClass().getClassLoader();

        Class<? extends ProxyedInterface> proxyType = new ByteBuddy(ClassFileVersion.JAVA_V8)
                .with(new ProxyNamingStrategy(type))
                .subclass(type)
                .method(isDeclaredBy(type))
                .intercept(MethodDelegation.to(new GeneralInterceptor(
                        new ProxyedInterfaceDelegate(10L))))
                .make()
                .load(classLoader, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();

        ProxyedInterface result = proxyType.newInstance();
        result.setObject(11l);
        Long f = result.getObject();

        System.out.println("");

    }

    public static interface ProxyedInterface {

        void setObject(Long value);

        Long getObject();

    }

    public static class ProxyedInterfaceDelegate implements ProxyedInterface {

        private Long value;

        public ProxyedInterfaceDelegate(Long value) {
            this.value = value;
        }

        @Override
        public void setObject(Long value) {
            this.value = value;
        }

        @Override
        public Long getObject() {
            return value;
        }

    }

    public class GeneralInterceptor {

        private final ProxyedInterface delegate;

        public GeneralInterceptor(ProxyedInterface delegate) {
            this.delegate = delegate;
        }

        @RuntimeType
        public Object intercept(@AllArguments Object[] allArguments, @Origin Method method) {
            try {
                System.out.println("Intercepted method: " + method.getName());
                return method.invoke(delegate, allArguments);
            } catch (IllegalAccessException | IllegalArgumentException |
                    InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static class ProxyNamingStrategy implements NamingStrategy {

        private final Class<?> type;

        public ProxyNamingStrategy(Class<?> type) {
            this.type = type;
        }

        @Override
        public String subclass(TypeDescription.Generic superClass) {
            return getProxyName();
        }

        @Override
        public String redefine(TypeDescription typeDescription) {
            return getProxyName();
        }

        @Override
        public String rebase(TypeDescription typeDescription) {
            return getProxyName();
        }

        String getProxyName() {
            String simpleName = type.getSimpleName();
            return format("org.testifyproject.proxy.%s_%s", simpleName, RandomString.make());
        }

    }

}
