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
package org.testifyproject.container.docker.callback;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import org.testifyproject.TestContext;
import org.testifyproject.github.dockerjava.api.async.ResultCallback;
import org.testifyproject.github.dockerjava.api.model.PullResponseItem;
import org.testifyproject.github.dockerjava.api.model.ResponseItem;
import org.testifyproject.annotation.VirtualResource;

/**
 * A callback class that listens for image pull request and prints progress
 * information.
 *
 * @author saden
 */
public class PullCallback implements ResultCallback<PullResponseItem> {

    private final TestContext testContext;
    private final VirtualResource virtualResource;
    private final CountDownLatch latch;

    public PullCallback(TestContext testContext, VirtualResource virtualResource, CountDownLatch latch) {
        this.testContext = testContext;
        this.virtualResource = virtualResource;
        this.latch = latch;
    }

    @Override
    public void onStart(Closeable closeable) {
        testContext.info("Pulling '{}:{}' image", virtualResource.value(), virtualResource.version());
    }

    @Override
    public void onNext(PullResponseItem item) {
        String id = "N/A";

        if (item.getId() != null) {
            id = item.getId();
        }

        ResponseItem.ProgressDetail details = item.getProgressDetail();
        String status = item.getStatus();

        if (details != null
                && details.getCurrent() != null
                && details.getTotal() != null) {
            double current = details.getCurrent();
            double total = details.getTotal();
            double percent = (current / total) * 100;

            System.out.printf("%1$s %2$s (%3$.2f%%)\r", status, id, percent);
        } else if (status != null && !status.contains("Already exists")) {
            System.out.printf("%1$s %2$s\r", status, id);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        testContext.error("Pull failed: ", throwable);
    }

    @Override
    public void onComplete() {
        testContext.info("Image '{}:{}' pulled", virtualResource.value(), virtualResource.version());
        latch.countDown();
    }

    @Override
    public void close() throws IOException {
        testContext.debug("Closing pull of '{}:{}' image", virtualResource.value(), virtualResource.version());
    }

}
