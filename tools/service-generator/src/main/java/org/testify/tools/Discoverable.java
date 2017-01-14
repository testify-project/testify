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
package org.testify.tools;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * An annotation that can be placed on service implementation classes to make
 * them eligible for META-INF/services directory entry and discoverable through
 * the JDK's {@link java.util.ServiceLoader service-provider loading facility}.
 *
 * @author saden
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface Discoverable {

    /**
     * <p>
     * Specifies the contracts implemented by SPI contract.
     * </p>
     * <p>
     * Note that if the value is not set all the contracts implemented by the
     * service will be used and entries for these contracts will be added to the
     * META-INF/services directory. If you do specify the value then no
     * detection will be performed the contracts specified used instead.
     * </p>
     *
     * @return the contracts implemented by
     */
    Class<?>[] value() default void.class;

}
