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
package org.testifyproject.di.fixture.dynamic;

import org.testifyproject.annotation.Fixture;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

/**
 *
 * @author saden
 */
@Fixture
public class FixtureModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(DynamicContract.class).to(DynamicService.class).in(Singleton.class);
    }

}
