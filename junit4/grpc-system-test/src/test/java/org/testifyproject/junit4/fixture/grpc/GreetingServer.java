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
package org.testifyproject.junit4.fixture.grpc;

import java.io.IOException;
import java.util.Set;

import org.slf4j.LoggerFactory;
import org.testifyproject.junit4.fixture.grpc.service.ServiceModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class GreetingServer {

    public static final int DEFAULT_PORT = 50051;
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(GreetingServer.class);

    private Server server;

    private void start() throws IOException {
        ServerBuilder serverBuilder = ServerBuilder.forPort(DEFAULT_PORT);

        Injector injector = Guice.createInjector(new ServiceModule());
        TypeLiteral<?> types = TypeLiteral.get(Types.setOf(BindableService.class));
        Key<?> key = Key.get(types);

        ((Set<BindableService>) injector.getInstance(key))
                .forEach(serverBuilder::addService);

        server = serverBuilder
                .build()
                .start();

        LOGGER.info("Server started, listening on {}", DEFAULT_PORT);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                GreetingServer.this.stop();
            }
        });
    }

    private void stop() {
        if (server != null) {
            LOGGER.info("Server shutting down");
            server.shutdownNow();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final GreetingServer server = new GreetingServer();
        server.start();
        server.blockUntilShutdown();
    }

}
