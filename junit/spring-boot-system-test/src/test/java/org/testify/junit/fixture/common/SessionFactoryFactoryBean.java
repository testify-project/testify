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

import javax.persistence.AttributeConverter;
import javax.persistence.Entity;
import javax.sql.DataSource;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyComponentPathImpl;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.reflections.Reflections;
import org.springframework.beans.factory.FactoryBean;

/**
 * Common session factory bean factory class.
 *
 * @author saden
 */
public class SessionFactoryFactoryBean implements FactoryBean<SessionFactory> {

    private final DataSource dataSource;

    public SessionFactoryFactoryBean(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public SessionFactory getObject() throws Exception {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .loadProperties("hibernate.properties")
                .applySetting(Environment.DATASOURCE, dataSource)
                .build();

        Reflections reflections = new Reflections(DatabaseConfig.class.getPackage().getName());
        MetadataSources metadataSources = new MetadataSources(registry);

        MetadataBuilder metadataBuilder = metadataSources.getMetadataBuilder()
                .applyPhysicalNamingStrategy(new PhysicalNamingStrategyStandardImpl())
                .applyImplicitNamingStrategy(new ImplicitNamingStrategyComponentPathImpl());

        reflections.getSubTypesOf(AttributeConverter.class).parallelStream()
                .forEach(metadataBuilder::applyAttributeConverter);

        reflections.getTypesAnnotatedWith(Entity.class)
                .parallelStream()
                .forEach(metadataSources::addAnnotatedClass);

        Metadata metadata = metadataBuilder.build();

        return metadata.buildSessionFactory();
    }

    @Override
    public Class<?> getObjectType() {
        return SessionFactory.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
