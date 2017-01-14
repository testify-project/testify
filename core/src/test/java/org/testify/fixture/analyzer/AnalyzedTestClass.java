/*
 * Copyright 2016-2017 Sharmarke Aden.
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
package org.testify.fixture.analyzer;

import java.util.Map;
import java.util.UUID;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import org.testify.annotation.CollaboratorProvider;
import org.testify.annotation.ConfigHandler;
import org.testify.annotation.Cut;
import org.testify.annotation.Fake;
import org.testify.annotation.Module;
import org.testify.annotation.Scan;

/**
 *
 * @author saden
 */
@Scan("org.testify.fixture.analyzer")
@Module(AnalyzedModule.class)
@AnalyzedGroup
@ConfigHandler(AnalyzedConfigHandler.class)
public class AnalyzedTestClass {

    @Cut
    AnalyzedCutClass cut;

    @Fake
    Map<UUID, String> store;

    @CollaboratorProvider
    Object[] collaborators() {
        return new Object[]{mock(Map.class)};
    }

    @ConfigHandler
    Object config(Object config) {
        return new Object();
    }

    @Test
    public void givenMessageSaveShouldPersistMessage() {

    }

}
