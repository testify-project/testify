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
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.testify.annotation.Application;
import org.testify.annotation.ConfigHandler;
import org.testify.annotation.Real;
import org.testify.annotation.RequiresContainer;
import org.testify.github.dockerjava.core.DockerClientConfig;
import org.testify.junit.system.SpringSystemTest;
import org.testify.tools.category.ContainerTests;
import org.testify.junit.fixture.common.UserEntity;
import org.testify.junit.fixture.need.container.DockerContainerApplication;

/**
 *
 * @author saden
 */
@RunWith(SpringSystemTest.class)
@Application(DockerContainerApplication.class)
@RequiresContainer(value = "postgres", version = "9.4")
@Category(ContainerTests.class)
public class GreetingResourceRequiresContainerST {

    @Real
    SessionFactory factory;

    @ConfigHandler
    public void configure(DockerClientConfig.DockerClientConfigBuilder builder) {
        assertThat(builder).isNotNull();
    }

    @Test
    public void givenUserEntitySaveShouldPerisistEntityToPostgres() {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            UserEntity entity = new UserEntity(null, "saden", "test", "test");
            Serializable id = session.save(entity);
            tx.commit();
            assertThat(id).isNotNull();

            entity = session.get(UserEntity.class, id);
            assertThat(entity).isNotNull();
            javax.transaction.SystemException s;
        }
    }

}
