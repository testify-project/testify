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
package org.testify.container.docker;

import org.testify.ContainerInstance;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A Docker implementation of {@link ContainerInstance SPI Contract} that
 * provides information about running docker container.
 *
 * @author saden
 */
public class DockerContainerInstance implements ContainerInstance {

    private final String name;
    private final String host;
    private final List<Integer> ports;

    public static ContainerInstance of(String name, String host, List<Integer> ports) {
        return new DockerContainerInstance(name, host, ports);
    }

    DockerContainerInstance(String name, String host, List<Integer> ports) {
        this.name = name;
        this.host = host;
        this.ports = ports;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public List<Integer> getPorts() {
        return ports;
    }

    @Override
    public Optional<Integer> findFirstPort() {
        return ports.stream().findFirst();
    }

    @Override
    public URI getURI(String scheme, Integer containerPort) {
        String uri = String.format(
                "%s://%s:%d",
                scheme,
                getHost(),
                containerPort
        );

        return URI.create(uri);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.name);
        hash = 41 * hash + Objects.hashCode(this.host);
        hash = 41 * hash + Objects.hashCode(this.ports);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DockerContainerInstance other = (DockerContainerInstance) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.host, other.host)) {
            return false;
        }
        return Objects.equals(this.ports, other.ports);
    }

    @Override
    public String toString() {
        return "DockerContainerInstance{" + "name=" + name + ", host=" + host + ", ports=" + ports + '}';
    }

}
