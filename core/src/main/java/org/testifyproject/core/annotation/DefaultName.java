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
import org.testifyproject.annotation.Name;

/**
 * An an implementation of {@link Name} annotation for testing purpose.
 *
 * @author saden
 */
@SuppressWarnings("AnnotationAsSuperInterface")
public class DefaultName implements Name {

    private final String value;

    public DefaultName(String value) {
        this.value = value;
    }

    /**
     * Create a new instance of Name with the given value.
     *
     * @param value the name
     * @return a new Name instance
     */
    public static Name of(String value) {
        return new DefaultName(value);
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Name.class;
    }

}
