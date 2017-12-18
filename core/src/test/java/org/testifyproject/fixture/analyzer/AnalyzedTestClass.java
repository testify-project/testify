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
package org.testifyproject.fixture.analyzer;

import static org.mockito.Mockito.mock;

import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.testifyproject.annotation.CollaboratorProvider;
import org.testifyproject.annotation.ConfigHandler;
import org.testifyproject.annotation.Fake;
import org.testifyproject.annotation.Module;
import org.testifyproject.annotation.Scan;
import org.testifyproject.annotation.Sut;

/**
 *
 * @author saden
 */
@Scan("org.testifyproject.fixture.analyzer")
@Module(AnalyzedModule.class)
@AnalyzedBundle
@ConfigHandler(AnalyzedConfigHandler.class)
public class AnalyzedTestClass {

    @Sut
    AnalyzedSutClass sut;

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
    public void verifyTest() {

    }

}
