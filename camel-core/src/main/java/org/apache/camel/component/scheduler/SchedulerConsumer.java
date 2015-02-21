/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.scheduler;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.ScheduledPollConsumer;

public class SchedulerConsumer extends ScheduledPollConsumer {

    public SchedulerConsumer(Endpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }

    @Override
    protected int poll() throws Exception {
        Exchange exchange = getEndpoint().createExchange();
        try {
            getProcessor().process(exchange);
        } catch (Exception e) {
            exchange.setException(e);
        }

        if (exchange.getException() != null) {
            getExceptionHandler().handleException("Error processing exchange", exchange, exchange.getException());
        }

        // a property can be used to control if the scheduler polled a message or not
        // for example to overrule and indicate no message was polled, which can affect the scheduler
        // to leverage backoff on idle etc.
        boolean polled = exchange.getProperty(Exchange.SCHEDULER_POLLED_MESSAGES, true, boolean.class);
        return polled ? 1 : 0;
    }

}
