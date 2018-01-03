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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.TestifyException;

/**
 *
 * @author saden
 */
public class FileSystemUtilTest {

    FileSystemUtil sut;

    @Before
    public void init() {
        sut = new FileSystemUtil();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullCreatePathShouldThrowException() {
        sut.createPath(null);
    }

    @Test
    public void givenOneParamterCreatePathShouldReturnPath() {
        String first = "first";
        String result = sut.createPath(first);

        assertThat(result).isEqualTo("first");
    }

    @Test
    public void givenTwoParamterCreatePathShouldReturnPath() {
        String first = "first";
        String second = "second";

        String result = sut.createPath(first, second);

        assertThat(result).isEqualTo("first/second");
    }

    @Test(expected = NullPointerException.class)
    public void givenNullDeleteDirecotryShouldThrowException() {
        sut.deleteDirectory(null);
    }

    @Test(expected = TestifyException.class)
    public void givenNonExistentDirectoryDeleteDirectoryShouldThrowException() throws
            IOException {
        Path directoryPath = Files.createTempDirectory("FileSystemUtil");
        directoryPath.toFile().delete();

        sut.deleteDirectory(directoryPath.toString());
    }

    @Test
    public void givenValidPathDeleteDirectoryShouldDeleteDirectory() throws IOException {
        Path directory = Files.createTempDirectory("FileSystemUtil");

        sut.deleteDirectory(directory.toString());

        assertThat(directory).doesNotExist();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullRecreateDirecotryShouldThrowException() {
        sut.deleteDirectory(null);
    }

    @Test
    public void givenNonExistentDirectoryRecreateDirectoryCreateDirectory() throws IOException {
        Path directoryPath = Files.createTempDirectory("FileSystemUtil");
        directoryPath.toFile().delete();

        File result = sut.recreateDirectory(directoryPath.toString());

        assertThat(result).exists();
        assertThat(result.list()).isEmpty();

        //delete the directory
        result.delete();
    }

    @Test
    public void givenValidPathRecreateDirectoryShouldRecreateDirectory() throws IOException {
        Path directory = Files.createTempDirectory("FileSystemUtil");
        Files.createTempFile(directory, "FileSystemUtil", "test");
        File directoryFile = directory.toFile();

        assertThat(directoryFile.list()).isNotEmpty();
        File result = sut.recreateDirectory(directory.toString());

        assertThat(result).isDirectory();
        assertThat(result.list()).isEmpty();
    }

    @Test
    public void givenGlobForNonExistentFilesFindClasspathFilesShouldReturnEmptyList() {
        String[] patterns = {"*.testext"};

        Set<Path> result = sut.findClasspathFiles(patterns);

        assertThat(result).isEmpty();
    }

    @Test
    public void givenGlobForExistentFilesFindClasspathFilesShouldReturnList() throws IOException {
        String pattern = String.format(
                "**/{%s,%s}.class",
                FileSystemUtil.class.getSimpleName(),
                FileSystemUtilTest.class.getSimpleName()
        );

        Set<Path> result = sut.findClasspathFiles(new String[]{pattern});

        assertThat(result).hasSize(2);
    }

}
