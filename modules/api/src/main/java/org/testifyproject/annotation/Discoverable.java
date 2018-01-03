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
package org.testifyproject.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * An annotation that can be placed on service implementation classes to add entries for the
 * service to the META-INF/services directory and allow the service to be discoverable through
 * the JDK's {@link java.util.ServiceLoader service-provider loading facility}.
 *
 * @author saden
 */
@Documented
@Retention(SOURCE)
@Target(TYPE)
public @interface Discoverable {

    /**
     * Specifies the contracts implemented by SPI contract. Note that if the value is not
     * specified entries for the interfaces implemented by the service will added to the
     * META-INF/services directory. If a value(s) are specified then entries for the specified
     * interfaces will be added to the META-INF/services directory.
     *
     * @return the interfaces implemented by service.
     */
    Class<?>[] value() default void.class;

}
