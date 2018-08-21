/*
 * Copyright 2016-2018 Testify Project.
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
package org.testifyproject.junit5;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testifyproject.core.TestCategory;
import org.testifyproject.core.setting.TestSettingsBuilder;
import org.testifyproject.core.setting.TestSettingsProperties;

/**
 * TODO.
 *
 * @author saden
 */
public class IntegrationTestExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        ExtensionContext.Namespace namespace = create(TestifyExtension.class);
        ExtensionContext.Store store = context.getStore(namespace);

        store.put(
                TestSettingsProperties.class,
                TestSettingsBuilder.builder()
                        .level(TestCategory.Level.INTEGRATION)
                        .build()
        );
    }

}
