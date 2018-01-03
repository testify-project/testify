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
package org.testifyproject.di.jersey;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.TestContext;

/**
 *
 * @author saden
 */
public class JerseyIntegrationPreVerifierTest {

    JerseyIntegrationPreVerifier sut;

    @Before
    public void init() {
        sut = new JerseyIntegrationPreVerifier();
    }

    @Test
    public void callToVerifyShouldVerifyJerseyInjectionManager() {
        TestContext testContext = mock(TestContext.class);
        String className = "org.glassfish.jersey.inject.hk2.AbstractHk2InjectionManager";

        sut.verify(testContext);

        verify(testContext).addError(eq(false), anyString(), eq(className));
    }

}
