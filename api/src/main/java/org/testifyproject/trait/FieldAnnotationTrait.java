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
package org.testifyproject.trait;

import java.util.Optional;
import static java.util.Optional.ofNullable;
import org.testifyproject.annotation.Fake;
import org.testifyproject.annotation.Fixture;
import org.testifyproject.annotation.Property;
import org.testifyproject.annotation.Real;
import org.testifyproject.annotation.Virtual;

/**
 * A contract that specifies mock traits.
 *
 * @author saden
 */
public interface FieldAnnotationTrait extends FieldTrait {

    /**
     * Get {@link Fake} annotation.
     *
     * @return optional with fake annotation, empty optional otherwise
     */
    default Optional<Fake> getFake() {
        return ofNullable(getMember().getDeclaredAnnotation(Fake.class));
    }

    /**
     * Get {@link Real} annotation.
     *
     * @return optional with real annotation, empty optional otherwise
     */
    default Optional<Real> getReal() {
        return ofNullable(getMember().getDeclaredAnnotation(Real.class));
    }

    /**
     * Get {@link Virtual} annotation.
     *
     * @return optional with virtual annotation, empty optional otherwise
     */
    default Optional<Virtual> getVirtual() {
        return ofNullable(getMember().getDeclaredAnnotation(Virtual.class));
    }

    /**
     * Get {@link Fixture} annotation.
     *
     * @return optional with fixture annotation, empty optional otherwise
     */
    default Optional<Fixture> getFixture() {
        return ofNullable(getMember().getDeclaredAnnotation(Fixture.class));
    }

    /**
     * Get {@link Property} annotation.
     *
     * @return optional with property annotation, empty optional otherwise
     */
    default Optional<Property> getProperty() {
        return ofNullable(getMember().getDeclaredAnnotation(Property.class));
    }

    /**
     * Determine if the member is annotated with {@link Fake} or {@link Virtual}
     * annotation.
     *
     * @return true if the member is a mock, false otherwise
     */
    default Boolean isMock() {
        return getFake().isPresent()
                || getVirtual().isPresent();
    }

    /**
     * Determine if the member is annotated with {@link Fake}, {@link Virtual},
     * or {@link Real} annotation.
     *
     * @return true if the member is injectable, false otherwise
     */
    default Boolean isInjectable() {
        return getFake().isPresent()
                || getReal().isPresent()
                || getVirtual().isPresent();
    }

}
