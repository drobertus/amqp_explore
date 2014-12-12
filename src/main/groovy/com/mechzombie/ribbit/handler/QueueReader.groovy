package com.mechzombie.ribbit.handler

import com.rabbitmq.client.QueueingConsumer

/**
 * Created by David on 12/11/2014.
 */
abstract class QueueReader implements Runnable {

    QueueingConsumer consumer
    def keepReading = true

    void setConsumer(QueueingConsumer qc) {
        consumer = qc

    }

    abstract void onRead(byte[] msg)

    void run() {
        while (keepReading) {
            try {
                QueueingConsumer.Delivery delivery = consumer.nextDelivery()
                onRead(delivery.body)
            }
            catch (Exception e) {

            }
        }
    }
}