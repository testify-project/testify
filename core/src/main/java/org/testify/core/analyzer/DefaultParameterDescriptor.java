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

import org.testify.ParameterDescriptor;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * A descriptor class used to access or perform operations on a class under test
 * (CUT) constructor parameters.
 *
 * @author saden
 */
public class DefaultParameterDescriptor implements ParameterDescriptor {

    private final Parameter parameter;
    private final Integer index;

    public DefaultParameterDescriptor(Parameter parameter, Integer index) {
        this.parameter = parameter;
        this.index = index;
    }

    @Override
    public String getName() {
        return parameter.getName();
    }

    @Override
    public Class<?> getType() {
        return parameter.getType();
    }

    @Override
    public Type getGenericType() {
        return parameter.getParameterizedType();
    }

    @Override
    public Parameter getAnnotatedElement() {
        return parameter;
    }

    @Override
    public Parameter getParameter() {
        return parameter;
    }

    @Override
    public Integer getIndex() {
        return index;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.parameter);
        hash = 59 * hash + Objects.hashCode(this.index);
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
        final DefaultParameterDescriptor other = (DefaultParameterDescriptor) obj;
        if (!Objects.equals(this.parameter, other.parameter)) {
            return false;
        }

        return Objects.equals(this.index, other.index);
    }

    @Override
    public String toString() {
        return "ParameterDescriptor{"
                + "parameter=" + parameter
                + ", index=" + index
                + '}';
    }

}
