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
import java.util.Map;
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
    public void callToIsEmptyShouldReturnTrue() {
        Boolean result = sut.isEmpty();

        assertThat(result).isTrue();
    }

}
