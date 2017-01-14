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
package org.testify;

import org.testify.trait.AnnotationTrait;
import org.testify.trait.TypeTrait;
import java.lang.reflect.Parameter;

/**
 * A contract that defines methods to access properties of or perform operations
 * on a class under test (CUT) constructor parameters.
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

    /**
     * Get the underlying parameter object.
     *
     * @return the underlying parameter object.
     */
    Parameter getParameter();

}
