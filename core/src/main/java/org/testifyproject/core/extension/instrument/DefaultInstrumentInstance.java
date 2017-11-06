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

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A default implementation of {@link InstrumentInstance}.
 *
 * @author saden
 */
@ToString
@EqualsAndHashCode
public class DefaultInstrumentInstance implements InstrumentInstance {

    private final String className;
    private final Object interceptor;

    DefaultInstrumentInstance(String className, Object interceptor) {
        this.className = className;
        this.interceptor = interceptor;
    }

    /**
     * Create an instrumented instance using the given parameters.
     *
     * @param className the name of the class that will be instrumented
     * @param interceptor the interceptor that will be used
     * @return an instrumentation instance
     */
    public static InstrumentInstance of(String className, Object interceptor) {
        return new DefaultInstrumentInstance(className, interceptor);
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public Object getInterceptor() {
        return interceptor;
    }

}
