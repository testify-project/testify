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
package org.testifyproject.core.extension.instrument;

import org.testifyproject.extension.InstrumentInstance;

/**
 * A builder class used to construction {@link InstrumentInstance} instances.
 *
 * @author saden
 */
public class InstrumentInstanceBuilder {

    boolean constructor = false;

    public InstrumentInstanceBuilder constructor() {
        this.constructor = true;

        return this;
    }

    /**
     * Create a new instance of InstrumentInstanceBuilder.
     *
     * @return a new instrumentation instance builder
     */
    public static InstrumentInstanceBuilder builder() {
        return new InstrumentInstanceBuilder();
    }

    /**
     * Build and return an instrumented instance based builder state and given className and
     * interceptor.
     *
     * @param className the fully qualified class name of the class to be instrumented
     * @param interceptor the interceptor associated instrumented instance
     * @return an instrumentation instance
     */
    public InstrumentInstance build(String className, Object interceptor) {
        return DefaultInstrumentInstance.of(className, constructor, interceptor);
    }

}
