package com.mechzombie.test

import com.google.common.io.Files
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.QueueingConsumer

//import com.rabbitmq.client.Channel
//import com.rabbitmq.client.Connection
//import com.rabbitmq.client.ConnectionFactory
//import com.rabbitmq.client.ConnectionParameters
//import com.rabbitmq.client.QueueingConsumer
import groovy.util.logging.Log
import org.apache.commons.io.FileUtils

import org.apache.qpid.server.Broker
import org.apache.qpid.server.BrokerOptions
import spock.lang.Specification


@Log
class AMQPClientServerTest extends AMQPTestSpecification {

//    def tmpFolder = Files.createTempDir()
//    Broker broker
//
//    def amqpPort = 9234
//    def httpPort = 9235
//
//    def qpidHomeDir = 'src/test/resources/'
//    def configFileName = "/test-config.json"
//
//    void setup() {
//        broker = new Broker();
//        def brokerOptions = new BrokerOptions()
//
//        File file = new File(qpidHomeDir)
//        String homePath = file.getAbsolutePath();
//        log.info(' qpid home dir=' + homePath)
//        log.info(' qpid work dir=' + tmpFolder.absolutePath)
//
//        brokerOptions.setConfigProperty('qpid.work_dir', tmpFolder.absolutePath);
//
//        brokerOptions.setConfigProperty('qpid.amqp_port',"${amqpPort}")
//        brokerOptions.setConfigProperty('qpid.http_port', "${httpPort}")
//        brokerOptions.setConfigProperty('qpid.home_dir', homePath);
//
//
//        brokerOptions.setInitialConfigurationLocation(homePath + configFileName)
//        broker.startup(brokerOptions)
//        log.info('broker started')
//
//    }
//
//    void cleanup() {
//        broker.shutdown()
//        FileUtils.deleteDirectory(tmpFolder)
//    }

    def "test client connection" () {
        expect:
//        ConnectionParameters connParams = new ConnectionParameters()
//        connParams.setUsername('guest')
//       connParams.setPassword('password')
//
//        ConnectionFactory connectionFactory = new ConnectionFactory(connParams);// (ConnectionFactory) context.lookup("qpidConnectionFactory");
//        //def String URL = "amqp://guest:password@clientID/test?brokerlist='tcp://localhost:${amqpPort}'";
//        connectionFactory.useSslProtocol()// .setConnectionURLString(URL)

        //AMQConnection connection = connectionFactory.newConnection('localhost', amqpPort)
        def QUEUE_NAME = 'test_q'
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri("amqp://guest:password@localhost:${amqpPort}")
        factory.useSslProtocol()

        //factory.setHost("localhost");
        //connectionFactory = new ConnectionFactory();
        //connectionFactory.setHost("localhost");
       // //factory.setUsername("guest");
        //connectionFactory.setPassword("guest");
        def connection = factory.newConnection()
        def channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);


        String message = "Hello World!";
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");

        def channel2 = connection.createChannel();

        channel2.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        QueueingConsumer consumer = new QueueingConsumer(channel2);
        channel.basicConsume(QUEUE_NAME, true, consumer);

        def received
        while (!received) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String messagereceived = new String(delivery.getBody());
            System.out.println(" [x] Received '" + messagereceived + "'");
            received = messagereceived
        }

        channel2.close()
        channel.close();
        connection.close();

//        Destination
//        Channel channel = connection.createConnectionConsumer()
//
//        connection.close();
//        context.close();
    }


}
