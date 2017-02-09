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

/**
 * A class that defines test descriptor property keys.
 *
 * @author saden
 */
public class TestDescriptorProperties {

    /**
     * Test Descriptor Application property key.
     */
    public static final String APPLICATION = "application";

    /**
     * Test Descriptor CollaboratorProvider property key.
     */
    public static final String COLLABORATOR_PROVIDER = "collaboratorProvider";

    /**
     * Test Descriptor Config Handlers property key.
     */
    public static final String CONFIG_HANDLERS = "configHandlers";

    /**
     * Test Descriptor Modules property key.
     */
    public static final String MODULES = "modules";

    /**
     * Test Descriptor Scans property key.
     */
    public static final String SCANS = "scans";

    /**
     * Test Descriptor Requires Containers property key.
     */
    public static final String REQUIRES_CONTAINERS = "requiresContainers";

    /**
     * Test Descriptor Requires Resources property key.
     */
    public static final String REQUIRES_RESOURCES = "requiresResources";

    /**
     * Test Descriptor cut field property key.
     */
    public static final String CUT_FIELD = "cutField";

    /**
     * Test Descriptor field descriptors property key.
     */
    public static final String FIELD_DESCRIPTORS = "fieldDescriptors";

    /**
     * Test Descriptor field descriptors cache property key.
     */
    public static final String FIELD_DESCRIPTORS_CACHE = "fieldDescriptorsCache";

    private TestDescriptorProperties() {
    }
}
