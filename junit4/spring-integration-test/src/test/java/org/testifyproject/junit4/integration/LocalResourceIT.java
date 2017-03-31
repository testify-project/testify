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
package org.testifyproject.junit4.integration;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testifyproject.annotation.LocalResource;
import org.testifyproject.annotation.Module;
import org.testifyproject.annotation.Real;
import org.testifyproject.junit4.fixture.InMemoryHSQLResource;
import org.testifyproject.junit4.fixture.need.LocalResourceConfig;
import org.testifyproject.junit4.fixture.need.common.GreetingService;
import org.testifyproject.junit4.fixture.need.common.entity.GreetingEntity;

/**
 *
 * @author saden
 */
@Module(LocalResourceConfig.class)
@LocalResource(InMemoryHSQLResource.class)
@RunWith(SpringIntegrationTest.class)
public class LocalResourceIT {

    @Real
    EntityManagerFactory cut;

    @Real
    GreetingService greetingService;

    @Test
    public void givenHelloGreetShouldSaveHello() {
        //Arrange
        String phrase = "Hello";
        GreetingEntity entity = new GreetingEntity(phrase);

        //Act
        EntityManager em = cut.createEntityManager();
        em.getTransaction().begin();
        em.persist(entity);
        em.getTransaction().commit();

        //Assert
        em = cut.createEntityManager();
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
