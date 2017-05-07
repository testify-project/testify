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
package org.testifyproject;

import java.net.InetAddress;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import org.testifyproject.guava.common.collect.ImmutableMap;

/**
 *
 * @author saden
 */
public class VirtualResourceInstanceTest {

    VirtualResourceInstance sut;

    @Before
    public void init() {
        sut = mock(VirtualResourceInstance.class, Answers.CALLS_REAL_METHODS);
    }

    @Test
    public void callToFindFirstExposedPortShouldReturnFirstPortFound() {
        Integer hostPort = 1000;
        Integer localPort = 2000;
        Map<Integer, Integer> mappedPorts = ImmutableMap.of(hostPort, localPort);

        given(sut.getMappedPorts()).willReturn(mappedPorts);

        Optional<Integer> result = sut.findFirstExposedPort();

        assertThat(result).contains(hostPort);
    }

    @Test
    public void givenSchemeAndPortGetURIShouldReturnURI() {
        String scheme = "http";
        Integer port = 2000;
        InetAddress address = mock(InetAddress.class);
        String hostAddress = "127.0.0.1";

        given(address.getHostAddress()).willReturn(hostAddress);
        given(sut.getAddress()).willReturn(address);

        URI result = sut.getURI(scheme, port);

        assertThat(result)
                .hasScheme(scheme)
                .hasHost(hostAddress)
                .hasPort(port);
    }

}
