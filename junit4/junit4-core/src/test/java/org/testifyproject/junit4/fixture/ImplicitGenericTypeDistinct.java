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
package org.testifyproject.junit4.fixture;

import java.util.function.Supplier;
import org.testifyproject.junit4.fixture.collaborator.Hello;
import org.testifyproject.junit4.fixture.collaborator.World;

/**
 *
 * @author saden
 */
public class ImplicitGenericTypeDistinct {

    private final Supplier<Hello> hello;
    private final Supplier<World> world;

    ImplicitGenericTypeDistinct(Supplier<Hello> hello, Supplier<World> world) {
        this.hello = hello;
        this.world = world;
    }

    public String execute() {
        return hello.get().greet() + " " + world.get().greet();

    }

    public Supplier<Hello> getHello() {
        return hello;
    }

    public Supplier<World> getWorld() {
        return world;
    }

}
