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
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import org.testifyproject.annotation.Module;
import org.testifyproject.annotation.Scan;

/**
 *
 * @author saden
 */
public class ServiceInstanceTest {

    ServiceInstance sut;

    @Before
    public void init() {
        sut = mock(ServiceInstance.class, Answers.CALLS_REAL_METHODS);
    }

    @Test(expected = NullPointerException.class)
    public void givenNullInstanceCallToReplaceShouldThrowException() {
        Instance<Object> instance = null;

        sut.replace(instance);
    }

    @Test
    public void givenInstanceWithoutNameAndContractReplaceShouldCallReplace() {
        Instance<Object> instance = mock(Instance.class);
        String name = null;
        Class<Object> contract = null;
        Object constant = mock(Object.class);

        Optional<String> foundName = Optional.ofNullable(name);
        Optional<Class<? extends Object>> foundContract = Optional.ofNullable(contract);

        given(instance.getName()).willReturn(foundName);
        given(instance.getContract()).willReturn(foundContract);
        given(instance.getValue()).willReturn(constant);

        sut.replace(instance);

        verify(instance).getValue();
        verify(instance).getName();
        verify(instance).getContract();

        verify(sut).replace(constant, name, contract);
    }

    @Test
    public void givenInstanceWithNameAndContractReplaceShouldCallReplace() {
        Instance<Object> instance = mock(Instance.class);
        String name = "test";
        Class<Object> contract = Object.class;
        Object constant = mock(Object.class);

        Optional<String> foundName = Optional.ofNullable(name);
        Optional<Class<? extends Object>> foundContract = Optional.ofNullable(contract);

        given(instance.getName()).willReturn(foundName);
        given(instance.getContract()).willReturn(foundContract);
        given(instance.getValue()).willReturn(constant);

        sut.replace(instance);

        verify(instance).getValue();
        verify(instance).getName();
        verify(instance).getContract();

        verify(sut).replace(constant, name, contract);
    }

    @Test
    public void givenInstanceWithoutNameAndContractAddConstantShouldCallReplace() {
        Instance<Object> instance = mock(Instance.class);
        String name = null;
        Class<Object> contract = null;
        Object constant = mock(Object.class);

        Optional<String> foundName = Optional.ofNullable(name);
        Optional<Class<? extends Object>> foundContract = Optional.ofNullable(contract);

        given(instance.getName()).willReturn(foundName);
        given(instance.getContract()).willReturn(foundContract);
        given(instance.getValue()).willReturn(constant);

        sut.addConstant(instance);

        verify(instance).getValue();
        verify(instance).getName();
        verify(instance).getContract();

        verify(sut).addConstant(constant, name, contract);
    }

    @Test
    public void givenInstanceWithNameAndContractAddConstantShouldCallReplace() {
        Instance<Object> instance = mock(Instance.class);
        String name = "test";
        Class<Object> contract = Object.class;
        Object constant = mock(Object.class);

        Optional<String> foundName = Optional.ofNullable(name);
        Optional<Class<? extends Object>> foundContract = Optional.ofNullable(contract);

        given(instance.getName()).willReturn(foundName);
        given(instance.getContract()).willReturn(foundContract);
        given(instance.getValue()).willReturn(constant);

        sut.addConstant(instance);

        verify(instance).getValue();
        verify(instance).getName();
        verify(instance).getContract();

        verify(sut).addConstant(constant, name, contract);
    }

    @Test
    public void callToIsRunningShouldReturnFalse() {
        Boolean result = sut.isRunning();

        assertThat(result).isFalse();
        verify(sut).isRunning();
    }

    @Test
    public void callToInitShouldDoNothing() {
        sut.init();

        verify(sut).init();
    }

    @Test
    public void callToDestroyShouldDoNothing() {
        sut.destroy();

        verify(sut).destroy();
    }

    @Test
    public void callToAddModulesShouldDoNothing() {
        Module module = mock(Module.class);

        sut.addModules(module);

        verify(sut).addModules(module);
        verifyZeroInteractions(module);
    }

    @Test
    public void callToAddScansShouldDoNothing() {
        Scan scan = mock(Scan.class);

        sut.addScans(scan);

        verify(sut).addScans(scan);
        verifyZeroInteractions(scan);
    }

    @Test
    public void callToInjectShouldDoNothing() {
        Object instance = mock(Object.class);

        sut.inject(instance);

        verify(sut).inject(instance);
        verifyZeroInteractions(instance);
    }

    @Test
    public void callToGetNameQualifiersShouldReturnAnnotations() {
        Set<Class<? extends Annotation>> result = sut.getNameQualifers();

        assertThat(result).isEmpty();
        verify(sut).getNameQualifers();
    }

    @Test
    public void callToGetCustomQualifiersShouldReturnAnnotations() {
        Set<Class<? extends Annotation>> result = sut.getCustomQualifiers();

        assertThat(result).isEmpty();
        verify(sut).getCustomQualifiers();
    }
}
