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
package org.testifyproject.junit4.fixture.common;

import static java.lang.String.format;
import javax.inject.Singleton;
import javax.sql.DataSource;
import org.glassfish.hk2.api.Factory;
import org.hsqldb.jdbc.JDBCDataSource;
import org.jvnet.hk2.annotations.Service;

/**
 * A provider of a JDBC H2 production DataSource.
 *
 * @author saden
 */
@Service
public class DefaultDataSourceProvider implements Factory<DataSource> {

    @Singleton
    @Override
    public DataSource provide() {
        JDBCDataSource dataSource = new JDBCDataSource();
        dataSource.setUrl(format("jdbc:hsqldb:mem:%s?default_schema=public", "greeter"));
        dataSource.setUser("sa");
        dataSource.setPassword("");

        return dataSource;
    }

    @Override
    public void dispose(DataSource instance) {
    }

}
