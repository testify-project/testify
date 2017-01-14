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
package org.testify.junit.fixture.common;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.glassfish.hk2.api.ServiceLocator;
import org.jvnet.hk2.annotations.Service;

/**
 * A method interceptor that intercepts methods annotated with @Transactional.
 * This interceptor begins, commits, and rolls back the currently active entity
 * manager.
 *
 * @author saden
 */
@Service
public class TransactionalInterceptor implements MethodInterceptor {

    private final ServiceLocator serviceLocator;

    @Inject
    TransactionalInterceptor(ServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        EntityManager entityManager = serviceLocator.getService(EntityManager.class);

        try {
            entityManager.getTransaction().begin();
            Object result = invocation.proceed();
            entityManager.getTransaction().commit();
            entityManager.close();

            return result;
        } catch (Throwable t) {
            entityManager.getTransaction().rollback();
            entityManager.close();

            throw t;
        } finally {
            TransactionContext.remove();
        }
    }

}
