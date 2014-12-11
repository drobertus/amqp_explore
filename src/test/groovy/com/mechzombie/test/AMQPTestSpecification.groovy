package com.mechzombie.test

import com.google.common.io.Files
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import org.apache.commons.io.FileUtils
import org.apache.qpid.server.Broker
import org.apache.qpid.server.BrokerOptions
import spock.lang.Specification


/**
 * Created by David on 12/10/2014.
 */
class AMQPTestSpecification extends Specification {

    def tmpFolder = Files.createTempDir()
    Broker broker
    ConnectionFactory factory

    def amqpPort = 9234
    def httpPort = 9235

    def qpidHomeDir = 'src/test/resources/'
    def configFileName = "/test-config.json"

    Connection getConection() {
        if (!factory) {
            factory = new ConnectionFactory()
            factory.setUri(getConnectionURL())
            factory.useSslProtocol()
        }
        return factory.newConnection()
    }

    String getConnectionURL() {
        "amqp://guest:password@localhost:${amqpPort}"
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