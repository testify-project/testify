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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ProvideSystemProperty;

/**
 *
 * @author saden
 */
public class SettingUtilTest {

    SettingUtil sut;

    @Rule
    public ProvideSystemProperty provideSystemProperty 
            = new ProvideSystemProperty("testify.categories", "unit,integration");
    
    @Before
    public void init() {
        sut = new SettingUtil();
    }

    @Test
    public void givenTestifyYamlFileInUserDirGetPropertiesShouldReturnProperties()
            throws FileNotFoundException, IOException {
        String document = "hello: world";
        Path path = Paths.get(System.getProperty("user.dir"), ".testify.yml");

        if (!Files.exists(path)) {
            Files.createFile(path);
        }

        Files.write(path, document.getBytes(),
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING);

        Map<String, Object> result = sut.getSettings();

        assertThat(result).containsEntry("hello", "world");
        Files.deleteIfExists(path);
    }

    @Test
    public void givenTestifyYamlFileInUserHomeGetPropertiesShouldReturnProperties()
            throws FileNotFoundException, IOException {
        String document = "hello: world";
        Path path = Paths.get(System.getProperty("user.home"), ".testify.yml");

        if (!Files.exists(path)) {
            Files.createFile(path);
        }

        Files.write(path, document.getBytes(),
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING);

        Map<String, Object> result = sut.getSettings();

        assertThat(result).containsEntry("hello", "world");
        Files.deleteIfExists(path);
    }

    @Test
    public void givenCategoriesGetSystemCategoriesShouldReturnEmptyArray() {
        String[] result = sut.getSystemCategories();

        assertThat(result).contains("UNIT", "INTEGRATION");
    }
}
