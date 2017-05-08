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
package org.testifyproject.annotation;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;
import org.testifyproject.VirtualResourceProvider;

/**
 * An annotation that can be placed on integration and system tests to specify
 * virtual resources that should be configured, started, stopped before and
 * after each test run. This is useful when performing integration and system
 * tests using real production environment (i.e. using real PostgresSQL or
 * Cassandra NoSQL).
 *
 * @author saden
 */
@Documented
@Retention(RUNTIME)
@Target({ANNOTATION_TYPE, TYPE})
@Repeatable(VirtualResources.class)
public @interface VirtualResource {

    /**
     * The name of the virtual resource. Note that the name of the virtual
     * resource depends on the virtual resource provider implementation (i.e.
     * for docker the name is the image name).
     *
     * @return image name
     */
    String value();

    /**
     * The version of the virtual resource. Note that the version of the virtual
     * resource depends on the virtual resource provider implementation (i.e.
     * for docker the version is the image version).
     *
     * @return the image version.
     */
    String version() default "latest";

    /**
     * The command used to exesute the virtual resource.
     *
     * @return the command.
     */
    String cmd() default "";

    /**
     * Auto pull the image from the virtual resource a registry.
     *
     * @return true if the image will be pulled, false otherwise
     */
    boolean pull() default true;

    /**
     * <p>
     * The virtual instance name. This useful for giving the virtual instance a
     * unique name to distinguish it from other virtual instances.
     * </p>
     * <p>
     * By default if the name is not specified the {@link #value() image name}
     * will be used as the name of the virtual instance.
     * </p>
     *
     * @return the unique name of the virtual instance
     */
    String name() default "";

    /**
     * The number of virtual resource nodes to start.
     *
     * @return the number of nodes.
     */
    int nodes() default 1;

    /**
     * A flag that indicates whether to link the
     * {@link #nodes() virtual resource nodes}.
     *
     * @return true if the nodes should be linked.
     */
    boolean link() default true;

    /**
     * A flag that indicate whether to wait for all virtual resource ports to be
     * reachable.
     *
     * @return wait for ports to be reachable if true, false otherwise.
     */
    boolean await() default true;

    /**
     * The amount of time to wait between port reachability retries. By default
     * the time unit is milliseconds and the time unit can be set via
     * {@link #unit()}.
     *
     * @return delay time between retries
     */
    long delay() default 1000;

    /**
     * Max delay for exponentially backoff between port reachability retries. By
     * default the time unit is milliseconds and the time unit can be set via
     * {@link #unit()}.
     *
     * @return max delay
     */
    long maxDelay() default 8000;

    /**
     * Maximum number of retries before giving up waiting for ports to be
     * reachable.
     *
     * @return max retries.
     */
    int maxRetries() default 3;

    /**
     * Maximum retry duration before giving up waiting for ports to be
     * reachable. By default the time unit is milliseconds and the time unit can
     * be set via {@link #unit()}.
     *
     *
     * @return max retries.
     */
    long maxDuration() default 8000;

    /**
     * Maximum duration to wait for an image to be pulled before giving up
     * entirely. By default the time unit is milliseconds and the time unit can
     * be set via {@link #unit()}.
     *
     * @return timeout duration.
     */
    long timeout() default 60000;

    /**
     * Time unit for delay, max delay, and duration.
     *
     * @return time unit
     */
    TimeUnit unit() default TimeUnit.MILLISECONDS;

    /**
     * Specifies the virtual resource provider implementation class to use. If a
     * provider is not specified one will be discovered in the class path.
     *
     * @return virtual resource provider implementation class.
     */
    Class<? extends VirtualResourceProvider> provider() default VirtualResourceProvider.class;

}
