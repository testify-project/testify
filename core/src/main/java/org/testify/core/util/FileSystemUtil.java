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
package org.testify.core.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import static java.nio.file.FileVisitResult.CONTINUE;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;
import static java.util.stream.Collectors.joining;

/**
 * A utility class that provides common functionality for working with
 * resources.
 *
 * @author saden
 */
public class FileSystemUtil {

    public static final FileSystemUtil INSTANCE = new FileSystemUtil();

    public String createPath(String first, String... second) {
        return Paths.get(first, second).normalize().toString();
    }

    public void deleteDirectory(File directoryPath) {
        final List<IOException> exceptions = new LinkedList<>();

        try {
            Files.walkFileTree(directoryPath.toPath(), new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);

                    return CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
                    if (e == null) {
                        Files.delete(dir);
                    }

                    exceptions.add(e);

                    return CONTINUE;
                }
            });
        } catch (IOException e) {
            String msg = exceptions.stream()
                    .map(IOException::getMessage)
                    .collect(joining("\n", "[", "]"));

            throw new IllegalStateException(msg);
        }
    }

    public File recreateDirectory(String directoryPath) {
        File file = Paths.get(directoryPath).normalize().toFile();

        if (file.exists()) {
            deleteDirectory(file);
        }

        file.mkdirs();

        return file;
    }

}
