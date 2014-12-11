package com.mechzombie.test

import com.google.common.io.Files
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.QueueingConsumer

import groovy.util.logging.Log
import org.apache.commons.io.FileUtils

import org.apache.qpid.server.Broker
import org.apache.qpid.server.BrokerOptions
import spock.lang.Specification


@Log
class ExchangeTest extends Specification {

    def tmpFolder = Files.createTempDir()
    Broker broker

    def amqpPort = 9234
    def httpPort = 9235

    def qpidHomeDir = 'src/test/resources/'
    def configFileName = "/test-config.json"

    def "test client connection" () {
        setup:
        //def QUEUE_NAME = 'test_q'

        def TOPIC_NAME = 'TEST_TOPIC'

        ConnectionFactory factory = new ConnectionFactory();

        factory.setUri("amqp://guest:password@localhost:${amqpPort}")
        factory.useSslProtocol()

        log.info(" ***** creating connection")
        def connection = factory.newConnection()
        log.info(" ***** creating channels")
        def channel = connection.createChannel();
        def channel2 = connection.createChannel();
        log.info " ***** declaring exchange of type topic -> ${TOPIC_NAME}"
        channel.exchangeDeclare(TOPIC_NAME, "topic", true)


        def queueName = channel.queueDeclare().getQueue()
        log.info " ***** binding to exchange of -> ${TOPIC_NAME} ; ${queueName}"

        def ok = channel2.queueBind(queueName, TOPIC_NAME, '*.sports.news', null)
        log.info ("okay=${ok}")

        QueueingConsumer consumer = new QueueingConsumer(channel2);
        channel2.basicConsume(queueName, true, consumer);

        when:

        log.info "Sending hello world message now"
        String message = "Hello World!";
        channel.basicPublish(TOPIC_NAME, 'us.sports.news', null, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");

        then:

        def received
        while (!received) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String messagereceived = new String(delivery.getBody());
            System.out.println(" [x] Received '" + messagereceived + "'");
            received = messagereceived
        }

        assert received == message
        //cleanup
        channel2.close()
        channel.close();
        connection.close();

    }


    void setup() {
        broker = new Broker();
        def brokerOptions = new BrokerOptions()

        File file = new File(qpidHomeDir)
        String homePath = file.getAbsolutePath();
        log.info(' qpid home dir=' + homePath)
        log.info(' qpid work dir=' + tmpFolder.absolutePath)

        brokerOptions.setConfigProperty('qpid.work_dir', tmpFolder.absolutePath);

        brokerOptions.setConfigProperty('qpid.amqp_port',"${amqpPort}")
        brokerOptions.setConfigProperty('qpid.http_port', "${httpPort}")
        brokerOptions.setConfigProperty('qpid.home_dir', homePath);


        brokerOptions.setInitialConfigurationLocation(homePath + configFileName)
        broker.startup(brokerOptions)
        log.info('broker started')

    }

    void cleanup() {
        broker.shutdown()
        FileUtils.deleteDirectory(tmpFolder)
    }


}
