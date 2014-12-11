package com.mechzombie.ribbit.handler

import com.mechzombie.test.AMQPTestSpecification
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.QueueingConsumer
import groovy.util.logging.Log

import static junit.framework.Assert.assertFalse
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull
import static org.junit.Assert.assertTrue


@Log
class BrokerHandlerTest extends AMQPTestSpecification {


    def "set up an exchange and a client, make sure they are chained"() {

        setup:
         def handler = new BrokerHandler(this.getConection())
        ExchangeWrapper wrapper = handler.getExchangeWrapper('XCHANGE', ExchangeType.topic, true)
        def queueToListen = 'queue1'
        QueueingConsumer consumer = handler.getExchangeConsumer( 'XCHANGE',queueToListen, '*.weather.us')

        when:
            wrapper.writeToExchange('news.weather.us', 'TaDA')
        then:
            QueueingConsumer.Delivery msg = consumer.nextDelivery(1000)
            assertEquals 'TaDA', new String( msg.body)

        when:
        QueueingConsumer consumer2 = handler.getExchangeConsumer(wrapper.exchangeName, 'caQ', '*.weather.ca')
            wrapper.writeToExchange('news.weather.ca', 'TEST')

        then:
            QueueingConsumer.Delivery msg2 = consumer.nextDelivery(1000)
            QueueingConsumer.Delivery msg3 = consumer2.nextDelivery(1000)
            assertNull msg2

            assertEquals 'TEST', new String( msg3.body)

    }

    def "one connection sets up an exchange another listens"() {

        setup:
            def handler1 = new BrokerHandler(this.getConection())
            def handler2 = new BrokerHandler(this.getConection())
            def XCHANGE_NAME = 'testExchange'

            def publishRoute = 'route.to.all'
            def listenRoute = '*.to.all'

            def testMsg1 = 'Message is off!'

        when:
            def present = handler1.doesExchangeExist(XCHANGE_NAME)
        then:
            println "the ok= ${present}"
            assertFalse(present)

        when:
            def wrapper1 = handler1.getExchangeWrapper(XCHANGE_NAME, ExchangeType.topic, true)
            println "private exchange queue name= ${wrapper1.queueName}"
            def okNext = handler2.doesExchangeExist(XCHANGE_NAME)
            println "the next ok= ${okNext}"
        then:
            assertTrue(okNext)
        when:
            def consumer = handler2.getExchangeConsumer(XCHANGE_NAME, ExchangeType.topic, listenRoute)
            wrapper1.writeToExchange(publishRoute, testMsg1)
        then:
            assertEquals(testMsg1, new String(consumer.nextDelivery(100).body))
    }
}