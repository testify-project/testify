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
package org.testifyproject.core.annotation;

import java.lang.annotation.Annotation;

import org.testifyproject.annotation.Module;

/**
 * An an implementation of {@link Module} annotation for testing purpose.
 *
 * @author saden
 */
@SuppressWarnings("AnnotationAsSuperInterface")
public class DefaultModule implements Module {

    private final Class<?> value;
    private final boolean test;

    DefaultModule(Class<?> value, boolean test) {
        this.value = value;
        this.test = test;
    }

    /**
     * Create a new instance of Module with the given value.
     *
     * @param value the module class
     * @return a new module instance
     */
    public static Module of(Class<?> value) {
        return new DefaultModule(value, false);
    }

    /**
     * Create a new instance of Module with the given value.
     *
     * @param value the module class
     * @param test true if the module is for testing purpose, false otherwise
     * @return a new module instance
     */
    public static Module of(Class<?> value, boolean test) {
        return new DefaultModule(value, test);
    }

    @Override
    public Class<?> value() {
        return value;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Module.class;
    }

    @Override
    public boolean test() {
        return test;
    }

}
