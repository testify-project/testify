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
package org.testifyproject.junit4.system;

import java.util.Queue;

import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.internal.inject.Binder;
import org.testifyproject.Instance;

/**
 * A wrapper class for dynamically registering constants.
 *
 * @author saden
 */
public class JerseyAbstractBinder extends AbstractBinder {

    private final Queue<Instance<?>> constants;

    JerseyAbstractBinder(Queue<Instance<?>> constants) {
        this.constants = constants;
    }

    /**
     * Create an instance of JerseyAbstractBinder using the given queue of constants.
     *
     * @param constants the constants
     * @return a module instance
     */
    public static final Binder of(Queue<Instance<?>> constants) {
        return new JerseyAbstractBinder(constants);
    }

    @Override
    protected void configure() {
        while (constants.peek() != null) {
            Instance constant = constants.poll();
            Object instance = constant.getValue();
            Class instanceType = instance.getClass();
            String name = constant.getName();
            Class contract = constant.getContract();

            if (name != null && contract != null) {
                bind(instance).named(name).to(contract);
                bind(instance).to(contract);
            } else if (name != null) {
                bind(instance).named(name).to(instanceType);
            } else if (contract != null) {
                bind(instance).to(contract);
            } else {
                for (Class instanceInterface : instanceType.getInterfaces()) {
                    bind(instance).to(instanceInterface);
                }
            }

            bind(instance).to(instanceType);
        }
    }

}
