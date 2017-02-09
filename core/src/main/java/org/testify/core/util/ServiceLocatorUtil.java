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
package org.testify.core.util;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.testify.guava.common.base.Preconditions.checkState;

/**
 * A utility class that uses {@link ServiceLoader} mechanism to locate service
 * descriptors under "META-INF/services" folder.
 *
 * @author saden
 */
public class ServiceLocatorUtil {

    public static final ServiceLocatorUtil INSTANCE = new ServiceLocatorUtil();

    /**
     * Find the first implementation of the given type.
     *
     * @param <T> the SPI type
     * @param type the SPI contract
     * @return optional containing implementation instance, empty otherwise
     */
    public <T> Optional<T> findOne(Class<T> type) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(type);

        return stream(serviceLoader.spliterator(), true)
                .distinct()
                .findFirst();
    }

    /**
     * Find all implementations of the given type.
     *
     * @param <T> the SPI type
     * @param type the SPI contract
     * @return a list that contains all implementations, empty list otherwise
     */
    public <T> List<T> findAll(Class<T> type) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(type);

        return stream(serviceLoader.spliterator(), true)
                .distinct()
                .collect(toList());
    }

    /**
     * Gets one implementation of the given type. If more than one
     * implementation is found in the classpath an IllegalStateException will be
     * thrown.
     *
     * @param <T> the SPI type
     * @param contract the SPI contract
     * @return an implementation of the service, throws an exception otherwise
     */
    public <T> T getOne(Class<T> contract) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(contract);

        List<T> result = stream(serviceLoader.spliterator(), true).
                distinct()
                .collect(toList());

        String name = contract.getName();

        checkState(!result.isEmpty(),
                "Could not find an implementaiton of '%s' contract in the classpath."
                + "Please insure an implementation of '%s' contract is in the classpath", name, name);

        checkState(result.size() == 1,
                "Found '%d' implementaitons of '%s' contract in the classpath (%s). "
                + "Please insure only one implementation of '%s' contract is in the classpath",
                result.size(), Arrays.toString(result.toArray()), name, name);

        return result.get(0);
    }

    /**
     * Get a specific implementation of the given contract. If no implementation
     * is found in the classpath an IllegalStateException will be thrown.
     *
     * @param <T> the SPI type
     * @param contract the SPI contract
     * @param implementation the implementation type of the contract
     * @return an implementation of the service, throws an exception otherwise
     */
    public <T> T getOne(Class<T> contract, Class<? extends T> implementation) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(contract);

        List<T> result = stream(serviceLoader.spliterator(), true)
                .filter(p -> p.getClass().equals(implementation))
                .distinct()
                .collect(toList());

        String contractName = contract.getName();
        String implementationName = implementation.getName();

        checkState(!result.isEmpty(),
                "Could not find '%s' implementaiton of '%s' contract in the classpath. "
                + "Please insure an implementation is in the",
                implementationName, contractName);

        return result.get(0);
    }

    /**
     * Get an implementation of the contract. If an implementation is not found
     * in the classpath then get the default implementation.
     *
     * @param <T> the SPI type
     * @param contract the SPI contract
     * @param defaultImplementation the default implementation of the contract
     * @return an implementation instance, default implementation otherwise
     */
    public <T> T getOneIfPresent(Class<T> contract, Class<? extends T> defaultImplementation) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(contract);

        List<T> result = stream(serviceLoader.spliterator(), true)
                .distinct()
                .collect(toList());

        String contractName = contract.getName();
        String defaultImplementationName = defaultImplementation.getClass().getName();

        checkState(!result.isEmpty(),
                "Could not find an implementaiton of '%s' contract in the classpath."
                + "Please insure an implementation of '%s' contract is in the classpath",
                contractName, contractName);

        Object[] implementations = result.stream()
                .filter(p -> p.getClass().equals(defaultImplementation))
                .toArray();

        checkState(result.size() <= 2,
                "Found '%d' implementaitons of '%s' contract in the classpath (%s). "
                + "Please insure only one implementation of '%s' contract is in the classpath",
                result.size() - 1, contractName, Arrays.toString(implementations), contractName);

        T defaultImplementationService = null;

        for (T service : result) {
            Class<?> serviceType = service.getClass();

            if (serviceType.equals(defaultImplementation)) {
                defaultImplementationService = service;
            } else {
                return service;
            }
        }

        checkState(defaultImplementationService != null,
                "Could not find an default implementation '%s' of '%s' contract in the classpath."
                + "Please insure that the default implementation is in the classpath",
                defaultImplementationName, contractName);

        return defaultImplementationService;
    }

    /**
     * Gets all implementations of the given type. If no implementation is found
     * in the classpath an IllegalStateException will be thrown.
     *
     * @param <T> the SPI type
     * @param contract the SPI contract
     * @return a list that contains all implementations, empty list otherwise
     */
    public <T> List<T> getAll(Class<T> contract) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(contract);

        List<T> result = stream(serviceLoader.spliterator(), true).distinct().collect(toList());

        checkState(
                !result.isEmpty(),
                "Could not find an implementaiton of '%s' contract in the classpath. "
                + "Please insure at least one implementation of '%s' contract is in the classpath",
                contract.getName(), contract.getName()
        );

        return result;
    }

}
