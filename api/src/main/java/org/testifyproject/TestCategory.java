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
package org.testifyproject;

import java.util.Arrays;
import java.util.List;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 * A enumeration class that defines test categories.
 *
 * @author saden
 */
public interface TestCategory {

    /**
     * An enumeration class that defines testing level test categories.
     */
    enum Level {

        /**
         * Unit test category.
         */
        Unit,
        /**
         * Integration test category.
         */
        Integration("int", "integ"),
        /**
         * System test category.
         */
        System("sys");

        private final List<String> categories;

        Level(String... categories) {
            this.categories = Arrays.asList(categories);
        }

        /**
         * Determine if the enum contains the given category value. Not that
         * this method is case insensitive.
         *
         * @param category we are searching for
         * @return true if category is present, false otherwise
         */
        public Boolean contains(String category) {
            Boolean searchResult = false;
            String lowerCaseName = name().toLowerCase();
            String cleanCategory = category.toLowerCase().trim();

            if (lowerCaseName.equals(cleanCategory)
                    || categories.contains(cleanCategory)) {
                searchResult = true;
            }

            return searchResult;
        }

        /**
         * Find enum categories with the given category names.
         *
         * @param categories the list of categories we are searching for
         * @return list with categories, empty list otherwise.
         */
        public static List<Level> find(String[] categories) {
            ImmutableList.Builder<Level> builder = ImmutableList.builder();

            for (String category : categories) {
                Level[] values = values();

                for (Level value : values) {
                    if (value.contains(category)) {
                        builder.add(value);
                    }
                }
            }

            return builder.build();
        }

    }

    /**
     * An enumeration class that dynamic test categories based on test class
     * properties.
     */
    enum Dynamic {

        /**
         * Resource test category. Note that classes that require resources
         * belong in this category.
         */
        Resource("res"),
        /**
         * Container test category. Note that classes that require container
         * belong in this category.
         */
        Container("con");

        private final List<String> categories;

        Dynamic(String... categories) {
            this.categories = Arrays.asList(categories);
        }

        /**
         * Determine if the enum contains the given category value. Not that
         * this method is case insensitive.
         *
         * @param category we are searching for
         * @return true if category is present, false otherwise
         */
        public Boolean contains(String category) {
            Boolean searchResult = false;
            String lowerCaseName = name().toLowerCase();
            String cleanCategory = category.toLowerCase().trim();

            if (lowerCaseName.equals(cleanCategory)
                    || categories.contains(cleanCategory)) {
                searchResult = true;
            }

            return searchResult;
        }

        /**
         * Find enum categories with the given category names.
         *
         * @param categories the list of categories we are searching for
         * @return list with categories, empty list otherwise.
         */
        public static List<Dynamic> find(String[] categories) {
            ImmutableList.Builder<Dynamic> builder = ImmutableList.builder();

            for (String category : categories) {
                Dynamic[] values = values();

                for (Dynamic value : values) {
                    if (value.contains(category)) {
                        builder.add(value);
                    }
                }
            }

            return builder.build();
        }

    }
}
