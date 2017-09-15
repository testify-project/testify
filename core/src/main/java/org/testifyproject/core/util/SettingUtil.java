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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.testifyproject.snakeyaml.Yaml;

/**
 * A utility class for managing .testify.yml settings file.
 *
 * @author saden
 */
public class SettingUtil {

    public static final SettingUtil INSTANCE = new SettingUtil();

    /**
     * Retrieve the {@code .testiy.yml} settings file from
     * {@code System.getProperty("user.dir")} directory. If one is not found then look in the
     * parent directory until {@code System.getProperty("user.home")} directory is reached.
     *
     * @return the .testify.yml file as a Map, empty map otherwise
     */
    public Map<String, Object> getSettings() {
        Map<String, Object> settings = new LinkedHashMap<>();

        Path currentDir = Paths.get(System.getProperty("user.dir"));
        Path userDir = Paths.get(System.getProperty("user.home"));
        Path testify = null;

        do {
            Path path = currentDir.resolve(".testify.yml");

            if (path.toFile().exists()) {
                testify = path;
                break;
            }

            currentDir = currentDir.getParent();
        } while (currentDir.compareTo(userDir) >= 0);

        if (testify != null) {
            try {
                Yaml yaml = new Yaml();
                settings = (Map) yaml.load(Files.newInputStream(testify));
            } catch (IOException e) {
                throw ExceptionUtil.INSTANCE.propagate("Could not load {} file", e, testify);
            }
        }

        return settings;
    }

    /**
     * Gets the {@code testify.categories} system property, split it by comma separator and
     * return it as an array of strings.
     *
     * @return testify.categories system properties
     */
    public String[] getSystemCategories() {
        String result = System.getProperty("testify.categories", "");

        return Stream.of(result.split(","))
                .filter(p -> !p.isEmpty())
                .map(String::trim)
                .map(String::toUpperCase)
                .toArray(String[]::new);
    }

}
