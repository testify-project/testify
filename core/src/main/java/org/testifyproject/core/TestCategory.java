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

import java.util.List;
import org.testifyproject.core.util.LoggingUtil;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 * A enumeration class that defines test categories.
 *
 * @author saden
 */
public interface TestCategory {

    /**
     * Find enum constants with the given categories for the given enum type.
     *
     * @param enumType the enum type we searching in
     * @param categories the list of categories we are searching for
     * @return list with categories, empty list otherwise.
     */
    static List<Enum> find(Class<? extends Enum> enumType, String[] categories) {
        ImmutableList.Builder<Enum> builder = ImmutableList.builder();

        for (String category : categories) {
            try {
                Enum result = Enum.valueOf(enumType, category.toUpperCase());
                builder.add(result);
            } catch (Exception e) {
                LoggingUtil.INSTANCE.warn("Could not find enum '{}'", e, category);
            }
        }

        return builder.build();
    }

    /**
     * An enumeration class that defines testing level test categories.
     */
    enum Level {

        /**
         * UNIT test category.
         */
        UNIT,
        /**
         * INTEGRATION test category.
         */
        INTEGRATION,
        /**
         * SYSTEM test category.
         */
        SYSTEM;

    }

    /**
     * An enumeration class that dynamic test categories based on test class
     * properties.
     */
    enum Dynamic {

        /**
         * LOCAL test category. Note that classes that require resources belong
         * in this category.
         */
        LOCAL,
        /**
         * VIRTUAL test category. Note that classes that require container
         * belong in this category.
         */
        VIRTUAL;

    }
}
