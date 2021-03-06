/**
 * Copyright © 2013 Antonin Stefanutti (antonin.stefanutti@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.astefanutti.metrics.cdi.se;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class CountedConstructorBeanTest {

    private final static String COUNTER_NAME = MetricRegistry.name(CountedConstructorBean.class, "countedConstructor");

    @Deployment
    static Archive<?> createTestArchive() {
        return ShrinkWrap.create(JavaArchive.class)
            // Test bean
            .addClass(CountedConstructorBean.class)
            // Bean archive deployment descriptor
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    private MetricRegistry registry;

    @Inject
    private Instance<CountedConstructorBean> instance;

    //This test case becomes irrelevant as the registry already contains Timers from other test cases. 
    /*
    @Test
    @InSequence(1)
    public void countedConstructorNotCalledYet() {
        assertThat("Counter is not registered correctly", registry.getCounters().keySet(), is(empty()));
    }
    */

    @Test
    @InSequence(1)
    public void countedConstructorCalled() {
        long count = 1L + Math.round(Math.random() * 10);
        for (int i = 0; i < count; i++)
            instance.get();

        assertThat("Counter is not registered correctly", registry.getCounters(), hasKey(COUNTER_NAME));
        Counter counter = registry.getCounters().get(COUNTER_NAME);

        // Make sure that the counter has been called
        assertThat("Counter count is incorrect", counter.getCount(), is(equalTo(count)));
    }
}
