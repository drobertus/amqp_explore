package com.mechzombie.test.gpars

import groovy.util.logging.Log
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicInteger

import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertEquals

@Ignore
@Log
class TimeConsumingRunnersTest extends Specification {

    def wasteTime= {sleep it}

    def "test that spending time in parallel takes less time than in serial"() {

        setup:

            def  pool = Executors.newFixedThreadPool(4);
            def timeInSerial
            def timeInParallel

            def timeLumpCount = 30
            def timeLumpSizeMS = 100

        when:
            def serialStart = System.currentTimeMillis()
            for(int i =0; i < timeLumpCount; i++) {
                wasteTime timeLumpSizeMS
            }
            timeInSerial = System.currentTimeMillis() - serialStart
            println "serial took ${timeInSerial} ms"

            def parallelStart = System.currentTimeMillis()
            for(int i = 0; i < timeLumpCount; i++) {
                pool.submit ({ wasteTime timeLumpSizeMS })
            }
            timeInParallel = System.currentTimeMillis() - parallelStart
            println "parallel took ${timeInParallel} ms"
        then:
            // we should eat seconds faster in parallel than in serial
            assertTrue timeInParallel < timeInSerial
    }



    def "test performance profile"() {

        expect:
            assertEquals (3000, (loopTime * loopCount))
            def threads = Executors.newFixedThreadPool((int)threadCount);
            def parallelStart = System.currentTimeMillis()

            AtomicInteger completed = new AtomicInteger(0)
            for(int i = 0; i < loopCount; i++) {
                threads.submit ({ sleep loopTime; completed.andIncrement })
            }


            while (completed.get() < loopCount) {
                //log.info("${completed.get()}")
                sleep 23
            }

            def runTime = System.currentTimeMillis() - parallelStart
            log.info("run results threads=${threadCount}, loops=${loopCount}, time=${loopTime}")
            log.info "difference runTime: ${runTime}, runTimeEst: ${estRunTime}, diff=${estRunTime -runTime}"

            assertTrue runTime < (estRunTime + bufferTime)
            assertTrue runTime > (estRunTime - bufferTime)


        where:
        threadCount | loopCount | loopTime | estRunTime | bufferTime
                1   | 1         | 3000     | 3100       | 75
               // 1   | 10        | 300      | 3050       | 50
               // 1   | 20        | 150      | 3040       | 30
               // 1   | 100       | 30       | 3065       | 40
               // 2   | 1         | 3000     | 3015       | 15
                2   | 10        | 300      | 1525       | 25
                2   | 20        | 150      | 1535       | 25
                2   | 100       | 30       | 1515       | 25
                //4   | 1         | 3000     | 3015       | 15
                4   | 10        | 300      | 910        | 15
                4   | 20        | 150      | 770        | 15
                4   | 100       | 30       | 785        | 15
                4   | 4         | 750      | 765        | 15
                8   | 10        | 300      | 610        | 15
                10  | 10        | 300      | 305        | 10
                20  | 20        | 150      | 185        | 15
                30  | 30        | 100      | 132        | 20
                50  | 50        | 60       | 95         | 15
    }
}