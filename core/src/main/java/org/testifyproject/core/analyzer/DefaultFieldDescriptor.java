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
package org.testifyproject.core.analyzer;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.testifyproject.FieldDescriptor;
import org.testifyproject.annotation.Fake;
import org.testifyproject.annotation.Real;
import org.testifyproject.annotation.Virtual;

/**
 * A descriptor class used to access properties of or perform operations on an analyzed test class
 * or the system under test fields. getF
 *
 * @author saden
 */
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true)
public class DefaultFieldDescriptor implements FieldDescriptor {

    private final Field field;

    DefaultFieldDescriptor(Field field) {
        this.field = field;
    }

    /**
     * Create a new field descriptor instance from the given field.
     *
     * @param field the underlying field
     * @return a field descriptor instance
     */
    public static FieldDescriptor of(Field field) {
        return new DefaultFieldDescriptor(field);
    }

    @Override
    public Field getMember() {
        return field;
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }

    @Override
    public Type getGenericType() {
        return field.getGenericType();
    }

    @Override
    public String getDefinedName() {
        String name = "";
        Optional<Fake> foundFake = getFake();
        Optional<Real> foundReal = getReal();
        Optional<Virtual> foundVirtual = getVirtual();

        if (foundFake.isPresent()) {
            name = foundFake.get().value();
        } else if (foundReal.isPresent()) {
            name = foundReal.get().value();
        } else if (foundVirtual.isPresent()) {
            name = foundVirtual.get().value();
        }

        //if name has not been spcified then use the field name
        if ("".equals(name)) {
            name = field.getName();
        }

        return name;
    }

}
