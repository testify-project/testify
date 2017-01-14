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
package org.testify.container.docker.callback;

import org.testify.github.dockerjava.api.model.WaitResponse;
import org.testify.github.dockerjava.core.command.WaitContainerResultCallback;
import org.testify.TestContext;

/**
 * A callback class that listens for container stop requests and prints progress
 * information.
 *
 * @author saden
 */
public class WaitCallback extends WaitContainerResultCallback {

    private final TestContext testContext;
    private final String containerId;

    public WaitCallback(TestContext testContext, String containerId) {
        this.testContext = testContext;
        this.containerId = containerId;
    }

    @Override
    public void onNext(WaitResponse waitResponse) {
        System.out.printf("Waiting for container %s to stop\r", containerId);
    }
}
