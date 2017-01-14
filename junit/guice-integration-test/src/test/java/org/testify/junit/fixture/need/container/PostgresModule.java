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
package org.testify.junit.fixture.need.container;

import org.testify.junit.fixture.need.database.DatabaseModule;
import com.google.inject.AbstractModule;
import javax.inject.Singleton;
import javax.sql.DataSource;

/**
 * Create Postgres data source dynamically. Note that we do this just so we can
 * test multiple data sources to avoid data sources clobbering each other.
 *
 * @author saden
 */
public class PostgresModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new DatabaseModule());

        bind(DataSource.class)
                .toProvider(PostgresDataSourceProvider.class)
                .in(Singleton.class);
    }

}
