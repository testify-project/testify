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

import static org.testify.guava.common.base.Preconditions.checkState;
import org.testify.InvokableDescriptor;
import org.testify.MethodDescriptor;
import org.testify.annotation.CollaboratorProvider;
import org.testify.annotation.ConfigHandler;
import java.util.Objects;
import java.util.Optional;
import static java.util.Optional.ofNullable;

/**
 * A class that encapsulating an instance object and a method descriptor. This
 * is useful for storing and executing a method on a specific object instance
 * (i.e. a configuration method or a method that provides a collaborator defined
 * in another class).
 *
 * @author saaden
 * @see ConfigHandler
 * @see CollaboratorProvider
 */
public class DefaultInvokableDescriptor implements InvokableDescriptor {

    private final MethodDescriptor methodDescriptor;
    private final Object instance;

    public DefaultInvokableDescriptor(MethodDescriptor methodDescriptor) {
        this(methodDescriptor, null);
    }

    public DefaultInvokableDescriptor(MethodDescriptor methodDescriptor, Object instance) {
        this.instance = instance;
        this.methodDescriptor = methodDescriptor;
    }

    @Override
    public Optional<Object> getInstance() {
        return ofNullable(instance);
    }

    @Override
    public MethodDescriptor getMethodDescriptor() {
        return methodDescriptor;
    }

    @Override
    public Optional<Object> invoke(Object... args) {
        checkState(instance != null, "Instance is null");
        return methodDescriptor.invoke(instance, args);
    }

    @Override
    public Optional<Object> invokeMethod(Object instance, Object... args) {
        return methodDescriptor.invoke(instance, args);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.instance);
        hash = 53 * hash + Objects.hashCode(this.methodDescriptor);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultInvokableDescriptor other = (DefaultInvokableDescriptor) obj;
        if (!Objects.equals(this.instance, other.instance)) {
            return false;
        }
        return Objects.equals(this.methodDescriptor, other.methodDescriptor);
    }

    @Override
    public String toString() {
        return "InvokableDescriptor{"
                + "instance=" + instance
                + ", methodDescriptor=" + methodDescriptor
                + '}';
    }

}
