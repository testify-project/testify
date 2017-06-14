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
package org.testifyproject.mock;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.stream.IntStream;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import org.easymock.cglib.proxy.Factory;
import org.testifyproject.MockProvider;
import org.testifyproject.tools.Discoverable;

/**
 * A EasyMock implementation of the {@link MockProvider} SPI contract.
 *
 * @author saden
 */
@Discoverable
public class EasyMockMockProvider implements MockProvider {

    @Override
    public <T> T createFake(Class<? extends T> type) {
        return EasyMock.createMock(type);
    }

    @Override
    public <T> T createVirtual(Class<? extends T> type, T delegate) {
        try {
            T instance = createMock(type);
            Method[] methods = delegate.getClass().getDeclaredMethods();

            for (Method method : methods) {
                if (Modifier.isStatic(method.getModifiers())) {
                    continue;
                }

                Object[] params = IntStream.range(0, method.getParameterCount())
                        .mapToObj(p -> EasyMock.anyObject())
                        .toArray(Object[]::new);

                expect(method.invoke(instance, params))
                        .andDelegateTo(delegate);
            }

            replay(instance);

            return instance;
        } catch (IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException e) {
            throw new IllegalArgumentException("Could not delegate method calls", e);
        }
    }

    @Override
    public void verifyAllInteraction(Object... collaborators) {
        verify(collaborators);
    }

    @Override
    public <T> Boolean isMock(T instance) {
        return instance instanceof Factory;
    }

}
