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

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import static org.mockito.Mockito.mock;

/**
 *
 * @author saden
 */
public class ReportingTraitTest {

    ReportingTrait cut;

    @Before
    public void init() {
        cut = mock(ReportingTrait.class, Answers.CALLS_REAL_METHODS);
    }

    @Test(expected = NullPointerException.class)
    public void givenNullMessageReportInformationShouldThrowException() {
        String message = null;

        cut.reportInformation(message);
    }

    @Test
    public void givenMessageReportInformationShouldAddMessage() {
        String message = "information";

        cut.reportInformation(message);

        assertThat(cut.hasInformation()).isTrue();
        assertThat(cut.getInformation()).contains(message);
    }

    @Test(expected = NullPointerException.class)
    public void givenNullMessageReportWarningShouldThrowException() {
        String message = null;
        cut.reportWarning(message);
    }

    @Test
    public void givenMessageReportWarningShouldAddMessage() {
        String message = "warning";

        cut.reportWarning(message);

        assertThat(cut.hasWarnings()).isTrue();
        assertThat(cut.getWarnings()).contains(message);
    }

    @Test(expected = NullPointerException.class)
    public void givenNullMessageReportErrorShouldThrowException() {
        String message = null;

        cut.reportError(message);
    }

    @Test
    public void givenMessageReportErrorShouldAddMessage() {
        String message = "erroring";

        cut.reportError(message);

        assertThat(cut.hasErrors()).isTrue();
        assertThat(cut.getErrors()).contains(message);
    }
}
