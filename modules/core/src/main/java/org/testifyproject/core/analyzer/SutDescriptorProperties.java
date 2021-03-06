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

/**
 * A class that defines test descriptor property keys.
 *
 * @author saden
 */
public class SutDescriptorProperties {

    /**
     * Sut Descriptor Constructor property key.
     */
    public static final String CONSTRUCTOR = "constructor";

    /**
     * Sut Descriptor Field Descriptor property key.
     */
    public static final String FIELD_DESCRIPTORS = "fieldDescriptors";

    /**
     * Sut Descriptor field descriptors cache property key.
     */
    public static final String FIELD_DESCRIPTORS_CACHE = "fieldDescriptorsCache";

    /**
     * Sut Descriptor Parameter Descriptor property key.
     */
    public static final String PARAMETER_DESCRIPTORS = "paramterDescriptors";
    /**
     * Sut Descriptor Parameter Descriptor cache property key.
     */
    public static final String PARAMETER_DESCRIPTORS_CACHE = "paramterDescriptorsCache";

    private SutDescriptorProperties() {
    }
}
