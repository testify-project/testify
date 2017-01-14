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
package org.testify.core.analyzer;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;
import org.testify.FieldDescriptor;
import org.testify.annotation.Fake;
import org.testify.annotation.Real;
import org.testify.annotation.Virtual;

/**
 * A descriptor class used to access properties of or perform operations on an
 * analyzed test class or the class under test fields. getF
 *
 * @author saden
 */
public class DefaultFieldDescriptor implements FieldDescriptor {

    private final Field field;

    public DefaultFieldDescriptor(Field field) {
        this.field = field;
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
        String name = null;
        Optional<Fake> fake = getFake();
        Optional<Real> real = getReal();
        Optional<Virtual> virtual = getVirtual();

        if (fake.isPresent()) {
            name = fake.get().value();
        } else if (real.isPresent()) {
            name = real.get().value();
        } else if (virtual.isPresent()) {
            name = virtual.get().value();
        }

        if ("".equals(name)) {
            name = field.getName();
        }

        return name;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.field);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultFieldDescriptor other = (DefaultFieldDescriptor) obj;

        return Objects.equals(this.field, other.field);
    }

    @Override
    public String toString() {
        return "FieldDescriptor{" + "field=" + field + '}';
    }

}
