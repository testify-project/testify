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
package org.testifyproject;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import org.testifyproject.annotation.Scan;

/**
 *
 * @author saden
 */
public class ServiceInstanceTest {

    ServiceInstance cut;

    @Before
    public void init() {
        cut = mock(ServiceInstance.class, Answers.CALLS_REAL_METHODS);
    }

    @Test(expected = NullPointerException.class)
    public void givenNullInstanceCallToReplaceShouldThrowException() {
        Instance<Object> instance = null;
        String overrideName = "overrideName";
        Class overrideContract = Class.class;

        cut.replace(instance, overrideName, overrideContract);
    }

    @Test
    public void givenOverrideNameReplaceShouldReplaceInstance() {
        Instance<Object> instance = mock(Instance.class);
        String overrideName = "overrideName";
        Class overrideContract = Class.class;

        Object constant = mock(Object.class);

        String name = "name";
        Optional<String> nameResult = Optional.of(name);

        Class contract = Class.class;
        Optional<Class<? extends Object>> contractResult = Optional.of(contract);

        given(instance.getInstance()).willReturn(constant);
        given(instance.getName()).willReturn(nameResult);
        given(instance.getContract()).willReturn(contractResult);

        cut.replace(instance, overrideName, overrideContract);

        verify(instance).getInstance();
        verify(instance).getName();
        verify(instance).getContract();

        verify(cut).replace(instance, overrideName, overrideContract);
        verify(cut).replace(constant, overrideName, contract);
    }

    @Test
    public void givenOverrideContractReplaceShouldReplaceInstance() {
        Instance<Object> instance = mock(Instance.class);
        String overrideName = "";
        Class overrideContract = Object.class;

        Object constant = mock(Object.class);

        String name = "name";
        Optional<String> nameResult = Optional.of(name);

        Class contract = Class.class;
        Optional<Class<? extends Object>> contractResult = Optional.of(contract);

        given(instance.getInstance()).willReturn(constant);
        given(instance.getName()).willReturn(nameResult);
        given(instance.getContract()).willReturn(contractResult);

        cut.replace(instance, overrideName, overrideContract);

        verify(instance).getInstance();
        verify(instance).getName();
        verify(instance).getContract();

        verify(cut).replace(instance, overrideName, overrideContract);
        verify(cut).replace(constant, name, overrideContract);
    }

    @Test
    public void callToIsRunningShouldReturnFalse() {
        Boolean result = cut.isRunning();

        assertThat(result).isFalse();
        verify(cut).isRunning();
    }

    @Test
    public void callToInitShouldDoNothing() {
        cut.init();

        verify(cut).init();
    }

    @Test
    public void callToDestroyShouldDoNothing() {
        cut.destroy();

        verify(cut).destroy();
    }

    @Test
    public void callToAddScansShouldDoNothing() {
        Scan scan = mock(Scan.class);

        cut.addScans(scan);

        verify(cut).addScans(scan);
        verifyZeroInteractions(scan);
    }

    @Test
    public void callToInjectShouldDoNothing() {
        Object instance = mock(Object.class);

        cut.inject(instance);

        verify(cut).inject(instance);
        verifyZeroInteractions(instance);
    }

    @Test
    public void callToGetInjectionAnnotationsShouldReturnAnnotations() {
        Set<Class<? extends Annotation>> result = cut.getInjectionAnnotations();

        assertThat(result).containsExactly(Inject.class);
        verify(cut).getInjectionAnnotations();
    }

    @Test
    public void callToGetNameQualifiersShouldReturnAnnotations() {
        Set<Class<? extends Annotation>> result = cut.getNameQualifers();

        assertThat(result).containsExactly(Named.class);
        verify(cut).getNameQualifers();
    }

    @Test
    public void callToGetCustomQualifiersShouldReturnAnnotations() {
        Set<Class<? extends Annotation>> result = cut.getCustomQualifiers();

        assertThat(result).containsExactly(Qualifier.class);
        verify(cut).getCustomQualifiers();
    }
}
