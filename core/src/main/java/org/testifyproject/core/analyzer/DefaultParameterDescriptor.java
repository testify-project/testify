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

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import org.testifyproject.ParameterDescriptor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A descriptor class used to access or perform operations on a system under test (SUT)
 * constructor parameters.
 *
 * @author saden
 */
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true)
public class DefaultParameterDescriptor implements ParameterDescriptor {

    private final Parameter parameter;
    private final Integer index;

    DefaultParameterDescriptor(Parameter parameter, Integer index) {
        this.parameter = parameter;
        this.index = index;
    }

    /**
     * Create a new parameter descriptor instance from the given parameter and index.
     *
     * @param parameter the parameter instance
     * @param index the parameter index
     * @return a parameter descriptor instance
     */
    public static ParameterDescriptor of(Parameter parameter, Integer index) {
        return new DefaultParameterDescriptor(parameter, index);
    }

    @Override
    public Parameter getAnnotatedElement() {
        return parameter;
    }

    @Override
    public String getName() {
        return parameter.getName();
    }

    @Override
    public Integer getIndex() {
        return index;
    }

    @Override
    public Class<?> getType() {
        return parameter.getType();
    }

    @Override
    public Type getGenericType() {
        return parameter.getParameterizedType();
    }

}
