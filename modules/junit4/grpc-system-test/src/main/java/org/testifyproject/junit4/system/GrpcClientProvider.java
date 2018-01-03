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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Optional;

import org.testifyproject.ClientInstance;
import org.testifyproject.ClientProvider;
import org.testifyproject.Instance;
import org.testifyproject.SutDescriptor;
import org.testifyproject.TestContext;
import org.testifyproject.annotation.Application;
import org.testifyproject.annotation.Discoverable;
import org.testifyproject.core.ClientInstanceBuilder;
import org.testifyproject.core.util.ExceptionUtil;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.AbstractStub;

/**
 * An implementation of {@link ClientProvider} that provides a gRPC Client instance.
 *
 * @author saden
 */
@Discoverable
public class GrpcClientProvider
        implements ClientProvider<ManagedChannelBuilder, AbstractStub, ManagedChannel> {

    @Override
    public ManagedChannelBuilder configure(TestContext testContext,
            Application application, URI baseURI) {
        return ManagedChannelBuilder
                .forAddress(baseURI.getHost(), baseURI.getPort())
                .usePlaintext(true);
    }

    @Override
    public ClientInstance<AbstractStub, ManagedChannel> create(TestContext testContext,
            Application application,
            URI baseURI,
            ManagedChannelBuilder managedChannelBuilder) {
        Optional<SutDescriptor> foundSutDescriptor = testContext.getSutDescriptor();

        if (foundSutDescriptor.isPresent()) {
            try {
                SutDescriptor sutDescriptor = foundSutDescriptor.get();
                ManagedChannel managedChannel = managedChannelBuilder.build();

                Class<?> stubType = sutDescriptor.getType();
                String stubTypeName = stubType.getSimpleName();
                String factoryMethod;

                if (stubTypeName.endsWith("BlockingStub")) {
                    factoryMethod = "newBlockingStub";
                } else if (stubTypeName.endsWith("FutureStub")) {
                    factoryMethod = "newFutureStub";
                } else {
                    factoryMethod = "newStub";
                }

                Class<?> stubParentType = stubType.getDeclaringClass();
                Method method = stubParentType.getMethod(factoryMethod, Channel.class);
                AbstractStub abstractStub = (AbstractStub) method.invoke(null, managedChannel);

                return ClientInstanceBuilder.builder()
                        .client(abstractStub, abstractStub.getClass())
                        .clientSupplier(managedChannel, ManagedChannel.class)
                        .build("grpcClient", application);
            } catch (IllegalAccessException |
                    IllegalArgumentException |
                    NoSuchMethodException |
                    SecurityException |
                    InvocationTargetException e) {
                throw ExceptionUtil.INSTANCE.propagate("Could not create gRPC client", e);
            }
        }

        throw ExceptionUtil.INSTANCE.propagate("could not create client for grpc server '{}'",
                baseURI);
    }

    @Override
    public void destroy(ClientInstance<AbstractStub, ManagedChannel> clientInstance) {
        clientInstance.getClientSupplier()
                .map(Instance::getValue)
                .map(ManagedChannel.class::cast)
                .ifPresent(ManagedChannel::shutdownNow);

    }

    @Override
    public Class<AbstractStub> getClientType() {
        return AbstractStub.class;
    }

    @Override
    public Class<ManagedChannel> getClientSupplierType() {
        return ManagedChannel.class;
    }

}
