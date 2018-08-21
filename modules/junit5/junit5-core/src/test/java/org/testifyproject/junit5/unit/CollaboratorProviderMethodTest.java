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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.testifyproject.annotation.CollaboratorProvider;
import org.testifyproject.annotation.Name;
import org.testifyproject.annotation.Sut;
import org.testifyproject.junit5.UnitTest;
import org.testifyproject.junit5.fixture.collaborator.CollaboratorType;
import org.testifyproject.junit5.fixture.collaborator.InvidualCollaboratorProvider;
import org.testifyproject.junit5.fixture.common.Hello;

/**
 * TODO.
 *
 * @author saden
 */
@CollaboratorProvider(InvidualCollaboratorProvider.class)
@UnitTest
public class CollaboratorProviderMethodTest {

    @Sut
    CollaboratorType sut;

    @Test
    public void verifyInjections(@Name("helloProvider") Hello helloProvider) {
        assertThat(sut).isNotNull();
        assertThat(helloProvider).isNotNull();
    }
}
