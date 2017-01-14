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

import static org.testify.guava.common.base.Preconditions.checkState;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

/**
 * A utility class to locate services.
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

        return stream(serviceLoader.spliterator(), true).distinct().findFirst();
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

        return stream(serviceLoader.spliterator(), true).distinct().collect(toList());
    }

    /**
     * Gets one implementation of the given type. If more than one
     * implementation is found in the classpath an IllegalStateException will be
     * thrown.
     *
     * @param <T> the SPI type
     * @param contract the SPI contract
     * @return the implementation instance, empty otherwise
     */
    public <T> T getOne(Class<T> contract) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(contract);

        List<T> result = stream(serviceLoader.spliterator(), true).distinct().collect(toList());
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
     * @return the implementation instance, empty otherwise
     */
    public <T> T getOne(Class<T> contract, Class<? extends T> implementation) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(contract);

        List<T> result = stream(serviceLoader.spliterator(), true)
                .filter(p -> p.getClass().equals(implementation))
                .distinct()
                .collect(toList());

        checkState(
                !result.isEmpty(),
                "Could not find '%s' implementaiton of '%s' contract in the classpath. "
                + "Please insure an implementation is in the",
                implementation, contract.getName()
        );

        return result.get(0);
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
