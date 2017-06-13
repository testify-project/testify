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
package org.testifyproject.core.verifier;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestifyException;
import org.testifyproject.annotation.Module;
import org.testifyproject.annotation.Scan;
import org.testifyproject.fixture.common.InvalidTestClass;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 *
 * @author saden
 */
public class ModuleConfigurationVerifierTest {

    ModuleConfigurationVerifier sut;

    @Before
    public void init() {
        sut = new ModuleConfigurationVerifier();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullTestContextReifyShouldThrowException() {
        sut.verify(null);
    }

    @Test(expected = TestifyException.class)
    public void givenInvalidTestContextVerifyShouldThrowException() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        String testClassName = InvalidTestClass.class.getSimpleName();

        List<Module> foundModules = ImmutableList.of();
        List<Scan> foundScans = ImmutableList.of();

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getTestClassName()).willReturn(testClassName);
        given(testDescriptor.getModules()).willReturn(foundModules);
        given(testDescriptor.getScans()).willReturn(foundScans);

        try {
            sut.verify(testContext);
        }
        catch (Exception e) {
            verify(testContext).getTestDescriptor();
            verify(testDescriptor).getTestClassName();
            verify(testDescriptor).getModules();
            verify(testDescriptor).getScans();
            throw e;
        }
    }

    @Test
    public void givenModulesTestContextVerifyShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        String testClassName = InvalidTestClass.class.getSimpleName();
        Module module = mock(Module.class);
        List<Module> foundModules = ImmutableList.of(module);
        List<Scan> foundScans = ImmutableList.of();

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getTestClassName()).willReturn(testClassName);
        given(testDescriptor.getModules()).willReturn(foundModules);
        given(testDescriptor.getScans()).willReturn(foundScans);

        sut.verify(testContext);

        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getTestClassName();
        verify(testDescriptor).getModules();
        verify(testDescriptor).getScans();
    }
    
    @Test
    public void givenScansTestContextVerifyShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        String testClassName = InvalidTestClass.class.getSimpleName();
        Scan scan = mock(Scan.class);
        List<Module> foundModules = ImmutableList.of();
        List<Scan> foundScans = ImmutableList.of(scan);

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getTestClassName()).willReturn(testClassName);
        given(testDescriptor.getModules()).willReturn(foundModules);
        given(testDescriptor.getScans()).willReturn(foundScans);

        sut.verify(testContext);

        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getTestClassName();
        verify(testDescriptor).getModules();
        verify(testDescriptor).getScans();
    }

}
