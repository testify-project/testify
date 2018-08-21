/*
 * Copyright 2016-2018 Testify Project.
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
package org.testifyproject.junit5.unit;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.testifyproject.annotation.Fake;
import org.testifyproject.annotation.Sut;
import org.testifyproject.junit5.UnitTest;
import org.testifyproject.junit5.fixture.ImplicitTypeDistinctTypes;
import org.testifyproject.junit5.fixture.common.Hello;

/**
 *
 * @author saden
 */
@UnitTest
public class MissingFakeDeclarationTest {

    @Sut
    ImplicitTypeDistinctTypes sut;

    @Fake
    Hello hello;

    @Test
    public void givenMissingMockShouldThrowException() {
        assertThrows(NullPointerException.class, () -> {
            sut.execute();
        });
    }

}
