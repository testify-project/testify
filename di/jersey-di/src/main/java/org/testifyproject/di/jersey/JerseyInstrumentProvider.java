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
package org.testifyproject.di.jersey;

import org.testifyproject.annotation.Discoverable;
import org.testifyproject.core.extension.instrument.InstrumentInstanceBuilder;
import org.testifyproject.extension.InstrumentInstance;
import org.testifyproject.extension.InstrumentProvider;

/**
 * An implementation of InstrumentProvider contract that configures rebasing and interception of
 * HK2 Service locator operations.
 *
 * @author saden
 */
@Discoverable
public class JerseyInstrumentProvider implements InstrumentProvider {

    @Override
    public InstrumentInstance get() {
        return InstrumentInstanceBuilder.builder()
                .build("org.glassfish.jersey.inject.hk2.Hk2InjectionManagerFactory",
                        new JerseyInterceptor());
    }

}
