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
package org.testifyproject.di.guice;

import java.util.Queue;

import org.testifyproject.Instance;
import org.testifyproject.guava.common.collect.Queues;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.name.Names;

/**
 * A wrapper class for dynamically registering constants.
 *
 * @author saden
 */
public class GuiceAbstractModule extends AbstractModule {

    private final Queue<Instance> instances;

    GuiceAbstractModule(Queue<Instance> instances) {
        this.instances = instances;
    }

    /**
     * Create an instance of GuiceAbstractModule using the given queue of instances.
     *
     * @param instances the instances
     * @return a module instance
     */
    public static final Module of(Queue<Instance> instances) {
        return new GuiceAbstractModule(instances);
    }

    /**
     * Create an instance of GuiceAbstractModule using the given queue of instances.
     *
     * @param instance the instance
     * @return a module instance
     */
    public static final Module of(Instance instance) {
        Queue<Instance> instances = Queues.newConcurrentLinkedQueue();
        instances.add(instance);
        return new GuiceAbstractModule(instances);
    }

    @Override
    protected void configure() {
        while (instances.peek() != null) {
            Instance constant = instances.poll();
            Object instance = constant.getValue();
            Class instanceType = instance.getClass();
            String name = constant.getName();
            Class contract = constant.getContract();

            if (name != null && contract != null) {
                bind(instanceType).annotatedWith(Names.named(name)).toInstance(instance);
                bind(contract).annotatedWith(Names.named(name)).toInstance(instance);
                bind(contract).toInstance(instance);
            } else if (name != null) {
                bind(instanceType).annotatedWith(Names.named(name)).toInstance(instance);
            } else if (contract != null) {
                bind(contract).toInstance(instance);
            } else {
                for (Class instanceInterface : instanceType.getInterfaces()) {
                    bind(instanceInterface).toInstance(instance);
                }
            }

            bind(instanceType).toInstance(instance);
        }
    }

}
