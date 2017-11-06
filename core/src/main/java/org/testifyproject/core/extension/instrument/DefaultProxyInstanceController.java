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
package org.testifyproject.core.extension.instrument;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.function.Supplier;

import org.testifyproject.Instance;
import org.testifyproject.TestContext;
import org.testifyproject.core.DefaultInstance;
import org.testifyproject.core.util.InstrumentUtil;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.extension.ProxyInstanceController;
import org.testifyproject.extension.ProxyInstanceProvider;
import org.testifyproject.guava.common.collect.ImmutableList;
import org.testifyproject.tools.Discoverable;

/**
 * TODO.
 *
 * @author saden
 */
@Discoverable
public class DefaultProxyInstanceController implements ProxyInstanceController {

    private final InstrumentUtil instrumentUtil;

    public DefaultProxyInstanceController() {
        this(InstrumentUtil.INSTANCE);
    }

    public DefaultProxyInstanceController(InstrumentUtil instrumentUtil) {
        this.instrumentUtil = instrumentUtil;
    }

    @Override
    public List<Instance> create(TestContext testContext) {
        ImmutableList.Builder<Instance> builder = ImmutableList.builder();

        ServiceLocatorUtil.INSTANCE.findAll(ProxyInstanceProvider.class)
                .stream()
                .flatMap(p -> p.get(testContext).parallelStream())
                .forEach(proxyInstance -> {
                    Class proxyType = proxyInstance.getType();
                    int proxyTypeModifiers = proxyType.getModifiers();

                    //we cant proxy final classes so ignore them
                    if (!Modifier.isFinal(proxyTypeModifiers)) {
                        String proxyName = proxyInstance.getName();
                        Supplier proxyDelegate = proxyInstance.getDelegate();

                        Object proxy = instrumentUtil.createProxy(proxyType,
                                testContext.getTestClassLoader(), proxyDelegate);

                        builder.add(DefaultInstance.of(proxy, proxyName, proxyType));
                    }
                });

        return builder.build();
    }

}
