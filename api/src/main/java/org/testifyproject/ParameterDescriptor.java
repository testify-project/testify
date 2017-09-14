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
package org.testifyproject;

import java.lang.reflect.Parameter;

import org.testifyproject.trait.AnnotationTrait;
import org.testifyproject.trait.TypeTrait;

/**
 * A contract that defines methods to access properties of or perform operations on a system under
 * test (SUT) constructor parameters.
 *
 * @author saden
 */
public interface ParameterDescriptor extends AnnotationTrait<Parameter>, TypeTrait {

    /**
     * Get the index of the parameter.
     *
     * @return the parameter index
     */
    Integer getIndex();

    /**
     * Get the parameter name.
     *
     * @return the parameter name.
     */
    String getName();

}
