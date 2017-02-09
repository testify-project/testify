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
import java.nio.file.Files;
import java.nio.file.Path;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author saden
 */
public class FileSystemUtilTest {

    FileSystemUtil cut;

    @Before
    public void init() {
        cut = new FileSystemUtil();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullCreatePathShouldThrowException() {
        cut.createPath(null);
    }

    @Test
    public void givenOneParamterCreatePathShouldReturnPath() {
        String first = "first";
        String result = cut.createPath(first);

        assertThat(result).isEqualTo("first");
    }

    @Test
    public void givenTwoParamterCreatePathShouldReturnPath() {
        String first = "first";
        String second = "second";

        String result = cut.createPath(first, second);

        assertThat(result).isEqualTo("first/second");
    }

    @Test(expected = NullPointerException.class)
    public void givenNullDeleteDirecotryShouldThrowException() {
        cut.deleteDirectory(null);
    }

    @Test(expected = IllegalStateException.class)
    public void givenNonExistentDirectoryDeleteDirectoryShouldThrowException() throws IOException {
        Path directoryPath = Files.createTempDirectory("FileSystemUtil");
        directoryPath.toFile().delete();

        cut.deleteDirectory(directoryPath.toString());
    }

    @Test
    public void givenValidPathDeleteDirectoryShouldDeleteDirectory() throws IOException {
        Path directory = Files.createTempDirectory("FileSystemUtil");

        cut.deleteDirectory(directory.toString());

        assertThat(directory).doesNotExist();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullRecreateDirecotryShouldThrowException() {
        cut.deleteDirectory(null);
    }

    @Test
    public void givenNonExistentDirectoryRecreateDirectoryCreateDirectory() throws IOException {
        Path directoryPath = Files.createTempDirectory("FileSystemUtil");
        directoryPath.toFile().delete();

        File result = cut.recreateDirectory(directoryPath.toString());

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
        File result = cut.recreateDirectory(directory.toString());

        assertThat(result).isDirectory();
        assertThat(result.list()).isEmpty();
    }

}
