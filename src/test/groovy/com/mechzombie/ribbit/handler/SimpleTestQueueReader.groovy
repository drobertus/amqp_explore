package com.mechzombie.ribbit.handler

/**
 * Created by David on 12/11/2014.
 */
class SimpleTestQueueReader extends QueueReader {

    def receivedMessages = []

    @Override
    void onRead(byte[] msg) {
        def inbound = new String(msg)
        receivedMessages << inbound
        println "I have received ${inbound}"
    }
}
