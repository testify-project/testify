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
import org.testifyproject.annotation.Module;
import org.testifyproject.annotation.Real;
import org.testifyproject.annotation.Scan;
import org.testifyproject.annotation.VirtualResource;
import static org.testifyproject.di.hk2.HK2Properties.DEFAULT_DESCRIPTOR;
import org.testifyproject.junit4.fixture.need.common.GreetingEntity;
import org.testifyproject.junit4.fixture.need.container.PostgresAbstractBinder;

/**
 *
 * @author saden
 */
@VirtualResource(value = "postgres", version = "9.4")
@Module(PostgresAbstractBinder.class)
@Scan(DEFAULT_DESCRIPTOR)
@RunWith(HK2IntegrationTest.class)
public class VirtualResourceIT {

    @Real
    EntityManagerFactory sut;

    @Test
    public void givenHelloGreetShouldSaveHello() {
        //Arrange
        String phrase = "Hello";
        GreetingEntity entity = new GreetingEntity(phrase);

        //Act
        EntityManager entityManager = sut.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(entity);
        entityManager.getTransaction().commit();

        //Assert
        EntityManager em = sut.createEntityManager();
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
