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
package org.testify.di.hk2;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import org.glassfish.hk2.api.DescriptorFileFinder;

/**
 * A helper class that helps discover HK2 Descriptor files in the classpath.
 *
 * @author saden
 */
public class HK2DescriptorPopulator implements DescriptorFileFinder {

    private final ClassLoader classLoader;
    private final String[] resources;

    public HK2DescriptorPopulator(String... resources) {
        this(HK2DescriptorPopulator.class.getClassLoader(), resources);
    }

    public HK2DescriptorPopulator(ClassLoader classLoader, String... resources) {
        this.classLoader = classLoader;
        this.resources = resources;
    }

    @Override
    public List<InputStream> findDescriptorFiles() throws IOException {
        List<InputStream> files = new LinkedList<>();

        for (String resource : resources) {
            Enumeration<URL> urls = classLoader.getResources(resource);

            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();

                files.add(url.openStream());
            }

        }

        return files;
    }

}
