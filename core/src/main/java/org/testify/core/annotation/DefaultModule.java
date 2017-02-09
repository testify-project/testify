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
package org.testify.core.annotation;

import java.lang.annotation.Annotation;
import org.testify.annotation.Module;

/**
 * An an implementation of {@link Module} annotation for testing purpose.
 *
 * @author saden
 */
@SuppressWarnings("AnnotationAsSuperInterface")
public class DefaultModule implements Module {

    private final Class<?> value;

    /**
     * Create a new instance of Module with the given value.
     *
     * @param value the module class
     * @return a new module instance
     */
    public static Module of(Class<?> value) {
        return new DefaultModule(value);
    }

    public DefaultModule(Class<?> value) {
        this.value = value;
    }

    @Override
    public Class<?> value() {
        return value;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Module.class;
    }

}
