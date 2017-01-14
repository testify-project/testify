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
package org.testify.di.fixture.common;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import org.testify.di.fixture.common.impl.Caio;
import org.testify.di.fixture.common.impl.Haye;
import org.testify.di.fixture.common.impl.Hello;

/**
 * Greeting module.
 *
 * @author saden
 */
public class GreetingModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Greeting.class).to(Hello.class).in(Singleton.class);
        bind(Greeting.class).annotatedWith(GreetingQualfier.class).to(Caio.class).in(Singleton.class);
        bind(Greeting.class).annotatedWith(Names.named("Haye")).to(Haye.class).in(Singleton.class);
        bind(Greeter.class).in(Singleton.class);
    }

}
