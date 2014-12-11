package com.mechzombie.ribbit.handler

import com.mechzombie.test.AMQPTestSpecification
import com.rabbitmq.client.QueueingConsumer
import groovy.util.logging.Log

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull


@Log
class BrokerHandlerTest extends AMQPTestSpecification {


    def "set up an exchange and a client, make sure they are chained"() {

        setup:
         def handler = new BrokerHandler(this.getConection())
        ExchangeWrapper wrapper = handler.getExchangeWrapper('XCHANGE', ExchangeType.topic, true)

        QueueingConsumer consumer = handler.getExchangeConsumer(wrapper, '*.weather.us')

        when:
            wrapper.writeToExchange('news.weather.us', 'TaDA')
        then:
            QueueingConsumer.Delivery msg = consumer.nextDelivery(1000)
            assertEquals 'TaDA', new String( msg.body)

        when:
        QueueingConsumer consumer2 = handler.getExchangeConsumer(wrapper, '*.weather.ca')
            wrapper.writeToExchange('news.weather.ca', 'TEST')

        then:
            QueueingConsumer.Delivery msg2 = consumer.nextDelivery(1000)
            QueueingConsumer.Delivery msg3 = consumer2.nextDelivery(1000)
            assertNull msg2

            assertEquals 'TEST', new String( msg3.body)

    }
}