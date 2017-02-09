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
package org.testify.junit.fixture;

import static java.lang.String.format;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.hsqldb.jdbc.JDBCDataSource;
import org.testify.ResourceInstance;
import org.testify.ResourceProvider;
import org.testify.TestContext;
import org.testify.core.ResourceInstanceBuilder;

/**
 * An implementation of ResourceProvider that provides an in-memory HSQL
 * DataSource and connection.
 *
 * @author saden
 */
public class InMemoryHSQLResource implements ResourceProvider<JDBCDataSource, DataSource, Connection> {

    private JDBCDataSource server;
    private Connection client;

    @Override
    public JDBCDataSource configure(TestContext testContext) {
        JDBCDataSource dataSource = new JDBCDataSource();
        dataSource.setUrl(format("jdbc:hsqldb:mem:%s?default_schema=public", testContext.getName()));
        dataSource.setUser("sa");
        dataSource.setPassword("");

        return dataSource;
    }

    @Override
    public ResourceInstance<DataSource, Connection> start(TestContext testContext, JDBCDataSource dataSource) {
        try {
            server = dataSource;
            client = dataSource.getConnection();

            return new ResourceInstanceBuilder<DataSource, Connection>()
                    .server(server, DataSource.class)
                    .client(client, Connection.class)
                    .build();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void stop() {
        try {
            server.getConnection()
                    .createStatement()
                    .executeQuery("SHUTDOWN");
            client.close();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

}
