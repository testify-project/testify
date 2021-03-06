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

import java.util.Map;
import java.util.UUID;

/**
 *
 * @author saden
 */
public class AnalyzedSutClass {

    private final Map<UUID, String> store;

    public static final AnalyzedSutClass builder(Map<UUID, String> store) {
        return new AnalyzedSutClass(store);
    }

    public AnalyzedSutClass() {
        this.store = null;
    }

    AnalyzedSutClass(Map<UUID, String> store) {
        this.store = store;
    }

    public void save(String message) {

    }

}
