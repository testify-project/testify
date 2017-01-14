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

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import org.testify.FieldDescriptor;
import org.testify.InvokableDescriptor;
import org.testify.TestDescriptor;
import org.testify.annotation.Application;
import org.testify.annotation.Module;
import org.testify.annotation.RequiresContainer;
import org.testify.annotation.RequiresResource;
import org.testify.annotation.Scan;
import org.testify.guava.common.collect.ImmutableList;
import org.testify.guava.common.collect.ImmutableMap;

/**
 * A builder class that facilitates the construction of a {@link TestDescriptor}
 * instance.
 *
 * @author saden
 */
public class TestDescriptorBuilder {

    private final Class<?> testClass;
    private Application application;
    private Field cutField;
    private InvokableDescriptor collaboratorMethod;
    private final ImmutableList.Builder<InvokableDescriptor> configHandlers = ImmutableList.builder();
    private final ImmutableList.Builder<Module> modules = ImmutableList.builder();
    private final ImmutableList.Builder<RequiresContainer> containers = ImmutableList.builder();
    private final ImmutableList.Builder<RequiresResource> resources = ImmutableList.builder();
    private final ImmutableList.Builder<Scan> scans = ImmutableList.builder();
    private final ImmutableList.Builder<FieldDescriptor> fieldDescriptors = ImmutableList.builder();
    private final ImmutableMap.Builder<DescriptorKey, FieldDescriptor> fieldTypeCache = ImmutableMap.builder();
    private final ImmutableMap.Builder<DescriptorKey, FieldDescriptor> fieldCache = ImmutableMap.builder();

    public TestDescriptorBuilder(Class<?> testClass) {
        this.testClass = testClass;
    }

    public TestDescriptorBuilder application(Application application) {
        this.application = application;

        return this;
    }

    public TestDescriptorBuilder collaboratorMethod(InvokableDescriptor invokableDescriptor) {
        this.collaboratorMethod = invokableDescriptor;

        return this;
    }

    public TestDescriptorBuilder addConfigHandler(InvokableDescriptor invokableDescriptor) {
        this.configHandlers.add(invokableDescriptor);

        return this;
    }

    public TestDescriptorBuilder addModule(Module module) {
        this.modules.add(module);

        return this;
    }

    public TestDescriptorBuilder addRequiresContainer(RequiresContainer requiresContainer) {
        this.containers.add(requiresContainer);

        return this;
    }

    public TestDescriptorBuilder addRequiresResource(RequiresResource requiresResource) {
        this.resources.add(requiresResource);

        return this;
    }

    public TestDescriptorBuilder addScan(Scan scan) {
        this.scans.add(scan);

        return this;
    }

    public TestDescriptorBuilder addFieldDescriptor(FieldDescriptor descriptor) {
        Type type = descriptor.getGenericType();
        String name = descriptor.getName();

        this.fieldTypeCache.put(new DescriptorKey(type), descriptor);
        this.fieldCache.put(new DescriptorKey(type, name), descriptor);
        this.fieldDescriptors.add(descriptor);

        return this;
    }

    public TestDescriptorBuilder cutField(Field cutField) {
        this.cutField = cutField;

        return this;
    }

    public TestDescriptor build() {
        DefaultTestDescriptor testDescriptor = new DefaultTestDescriptor(testClass);

        testDescriptor.setApplication(application);
        testDescriptor.setCollaboratorMethod(collaboratorMethod);
        testDescriptor.setConfigHandlers(configHandlers.build());
        testDescriptor.setContainers(containers.build());
        testDescriptor.setCutField(cutField);
        testDescriptor.setFieldCache(fieldCache.build());
        testDescriptor.setModules(modules.build());
        testDescriptor.setResources(resources.build());
        testDescriptor.setScans(scans.build());
        testDescriptor.setFieldDescriptors(fieldDescriptors.build());

        return testDescriptor;
    }

}
