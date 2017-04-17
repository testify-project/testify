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

import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.testifyproject.TestifyException;
import org.testifyproject.extension.annotation.UnitTest;
import org.testifyproject.fixture.locator.MultiImplmentationContract;
import org.testifyproject.fixture.locator.NoImplementationConract;
import org.testifyproject.fixture.locator.SingleImplementationContract;
import org.testifyproject.fixture.locator.impl.FirstMultiImplmentationContract;
import org.testifyproject.fixture.locator.impl.SecondMultiImplmentationContract;
import org.testifyproject.fixture.locator.impl.SingleImplementationContractImpl;

/**
 *
 * @author saden
 */
public class ServiceLocatorUtilTest {

    ServiceLocatorUtil cut;

    @Before
    public void init() {
        cut = new ServiceLocatorUtil();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullFindOneShouldReturnThrowException() {
        cut.findOne(null);
    }

    @Test
    public void givenContractWithoutImplementationFindOneShouldReturnEmptyOptional() {
        Class<NoImplementationConract> contract = NoImplementationConract.class;

        Optional<NoImplementationConract> result = cut.findOne(contract);

        assertThat(result).isEmpty();
    }

    @Test
    public void givenContractWithImplementationsFindOneShouldReturnOptionalWithImplementation() {
        Class<MultiImplmentationContract> contract = MultiImplmentationContract.class;

        Optional<MultiImplmentationContract> result = cut.findOne(contract);

        assertThat(result).isNotEmpty();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullFindAllShouldReturnThrowException() {
        cut.findAll(null);
    }

    @Test
    public void givenContractWithoutImplementationFindAllShouldReturnEmptyList() {
        Class<NoImplementationConract> contract = NoImplementationConract.class;

        List<NoImplementationConract> result = cut.findAll(contract);

        assertThat(result).isEmpty();
    }

    @Test
    public void givenContractWithImplementationsFindAllShouldReturnListWithImplementation() {
        Class<MultiImplmentationContract> contract = MultiImplmentationContract.class;

        List<MultiImplmentationContract> result = cut.findAll(contract);

        assertThat(result).hasSize(2);
    }

    @Test
    public void givenContractWithImplementationsFindAllWithFilterShouldReturnListWithImplementation() {
        Class<MultiImplmentationContract> contract = MultiImplmentationContract.class;

        List<MultiImplmentationContract> result = cut.findAllWithFilter(contract, UnitTest.class);

        assertThat(result).hasSize(1);
    }

    @Test(expected = NullPointerException.class)
    public void givenNullGetOneShouldReturnThrowException() {
        cut.getOne(null);
    }

    @Test(expected = TestifyException.class)
    public void givenContractWithoutImplementationGetOneShouldReturnThrowException() {
        Class<NoImplementationConract> contract = NoImplementationConract.class;
        cut.getOne(contract);
    }

    @Test(expected = TestifyException.class)
    public void givenContractWithMultipleImplementationsGetOneShouldReturnThrowException() {
        Class<MultiImplmentationContract> contract = MultiImplmentationContract.class;
        cut.getOne(contract);
    }

    @Test
    public void givenContractWithSingleImplementationsGetOneShouldReturnImplementation() {
        Class<SingleImplementationContract> contract = SingleImplementationContract.class;

        SingleImplementationContract result = cut.getOne(contract);

        assertThat(result).isNotNull();
    }

    @Test
    public void givenContractWithSingleImplementationsGetOneWithFilterShouldReturnImplementation() {
        Class<SingleImplementationContract> contract = SingleImplementationContract.class;

        SingleImplementationContract result = cut.getOneWithFilter(contract, UnitTest.class);

        assertThat(result).isNotNull();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullContractAndImplementationGetOneShouldThrowException() {
        Class<MultiImplmentationContract> contract = null;
        Class<SecondMultiImplmentationContract> implementation = SecondMultiImplmentationContract.class;

        cut.getOne(contract, implementation);
    }

    @Test(expected = NullPointerException.class)
    public void givenContractAndNullImplementationGetOneShouldThrowException() {
        Class<MultiImplmentationContract> contract = MultiImplmentationContract.class;
        Class<? extends MultiImplmentationContract> implementation = null;

        cut.getOne(contract, implementation);
    }

    @Test(expected = TestifyException.class)
    public void givenContractAndNonExistentImplementationGetOneShouldThrowException() {
        Class<MultiImplmentationContract> contract = MultiImplmentationContract.class;
        Class<MultiImplmentationContract> implementation = MultiImplmentationContract.class;

        cut.getOne(contract, implementation);
    }

    @Test
    public void givenContractAndImplementationGetOneShouldReturnImplementation() {
        Class<MultiImplmentationContract> contract = MultiImplmentationContract.class;
        Class<SecondMultiImplmentationContract> implementation = SecondMultiImplmentationContract.class;

        MultiImplmentationContract result = cut.getOne(contract, implementation);

        assertThat(result).isNotNull();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullContractAndImplementationGetOneOrDefaultShouldThrowException() {
        Class<MultiImplmentationContract> contract = null;
        Class<SecondMultiImplmentationContract> defaultImplementation = SecondMultiImplmentationContract.class;

        cut.getOneOrDefault(contract, defaultImplementation);
    }

    @Test(expected = NullPointerException.class)
    public void givenContractAndNullImplementationGetOneOrDefaultShouldThrowException() {
        Class<MultiImplmentationContract> contract = MultiImplmentationContract.class;
        Class<? extends MultiImplmentationContract> defaultImplementation = null;

        cut.getOneOrDefault(contract, defaultImplementation);
    }

    @Test(expected = TestifyException.class)
    public void givenContractWithoutImplementationGetOneOrDefaultShouldThrowException() {
        Class<NoImplementationConract> contract = NoImplementationConract.class;
        Class<NoImplementationConract> defaultImplementation = NoImplementationConract.class;

        cut.getOneOrDefault(contract, defaultImplementation);
    }

    @Test
    public void givenContractWithMultipleImplementationAndDefaultImplementationGetOneOrDefaultShouldReturnFoundImplementation() {
        Class<MultiImplmentationContract> contract = MultiImplmentationContract.class;
        Class<SecondMultiImplmentationContract> defaultImplementation = SecondMultiImplmentationContract.class;

        MultiImplmentationContract result = cut.getOneOrDefault(contract, defaultImplementation);

        assertThat(result).isInstanceOf(FirstMultiImplmentationContract.class);
    }

    @Test
    public void givenContractWithSingleImplementationAndDefaultImplementationGetOneOrDefaultShouldReturnDefaultImplementation() {
        Class<SingleImplementationContract> contract = SingleImplementationContract.class;
        Class<SingleImplementationContractImpl> defaultImplementation = SingleImplementationContractImpl.class;

        SingleImplementationContract result = cut.getOneOrDefault(contract, defaultImplementation);

        assertThat(result).isInstanceOf(SingleImplementationContractImpl.class);
    }

    @Test(expected = NullPointerException.class)
    public void givenNullGetAllShouldReturnThrowException() {
        cut.getAll(null);
    }

    @Test(expected = TestifyException.class)
    public void givenContractWithoutImplementationGetAllShouldThrowException() {
        Class<NoImplementationConract> contract = NoImplementationConract.class;

        cut.getAll(contract);
    }

    @Test
    public void givenContractWithImplementationsGetAllShouldReturnListWithImplementation() {
        Class<MultiImplmentationContract> contract = MultiImplmentationContract.class;

        List<MultiImplmentationContract> result = cut.getAll(contract);

        assertThat(result).hasSize(2);
    }

}
