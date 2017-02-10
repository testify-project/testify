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
package org.testify.annotation;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;
import org.testify.ContainerProvider;

/**
 * An annotation that can be placed on integration and system tests to specify
 * container based resources that should be loaded, configured, started, stopped
 * before and after each test run. This is useful when performing system tests
 * using real production environment (i.e. using real PostgresSQL or Cassandra
 * Cluster). It's also useful when performing integration tests where simulating
 * external resources is not prudent (i.e. testing database specific features).
 *
 * @author saden
 */
@Documented
@Retention(RUNTIME)
@Target({ANNOTATION_TYPE, TYPE})
@Repeatable(RequiresContainers.class)
public @interface RequiresContainer {

    /**
     * The name of the container image.
     *
     * @return image name
     */
    String value();

    /**
     * The version of the container image.
     *
     * @return the image version.
     */
    String version() default "latest";

    /**
     * The command that should be used to execute the container.
     *
     * @return the command.
     */
    String cmd() default "";

    /**
     * Auto pull the image from the container a registry.
     *
     * @return true if the image will be pulled, false otherwise
     */
    boolean pull() default true;

    /**
     * <p>
     * The container instance name. This useful for giving the container
     * instance a unique name to distinguish it from other container instances.
     * </p>
     * <p>
     * By default if the name is not specified the {@link #value() image name}
     * will be used as the name of the container instance.
     * </p>
     *
     * @return the unique name of the container instance
     */
    String name() default "";

    /**
     * The number of container nodes to start.
     *
     * @return the number of nodes.
     */
    int nodes() default 1;

    /**
     * A flag that indicates whether to link the
     * {@link #nodes() container nodes}.
     *
     * @return true if the nodes should be linked.
     */
    boolean link() default true;

    /**
     * A flag that indicate whether to wait for all container ports to be
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
     * Max delay for exponentially backoff between port reachability retries.By
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
    long timeout() default 300000;

    /**
     * Time unit for delay, max delay, and duration.
     *
     * @return time unit
     */
    TimeUnit unit() default TimeUnit.MILLISECONDS;

    /**
     * Specifies the required container provider implementation class to use. If
     * a provider is not specified one will be discovered in the class path.
     *
     * @return required container provider implementation class.
     */
    Class<? extends ContainerProvider> provider() default ContainerProvider.class;

}
