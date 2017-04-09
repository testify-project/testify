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
package org.testifyproject.trait;

import java.lang.reflect.Field;
import static java.security.AccessController.doPrivileged;
import java.security.PrivilegedAction;
import java.util.Optional;
import static java.util.Optional.ofNullable;
import org.testifyproject.TestifyException;
import org.testifyproject.annotation.Fixture;

/**
 * A contract that specifies field traits.
 *
 * @author saden
 */
public interface FieldTrait extends TypeTrait, MemberTrait<Field>, AnnotationTrait<Field> {

    /**
     * Get the field as the annotated element.
     *
     * @return the underlying field instance.
     */
    @Override
    default Field getAnnotatedElement() {
        return getMember();
    }

    /**
     * Set the field value for the given instance to the given value.
     *
     * @param instance the instance whose field should be modified
     * @param value the new value for the field of instance being modified
     */
    default void setValue(Object instance, Object value) {
        doPrivileged((PrivilegedAction<Object>) () -> {
            try {
                Field field = getAnnotatedElement();
                field.setAccessible(true);
                field.set(instance, value);

                return null;
            } catch (SecurityException
                    | IllegalAccessException
                    | IllegalArgumentException e) {
                throw TestifyException.of(e);
            }
        });
    }

    /**
     * Get the field value for the given instance.
     *
     * @param <T> the value type
     * @param instance the instance whose field value will be retrieved
     * @return an optional with the field value, empty optional otherwise
     */
    default <T> Optional<T> getValue(Object instance) {
        return doPrivileged((PrivilegedAction<Optional<T>>) () -> {
            try {
                Field field = getAnnotatedElement();
                field.setAccessible(true);

                return ofNullable((T) field.get(instance));
            } catch (SecurityException
                    | IllegalAccessException
                    | IllegalArgumentException e) {
                throw TestifyException.of(e);
            }
        });
    }

    /**
     * Initialize the field for the given instance if it is annotated with
     * {@link Fixture}.
     *
     * @param instance the instance whose field value will be retrieved
     */
    default void init(Object instance) {
        Optional<Fixture> fixtureAnnotaiton = getAnnotation(Fixture.class);

        if (fixtureAnnotaiton.isPresent()) {
            Fixture fixture = fixtureAnnotaiton.get();
            Optional<Object> fieldValue = getValue(instance);
            if (fieldValue.isPresent()) {
                Object value = fieldValue.get();
                String init = fixture.init();

                if (!init.isEmpty()) {
                    invoke(value, init);
                }
            }
        }
    }

    /**
     * Destroy the field for the given instance if it is annotated with
     * {@link Fixture}.
     *
     * @param instance the instance whose field value will be retrieved
     */
    default void destroy(Object instance) {
        Optional<Fixture> fixtureAnnotaiton = getAnnotation(Fixture.class);

        if (fixtureAnnotaiton.isPresent()) {
            Fixture fixture = fixtureAnnotaiton.get();
            Optional<Object> fieldValue = getValue(instance);
            if (fieldValue.isPresent()) {
                Object value = fieldValue.get();
                String destroyMethod = fixture.destroy();

                if (!destroyMethod.isEmpty()) {
                    invoke(value, destroyMethod);
                } else if (fixture.autoClose() && value instanceof AutoCloseable) {
                    invoke(value, "close");
                }
            }
        }
    }

}
