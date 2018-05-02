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
package org.testifyproject.core.util.logger;

import static java.time.Instant.ofEpochMilli;
import static java.time.ZoneOffset.UTC;

import static org.fusesource.jansi.Ansi.ansi;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.slf4j.event.LoggingEvent;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.LocationAwareLogger;
import org.testifyproject.core.TestContextHolder;

/**
 * <p>
 * Simple implementation of {@link org.slf4j.Logger} that sends all enabled log messages, for
 * all defined loggers, to the console ({@code System.err}). The following system properties are
 * supported to configure the behavior of this logger:
 * </p>
 *
 * <ul>
 * <li><code>org.testifyproject.logger.logFile</code> - The output target which can be the
 * <em>path</em> to a file, or the special values "System.out" and "System.err". Default is
 * "System.err".</li>
 *
 * <li><code>org.testifyproject.logger.cacheOutputStream</code> - If the output target is set to
 * "System.out" or "System.err" (see preceding entry), by default, logs will be output to the
 * latest value referenced by <code>System.out/err</code> variables. By setting this parameter
 * to true, the output stream will be cached, i.e. assigned once at initialization time and
 * re-used independently of the current value referenced by <code>System.out/err</code>.
 * </li>
 *
 * <li><code>org.testifyproject.logger.defaultLogLevel</code> - Default log level for all
 * instances of SimpleLogger. Must be one of ("trace", "debug", "info", "warn", "error" or
 * "off"). If not specified, defaults to "info".</li>
 *
 * <li><code>org.testifyproject.logger.log.<em>a.b.c</em></code> - Logging detail level for a
 * SimpleLogger instance named "a.b.c". Right-side value must be one of "trace", "debug",
 * "info", "warn", "error" or "off". When a SimpleLogger named "a.b.c" is initialized, its level
 * is assigned from this property. If unspecified, the level of nearest parent logger will be
 * used, and if none is set, then the value specified by
 * <code>org.testifyproject.logger.defaultLogLevel</code> will be used.</li>
 *
 * <li><code>org.testifyproject.logger.showDateTime</code> - Set to <code>true</code> if you
 * want the current date and time to be included in output messages. Default is
 * <code>false</code></li>
 *
 * <li><code>org.testifyproject.logger.dateTimeFormat</code> - The date and time format to be
 * used in the output messages. The pattern describing the date and time format is defined by <a href=
 * "http://docs.oracle.com/javase/1.5.0/docs/api/java/text/SimpleDateFormat.html">
 * <code>SimpleDateFormat</code></a>. If the format is not specified or is invalid, the number
 * of milliseconds since start up will be output.</li>
 *
 * <li><code>org.testifyproject.logger.showThreadName</code> -Set to <code>true</code> if you
 * want to output the current thread name. Defaults to <code>true</code>.</li>
 *
 * <li><code>org.testifyproject.logger.showLogName</code> - Set to <code>true</code> if you want
 * the Logger instance name to be included in output messages. Defaults to
 * <code>true</code>.</li>
 *
 * <li><code>org.testifyproject.logger.showShortLogName</code> - Set to <code>true</code> if you
 * want the last component of the name to be included in output messages. Defaults to
 * <code>false</code>.</li>
 *
 * <li><code>org.testifyproject.logger.levelInBrackets</code> - Should the level string be
 * output in brackets? Defaults to <code>false</code>.</li>
 *
 * <li><code>org.testifyproject.logger.warnLevelString</code> - The string value output for the
 * warn level. Defaults to <code>WARN</code>.</li>
 *
 * </ul>
 *
 * <p>
 * In addition to looking for system properties with the names specified above, this
 * implementation also checks for a class loader resource named
 * <code>"simplelogger.properties"</code>, and includes any matching definitions from this
 * resource (if it exists).
 * </p>
 *
 * <p>
 * With no configuration, the default output includes the relative time in milliseconds, thread
 * name, the level, logger name, and the message followed by the line separator for the host. In
 * log4j terms it amounts to the "%r [%t] %level %logger - %m%n" pattern.
 * </p>
 * <p>
 * Sample output follows.
 * </p>
 *
 * <pre>
 * 176 [main] INFO examples.Sort - Populating an array of 2 elements in reverse order.
 * 225 [main] INFO examples.SortAlgo - Entered the sort method.
 * 304 [main] INFO examples.SortAlgo - Dump of integer array:
 * 317 [main] INFO examples.SortAlgo - Element [0] = 0
 * 331 [main] INFO examples.SortAlgo - Element [1] = 1
 * 343 [main] INFO examples.Sort - The next log statement should be an error message.
 * 346 [main] ERROR examples.SortAlgo - Tried to dump an uninitialized array.
 *   at org.log4j.examples.SortAlgo.dump(SortAlgo.java:58)
 *   at org.log4j.examples.Sort.main(Sort.java:64)
 * 467 [main] INFO  examples.Sort - Exiting main method.
 * </pre>
 *
 * <p>
 * This implementation is heavily inspired by
 * <a href="http://commons.apache.org/logging/">Apache Commons Logging</a>'s SimpleLog.
 * </p>
 *
 * @author Ceki G&uuml;lc&uuml;
 * @author Scott Sanders
 * @author Rod Waldhoff
 * @author Robert Burrell Donkin
 * @author C&eacute;drik LIME
 */
public class SimpleLogger extends MarkerIgnoringBase {

    private static final long serialVersionUID = -632788891211436180L;

    public static final String METHOD_MDC_KEY = "method";
    public static final String TEST_MDC_KEY = "test";
    private static final long START_TIME = System.currentTimeMillis();

    protected static final int LOG_LEVEL_TRACE = LocationAwareLogger.TRACE_INT;
    protected static final int LOG_LEVEL_DEBUG = LocationAwareLogger.DEBUG_INT;
    protected static final int LOG_LEVEL_INFO = LocationAwareLogger.INFO_INT;
    protected static final int LOG_LEVEL_WARN = LocationAwareLogger.WARN_INT;
    protected static final int LOG_LEVEL_ERROR = LocationAwareLogger.ERROR_INT;
    // The OFF level can only be used in configuration files to disable logging.
    // It has
    // no printing method associated with it in o.s.Logger interface.
    protected static final int LOG_LEVEL_OFF = LOG_LEVEL_ERROR + 10;

    private static boolean initialized = false;
    static SimpleLoggerConfiguration configParams = null;

    public static void lazyInit() {
        if (initialized) {
            return;
        }
        initialized = true;
        init();
    }

    // external software might be invoking this method directly. Do not rename
    // or change its semantics.
    static void init() {
        configParams = new SimpleLoggerConfiguration();
        configParams.init();
    }

    /**
     * The current log level.
     */
    protected int currentLogLevel = LOG_LEVEL_INFO;
    /**
     * The short name of this simple log instance.
     */
    private transient String shortLogName = null;

    /**
     * All system properties used by <code>SimpleLogger</code> start with this prefix.
     */
    public static final String SYSTEM_PREFIX = "org.testifyproject.";
    public static final String LOG_LEVEL = "logLevel";

    public static final String LOG_KEY_PREFIX = SimpleLogger.SYSTEM_PREFIX + "log.";

    public static final String CACHE_OUTPUT_STREAM_STRING_KEY = SimpleLogger.SYSTEM_PREFIX
            + "cacheOutputStream";

    public static final String WARN_LEVEL_STRING_KEY = SimpleLogger.SYSTEM_PREFIX
            + "warnLevelString";

    public static final String LEVEL_IN_BRACKETS_KEY = SimpleLogger.SYSTEM_PREFIX
            + "levelInBrackets";

    public static final String LOG_FILE_KEY = SimpleLogger.SYSTEM_PREFIX + "logFile";

    public static final String SHOW_SHORT_LOG_NAME_KEY = SimpleLogger.SYSTEM_PREFIX
            + "showShortLogName";

    public static final String SHOW_LOG_NAME_KEY = SimpleLogger.SYSTEM_PREFIX + "showLogName";

    public static final String SHOW_THREAD_NAME_KEY = SimpleLogger.SYSTEM_PREFIX
            + "showThreadName";

    public static final String DATE_TIME_FORMAT_KEY = SimpleLogger.SYSTEM_PREFIX
            + "dateTimeFormat";

    public static final String SHOW_DATE_TIME_KEY = SimpleLogger.SYSTEM_PREFIX + "showDateTime";

    public static final String DEFAULT_LOG_LEVEL_KEY = SimpleLogger.SYSTEM_PREFIX
            + "defaultLogLevel";

    /**
     * Create a new simple logger instance with the given name.
     *
     * @param name the logger name
     */
    public SimpleLogger(String name) {
        this.name = name;

        String levelString = recursivelyComputeLevelString();
        if (levelString != null) {
            this.currentLogLevel = SimpleLoggerConfiguration.stringToLevel(levelString);
        } else {
            this.currentLogLevel = configParams.defaultLogLevel;
        }
    }

    String recursivelyComputeLevelString() {
        return configParams.getStringProperty(SYSTEM_PREFIX + LOG_LEVEL, null);
    }

    /**
     * This is our internal implementation for logging regular (non-parameterized) log messages.
     *
     * @param level One of the LOG_LEVEL_XXX constants defining the log level
     * @param message The message itself
     * @param t The exception whose stack trace should be logged
     */
    private void log(int level, String message, Throwable t) {
        if (!isLevelEnabled(level)) {
            return;
        }

        StringBuilder builder = new StringBuilder(512);
        LocalDateTime dateTime = ofEpochMilli(System.currentTimeMillis())
                .atZone(UTC)
                .toLocalDateTime();
        String foramttedDateTime = paddRight(dateTime.format(
                DateTimeFormatter.ISO_LOCAL_DATE_TIME), 23);

        builder.append("[")
                .append(foramttedDateTime)
                .append("]")
                .append("[")
                .append(renderLevel(level))
                .append("]");

        builder.append("[")
                .append(getMagentaMessage(name))
                .append("]");

        TestContextHolder.INSTANCE.command(context -> {
            builder.append("[")
                    .append(getMagentaMessage(context.getTestName()))
                    .append("]");

            builder.append("[")
                    .append(getMagentaMessage(context.getMethodName()))
                    .append("]");
        });

        builder.append(": ")
                .append(message);

        write(builder, t);
    }

    String paddRight(String value, int maxColumns) {
        return String.format("%1$-" + maxColumns + "s", value);
    }

    String getMagentaMessage(String loggerName) {
        return ansi()
                .bold()
                .fgMagenta()
                .a(loggerName)
                .reset()
                .toString();
    }

    protected String renderLevel(int level) {
        switch (level) {
            case LOG_LEVEL_TRACE:
                return "TRACE";
            case LOG_LEVEL_DEBUG:
                return "DEBUG";
            case LOG_LEVEL_INFO:
                return "INFO";
            case LOG_LEVEL_WARN:
                return configParams.warnLevelString;
            case LOG_LEVEL_ERROR:
                return "ERROR";
        }
        throw new IllegalStateException("Unrecognized level [" + level + "]");
    }

    void write(StringBuilder buf, Throwable t) {
        PrintStream targetStream = configParams.outputChoice.getTargetPrintStream();

        targetStream.println(buf.toString());
        writeThrowable(t, targetStream);
        targetStream.flush();
    }

    protected void writeThrowable(Throwable t, PrintStream targetStream) {
        if (t != null) {
            t.printStackTrace(targetStream);
        }
    }

    private String getFormattedDate() {
        Date now = new Date();
        String dateText;
        synchronized (configParams.dateFormatter) {
            dateText = configParams.dateFormatter.format(now);
        }
        return dateText;
    }

    private String computeShortName() {
        return name.substring(name.lastIndexOf(".") + 1);
    }

    /**
     * For formatted messages, first substitute arguments and then log.
     *
     * @param level
     * @param format
     * @param arg1
     * @param arg2
     */
    private void formatAndLog(int level, String format, Object arg1, Object arg2) {
        if (!isLevelEnabled(level)) {
            return;
        }
        FormattingTuple tp = MessageFormatter.format(format, arg1, arg2);
        log(level, tp.getMessage(), tp.getThrowable());
    }

    /**
     * For formatted messages, first substitute arguments and then log.
     *
     * @param level the logging level
     * @param format the message format
     * @param arguments a list of 3 ore more arguments
     */
    private void formatAndLog(int level, String format, Object... arguments) {
        if (!isLevelEnabled(level)) {
            return;
        }
        FormattingTuple tp = MessageFormatter.arrayFormat(format, arguments);
        log(level, tp.getMessage(), tp.getThrowable());
    }

    /**
     * Is the given log level currently enabled.
     *
     * @param logLevel is this level enabled.
     * @return true if enabled, false otherwise
     */
    protected boolean isLevelEnabled(int logLevel) {
        // log level are numerically ordered so can use simple numeric
        // comparison
        return logLevel >= currentLogLevel;
    }

    /**
     * Are {@code trace} messages currently enabled.
     *
     * @return true if enabled, false otherwise
     */
    @Override
    public boolean isTraceEnabled() {
        return isLevelEnabled(LOG_LEVEL_TRACE);
    }

    /**
     * A simple implementation which logs messages of level TRACE according to the format
     * outlined above.
     *
     * @param msg the message
     */
    @Override
    public void trace(String msg) {
        log(LOG_LEVEL_TRACE, msg, null);
    }

    /**
     * Perform single parameter substitution before logging the message of level TRACE according
     * to the format outlined above.
     *
     * @param format message format
     * @param arg1 first message parameter
     */
    @Override
    public void trace(String format, Object arg1) {
        formatAndLog(LOG_LEVEL_TRACE, format, arg1, null);
    }

    /**
     * Perform double parameter substitution before logging the message of level TRACE according
     * to the format outlined above.
     *
     * @param format message format
     * @param arg1 first message parameter
     * @param arg2 second message parameter
     */
    @Override
    public void trace(String format, Object arg1, Object arg2) {
        formatAndLog(LOG_LEVEL_TRACE, format, arg1, arg2);
    }

    /**
     * Perform double parameter substitution before logging the message of level TRACE according
     * to the format outlined above.
     *
     * @param format message format
     * @param argArray message parameters
     */
    @Override
    public void trace(String format, Object... argArray) {
        formatAndLog(LOG_LEVEL_TRACE, format, argArray);
    }

    /**
     * Log a message of level TRACE, including an exception.
     *
     * @param msg the message
     * @param t the error
     */
    @Override
    public void trace(String msg, Throwable t) {
        log(LOG_LEVEL_TRACE, msg, t);
    }

    /**
     * Are {@code debug} messages currently enabled.
     *
     * @return true if enabled, false otherwise
     */
    @Override
    public boolean isDebugEnabled() {
        return isLevelEnabled(LOG_LEVEL_DEBUG);
    }

    /**
     * A simple implementation which logs messages of level DEBUG according to the format
     * outlined above.
     *
     * @param msg the message
     */
    @Override
    public void debug(String msg) {
        log(LOG_LEVEL_DEBUG, msg, null);
    }

    /**
     * Perform single parameter substitution before logging the message of level DEBUG according
     * to the format outlined above.
     *
     * @param format message format
     * @param arg1 first message parameter
     */
    @Override
    public void debug(String format, Object arg1) {
        formatAndLog(LOG_LEVEL_DEBUG, format, arg1, null);
    }

    /**
     * Perform double parameter substitution before logging the message of level DEBUG according
     * to the format outlined above.
     *
     * @param format message format
     * @param arg1 first message parameter
     * @param arg2 second message parameter
     */
    @Override
    public void debug(String format, Object arg1, Object arg2) {
        formatAndLog(LOG_LEVEL_DEBUG, format, arg1, arg2);
    }

    /**
     * Perform double parameter substitution before logging the message of level DEBUG according
     * to the format outlined above.
     *
     * @param format message format
     * @param argArray message parameters
     */
    @Override
    public void debug(String format, Object... argArray) {
        formatAndLog(LOG_LEVEL_DEBUG, format, argArray);
    }

    /**
     * Log a message of level DEBUG, including an exception.
     *
     * @param msg the message
     * @param t the error
     */
    @Override
    public void debug(String msg, Throwable t) {
        log(LOG_LEVEL_DEBUG, msg, t);
    }

    /**
     * Are {@code info} messages currently enabled.
     *
     * @return true if enabled, false otherwise
     */
    @Override
    public boolean isInfoEnabled() {
        return isLevelEnabled(LOG_LEVEL_INFO);
    }

    /**
     * A simple implementation which logs messages of level INFO according to the format
     * outlined above.
     *
     * @param msg the message
     */
    @Override
    public void info(String msg) {
        log(LOG_LEVEL_INFO, msg, null);
    }

    /**
     * Perform single parameter substitution before logging the message of level INFO according
     * to the format outlined above.
     *
     * @param format message format
     * @param arg1 first message parameter
     */
    @Override
    public void info(String format, Object arg1) {
        formatAndLog(LOG_LEVEL_INFO, format, arg1, null);
    }

    /**
     * Perform double parameter substitution before logging the message of level INFO according
     * to the format outlined above.
     *
     * @param format message format
     * @param arg1 first message parameter
     * @param arg2 second message parameter
     */
    @Override
    public void info(String format, Object arg1, Object arg2) {
        formatAndLog(LOG_LEVEL_INFO, format, arg1, arg2);
    }

    /**
     * Perform double parameter substitution before logging the message of level INFO according
     * to the format outlined above.
     *
     * @param format message format
     * @param argArray message parameters
     */
    @Override
    public void info(String format, Object... argArray) {
        formatAndLog(LOG_LEVEL_INFO, format, argArray);
    }

    /**
     * Log a message of level INFO, including an exception.
     *
     * @param msg the message
     * @param t the error
     */
    @Override
    public void info(String msg, Throwable t) {
        log(LOG_LEVEL_INFO, msg, t);
    }

    /**
     * Are {@code warn} messages currently enabled.
     *
     * @return true if enabled, false otherwise
     */
    @Override
    public boolean isWarnEnabled() {
        return isLevelEnabled(LOG_LEVEL_WARN);
    }

    /**
     * A simple implementation which always logs messages of level WARN according to the format
     * outlined above.
     *
     * @param msg the message
     */
    @Override
    public void warn(String msg) {
        log(LOG_LEVEL_WARN, msg, null);
    }

    /**
     * Perform single parameter substitution before logging the message of level WARN according
     * to the format outlined above.
     *
     * @param format message format
     * @param arg1 first message parameter
     */
    @Override
    public void warn(String format, Object arg1) {
        formatAndLog(LOG_LEVEL_WARN, format, arg1, null);
    }

    /**
     * Perform double parameter substitution before logging the message of level WARN according
     * to the format outlined above.
     *
     * @param format message format
     * @param arg1 first message parameter
     * @param arg2 second message parameter
     */
    @Override
    public void warn(String format, Object arg1, Object arg2) {
        formatAndLog(LOG_LEVEL_WARN, format, arg1, arg2);
    }

    /**
     * Perform double parameter substitution before logging the message of level WARN according
     * to the format outlined above.
     *
     * @param format message format
     * @param argArray message parameters
     */
    @Override
    public void warn(String format, Object... argArray) {
        formatAndLog(LOG_LEVEL_WARN, format, argArray);
    }

    /**
     * Log a message of level WARN, including an exception.
     *
     * @param msg the message
     * @param t the error
     */
    @Override
    public void warn(String msg, Throwable t) {
        log(LOG_LEVEL_WARN, msg, t);
    }

    /**
     * Are {@code error} messages currently enabled.
     *
     * @return true if enabled, false otherwise
     */
    @Override
    public boolean isErrorEnabled() {
        return isLevelEnabled(LOG_LEVEL_ERROR);
    }

    /**
     * A simple implementation which always logs messages of level ERROR according to the format
     * outlined above.
     *
     * @param msg the message
     */
    @Override
    public void error(String msg) {
        log(LOG_LEVEL_ERROR, msg, null);
    }

    /**
     * Perform single parameter substitution before logging the message of level ERROR according
     * to the format outlined above.
     *
     * @param format message format
     * @param arg1 first message parameter
     */
    @Override
    public void error(String format, Object arg1) {
        formatAndLog(LOG_LEVEL_ERROR, format, arg1, null);
    }

    /**
     * Perform double parameter substitution before logging the message of level ERROR according
     * to the format outlined above.
     *
     * @param format message format
     * @param arg1 first message parameter
     * @param arg2 second message parameter
     */
    @Override
    public void error(String format, Object arg1, Object arg2) {
        formatAndLog(LOG_LEVEL_ERROR, format, arg1, arg2);
    }

    /**
     * Perform double parameter substitution before logging the message of level ERROR according
     * to the format outlined above.
     *
     * @param format message format
     * @param argArray message parameters
     */
    @Override
    public void error(String format, Object... argArray) {
        formatAndLog(LOG_LEVEL_ERROR, format, argArray);
    }

    /**
     * Log a message of level ERROR, including an exception.
     *
     * @param msg the message
     * @param t the error
     */
    @Override
    public void error(String msg, Throwable t) {
        log(LOG_LEVEL_ERROR, msg, t);
    }

    public void log(LoggingEvent event) {
        int levelInt = event.getLevel().toInt();

        if (!isLevelEnabled(levelInt)) {
            return;
        }
        FormattingTuple tp = MessageFormatter.arrayFormat(event.getMessage(), event
                .getArgumentArray(), event.getThrowable());
        log(levelInt, tp.getMessage(), event.getThrowable());
    }

}
