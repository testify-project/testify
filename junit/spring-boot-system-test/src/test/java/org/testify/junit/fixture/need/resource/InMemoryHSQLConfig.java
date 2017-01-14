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
package org.testify.junit.fixture.need.resource;

import org.testify.junit.fixture.common.DatabaseConfig;
import org.testify.junit.fixture.common.SessionFactoryFactoryBean;
import javax.sql.DataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring based java config for testify database need.
 *
 * @author saden
 */
@Configuration
@Import(DatabaseConfig.class)
public class InMemoryHSQLConfig {

    @Bean
    FactoryBean<SessionFactory> sessionFactoryImplProvider(DataSource dataSource) {
        return new SessionFactoryFactoryBean(dataSource);
    }
}
