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
package org.testify.junit.integration;

import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.testify.annotation.Module;
import org.testify.annotation.RequiresContainer;
import org.testify.junit.fixture.need.container.PostgresModule;
import org.testify.junit.fixture.need.database.GreetingEntity;
import org.testify.tools.category.ContainerTests;

/**
 *
 * @author saden
 */
@RequiresContainer(value = "postgres", version = "9.4")
@Module(PostgresModule.class)
@Category({ContainerTests.class})
@RunWith(GuiceIntegrationTest.class)
public class RequiresContainerIT {

    @Inject
    EntityManagerFactory cut;

    @Test
    public void givenHelloGreetShouldSaveHello() {
        //Arrange
        String phrase = "Hello";
        GreetingEntity entity = new GreetingEntity(phrase);

        //Act
        EntityManager entityManager = cut.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(entity);
        entityManager.getTransaction().commit();

        //Assert
        EntityManager em = cut.createEntityManager();
        Query query = em.createQuery("SELECT e FROM GreetingEntity e");
        assertThat(query).isNotNull();
        List<GreetingEntity> entities = query.getResultList();
        assertThat(entities).hasSize(1);

        entity = entities.get(0);
        assertThat(entity.getId()).isNotNull();
        assertThat(entity.getPhrase()).isEqualTo(phrase);
        em.close();
    }

}
