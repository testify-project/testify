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
package org.testify.junit.system;

import java.io.Serializable;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hsqldb.jdbc.JDBCDataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testify.annotation.Application;
import org.testify.annotation.ConfigHandler;
import org.testify.annotation.Real;
import org.testify.annotation.RequiresResource;
import org.testify.junit.fixture.InMemoryHSQLResource;
import org.testify.junit.fixture.common.UserEntity;
import org.testify.junit.fixture.need.resource.InMemoryHSQLApplication;

/**
 *
 * @author saden
 */
@Application(InMemoryHSQLApplication.class)
@RequiresResource(InMemoryHSQLResource.class)
@RunWith(SpringBootSystemTest.class)
public class GreetingResourceRequiresResourceST {

    @Real
    SessionFactory factory;

    @ConfigHandler
    void configure(JDBCDataSource dataSource) {
        assertThat(dataSource).isNotNull();
    }

    @Test
    public void givenUserEntitySaveShouldPerisistEntityToInMemoryHSQLResource() {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            UserEntity entity = new UserEntity(null, "saden", "test", "test");
            Serializable id = session.save(entity);
            tx.commit();
            assertThat(id).isNotNull();

            entity = session.get(UserEntity.class, id);
            assertThat(entity).isNotNull();
        }
    }

    @Test
    public void givenAnotherUserEntitySaveShouldPerisistEntityToInMemoryHSQLResource() {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            UserEntity entity = new UserEntity(null, "saden", "test", "test");
            Serializable id = session.save(entity);
            tx.commit();
            assertThat(id).isNotNull();

            entity = session.get(UserEntity.class, id);
            assertThat(entity).isNotNull();
        }
    }
}
