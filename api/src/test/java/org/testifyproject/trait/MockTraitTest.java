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
package org.testifyproject.trait;

import java.lang.reflect.Field;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import org.testifyproject.annotation.Fake;
import org.testifyproject.annotation.Fixture;
import org.testifyproject.annotation.Real;
import org.testifyproject.annotation.Virtual;
import org.testifyproject.fixture.InjectableFieldService;

/**
 *
 * @author saden
 */
public class MockTraitTest {

    MockTrait cut;

    @Before
    public void init() {
        cut = mock(MockTrait.class, Answers.CALLS_REAL_METHODS);
    }

    @Test
    public void givenNonRealFieldGetRealShouldReturnEmptyOptional() throws NoSuchFieldException {
        Field field = InjectableFieldService.class.getDeclaredField("non");

        given(cut.getMember()).willReturn(field);

        Optional<Real> result = cut.getReal();

        assertThat(result).isEmpty();
    }

    @Test
    public void givenRealFieldGetRealShouldReturnEmptyOptional() throws NoSuchFieldException {
        Field field = InjectableFieldService.class.getDeclaredField("real");

        given(cut.getMember()).willReturn(field);

        Optional<Real> result = cut.getReal();

        assertThat(result).isPresent();
    }

    @Test
    public void givenNonFakeFieldGetFakeShouldReturnEmptyOptional() throws NoSuchFieldException {
        Field field = InjectableFieldService.class.getDeclaredField("non");

        given(cut.getMember()).willReturn(field);

        Optional<Fake> result = cut.getFake();

        assertThat(result).isEmpty();
    }

    @Test
    public void givenFakeFieldGetFakeShouldReturnEmptyOptional() throws NoSuchFieldException {
        Field field = InjectableFieldService.class.getDeclaredField("fake");

        given(cut.getMember()).willReturn(field);

        Optional<Fake> result = cut.getFake();

        assertThat(result).isPresent();
    }

    @Test
    public void givenNonVirtualFieldGetVirtualShouldReturnEmptyOptional() throws NoSuchFieldException {
        Field field = InjectableFieldService.class.getDeclaredField("non");

        given(cut.getMember()).willReturn(field);

        Optional<Virtual> result = cut.getVirtual();

        assertThat(result).isEmpty();
    }

    @Test
    public void givenVirutalFieldGetVirtualShouldReturnEmptyOptional() throws NoSuchFieldException {
        Field field = InjectableFieldService.class.getDeclaredField("virtual");

        given(cut.getMember()).willReturn(field);

        Optional<Virtual> result = cut.getVirtual();

        assertThat(result).isPresent();
    }

    @Test
    public void givenNonFixtureFieldGetFixtureShouldReturnEmptyOptional() throws NoSuchFieldException {
        Field field = InjectableFieldService.class.getDeclaredField("non");

        given(cut.getMember()).willReturn(field);

        Optional<Fixture> result = cut.getFixture();

        assertThat(result).isEmpty();
    }

    @Test
    public void givenFixtureFieldGetFixtureShouldReturnEmptyOptional() throws NoSuchFieldException {
        Field field = InjectableFieldService.class.getDeclaredField("real");

        given(cut.getMember()).willReturn(field);

        Optional<Fixture> result = cut.getFixture();

        assertThat(result).isPresent();
    }

    @Test
    public void callToIsMockOnRealFieldShouldReturnFalse() throws NoSuchFieldException {
        Field field = InjectableFieldService.class.getDeclaredField("real");

        given(cut.getMember()).willReturn(field);

        Boolean result = cut.isMock();

        assertThat(result).isFalse();
    }

    @Test
    public void callToIsMockOnFakeFieldShouldReturnTrue() throws NoSuchFieldException {
        Field field = InjectableFieldService.class.getDeclaredField("fake");

        given(cut.getMember()).willReturn(field);

        Boolean result = cut.isMock();

        assertThat(result).isTrue();
    }

    @Test
    public void callToIsMockOnVirtualFieldShouldReturnTrue() throws NoSuchFieldException {
        Field field = InjectableFieldService.class.getDeclaredField("virtual");

        given(cut.getMember()).willReturn(field);

        Boolean result = cut.isMock();

        assertThat(result).isTrue();
    }

    @Test
    public void callToIsInjectableOnRealFieldShouldReturnTrue() throws NoSuchFieldException {
        Field field = InjectableFieldService.class.getDeclaredField("real");

        given(cut.getMember()).willReturn(field);

        Boolean result = cut.isInjectable();

        assertThat(result).isTrue();
    }

    @Test
    public void callToIsInjectableOnFakeFieldShouldReturnTrue() throws NoSuchFieldException {
        Field field = InjectableFieldService.class.getDeclaredField("fake");

        given(cut.getMember()).willReturn(field);

        Boolean result = cut.isInjectable();

        assertThat(result).isTrue();
    }

    @Test
    public void callToIsInjectableOnVirtualFieldShouldReturnTrue() throws NoSuchFieldException {
        Field field = InjectableFieldService.class.getDeclaredField("virtual");

        given(cut.getMember()).willReturn(field);

        Boolean result = cut.isInjectable();

        assertThat(result).isTrue();
    }

}
