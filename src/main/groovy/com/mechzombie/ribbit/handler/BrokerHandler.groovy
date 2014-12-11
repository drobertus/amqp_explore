package com.mechzombie.ribbit.handler

import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.QueueingConsumer
import com.rabbitmq.client.impl.Method
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
            //def queueName = channel.queueDeclare().getQueue()
            wrapper = new ExchangeWrapper(channel: channel,
                exchangeName: exchangeName)
            knownExchanges.put(exchangeName, wrapper)
            //println()
        }

        return wrapper
    }

    def doesExchangeExist(String exchangeName) {
        def channel = connection.createChannel()
        try {
            def ok = channel.exchangeDeclarePassive(exchangeName)
            //channel.queueDeclare()
            println("ok returned = ${ok}")
            return true
        }catch (Exception ioe) {
            log.info("error on passive exchange declaration: ${ioe}")
        }
        return false
    }

    QueueingConsumer getExchangeConsumer(String name, ExchangeType type, String route) {
        def wrapper = this.knownExchanges[name]

        if(!wrapper && !doesExchangeExist(name)) {
            wrapper = getExchangeWrapper(name, type, true)
        }
        getExchangeConsumer(wrapper, route)

    }

    QueueingConsumer getExchangeConsumer(String exchangeName, String queueToBind, String route) {

        def channel = connection.createChannel()

        def declareOk = channel.queueDeclare(queueToBind,true, true, true, null)
        def ok = channel.queueBind(queueToBind, exchangeName, route, null)
        log.info ("okay=${ok}")

        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(queueToBind, true, consumer);

        //TODO: this channel is orphaned from the consumer.
        return consumer
    }
}
