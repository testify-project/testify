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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.testifyproject.annotation.VirtualResource;
import org.testifyproject.fixture.PrimaryTestService;
import org.testifyproject.fixture.TestAnnotation;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 *
 * @author saden
 */
public class AnnotationTraitTest {

    AnnotationTrait<Class<PrimaryTestService>> sut;

    @Before
    public void init() {
        sut = mock(AnnotationTrait.class, Answers.CALLS_REAL_METHODS);
        given(sut.getAnnotatedElement()).willReturn(PrimaryTestService.class);
    }

    @Test
    public void callToGetAnnotatedElementShouldReturnTestService() {
        Class<PrimaryTestService> result = sut.getAnnotatedElement();

        assertThat(result).isEqualTo(PrimaryTestService.class);
    }

    @Test(expected = NullPointerException.class)
    public void givenNullGetAnnotationShouldThrowException() {
        sut.getAnnotation(null);
    }

    @Test
    public void givenNonExistenAnnotationGetAnnotationShouldReturnEmptyOptional() {
        Optional<Documented> result = sut.getAnnotation(Documented.class);

        assertThat(result).isEmpty();
    }

    @Test
    public void givenExistingAnnotationGetAnnotationShouldReturnOptionalWithAnnotation() {
        Class<TestAnnotation> annotationType = TestAnnotation.class;

        Optional<TestAnnotation> result = sut.getAnnotation(annotationType);

        assertThat(result).containsInstanceOf(annotationType);
    }

    @Test
    public void givenBundleAnnotatedAnnotationGetAnnotationShouldReturnOptionalWithAnnotation() {
        Class<VirtualResource> annotationType = VirtualResource.class;

        Optional<VirtualResource> result = sut.getAnnotation(annotationType);

        assertThat(result).containsInstanceOf(annotationType);
    }

    @Test(expected = NullPointerException.class)
    public void givenNullGetAnnotationsShouldReturnEmptyList() {
        Class<Annotation> annotationType = null;
        sut.getAnnotations(annotationType);
    }

    @Test
    public void givenNonExistenAnnotationGetAnnotationsShouldReturnEmptyList() {
        Class<Documented> annotationType = Documented.class;

        List<Documented> result = sut.getAnnotations(annotationType);

        assertThat(result).isEmpty();
    }

    @Test
    public void givenExistingAnnotationGetAnnotationsShouldReturnListWithAnnotations() {
        Class<TestAnnotation> annotationType = TestAnnotation.class;

        List<TestAnnotation> result = sut.getAnnotations(annotationType);

        assertThat(result).hasSize(1);
    }

    @Test
    public void givenBundleAnnotationGetAnnotationsShouldReturnListWithAnnotations() {
        Class<VirtualResource> annotationType = VirtualResource.class;

        List<VirtualResource> result = sut.getAnnotations(annotationType);

        assertThat(result).hasSize(1);
    }

    @Test(expected = NullPointerException.class)
    public void givenNullGetMetaAnnotationsShouldThrowException() {
        Collection<Class<Annotation>> annotationType = null;
        Collection[] annotationTypes = new Collection[]{annotationType};

        sut.getMetaAnnotations(annotationTypes);
    }

    @Test
    public void givenNonExistentAnnotationGetMetaAnnotationsShouldReturnEmptyArray() {
        Collection<Class<Documented>> annotationType = ImmutableList.of(Documented.class);
        Collection[] annotationTypes = new Collection[]{annotationType};

        Annotation[] result = sut.getMetaAnnotations(annotationTypes);

        assertThat(result).isEmpty();
    }

    @Test
    public void givenExistingAnnotationGetMetaAnnotationsShouldReturnEmptyArray() {
        Collection<Class<Retention>> annotationType = ImmutableList.of(Retention.class);
        Collection[] annotationTypes = new Collection[]{annotationType};

        Annotation[] result = sut.getMetaAnnotations(annotationTypes);

        assertThat(result).hasSize(1);
    }

    @Test(expected = NullPointerException.class)
    public void givenNullArrayHasAnyAnnotationsShouldThrowException() {
        Class<Annotation>[] annotationTypes = null;

        sut.hasAnyAnnotations(annotationTypes);
    }

    @Test
    public void givenNonExistentAnnotationArrayHasAnyAnnotationsShouldReturnFalse() {
        Class<Documented>[] annotationTypes = new Class[]{Documented.class};

        boolean result = sut.hasAnyAnnotations(annotationTypes);

        assertThat(result).isFalse();
    }

    @Test
    public void givenExistingAnnotationArrayHasAnyAnnotationsShouldReturnFalse() {
        Class<TestAnnotation>[] annotationTypes = new Class[]{TestAnnotation.class};

        boolean result = sut.hasAnyAnnotations(annotationTypes);

        assertThat(result).isTrue();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullCollectionHasAnyAnnotationsShouldThrowException() {
        Collection<Class<? extends Annotation>> annotationTypes = null;

        sut.hasAnyAnnotations(annotationTypes);
    }

    @Test
    public void givenNonExistentAnnotationCollectionHasAnyAnnotationsShouldReturnEmptyArray() {
        Collection<Class<? extends Annotation>> annotationTypes = ImmutableList.of(Documented.class);

        boolean result = sut.hasAnyAnnotations(annotationTypes);

        assertThat(result).isFalse();
    }

    @Test
    public void givenExistingAnnotationCollectionHasAnyAnnotationsShouldReturnEmptyArray() {
        Collection<Class<? extends Annotation>> annotationTypes = ImmutableList.of(
                TestAnnotation.class);

        boolean result = sut.hasAnyAnnotations(annotationTypes);

        assertThat(result).isTrue();
    }
}
