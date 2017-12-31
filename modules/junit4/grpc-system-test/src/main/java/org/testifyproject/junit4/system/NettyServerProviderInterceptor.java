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

import java.util.concurrent.Callable;

import org.testifyproject.bytebuddy.implementation.bind.annotation.AllArguments;
import org.testifyproject.bytebuddy.implementation.bind.annotation.BindingPriority;
import org.testifyproject.bytebuddy.implementation.bind.annotation.Morph;
import org.testifyproject.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.testifyproject.bytebuddy.implementation.bind.annotation.SuperCall;
import org.testifyproject.bytebuddy.implementation.bind.annotation.This;
import org.testifyproject.core.TestContextHolder;
import org.testifyproject.extension.InstrumentMorpher;

import io.grpc.netty.NettyServerBuilder;

/**
 * Netty Server Provider operation interceptor. This class intercepts certain Netty Server
 * Provider initialization calls to configure the test case.
 *
 * @author saden
 */
public class NettyServerProviderInterceptor {

    @RuntimeType
    @BindingPriority(Integer.MAX_VALUE)
    public Object anyMethod(@SuperCall Callable<?> zuper, @This(optional = true) Object object)
            throws Exception {
        return zuper.call();
    }

    public NettyServerBuilder builderForPort(
            @Morph InstrumentMorpher<NettyServerBuilder> morpher,
            @AllArguments Object[] args) {
        NettyServerBuilder result = TestContextHolder.INSTANCE.query(testContext -> {
            return morpher.morph(new Object[]{0});
        });

        if (result == null) {
            result = morpher.morph(args);
        }

        return result;
    }
}
