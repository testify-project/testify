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

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.TestifyException;
import org.testifyproject.extension.annotation.Strict;
import org.testifyproject.extension.annotation.UnitCategory;
import org.testifyproject.fixture.locator.MultiImplmentationContract;
import org.testifyproject.fixture.locator.NoImplementationConract;
import org.testifyproject.fixture.locator.SingleImplementationContract;
import org.testifyproject.fixture.locator.impl.FirstMultiImplmentationContract;
import org.testifyproject.fixture.locator.impl.SecondMultiImplmentationContract;
import org.testifyproject.fixture.locator.impl.SingleImplementationContractImpl;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 *
 * @author saden
 */
public class ServiceLocatorUtilTest {

    ServiceLocatorUtil sut;

    @Before
    public void init() {
        sut = new ServiceLocatorUtil();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullFindOneShouldReturnThrowException() {
        sut.findOne(null);
    }

    @Test
    public void givenContractWithoutImplementationFindOneShouldReturnEmptyOptional() {
        Class<NoImplementationConract> contract = NoImplementationConract.class;

        Optional<NoImplementationConract> result = sut.findOne(contract);

        assertThat(result).isEmpty();
    }

    @Test
    public void givenContractWithImplementationsFindOneShouldReturnOptionalWithImplementation() {
        Class<MultiImplmentationContract> contract = MultiImplmentationContract.class;

        Optional<MultiImplmentationContract> result = sut.findOne(contract);

        assertThat(result).isNotEmpty();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullFindAllShouldReturnThrowException() {
        sut.findAll(null);
    }

    @Test
    public void givenContractWithoutImplementationFindAllShouldReturnEmptyList() {
        Class<NoImplementationConract> contract = NoImplementationConract.class;

        List<NoImplementationConract> result = sut.findAll(contract);

        assertThat(result).isEmpty();
    }

    @Test
    public void givenContractWithImplementationsFindAllShouldReturnListWithImplementation() {
        Class<MultiImplmentationContract> contract = MultiImplmentationContract.class;

        List<MultiImplmentationContract> result = sut.findAll(contract);

        assertThat(result).hasSize(2);
    }

    @Test
    public void givenContractWithImplementationsFindAllWithFilterShouldReturnListWithImplementation() {
        Class<MultiImplmentationContract> contract = MultiImplmentationContract.class;

        List<MultiImplmentationContract> result = sut
                .findAllWithFilter(contract, UnitCategory.class);

        assertThat(result).hasSize(1);
    }

    @Test
    public void givenContractWithImplementationsFindAllWithFilterAndGuidelineShouldReturnListWithImplementation() {
        Class guideline = Strict.class;

        List<Class<? extends Annotation>> guidelines = ImmutableList.of(guideline);
        Class<MultiImplmentationContract> contract = MultiImplmentationContract.class;

        List<MultiImplmentationContract> result = sut.findAllWithFilter(contract, guidelines,
                UnitCategory.class);

        assertThat(result).hasSize(1);
    }

    @Test(expected = NullPointerException.class)
    public void givenNullGetOneShouldReturnThrowException() {
        sut.getOne(null);
    }

    @Test(expected = TestifyException.class)
    public void givenContractWithoutImplementationGetOneShouldReturnThrowException() {
        Class<NoImplementationConract> contract = NoImplementationConract.class;
        sut.getOne(contract);
    }

    @Test(expected = TestifyException.class)
    public void givenContractWithMultipleImplementationsGetOneShouldReturnThrowException() {
        Class<MultiImplmentationContract> contract = MultiImplmentationContract.class;
        sut.getOne(contract);
    }

    @Test
    public void givenContractWithSingleImplementationsGetOneShouldReturnImplementation() {
        Class<SingleImplementationContract> contract = SingleImplementationContract.class;

        SingleImplementationContract result = sut.getOne(contract);

        assertThat(result).isNotNull();
    }

    @Test
    public void givenContractWithSingleImplementationsGetOneWithFilterShouldReturnImplementation() {
        Class<SingleImplementationContract> contract = SingleImplementationContract.class;

        SingleImplementationContract result = sut.getOneWithFilter(contract, UnitCategory.class);

        assertThat(result).isNotNull();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullContractAndImplementationGetOneShouldThrowException() {
        Class<MultiImplmentationContract> contract = null;
        Class<SecondMultiImplmentationContract> implementation =
                SecondMultiImplmentationContract.class;

        sut.getOne(contract, implementation);
    }

    @Test(expected = NullPointerException.class)
    public void givenContractAndNullImplementationGetOneShouldThrowException() {
        Class<MultiImplmentationContract> contract = MultiImplmentationContract.class;
        Class<? extends MultiImplmentationContract> implementation = null;

        sut.getOne(contract, implementation);
    }

    @Test(expected = TestifyException.class)
    public void givenContractAndNonExistentImplementationGetOneShouldThrowException() {
        Class<MultiImplmentationContract> contract = MultiImplmentationContract.class;
        Class<MultiImplmentationContract> implementation = MultiImplmentationContract.class;

        sut.getOne(contract, implementation);
    }

    @Test
    public void givenContractAndImplementationGetOneShouldReturnImplementation() {
        Class<MultiImplmentationContract> contract = MultiImplmentationContract.class;
        Class<SecondMultiImplmentationContract> implementation =
                SecondMultiImplmentationContract.class;

        MultiImplmentationContract result = sut.getOne(contract, implementation);

        assertThat(result).isNotNull();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullContractAndImplementationGetOneOrDefaultShouldThrowException() {
        Class<MultiImplmentationContract> contract = null;
        Class<SecondMultiImplmentationContract> defaultImplementation =
                SecondMultiImplmentationContract.class;

        sut.getOneOrDefault(contract, defaultImplementation);
    }

    @Test(expected = NullPointerException.class)
    public void givenContractAndNullImplementationGetOneOrDefaultShouldThrowException() {
        Class<MultiImplmentationContract> contract = MultiImplmentationContract.class;
        Class<? extends MultiImplmentationContract> defaultImplementation = null;

        sut.getOneOrDefault(contract, defaultImplementation);
    }

    @Test(expected = TestifyException.class)
    public void givenContractWithoutImplementationGetOneOrDefaultShouldThrowException() {
        Class<NoImplementationConract> contract = NoImplementationConract.class;
        Class<NoImplementationConract> defaultImplementation = NoImplementationConract.class;

        sut.getOneOrDefault(contract, defaultImplementation);
    }

    @Test
    public void givenContractWithMultipleImplementationAndDefaultImplementationGetOneOrDefaultShouldReturnFoundImplementation() {
        Class<MultiImplmentationContract> contract = MultiImplmentationContract.class;
        Class<SecondMultiImplmentationContract> defaultImplementation =
                SecondMultiImplmentationContract.class;

        MultiImplmentationContract result = sut.getOneOrDefault(contract, defaultImplementation);

        assertThat(result).isInstanceOf(FirstMultiImplmentationContract.class);
    }

    @Test
    public void givenContractWithSingleImplementationAndDefaultImplementationGetOneOrDefaultShouldReturnDefaultImplementation() {
        Class<SingleImplementationContract> contract = SingleImplementationContract.class;
        Class<SingleImplementationContractImpl> defaultImplementation =
                SingleImplementationContractImpl.class;

        SingleImplementationContract result = sut.getOneOrDefault(contract,
                defaultImplementation);

        assertThat(result).isInstanceOf(SingleImplementationContractImpl.class);
    }

    @Test(expected = NullPointerException.class)
    public void givenNullGetAllShouldReturnThrowException() {
        sut.getAll(null);
    }

    @Test(expected = TestifyException.class)
    public void givenContractWithoutImplementationGetAllShouldThrowException() {
        Class<NoImplementationConract> contract = NoImplementationConract.class;

        sut.getAll(contract);
    }

    @Test
    public void givenContractWithImplementationsGetAllShouldReturnListWithImplementation() {
        Class<MultiImplmentationContract> contract = MultiImplmentationContract.class;

        List<MultiImplmentationContract> result = sut.getAll(contract);

        assertThat(result).hasSize(2);
    }

}
