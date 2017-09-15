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

import org.testifyproject.annotation.Scan;

/**
 * An an implementation of {@link Scan} annotation for testing purpose.
 *
 * @author saden
 */
@SuppressWarnings("AnnotationAsSuperInterface")
public class DefaultScan implements Scan {

    private final String value;

    public DefaultScan(String value) {
        this.value = value;
    }

    /**
     * Create a new instance of Scan with the given value.
     *
     * @param value the resource in the classpath
     * @return a new scan instance
     */
    public static Scan of(String value) {
        return new DefaultScan(value);
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Scan.class;
    }

}
