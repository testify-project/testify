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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;

/**
 *
 * @author saden
 */
public class PropertiesReaderTest {

    PropertiesReader sut;
    Map properties;

    @Before
    public void init() {
        sut = mock(PropertiesReader.class, Answers.CALLS_REAL_METHODS);
        properties = new HashMap();

        given(sut.getProperties()).willReturn(properties);
    }

    @Test
    public void callToGetPropertiesShouldReturnEmptyResult() {
        Map<String, Object> result = sut.getProperties();

        assertThat(result).isEmpty();
    }

    @Test
    public void givenNonExistentKeyGetPropertyShouldReturnEmptyResult() {
        String key = "non";

        Object result = sut.getProperty(key);

        assertThat(result).isNull();
    }

    @Test
    public void givenExistentKeyGetPropertyShouldReturnOptionalWithValue() {
        String key = "key";
        String value = "value";

        properties.put(key, value);

        String result = sut.getProperty(key);

        assertThat(result).isEqualTo(value);
    }

    @Test
    public void givenNonExistentKeyFindPropertyShouldReturnEmptyResult() {
        String key = "non";

        Optional<Object> result = sut.findProperty(key);

        assertThat(result).isEmpty();
    }

    @Test
    public void givenExistentKeyFindPropertyShouldReturnOptionalWithValue() {
        String key = "key";
        String value = "value";

        properties.put(key, value);

        Optional<Object> result = sut.findProperty(key);

        assertThat(result).contains(value);
    }

    @Test
    public void givenNonExistingKeyFindListShouldReturnEmptyList() {
        String key = "key";

        Collection<Object> result = sut.findCollection(key);

        assertThat(result).isEmpty();

    }

    @Test
    public void givenExistingKeyFindListShouldReturnList() {
        String key = "key";
        List value = mock(List.class);

        properties.put(key, value);

        Collection<Object> result = sut.findCollection(key);

        assertThat(result).isEqualTo(value);
    }

    @Test
    public void givenNonExistingKeyFindMapShouldReturnEmptyList() {
        String key = "key";

        Map<Object, Object> result = sut.findMap(key);

        assertThat(result).isEmpty();

    }

    @Test
    public void givenExistingKeyFindMapShouldReturnMap() {
        String key = "key";
        Map value = mock(Map.class);

        properties.put(key, value);

        Map<Object, Object> result = sut.findMap(key);

        assertThat(result).isEqualTo(value);
    }

    @Test
    public void givenEmptyKeyGetPropertiesReaderShouldReturnPropertiesReaderWithMap() {
        String key = "";

        PropertiesReader result = sut.getPropertiesReader(key);

        assertThat(result).isEqualTo(sut);
    }

    @Test
    public void givenExistingKeyGetPropertiesReaderShouldReturnPropertiesReaderWithMap() {
        String key = "key";
        Map value = mock(Map.class);

        properties.put(key, value);

        PropertiesReader result = sut.getPropertiesReader(key);

        assertThat(result).isNotNull();
        assertThat(result.getProperties()).isEqualTo(value);
    }

}
