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
package org.testifyproject.junit.fixture;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import org.testifyproject.junit.fixture.common.CustomQualifier;
import org.testifyproject.junit.fixture.common.Greeting;
import org.testifyproject.junit.fixture.common.impl.Caio;
import org.testifyproject.junit.fixture.common.impl.Haye;
import org.testifyproject.junit.fixture.common.impl.Hello;
import org.testifyproject.junit.fixture.service.Greeter;
import org.testifyproject.junit.fixture.service.NamedGreeter;
import org.testifyproject.junit.fixture.service.QualifiedGreeter;

/**
 * Greeting module.
 *
 * @author saden
 */
public class GreetingModule extends AbstractModule {

    @Override
    protected void configure() {
        Multibinder<Greeting> setBinder = Multibinder.newSetBinder(binder(), Greeting.class);
        setBinder.addBinding().to(Hello.class).in(Singleton.class);
        setBinder.addBinding().to(Caio.class).in(Singleton.class);
        setBinder.addBinding().to(Haye.class).in(Singleton.class);

        MapBinder<String, Greeting> mapBinder = MapBinder.newMapBinder(binder(), String.class, Greeting.class);
        mapBinder.addBinding(Hello.class.getSimpleName()).to(Hello.class).in(Singleton.class);
        mapBinder.addBinding(Caio.class.getSimpleName()).to(Caio.class).in(Singleton.class);
        mapBinder.addBinding(Haye.class.getSimpleName()).to(Haye.class).in(Singleton.class);

        bind(Greeting.class).to(Hello.class).in(Singleton.class);
        bind(Greeting.class).annotatedWith(CustomQualifier.class).to(Caio.class).in(Singleton.class);
        bind(Greeting.class).annotatedWith(Names.named("Haye")).to(Haye.class).in(Singleton.class);

        bind(Greeter.class).in(Singleton.class);
        bind(NamedGreeter.class).in(Singleton.class);
        bind(QualifiedGreeter.class).in(Singleton.class);

    }

}
