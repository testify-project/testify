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
import org.testifyproject.fixture.MethodTestService;

/**
 *
 * @author saden
 */
public class DefaultMethodDescriptorTest {

    MethodDescriptor sut;

    Method method;
    Object instance;

    @Before
    public void init() throws NoSuchMethodException {
        method = MethodTestService.class.getDeclaredMethod("init");
        instance = new Object();

        sut = new DefaultMethodDescriptor(method, instance);
    }

    @Test
    public void validateSutInstance() {
        assertThat(sut).isNotNull();
        assertThat(sut.getMember()).isEqualTo(method);
        assertThat(sut.getInstance()).contains(instance);
    }

    @Test
    public void givenInstanceOfShouldReturn() {
        sut = DefaultMethodDescriptor.of(method);

        assertThat(sut).isNotNull();
        assertThat(sut.getMember()).isEqualTo(method);
        assertThat(sut.getInstance()).isEmpty();
    }

    @Test
    public void givenInstanceAndNameOfShouldReturn() {
        sut = DefaultMethodDescriptor.of(method, instance);

        assertThat(sut).isNotNull();
        assertThat(sut.getMember()).isEqualTo(method);
        assertThat(sut.getInstance()).contains(instance);
    }

    @Test
    public void givenNullInstancesShouldNotBeEqual() {
        assertThat(sut).isNotEqualTo(null);
    }

    @Test
    public void givenDefinedNameGetNameShouldReturnMethodName() throws NoSuchMethodException {
        method = MethodTestService.class.getDeclaredMethod("destroy");
        sut = DefaultMethodDescriptor.of(method, instance);

        assertThat(sut).isNotNull();
        assertThat(sut.getDefinedName()).isEqualTo("kill");
    }

    @Test
    public void givenUndefinedNameGetNameShouldReturnMethodName() {
        sut = DefaultMethodDescriptor.of(method, instance);

        assertThat(sut).isNotNull();
        assertThat(sut.getDefinedName()).isEqualTo(method.getName());
    }

    @Test
    public void givenDifferentTypeInstancesShouldNotBeEqual() {
        String differentType = "instance";

        assertThat(sut).isNotEqualTo(differentType);
        assertThat(sut.hashCode()).isNotEqualTo(differentType.hashCode());
    }

    @Test
    public void givenUnequalInstancesShouldNotBeEqual() {
        MethodDescriptor unequal = DefaultMethodDescriptor.of(method, null);

        assertThat(sut).isNotEqualTo(unequal);
        assertThat(sut.hashCode()).isNotEqualTo(unequal.hashCode());
    }

    @Test
    public void givenSameInstancesShouldBeEqual() {
        assertThat(sut).isEqualTo(sut);
    }

    @Test
    public void givenEqualInstancesShouldBeEqual() {
        MethodDescriptor equal = DefaultMethodDescriptor.of(method, instance);

        assertThat(sut).isEqualTo(equal);
        assertThat(sut.hashCode()).isEqualTo(equal.hashCode());
    }

    @Test
    public void callToToStringShouldReturnHumanReadableString() {
        String result = sut.toString();

        assertThat(result).contains("DefaultMethodDescriptor", "method", "instance");
    }

}
