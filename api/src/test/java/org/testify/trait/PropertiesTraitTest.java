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
package org.testify.trait;

import java.util.HashMap;
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

    PropertiesTrait cut;
    Map properties;

    @Before
    public void init() {
        cut = mock(PropertiesTrait.class, Answers.CALLS_REAL_METHODS);
        properties = new HashMap();

        given(cut.getProperties()).willReturn(properties);
    }

    @Test
    public void callToGetPropertiesShouldReturn() {
        Map<String, Object> result = cut.getProperties();

        assertThat(result).isEmpty();
    }

    @Test
    public void givenNullKeyGetPropertyShouldReturn() {
        String key = null;

        Optional<Object> result = cut.findProperty(key);

        assertThat(result).isNotNull();
    }

    @Test
    public void givenNonExistentKeyGetPropertyShouldReturnEmptyOptional() {
        String key = "non";

        Optional<Object> result = cut.findProperty(key);

        assertThat(result).isEmpty();
    }

    @Test
    public void givenExistentKeyGetPropertyShouldReturnOptionalWithValue() {
        String key = "key";
        String value = "value";

        properties.put(key, value);

        Optional<Object> result = cut.findProperty(key);

        assertThat(result).contains(value);
    }

    @Test
    public void givenKeyAndValueAddPropertyShouldAddProperty() {
        String key = "key";
        String value = "value";

        cut.addProperty(key, value);

        assertThat(properties).containsEntry(key, value);
    }

}
