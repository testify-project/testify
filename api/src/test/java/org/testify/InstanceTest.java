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
package org.testify;

import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 *
 * @author saden
 */
public class InstanceTest {

    Instance<String> cut;

    @Before
    public void init() {
        cut = mock(Instance.class, Answers.CALLS_REAL_METHODS);
    }

    @After
    public void destroy() {
        verifyNoMoreInteractions(cut);
    }

    @Test
    public void callToGetNameShouldReturnEmptyOptional() {
        Optional<String> result = cut.getName();

        assertThat(result).isEmpty();
        verify(cut).getName();
    }

    @Test
    public void callToGetContractShouldReturnEmptyOptional() {
        Optional<Class<? super String>> result = cut.getContract();

        assertThat(result).isEmpty();
        verify(cut).getContract();
    }

}
