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
package org.testifyproject.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import org.testifyproject.ServiceInstance;
import org.testifyproject.annotation.Name;
import org.testifyproject.core.util.ExceptionUtil;
import org.testifyproject.guava.common.collect.ImmutableSet;
import org.testifyproject.guava.common.reflect.TypeToken;
import org.testifyproject.tools.Discoverable;

/**
 * An implementation of {@link ServiceInstance} that is backed by objects in the
 * {@link org.testifyproject.TestContext}.
 *
 * @author saden
 */
@Discoverable
public class DefaultServiceInstance implements ServiceInstance {

    private final Map<ServiceKey, Object> serviceContext;

    private static final Set<Class<? extends Annotation>> NAME_QUALIFIER;

    static {
        NAME_QUALIFIER = ImmutableSet.of(Name.class);
    }

    DefaultServiceInstance(Map<ServiceKey, Object> serviceContext) {
        this.serviceContext = serviceContext;
    }

    @Override
    public <T> T getContext() {
        return (T) serviceContext;
    }

    @Override
    public <T> T getService(Type type, String name) {
        return (T) serviceContext.get(ServiceKey.of(type, name));
    }

    @Override
    public <T> T getService(Type type, Annotation... qualifiers) {
        Object instance = null;

        if (qualifiers == null || qualifiers.length == 0) {
            instance = serviceContext.get(ServiceKey.of(type));

            if (instance == null) {
                instance = serviceContext.get(ServiceKey.of(TypeToken.of(type).getRawType()));
            }

            if (instance == null) {
                List<Object> foundMatches = serviceContext.values()
                        .stream()
                        .filter(Objects::nonNull)
                        .filter(p -> TypeToken.of(type).isSupertypeOf(p.getClass()))
                        .collect(toList());

                ExceptionUtil.INSTANCE.raise(foundMatches.size() > 1,
                        "Found {} matches for type {}. Please specify a @Name qualifier on the field.",
                        foundMatches.size(), type.getTypeName());

                instance = foundMatches.get(0);
            }

        } else if (Name.class.equals(qualifiers[0].annotationType())) {
            Name name = (Name) qualifiers[0];
            instance = serviceContext.get(ServiceKey.of(type, name.value()));
        }

        return (T) instance;
    }

    @Override
    public void addConstant(Object instance, String name, Class contract) {
        serviceContext.putIfAbsent(ServiceKey.of(contract, name), instance);
    }

    @Override
    public void replace(Object value, String name, Class contract) {
        serviceContext.put(ServiceKey.of(contract, name), value);
    }

    @Override
    public Set<Class<? extends Annotation>> getNameQualifers() {
        return NAME_QUALIFIER;
    }

    @Override
    public void destroy() {
        serviceContext.clear();
    }

}
