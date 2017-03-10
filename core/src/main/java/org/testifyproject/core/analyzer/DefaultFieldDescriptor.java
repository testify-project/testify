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
import java.util.Objects;
import org.testifyproject.FieldDescriptor;

/**
 * A descriptor class used to access properties of or perform operations on an
 * analyzed test class or the class under test fields. getF
 *
 * @author saden
 */
public class DefaultFieldDescriptor implements FieldDescriptor {

    private final Field field;

    public static FieldDescriptor of(Field field) {
        return new DefaultFieldDescriptor(field);
    }

    DefaultFieldDescriptor(Field field) {
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

        if (getFake().isPresent()) {
            name = getFake().get().value();
        } else if (getReal().isPresent()) {
            name = getReal().get().value();
        } else if (getVirtual().isPresent()) {
            name = getVirtual().get().value();
        }

        //if name has not been spcified then use the field name
        if (name == null || "".equals(name)) {
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
        return "DefaultFieldDescriptor{" + "field=" + field + '}';
    }

}
