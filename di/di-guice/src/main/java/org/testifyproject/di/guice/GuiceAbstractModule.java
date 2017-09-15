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

import java.util.Optional;
import java.util.Queue;

import org.testifyproject.Instance;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.name.Names;

/**
 * A wrapper class for dynamically registering constants.
 *
 * @author saden
 */
public class GuiceAbstractModule extends AbstractModule {

    private final Queue<Instance> constants;

    GuiceAbstractModule(Queue<Instance> constants) {
        this.constants = constants;
    }

    /**
     * Create an instance of GuiceAbstractModule using the given queue of constants.
     *
     * @param constants the constants
     * @return a module instance
     */
    public static final Module of(Queue<Instance> constants) {
        return new GuiceAbstractModule(constants);
    }

    @Override
    protected void configure() {
        while (constants.peek() != null) {
            Instance constant = constants.poll();
            Object instance = constant.getValue();
            Class instanceType = instance.getClass();
            Optional<String> name = constant.getName();
            Optional<Class> contract = constant.getContract();

            if (name.isPresent() && contract.isPresent()) {
                Class contractType = contract.get();
                String instanceName = name.get();

                bind(instanceType).annotatedWith(Names.named(instanceName)).toInstance(
                        instance);
                bind(contractType).annotatedWith(Names.named(instanceName)).toInstance(
                        instance);
                bind(contractType).toInstance(instance);
            } else if (name.isPresent()) {
                String instanceName = name.get();

                bind(instanceType).annotatedWith(Names.named(instanceName)).toInstance(
                        instance);
            } else if (contract.isPresent()) {
                Class contractType = contract.get();

                bind(contractType).toInstance(instance);
            } else {
                for (Class instanceInterface : instanceType.getInterfaces()) {
                    bind(instanceInterface).toInstance(instance);
                }
            }

            bind(instanceType).toInstance(instance);
        }
    }

}
