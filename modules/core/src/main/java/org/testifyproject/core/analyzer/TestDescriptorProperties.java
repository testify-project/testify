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
public class TestDescriptorProperties {

    /**
     * Test Descriptor Application property key.
     */
    public static final String APPLICATION = "application";

    /**
     * Test Descriptor Collaborator Provider annotation property key.
     */
    public static final String COLLABORATOR_PROVIDER = "collaboratorProvider";

    /**
     * Test Descriptor Collaborator Provider methods property key.
     */
    public static final String COLLABORATOR_PROVIDERS = "collaboratorProviders";

    /**
     * Test Descriptor Config Handlers annotation property key.
     */
    public static final String CONFIG_HANDLER = "configHandler";

    /**
     * Test Descriptor Config Handler methods property key.
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
     * Test Descriptor Local Resources property key.
     */
    public static final String LOCAL_RESOURCES = "localResources";

    /**
     * Test Descriptor Virtual Containers property key.
     */
    public static final String VIRTUAL_RESOURCES = "virtualResources";

    /**
     * Test Descriptor Remote Resources property key.
     */
    public static final String REMOTE_RESOURCES = "remoteResources";

    /**
     * Test Descriptor sut field property key.
     */
    public static final String SUT_FIELD = "sutField";

    /**
     * Test Descriptor field descriptors property key.
     */
    public static final String FIELD_DESCRIPTORS = "fieldDescriptors";

    /**
     * Test Descriptor field descriptors cache property key.
     */
    public static final String FIELD_DESCRIPTORS_CACHE = "fieldDescriptorsCache";

    /**
     * Inspected annotations cache property key.
     */
    public static final String INSPECTED_ANNOTATIONS = "inspectedAnnotations";

    /**
     * Guideline annotations cache property key.
     */
    public static final String GUIDELINE_ANNOTATIONS = "guidelineAnnotations";

    /**
     * Hint annotations cache property key.
     */
    public static final String HINT_ANNOTATION = "hintAnnotation";

    private TestDescriptorProperties() {
    }
}
