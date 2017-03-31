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
package org.testifyproject.junit4.fixture.need;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.testifyproject.ContainerInstance;
import org.testifyproject.junit4.fixture.need.common.DatabaseConfig;

/**
 *
 * @author saden
 */
@Import(DatabaseConfig.class)
@EnableJpaRepositories
@Configuration
public class ContainerResourceConfig {

    @Bean
    static CommonAnnotationBeanPostProcessor annotationBeanPostProcessor() {
        return new CommonAnnotationBeanPostProcessor();
    }

    @Bean
    @Primary
    DataSource dataSourceProvider(ContainerInstance containerInstance) {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setServerName(containerInstance.getAddress().getHostName());
        dataSource.setPortNumber(containerInstance.findFirstExposedPort().get());
        //Default postgres image database name, user and postword
        dataSource.setDatabaseName("postgres");
        dataSource.setUser("postgres");
        dataSource.setPassword("mysecretpassword");

        return dataSource;
    }

    @Bean
    PersistenceUnitManager persistenceUnitManager(DataSource dataSource) {
        DefaultPersistenceUnitManager manager = new DefaultPersistenceUnitManager();
        manager.setDefaultDataSource(dataSource);
        manager.setDefaultPersistenceUnitName("example.greeter");

        return manager;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(PersistenceUnitManager manager) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setPersistenceUnitManager(manager);
        factoryBean.setPersistenceUnitName("example.greeter");

        return factoryBean;
    }

    @Primary
    @Bean
    EntityManager entityManagerProvider(@Qualifier("entityManagerFactory") EntityManager entityManager) {
        return entityManager;
    }

    @Bean
    public JpaTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}
