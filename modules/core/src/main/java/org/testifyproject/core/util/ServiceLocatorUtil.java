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
package org.testifyproject.core.util;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;

import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Hint;

/**
 * A utility class that uses {@link ServiceLoader} mechanism to locate service descriptors under
 * "META-INF/services" folder.
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
                .collect(toList());
    }

    /**
     * Find all implementations of the given type annotated with the given guideline and filter
     * annotations.
     *
     * @param <T> the SPI type
     * @param type the SPI contract
     * @param guidelines list of applicable guideline annotations types
     * @param filters the annotation to filter the implementations on
     * @return a list that contains all implementations, empty list otherwise
     */
    public <T> List<T> findAllWithFilter(Class<T> type,
            Collection<Class<? extends Annotation>> guidelines,
            Class<? extends Annotation>... filters) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(type);

        return stream(serviceLoader.spliterator(), true)
                .parallel()
                .filter(service -> {
                    Class serviceType = service.getClass();
                    boolean filter = true;

                    for (Class<? extends Annotation> guideline : guidelines) {
                        if (!serviceType.isAnnotationPresent(guideline)) {
                            filter = false;
                            break;
                        }
                    }

                    for (Class<? extends Annotation> filterAnnotation : filters) {
                        if (!serviceType.isAnnotationPresent(filterAnnotation)) {
                            filter = false;
                            break;
                        }
                    }

                    return filter;
                })
                .collect(toList());
    }

    /**
     * Find all implementations of the given type annotated with the given annotation.
     *
     * @param <T> the SPI type
     * @param type the SPI contract
     * @param filters the annotation to filter the implementations on
     * @return a list that contains all implementations, empty list otherwise
     */
    public <T> List<T> findAllWithFilter(Class<T> type, Class<? extends Annotation>... filters) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(type);

        return stream(serviceLoader.spliterator(), true)
                .parallel()
                .filter(service -> {
                    Class serviceType = service.getClass();
                    boolean filter = true;

                    for (Class<? extends Annotation> filterAnnotation : filters) {
                        if (!serviceType.isAnnotationPresent(filterAnnotation)) {
                            filter = false;
                            break;
                        }
                    }

                    return filter;
                })
                .collect(toList());
    }

    /**
     * Gets one implementation of the given type. If more than one implementation is found in
     * the classpath an IllegalStateException will be thrown.
     *
     * @param <T> the SPI type
     * @param contract the SPI contract
     * @return an implementation of the service, throws an exception otherwise
     */
    public <T> T getOne(Class<T> contract) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(contract);

        List<T> result = stream(serviceLoader.spliterator(), true)
                .collect(toList());

        insureOne(result, contract.getName());

        return result.get(0);
    }

    /**
     * Gets one implementation of the given type using the given annotation filter. If more than
     * one implementation is found in the classpath an IllegalStateException will be thrown.
     *
     * @param <T> the SPI type
     * @param contract the SPI contract
     * @param filters the annotations to filter the implementations on
     * @return a list that contains all implementations, empty list otherwise
     */
    public <T> T getOneWithFilter(Class<T> contract, Class<? extends Annotation>... filters) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(contract);

        List<T> result = stream(serviceLoader.spliterator(), true)
                .parallel()
                .filter(service -> {
                    Class serviceType = service.getClass();
                    boolean filter = true;

                    for (Class<? extends Annotation> filterAnnotation : filters) {
                        if (!serviceType.isAnnotationPresent(filterAnnotation)) {
                            filter = false;
                            break;
                        }
                    }

                    return filter;
                })
                .collect(toList());

        insureOne(result, contract.getName());

        return result.get(0);
    }

    /**
     * Get a specific implementation of the given contract. If no implementation is found in the
     * classpath an IllegalStateException will be thrown.
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
                .collect(toList());

        String contractName = contract.getName();
        String implementationName = implementation.getName();

        insureNotEmpty(result, contractName, implementationName);

        return result.get(0);
    }

    public <T> T getFromHintWithFilter(TestContext testContext,
            Class<T> contract,
            Function<Hint, Class<? extends T>> hintProvider) {

        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        Predicate predicate = contract::equals;

        Optional<Class<? extends T>> foundProvider = testDescriptor.getHint()
                .map(hintProvider)
                .filter(predicate.negate());

        return foundProvider
                .map(hintServiceProvider -> getOne(contract, hintServiceProvider))
                .orElseGet(() -> getOneWithFilter(contract, testContext.getTestCategory()));
    }

    public <T> T getFromHintOrDefault(TestContext testContext,
            Class<T> contract,
            Class<? extends T> implementation,
            Function<Hint, Class<? extends T>> hintProvider) {

        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        Predicate predicate = contract::equals;

        Optional<Class<? extends T>> foundProvider = testDescriptor.getHint()
                .map(hintProvider)
                .filter(predicate.negate());

        return foundProvider
                .map(hintServiceProvider -> getOne(contract, hintServiceProvider))
                .orElseGet(() -> getOne(contract, implementation));
    }

    /**
     * Get an implementation of the contract. If an implementation is not found in the classpath
     * then get the default implementation.
     *
     * @param <T> the SPI type
     * @param contract the SPI contract
     * @param defaultImplementation the implementation of the contract
     * @return an implementation instance, default implementation otherwise
     */
    public <T> T getOneOrDefault(Class<T> contract, Class<? extends T> defaultImplementation) {
        String contractName = contract.getName();
        String implementationName = defaultImplementation.getName();

        AtomicReference<T> contractRef = new AtomicReference<>();
        AtomicReference<T> implementationRef = new AtomicReference<>();

        ServiceLoader<T> serviceLoader = ServiceLoader.load(contract);

        List<T> result = stream(serviceLoader.spliterator(), true)
                .parallel()
                .filter(service -> {
                    if (service.getClass().equals(defaultImplementation)) {
                        implementationRef.compareAndSet(null, service);
                    } else {
                        contractRef.compareAndSet(null, service);
                    }

                    return true;
                })
                .collect(toList());

        insureAtMostTwo(result, contractName, implementationName);

        T instance = null;

        if (contractRef.get() != null) {
            instance = contractRef.get();
        } else if (implementationRef.get() != null) {
            instance = implementationRef.get();
        }

        insureOne(instance, contractName, implementationName);

        return instance;
    }

    /**
     * Gets all implementations of the given type. If no implementation is found in the
     * classpath an IllegalStateException will be thrown.
     *
     * @param <T> the SPI type
     * @param contract the SPI contract
     * @return a list that contains all implementations, empty list otherwise
     */
    public <T> List<T> getAll(Class<T> contract) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(contract);

        List<T> result = stream(serviceLoader.spliterator(), true)
                .collect(toList());

        String name = contract.getName();

        insureNotEmpty(result, name);

        return result;
    }

    void insureOne(Object instance, String contractName, String implementationName) {
        ExceptionUtil.INSTANCE.raise(instance == null,
                "Could not find an implementation of '{}' contract in the classpath."
                + "Please insure that at least one implementation is in the classpath",
                implementationName, contractName);
    }

    void insureNotEmpty(List<?> result, String contractName, String implementationName) {
        ExceptionUtil.INSTANCE.raise(result.isEmpty(),
                "Could not find '{}' implementaiton of '{}' contract in the classpath. "
                + "Please insure an implementation is in the",
                implementationName, contractName);
    }

    void insureAtMostTwo(List<?> result, String contractName, String implementationName) {
        ExceptionUtil.INSTANCE.raise(result.size() > 2,
                "Found '{}' implementaitons of '{}' contract in the classpath ({}). "
                + "Please insure two or less implementation of '{}' contract including '{}' "
                + "are in the classpath",
                result.size() - 1,
                contractName,
                Arrays.toString(result.toArray()),
                contractName,
                implementationName
        );
    }

    void insureNotEmpty(List<?> result, String name) {
        ExceptionUtil.INSTANCE.raise(result.isEmpty(),
                "Could not find an implementaiton of '{}' contract in the classpath. "
                + "Please insure at least one implementation of '{}' contract is in the classpath",
                name, name);
    }

    void insureOne(List<?> result, String name) {
        String implementations = result.stream()
                .map(p -> p.getClass().getSimpleName())
                .collect(joining(", "));

        ExceptionUtil.INSTANCE.raise(result.size() != 1,
                "Found '{}' implementaitons ({}) of '{}' contract in the classpath. "
                + "Please insure one implementation of '{}' contract is in the classpath",
                result.size(), implementations, name, name);
    }
}
