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
package org.testifyproject.junit4.fixture.need.database;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.SynchronizationType;

/**
 *
 * @author saden
 */
public class EntityManagerProvider implements Provider<EntityManager> {

    private final EntityManagerFactory factory;

    @Inject
    EntityManagerProvider(EntityManagerFactory factory) {
        this.factory = factory;
    }

    @Override
    public EntityManager get() {
        return factory.createEntityManager(SynchronizationType.SYNCHRONIZED);
    }

}
