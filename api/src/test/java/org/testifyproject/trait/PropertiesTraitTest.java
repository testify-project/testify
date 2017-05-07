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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 *
 * @author saden
 */
public class PropertiesTraitTest {

    PropertiesTrait sut;
    Map properties;

    @Before
    public void init() {
        sut = mock(PropertiesTrait.class, Answers.CALLS_REAL_METHODS);
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
    public void givenKeyAndValueAddPropertyShouldAddProperty() {
        String key = "key";
        String value = "value";

        sut.addProperty(key, value);

        assertThat(properties).containsEntry(key, value);
    }

    @Test
    public void givenNonExistingKeyFindListShouldReturnEmptyList() {
        String key = "key";

        List<Object> result = sut.findList(key);

        assertThat(result).isEmpty();

    }

    @Test
    public void givenExistingKeyFindListShouldReturnList() {
        String key = "key";
        List value = mock(List.class);

        properties.put(key, value);

        List<Object> result = sut.findList(key);

        assertThat(result).isEqualTo(value);
    }

    @Test
    public void givenKeyAddListElementShouldReturnElement() {
        String key = "key";
        String element = "element";

        sut.addListElement(key, element);

        List<String> result = (List) properties.get(key);

        assertThat(result).containsExactly(element);
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
    public void givenKeyAddMapEntryShouldReturnElement() {
        String key = "key";
        String entryKey = "entryKey";
        String entryValue = "entryValue";

        sut.addMapEntry(key, entryKey, entryValue);

        Map<Object, Object> result = (Map) properties.get(key);

        assertThat(result)
                .containsOnlyKeys(entryKey)
                .containsValue(entryValue);
    }

}
