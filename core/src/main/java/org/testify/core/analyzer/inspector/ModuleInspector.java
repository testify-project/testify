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
package org.testify.core.analyzer.inspector;

import org.testify.TestDescriptor;
import org.testify.annotation.Module;
import org.testify.core.analyzer.TestAnnotationInspector;
import org.testify.core.analyzer.TestDescriptorProperties;
import org.testify.tools.Discoverable;

/**
 * An annotation inspector that processes {@link Module} annotations.
 *
 * @author saden
 */
@Discoverable
public class ModuleInspector implements TestAnnotationInspector<Module> {

    @Override
    public boolean handles(Class<?> annotationType) {
        return Module.class.isAssignableFrom(annotationType);
    }

    @Override
    public void inspect(TestDescriptor testDescriptor, Class<?> annotatedType, Module module) throws Exception {
        testDescriptor.addListElement(TestDescriptorProperties.MODULES, module);
    }

}
