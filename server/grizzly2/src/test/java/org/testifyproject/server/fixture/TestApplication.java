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
package org.testifyproject.server.fixture;

import static org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory.createHttpServer;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * TODO.
 *
 * @author saden
 */
public class TestApplication {

    private HttpServer server;
    private final CountDownLatch latch = new CountDownLatch(1);

    public void start() throws IOException {
        URI baseURI = URI.create("http://127.0.0.1:8080");
        ResourceConfig resourceConfig = new ResourceConfig();
        this.server = createHttpServer(baseURI, resourceConfig, false);
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                TestApplication.this.stop();
            }
        });
    }

    public void stop() {
        server.shutdownNow();
    }

    public void block() {
        try {
            latch.await();
        } catch (InterruptedException e) {
        }
    }

    public static void main(String[] args) throws Exception {
        TestApplication application = new TestApplication();
        application.start();
        application.block();
    }

}
