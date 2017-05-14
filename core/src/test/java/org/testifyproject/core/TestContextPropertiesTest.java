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
package org.testifyproject.core;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

/**
 *
 * @author saden
 */
public class TestContextPropertiesTest {
    
    @Test
    public void verifyConstants() {
        assertThat(TestContextProperties.APP).isNotEmpty();
        assertThat(TestContextProperties.APP_ARGUMENTS).isNotEmpty();
        assertThat(TestContextProperties.APP_CONTEXT_PATH).isNotEmpty();
        assertThat(TestContextProperties.APP_NAME).isNotEmpty();
        assertThat(TestContextProperties.APP_PORT).isNotEmpty();
        assertThat(TestContextProperties.APP_SERVLET_CONTAINER).isNotEmpty();
        assertThat(TestContextProperties.APP_SERVLET_CONTEXT).isNotEmpty();
        assertThat(TestContextProperties.BASE_URI).isNotEmpty();
        assertThat(TestContextProperties.SUT_DESCRIPTOR).isNotEmpty();
        assertThat(TestContextProperties.SUT_INSTANCE).isNotEmpty();
        assertThat(TestContextProperties.SERVICE_INSTANCE).isNotEmpty();
    }
    
}
