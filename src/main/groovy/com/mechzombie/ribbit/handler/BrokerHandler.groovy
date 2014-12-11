package com.mechzombie.ribbit.handler

import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.QueueingConsumer
import groovy.util.logging.Log

@Log
class BrokerHandler {

    Connection  connection

    def knownExchanges = [:]

//    BrokerHandler(String address) {
//        ConnectionFactory factory = new ConnectionFactory()
//    }

    BrokerHandler(Connection conn) {
        this.connection = conn
    }

    ExchangeWrapper getExchangeWrapper(String exchangeName, ExchangeType type, boolean durable) {

        def wrapper = knownExchanges.get(exchangeName)
        if (!wrapper) {

            def channel = connection.createChannel()
            channel.exchangeDeclare(exchangeName, type.name(), true)
            def queueName = channel.queueDeclare().getQueue()
            wrapper = new ExchangeWrapper(channel: channel, queueName: queueName,
                exchangeName: exchangeName)
            knownExchanges.put(exchangeName, wrapper)
        }

        return wrapper
    }

    QueueingConsumer getExchangeConsumer(ExchangeWrapper wrapper, String route) {

        def channel = connection.createChannel()


        def ok = channel.queueBind(wrapper.queueName, wrapper.exchangeName, route, null)
        log.info ("okay=${ok}")

        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(wrapper.queueName, true, consumer);

        //TODO: this channel is orphaned from the consumer.
        return consumer
    }
}
