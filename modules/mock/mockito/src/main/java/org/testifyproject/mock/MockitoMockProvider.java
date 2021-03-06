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

import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.withSettings;

import org.mockito.AdditionalAnswers;
import org.testifyproject.MockProvider;
import org.testifyproject.annotation.Discoverable;

/**
 * A Mockito implementation of the {@link MockProvider} SPI contract.
 *
 * @author saden
 */
@Discoverable
public class MockitoMockProvider implements MockProvider {

    @Override
    public <T> T createFake(Class<? extends T> type) {
        return mock(type);
    }

    @Override
    public <T> T createVirtual(Class<? extends T> type, T delegate) {
        return mock(type, AdditionalAnswers.delegatesTo(delegate));
    }

    @Override
    public <T> T createVirtualSut(Class<? extends T> type, T delegate) {
        return mock(type, withSettings()
                .spiedInstance(delegate)
                .defaultAnswer(CALLS_REAL_METHODS));
    }

    @Override
    public void verifyAllInteraction(Object... collaborators) {
        verifyNoMoreInteractions(collaborators);
    }

    @Override
    public <T> Boolean isMock(T instance) {
        return mockingDetails(instance).isMock();
    }

}
