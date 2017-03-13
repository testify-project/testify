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
package org.testifyproject.junit4.fixture.need.common;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManagerFactory;
import static javax.persistence.Persistence.createEntityManagerFactory;
import javax.sql.DataSource;
import org.glassfish.hk2.api.Factory;
import static org.hibernate.cfg.AvailableSettings.DATASOURCE;
import org.jvnet.hk2.annotations.Service;

/**
 *
 * @author saden
 */
@Service
public class EntityManagerFactoryProvider implements Factory<EntityManagerFactory> {

    private final DataSource dataSource;

    @Inject
    EntityManagerFactoryProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Singleton
    @Override
    public EntityManagerFactory provide() {
        Map<String, Object> props = new HashMap<>();
        props.put(DATASOURCE, dataSource);

        return createEntityManagerFactory("example.greeter", props);
    }

    @Override
    public void dispose(EntityManagerFactory instance) {
        instance.close();
    }

}