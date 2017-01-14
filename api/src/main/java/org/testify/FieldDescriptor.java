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

import org.testify.trait.FieldTrait;
import org.testify.trait.MockTrait;

/**
 * A contract that defines methods to access properties of or perform operations
 * on a field.
 *
 * @author saden
 */
public interface FieldDescriptor extends FieldTrait, MockTrait {

    /**
     * <p>
     * Get the explicitly defined field name. Please note that field names are
     * explicitly defined by annotating the field with and specifying:
     * </p>
     * <ul>
     * <li>{@link org.testify.annotation.Fake#value()}</li>
     * <li>{@link org.testify.annotation.Real#value()}</li>
     * <li>{@link org.testify.annotation.Virtual#value()}</li>
     * </ul>
     * <p>
     * In the event a field name is not explicitly defined the value returned by
     * calling {@link java.lang.reflect.Field#getName()} will be returned.
     * </p>
     *
     * @return the field name
     */
    String getDefinedName();

}
