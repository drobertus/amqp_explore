package com.mechzombie.ribbit.handler

import com.rabbitmq.client.Channel

/**
 * Created by David on 12/10/2014.
 */
class ExchangeWrapper {

    Channel channel
    String exchangeName
    String queueName

    def writeToExchange(String route, byte[] msg) {
       channel.basicPublish(exchangeName, route, null, msg)
    }

    def writeToExchange(String route, String msg) {
        writeToExchange(route, msg.bytes)
    }
}
