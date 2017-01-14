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
package org.testify.junit.fixture.common;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.PerThread;
import org.glassfish.hk2.api.UseProxy;
import org.jvnet.hk2.annotations.Service;

/**
 * A provider that provides an {@link EntityManagerFactory} based on a
 * DataSource and persistence.xml in the classpath.
 *
 * @author saden
 */
@Service
public class EntityManagerProvider implements Factory<EntityManager> {

    private final EntityManagerFactory entityManagerFactory;

    @Inject
    EntityManagerProvider(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @PerThread
    @UseProxy(true)
    @Override
    public EntityManager provide() {
        return entityManagerFactory.createEntityManager();
    }

    @Override
    public void dispose(EntityManager instance) {
        instance.close();
    }

}
