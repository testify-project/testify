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
package org.testifyproject.tools.logger;

import static java.time.Instant.ofEpochMilli;
import static java.time.ZoneOffset.UTC;

import static org.fusesource.jansi.Ansi.Color.BLUE;
import static org.fusesource.jansi.Ansi.Color.DEFAULT;
import static org.fusesource.jansi.Ansi.Color.RED;
import static org.fusesource.jansi.Ansi.ansi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.fusesource.jansi.Ansi.Color;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.pattern.Abbreviator;
import ch.qos.logback.classic.pattern.TargetLengthBasedClassNameAbbreviator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.LayoutBase;

/**
 * A custom layout formatter that takes into consideration MDC test class and method names.
 *
 * @author saden
 */
public class TestifyLayout extends LayoutBase<ILoggingEvent> {

    public static final String METHOD_MDC_KEY = "method";
    public static final String TEST_MDC_KEY = "test";
    private final Abbreviator abbreviator = new TargetLengthBasedClassNameAbbreviator(36);

    @Override
    public String doLayout(ILoggingEvent event) {
        StringBuilder builder = new StringBuilder(512);
        Map<String, String> mdcMap = event.getMDCPropertyMap();
        LocalDateTime dateTime = ofEpochMilli(event.getTimeStamp())
                .atZone(UTC)
                .toLocalDateTime();
        String loggerName = event.getLoggerName();
        String foramttedDateTime = paddRight(dateTime.format(
                DateTimeFormatter.ISO_LOCAL_DATE_TIME), 23);

        builder.append("[")
                .append(foramttedDateTime)
                .append("]")
                .append("[")
                .append(getLevelMessage(event.getLevel()))
                .append("]");

        if (mdcMap.containsKey(TEST_MDC_KEY) && mdcMap.containsKey(METHOD_MDC_KEY)) {
            builder.append("[")
                    .append(getMagentaMessage(mdcMap.get(TEST_MDC_KEY)))
                    .append("]")
                    .append("[")
                    .append(getMagentaMessage(mdcMap.get(METHOD_MDC_KEY)))
                    .append("]");
        }

        String abbreviateLoggerName = abbreviator.abbreviate(loggerName);

        builder.append("[")
                .append(getMagentaMessage(abbreviateLoggerName))
                .append("]")
                .append(": ")
                .append(event.getFormattedMessage())
                .append(CoreConstants.LINE_SEPARATOR);

        return builder.toString();
    }

    String getMagentaMessage(String loggerName) {
        return ansi()
                .bold()
                .fgMagenta()
                .a(loggerName)
                .reset()
                .toString();
    }

    String getLevelMessage(Level level) {
        Color color;
        String message = paddRight(level.toString(), 5);

        switch (level.toInt()) {
            case Level.ERROR_INT:
                color = RED;
                break;
            case Level.WARN_INT:
                color = RED;
                break;
            case Level.INFO_INT:
                color = BLUE;
                break;
            default:
                color = DEFAULT;
        }

        return ansi()
                .bold()
                .fg(color)
                .a(message)
                .reset()
                .toString();
    }

    String paddRight(String value, int maxColumns) {
        return String.format("%1$-" + maxColumns + "s", value);
    }
}
