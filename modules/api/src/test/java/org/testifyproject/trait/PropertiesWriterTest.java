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
import java.util.Map;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;

/**
 *
 * @author saden
 */
public class PropertiesWriterTest {

    PropertiesWriter sut;
    Map properties;

    @Before
    public void init() {
        sut = mock(PropertiesWriter.class, Answers.CALLS_REAL_METHODS);
        properties = new HashMap();

        given(sut.getProperties()).willReturn(properties);
    }

    @Test
    public void callToGetPropertiesShouldReturnEmptyResult() {
        Map<String, Object> result = sut.getProperties();

        assertThat(result).isEmpty();
    }

    @Test
    public void callToComputeIfAbsentWithAbsentKeyShouldAddProperty() {
        String key = "key";
        String value = "value";
        Function<String, String> function = p -> value;

        sut.computeIfAbsent(key, function);

        assertThat(properties).containsEntry(key, value);
    }

    @Test
    public void givenKeyAndValueAddPropertyShouldAddProperty() {
        String key = "key";
        String value = "value";

        sut.addProperty(key, value);

        assertThat(properties).containsEntry(key, value);
    }

    @Test
    public void givenKeyaddCollectionElementShouldReturnElement() {
        String key = "key";
        String element = "element";

        sut.addCollectionElement(key, element);

        Collection<String> result = (Collection) properties.get(key);

        assertThat(result).containsExactly(element);
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
