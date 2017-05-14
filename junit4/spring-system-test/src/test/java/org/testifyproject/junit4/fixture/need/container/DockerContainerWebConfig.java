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
package org.testifyproject.junit4.fixture.need.container;

import javax.sql.DataSource;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.testifyproject.VirtualResourceInstance;
import org.testifyproject.junit4.fixture.common.DatabaseConfig;
import org.testifyproject.junit4.fixture.common.SessionFactoryFactoryBean;
import org.testifyproject.junit4.fixture.web.GreeterWebConfig;

/**
 *
 * @author saden
 */
@Configuration
@ComponentScan
@Import({GreeterWebConfig.class, DatabaseConfig.class})
public class DockerContainerWebConfig {

    @Bean
    DataSource dataSourceProvider(VirtualResourceInstance virtualResourceInstance) {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setServerName(virtualResourceInstance.getAddress().getHostName());
        dataSource.setPortNumber(virtualResourceInstance.findFirstExposedPort().get());
        //Default postgres image database name, user and postword
        dataSource.setDatabaseName("postgres");
        dataSource.setUser("postgres");
        dataSource.setPassword("mysecretpassword");

        return dataSource;
    }

    @Bean
    FactoryBean<SessionFactory> sessionFactoryImplProvider(DataSource dataSource) {
        return new SessionFactoryFactoryBean(dataSource);
    }

    @Bean
    Session sessionFactoryProvider(SessionFactory sessionFactory) {
        return sessionFactory.openSession();
    }
}
