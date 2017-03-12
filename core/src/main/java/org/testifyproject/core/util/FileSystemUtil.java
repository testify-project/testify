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
package org.testifyproject.core.util;

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
 * A utility class that provides functionality for working with file systems.
 *
 * @author saden
 */
public class FileSystemUtil {

    public static final FileSystemUtil INSTANCE = new FileSystemUtil();

    /**
     * Converts a path string, or a sequence of strings that when joined form a
     * normalized path string.
     *
     * @param first the path string or initial part of the path string
     * @param more additional strings to be joined to form the path string
     * @return the resulting path string
     */
    public String createPath(String first, String... more) {
        return Paths.get(first, more).normalize().toString();
    }

    /**
     * Delete the given directory.
     *
     * @param directoryPath the path of the directory
     */
    public void deleteDirectory(String directoryPath) {
        List<IOException> exceptions = new LinkedList<>();
        File file = Paths.get(directoryPath).normalize().toFile();

        try {
            Files.walkFileTree(file.toPath(), new DeleteDirectoryFileVisitor(exceptions));
        } catch (IOException e) {
            String msg = exceptions.stream()
                    .map(IOException::getMessage)
                    .collect(joining("\n", "[", "]"));

            throw new IllegalStateException(msg, e);
        }
    }

    /**
     * Recreate the given directory. Recreating a directory entails deleting the
     * content of the directory and then recreating the directory. If the
     * directory does not exist it will simply be created.
     *
     * @param directoryPath the directory that will be recreated
     * @return a file instance representing the recreated directory
     */
    public File recreateDirectory(String directoryPath) {
        Path path = Paths.get(directoryPath).normalize();
        File file = path.toFile();

        if (file.exists()) {
            deleteDirectory(path.toString());
        }

        file.mkdirs();

        return file;
    }

    /**
     * A File Visitor implementation that walks through a directory and deletes
     * its content.
     */
    private static class DeleteDirectoryFileVisitor extends SimpleFileVisitor<Path> {

        private final List<IOException> exceptions;

        DeleteDirectoryFileVisitor(List<IOException> exceptions) {
            this.exceptions = exceptions;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.delete(file);

            return CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
            if (e == null) {
                Files.delete(dir);
            } else {
                exceptions.add(e);
            }

            return CONTINUE;
        }
    }

}
