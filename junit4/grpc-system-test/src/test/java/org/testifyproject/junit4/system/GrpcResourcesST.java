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
package org.testifyproject.junit4.system;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.testify.example.greetings.GreeterGrpc;
import org.testify.example.greetings.GreetingReply;
import org.testify.example.greetings.GreetingRequest;
import org.testifyproject.annotation.Application;
import org.testifyproject.annotation.LocalResource;
import org.testifyproject.annotation.Sut;
import org.testifyproject.annotation.VirtualResource;
import org.testifyproject.junit4.SystemTest;
import org.testifyproject.junit4.fixture.grpc.GreetingServer;
import org.testifyproject.junit4.fixture.resource.TestLocalResourceProvider;

/**
 *
 * @author saden
 */
@LocalResource(TestLocalResourceProvider.class)
@VirtualResource("test")
@Application(value = GreetingServer.class, start = "start", stop = "stop")
@RunWith(SystemTest.class)
public class GrpcResourcesST {

    @Sut
    GreeterGrpc.GreeterBlockingStub sut;

    @Test
    public void verifyInjection() {
        assertThat(sut).isNotNull();
    }

    @Test
    public void givenNameSayHelloShouldReturnHelloReply() {
        String name = "test";
        GreetingRequest request = GreetingRequest.newBuilder().setPhrase(name).build();
        GreetingReply result = sut.greet(request);

        assertThat(result).isNotNull();
        assertThat(result.getGreeting()).contains(name);
    }

}
