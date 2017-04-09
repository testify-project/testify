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
package org.testifyproject.core.analyzer.inspector;

import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.VirtualResource;
import org.testifyproject.annotation.VirtualResources;
import org.testifyproject.core.analyzer.TestDescriptorProperties;
import org.testifyproject.extension.AnnotationInspector;
import org.testifyproject.extension.annotation.Handles;
import org.testifyproject.tools.Discoverable;

/**
 * An annotation inspector that processes {@link VirtualResources} annotations.
 *
 * @author saden
 */
@Discoverable
@Handles(VirtualResources.class)
public class VirtualResourcesInspector implements AnnotationInspector<VirtualResources> {

    @Override
    public void inspect(TestDescriptor testDescriptor, Class<?> annotatedType, VirtualResources annotation) {
        for (VirtualResource virtualResource : annotation.value()) {
            testDescriptor.addListElement(TestDescriptorProperties.VIRTUAL_RESOURCES, virtualResource);
        }
    }

}
