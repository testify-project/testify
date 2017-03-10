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
package org.testifyproject.core.analyzer;

import java.lang.reflect.Method;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.testifyproject.MethodDescriptor;
import org.testifyproject.fixture.analyzer.AnalyzedTestClass;

/**
 *
 * @author saden
 */
public class DefaultMethodDescriptorTest {

    MethodDescriptor cut;

    Method method;
    Object instance;

    @Before
    public void init() throws NoSuchMethodException {
        method = AnalyzedTestClass.class.getDeclaredMethod("collaborators");
        instance = new Object();

        cut = new DefaultMethodDescriptor(method, instance);
    }

    @Test
    public void validateCutInstance() {
        assertThat(cut).isNotNull();
        assertThat(cut.getMember()).isEqualTo(method);
        assertThat(cut.getInstance()).contains(instance);
    }

    @Test
    public void givenInstanceOfShouldReturn() {
        cut = DefaultMethodDescriptor.of(method);

        assertThat(cut).isNotNull();
        assertThat(cut.getMember()).isEqualTo(method);
        assertThat(cut.getInstance()).isEmpty();
    }

    @Test
    public void givenInstanceAndNameOfShouldReturn() {
        cut = DefaultMethodDescriptor.of(method, instance);

        assertThat(cut).isNotNull();
        assertThat(cut.getMember()).isEqualTo(method);
        assertThat(cut.getInstance()).contains(instance);
    }

    @Test
    public void givenNullInstancesShouldNotBeEqual() {
        assertThat(cut).isNotEqualTo(null);
    }

    @Test
    public void givenDifferentTypeInstancesShouldNotBeEqual() {
        String differentType = "instance";

        assertThat(cut).isNotEqualTo(differentType);
        assertThat(cut.hashCode()).isNotEqualTo(differentType.hashCode());
    }

    @Test
    public void givenUnequalInstancesShouldNotBeEqual() {
        MethodDescriptor unequal = DefaultMethodDescriptor.of(method, null);

        assertThat(cut).isNotEqualTo(unequal);
        assertThat(cut.hashCode()).isNotEqualTo(unequal.hashCode());
    }

    @Test
    public void givenSameInstancesShouldBeEqual() {
        assertThat(cut).isEqualTo(cut);
    }

    @Test
    public void givenEqualInstancesShouldBeEqual() {
        MethodDescriptor equal = DefaultMethodDescriptor.of(method, instance);

        assertThat(cut).isEqualTo(equal);
        assertThat(cut.hashCode()).isEqualTo(equal.hashCode());
    }

    @Test
    public void callToToStringShouldReturnHumanReadableString() {
        String result = cut.toString();

        assertThat(result).contains("DefaultMethodDescriptor", "method", "instance");
    }

}
